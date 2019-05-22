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

import com.pimenta.bestv.common.mvp.AutoDisposablePresenter
import com.pimenta.bestv.common.presentation.mapper.toSingle
import com.pimenta.bestv.common.presentation.mapper.toWork
import com.pimenta.bestv.common.presentation.model.CastViewModel
import com.pimenta.bestv.common.presentation.model.VideoViewModel
import com.pimenta.bestv.common.presentation.model.WorkViewModel
import com.pimenta.bestv.common.usecase.WorkUseCase
import com.pimenta.bestv.data.entity.Work
import com.pimenta.bestv.extension.addTo
import com.pimenta.bestv.feature.workdetail.usecase.GetRecommendationByWorkUseCase
import com.pimenta.bestv.feature.workdetail.usecase.GetSimilarByWorkUseCase
import com.pimenta.bestv.feature.workdetail.usecase.GetWorkDetailsUseCase
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
     * Sets if a [Work] is or not is favorite
     *
     * @param workViewModel [WorkViewModel]
     */
    fun setFavorite(workViewModel: WorkViewModel) {
        workViewModel.toWork().toSingle()
                .flatMap { workUseCase.setFavorite(it) }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    view.onResultSetFavoriteMovie(!workViewModel.isFavorite)
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
        workViewModel.toWork().toSingle()
                .flatMap { getWorkDetailsUseCase(it) }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ movieInfo ->
                    val recommendedPage = movieInfo.third
                    if (recommendedPage.page <= recommendedPage.totalPages) {
                        this.recommendedPage = recommendedPage.page
                    }
                    val similarPage = movieInfo.fourth
                    if (similarPage.page <= similarPage.totalPages) {
                        this.similarPage = similarPage.page
                    }

                    view.onDataLoaded(
                            movieInfo.first,
                            movieInfo.second,
                            recommendedPage.works,
                            similarPage.works,
                            movieInfo.fifth
                    )
                }, { throwable ->
                    Timber.e(throwable, "Error while loading data by work")
                    view.onDataLoaded(false, null, null, null, null)
                }).addTo(compositeDisposable)
    }

    /**
     * Loads the [<] recommended by the [Work]
     *
     * @param workViewModel [WorkViewModel]
     */
    fun loadRecommendationByWork(workViewModel: WorkViewModel) {
        workViewModel.toWork().toSingle()
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
        workViewModel.toWork().toSingle()
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

        fun onResultSetFavoriteMovie(isFavorite: Boolean)

        fun onDataLoaded(isFavorite: Boolean, casts: List<CastViewModel>?, recommendedWorks: List<WorkViewModel>?, similarWorks: List<WorkViewModel>?, videos: List<VideoViewModel>?)

        fun onRecommendationLoaded(works: List<WorkViewModel>?)

        fun onSimilarLoaded(works: List<WorkViewModel>?)

    }
}