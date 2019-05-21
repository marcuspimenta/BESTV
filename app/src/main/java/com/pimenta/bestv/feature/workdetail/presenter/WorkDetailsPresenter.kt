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

package com.pimenta.bestv.feature.workdetail.presenter

import com.pimenta.bestv.common.presentation.mapper.toWork
import com.pimenta.bestv.common.presentation.model.CastViewModel
import com.pimenta.bestv.common.presentation.model.VideoViewModel
import com.pimenta.bestv.common.presentation.model.WorkViewModel
import com.pimenta.bestv.common.usecase.GetRecommendationByWorkUseCase
import com.pimenta.bestv.common.usecase.GetSimilarByWorkUseCase
import com.pimenta.bestv.common.usecase.GetWorkDetailsUseCase
import com.pimenta.bestv.common.usecase.WorkUseCase
import com.pimenta.bestv.extension.addTo
import com.pimenta.bestv.common.mvp.AutoDisposablePresenter
import com.pimenta.bestv.data.entity.Work
import io.reactivex.Maybe
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import javax.inject.Inject

/**
 * Created by marcus on 07-02-2018.
 */
class WorkDetailsPresenter @Inject constructor(
        private val view: View,
        private val workUseCase: WorkUseCase,
        private val getRecommendationByWorkUseCase: GetRecommendationByWorkUseCase,
        private val getSimilarByWorkUseCase: GetSimilarByWorkUseCase,
        private val getWorkDetailsUseCase: GetWorkDetailsUseCase
) : AutoDisposablePresenter() {

    private var recommendedPage = 0
    private var similarPage = 0

    /**
     * Checks if the [Work] is favorite
     *
     * @param workViewModel [WorkViewModel]
     * @return `true` if yes, `false` otherwise
     */
    fun isFavorite(workViewModel: WorkViewModel): Boolean {
        workViewModel.isFavorite = workUseCase.isFavorite(workViewModel.toWork())
        return workViewModel.isFavorite
    }

    /**
     * Sets if a [Work] is or not is favorite
     *
     * @param workViewModel [WorkViewModel]
     */
    fun setFavorite(workViewModel: WorkViewModel) {
        Maybe.fromCallable { workViewModel.toWork() }
                .flatMap { work ->
                    Maybe.create<Boolean> {
                        val result = if (workViewModel.isFavorite) {
                            workUseCase.deleteFavorite(work)
                        } else {
                            workUseCase.saveFavorite(work)
                        }

                        if (result) {
                            it.onSuccess(true)
                        } else {
                            it.onComplete()
                        }
                    }
                }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    workViewModel.isFavorite = !workViewModel.isFavorite
                    view.onResultSetFavoriteMovie(true)
                }, { throwable ->
                    Timber.e(throwable, "Error while settings the work as favorite")
                    view.onResultSetFavoriteMovie(false)
                }).addTo(compositeDisposable)
    }

    /**
     * Loads the [<] by the [Work]
     *
     * @param workViewModel [WorkViewModel]
     */
    fun loadDataByWork(workViewModel: WorkViewModel) {
        Single.fromCallable { workViewModel.toWork() }
                .flatMap { getWorkDetailsUseCase(it) }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ movieInfo ->
                    val recommendedPage = movieInfo.second
                    if (recommendedPage.page <= recommendedPage.totalPages) {
                        this.recommendedPage = recommendedPage.page
                    }
                    val similarPage = movieInfo.third
                    if (similarPage.page <= similarPage.totalPages) {
                        this.similarPage = similarPage.page
                    }

                    view.onDataLoaded(
                            movieInfo.first,
                            recommendedPage.works,
                            similarPage.works,
                            movieInfo.fourth
                    )
                }, { throwable ->
                    Timber.e(throwable, "Error while loading data by work")
                    view.onDataLoaded(null, null, null, null)
                }).addTo(compositeDisposable)
    }

    /**
     * Loads the [<] recommended by the [Work]
     *
     * @param workViewModel [WorkViewModel]
     */
    fun loadRecommendationByWork(workViewModel: WorkViewModel) {
        Single.fromCallable { workViewModel.toWork() }
                .flatMap { getRecommendationByWorkUseCase(it, recommendedPage + 1) }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ movieList ->
                    if (movieList != null && movieList.page <= movieList.totalPages) {
                        recommendedPage = movieList.page
                        view.onRecommendationLoaded(movieList.works)
                    } else {
                        view.onRecommendationLoaded(null)
                    }
                }, { throwable ->
                    Timber.e(throwable, "Error while loading recommendations by work")
                    view.onRecommendationLoaded(null)
                }).addTo(compositeDisposable)
    }

    /**
     * Loads the [<] similar by the [Work]
     *
     * @param workViewModel [WorkViewModel]
     */
    fun loadSimilarByWork(workViewModel: WorkViewModel) {
        Single.fromCallable { workViewModel.toWork() }
                .flatMap { getSimilarByWorkUseCase(it, similarPage + 1) }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ movieList ->
                    if (movieList != null && movieList.page <= movieList.totalPages) {
                        similarPage = movieList.page
                        view.onSimilarLoaded(movieList.works)
                    } else {
                        view.onSimilarLoaded(null)
                    }
                }, { throwable ->
                    Timber.e(throwable, "Error while loading similar by work")
                    view.onSimilarLoaded(null)
                }).addTo(compositeDisposable)
    }

    interface View {

        fun onResultSetFavoriteMovie(success: Boolean)

        fun onDataLoaded(casts: List<CastViewModel>?, recommendedWorks: List<WorkViewModel>?, similarWorks: List<WorkViewModel>?, videos: List<VideoViewModel>?)

        fun onRecommendationLoaded(works: List<WorkViewModel>?)

        fun onSimilarLoaded(works: List<WorkViewModel>?)

    }
}