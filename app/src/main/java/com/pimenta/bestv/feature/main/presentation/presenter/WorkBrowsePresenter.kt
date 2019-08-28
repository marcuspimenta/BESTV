/*
 * Copyright (C) 2018 Marcus Pimenta
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package com.pimenta.bestv.feature.main.presentation.presenter

import com.pimenta.bestv.common.extension.addTo
import com.pimenta.bestv.common.mvp.AutoDisposablePresenter
import com.pimenta.bestv.common.presentation.model.GenreViewModel
import com.pimenta.bestv.feature.main.domain.GetWorkBrowseDetailsUseCase
import com.pimenta.bestv.feature.main.domain.HasFavoriteUseCase
import com.pimenta.bestv.scheduler.RxScheduler
import timber.log.Timber
import javax.inject.Inject

/**
 * Created by marcus on 06-02-2018.
 */
class WorkBrowsePresenter @Inject constructor(
        private val view: View,
        private val hasFavoriteUseCase: HasFavoriteUseCase,
        private val getWorkBrowseDetailsUseCase: GetWorkBrowseDetailsUseCase,
        private val rxScheduler: RxScheduler
) : AutoDisposablePresenter() {

    fun hasFavorite() {
        hasFavoriteUseCase()
                .subscribeOn(rxScheduler.ioScheduler)
                .observeOn(rxScheduler.mainScheduler)
                .subscribe({ result ->
                    view.onHasFavorite(result)
                }, { throwable ->
                    Timber.e(throwable, "Error while checking if has any work as favorite")
                    view.onHasFavorite(false)
                }).addTo(compositeDisposable)
    }

    fun loadData() {
        getWorkBrowseDetailsUseCase()
                .subscribeOn(rxScheduler.ioScheduler)
                .observeOn(rxScheduler.mainScheduler)
                .doOnSubscribe { view.onShowProgress() }
                .doFinally { view.onHideProgress() }
                .subscribe({
                    view.onDataLoaded(
                            it.first,
                            it.second,
                            it.third
                    )
                }, { throwable ->
                    Timber.e(throwable, "Error while loading data")
                    view.onErrorDataLoaded()
                }).addTo(compositeDisposable)
    }

    interface View {

        fun onShowProgress()

        fun onHideProgress()

        fun onDataLoaded(hasFavoriteMovie: Boolean, movieGenres: List<GenreViewModel>?, tvShowGenres: List<GenreViewModel>?)

        fun onHasFavorite(hasFavoriteMovie: Boolean)

        fun onErrorDataLoaded()

    }
}