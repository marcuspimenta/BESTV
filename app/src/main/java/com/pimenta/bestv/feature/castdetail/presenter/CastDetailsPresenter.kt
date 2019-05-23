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

package com.pimenta.bestv.feature.castdetail.presenter

import com.pimenta.bestv.common.mvp.AutoDisposablePresenter
import com.pimenta.bestv.common.presentation.mapper.toCast
import com.pimenta.bestv.common.presentation.mapper.toSingle
import com.pimenta.bestv.common.presentation.model.CastViewModel
import com.pimenta.bestv.common.presentation.model.WorkViewModel
import com.pimenta.bestv.data.entity.Cast
import com.pimenta.bestv.extension.addTo
import com.pimenta.bestv.feature.castdetail.usecase.GetCastDetailsUseCase
import com.pimenta.bestv.scheduler.RxScheduler
import timber.log.Timber
import javax.inject.Inject

/**
 * Created by marcus on 05-04-2018.
 */
class CastDetailsPresenter @Inject constructor(
        private val view: View,
        private val getCastDetailsUseCase: GetCastDetailsUseCase,
        private val rxScheduler: RxScheduler
) : AutoDisposablePresenter() {

    /**
     * Load the [Cast] details
     *
     * @param cast [Cast]
     */
    fun loadCastDetails(castViewModel: CastViewModel) {
        castViewModel.toCast().toSingle()
                .flatMap { getCastDetailsUseCase(it) }
                .subscribeOn(rxScheduler.ioScheduler)
                .observeOn(rxScheduler.mainScheduler)
                .subscribe({ triple ->
                    view.onCastLoaded(
                            triple.first,
                            triple.second,
                            triple.third
                    )
                }, { throwable ->
                    Timber.e(throwable, "Error while getting the cast details")
                    view.onCastLoaded(null, null, null)
                }).addTo(compositeDisposable)
    }

    interface View {

        fun onCastLoaded(castViewModel: CastViewModel?, movies: List<WorkViewModel>?, tvShow: List<WorkViewModel>?)

    }
}