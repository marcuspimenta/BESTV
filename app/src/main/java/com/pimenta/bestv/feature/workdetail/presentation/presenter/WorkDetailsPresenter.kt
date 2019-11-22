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

package com.pimenta.bestv.feature.workdetail.presentation.presenter

import androidx.leanback.widget.Presenter
import com.pimenta.bestv.common.extension.addTo
import com.pimenta.bestv.common.mvp.AutoDisposablePresenter
import com.pimenta.bestv.model.presentation.model.CastViewModel
import com.pimenta.bestv.model.presentation.model.VideoViewModel
import com.pimenta.bestv.model.presentation.model.WorkViewModel
import com.pimenta.bestv.feature.workdetail.domain.GetRecommendationByWorkUseCase
import com.pimenta.bestv.feature.workdetail.domain.GetSimilarByWorkUseCase
import com.pimenta.bestv.feature.workdetail.domain.GetWorkDetailsUseCase
import com.pimenta.bestv.feature.workdetail.domain.SetFavoriteUseCase
import com.pimenta.bestv.model.presentation.mapper.toViewModel
import com.pimenta.bestv.scheduler.RxScheduler
import timber.log.Timber
import javax.inject.Inject

/**
 * Created by marcus on 07-02-2018.
 */
class WorkDetailsPresenter @Inject constructor(
    private val view: View,
    private val setFavoriteUseCase: SetFavoriteUseCase,
    private val getRecommendationByWorkUseCase: GetRecommendationByWorkUseCase,
    private val getSimilarByWorkUseCase: GetSimilarByWorkUseCase,
    private val getWorkDetailsUseCase: GetWorkDetailsUseCase,
    private val rxScheduler: RxScheduler
) : AutoDisposablePresenter() {

    private var recommendedPage = 0
    private var similarPage = 0
    private val recommendedWorks = mutableListOf<WorkViewModel>()
    private val similarWorks = mutableListOf<WorkViewModel>()

    fun setFavorite(workViewModel: WorkViewModel) {
        setFavoriteUseCase(workViewModel)
                .subscribeOn(rxScheduler.ioScheduler)
                .observeOn(rxScheduler.mainScheduler)
                .subscribe({
                    view.onResultSetFavoriteMovie(!workViewModel.isFavorite)
                }, { throwable ->
                    Timber.e(throwable, "Error while settings the work as favorite")
                    view.onResultSetFavoriteMovie(false)
                }).addTo(compositeDisposable)
    }

    fun loadDataByWork(workViewModel: WorkViewModel) {
        getWorkDetailsUseCase(workViewModel)
                .subscribeOn(rxScheduler.ioScheduler)
                .observeOn(rxScheduler.mainScheduler)
                .doOnSubscribe { view.onShowProgress() }
                .doFinally { view.onHideProgress() }
                .subscribe({ movieInfo ->
                    val recommendedPage = movieInfo.third
                    if (recommendedPage.page <= recommendedPage.totalPages) {
                        this.recommendedPage = recommendedPage.page
                        recommendedPage.works?.let {
                            recommendedWorks.addAll(it.map { work -> work.toViewModel() })
                        }
                    }

                    val similarPage = movieInfo.fourth
                    if (similarPage.page <= similarPage.totalPages) {
                        this.similarPage = similarPage.page
                        similarPage.works?.let {
                            similarWorks.addAll(it.map { work -> work.toViewModel() })
                        }
                    }

                    view.onDataLoaded(
                            workViewModel.isFavorite,
                            movieInfo.first?.map { it.toViewModel() },
                            movieInfo.second?.map { it.toViewModel() },
                            recommendedWorks,
                            similarWorks
                    )
                }, { throwable ->
                    Timber.e(throwable, "Error while loading data by work")
                    view.onErrorWorkDetailsLoaded()
                }).addTo(compositeDisposable)
    }

    fun loadRecommendationByWork(workViewModel: WorkViewModel) {
        getRecommendationByWorkUseCase(workViewModel.type, workViewModel.id, recommendedPage + 1)
                .subscribeOn(rxScheduler.ioScheduler)
                .observeOn(rxScheduler.mainScheduler)
                .subscribe({ movieList ->
                    if (movieList != null && movieList.page <= movieList.totalPages) {
                        recommendedPage = movieList.page
                        movieList.works?.let {
                            recommendedWorks.addAll(it.map { work -> work.toViewModel() })
                            view.onRecommendationLoaded(recommendedWorks)
                        }
                    }
                }, { throwable ->
                    Timber.e(throwable, "Error while loading recommendations by work")
                }).addTo(compositeDisposable)
    }

    fun loadSimilarByWork(workViewModel: WorkViewModel) {
        getSimilarByWorkUseCase(workViewModel.type, workViewModel.id, similarPage + 1)
                .subscribeOn(rxScheduler.ioScheduler)
                .observeOn(rxScheduler.mainScheduler)
                .subscribe({ movieList ->
                    if (movieList != null && movieList.page <= movieList.totalPages) {
                        similarPage = movieList.page
                        movieList.works?.let {
                            similarWorks.addAll(it.map { work -> work.toViewModel() })
                            view.onSimilarLoaded(similarWorks)
                        }
                    }
                }, { throwable ->
                    Timber.e(throwable, "Error while loading similar by work")
                }).addTo(compositeDisposable)
    }

    fun workClicked(itemViewHolder: Presenter.ViewHolder, workViewModel: WorkViewModel) {
        view.openWorkDetails(itemViewHolder, workViewModel)
    }

    fun castClicked(itemViewHolder: Presenter.ViewHolder, castViewModel: CastViewModel) {
        view.openCastDetails(itemViewHolder, castViewModel)
    }

    interface View {

        fun onShowProgress()

        fun onHideProgress()

        fun onResultSetFavoriteMovie(isFavorite: Boolean)

        fun onDataLoaded(isFavorite: Boolean, videos: List<VideoViewModel>?, casts: List<CastViewModel>?, recommendedWorks: List<WorkViewModel>, similarWorks: List<WorkViewModel>)

        fun onRecommendationLoaded(works: List<WorkViewModel>)

        fun onSimilarLoaded(works: List<WorkViewModel>)

        fun onErrorWorkDetailsLoaded()

        fun openWorkDetails(itemViewHolder: Presenter.ViewHolder, workViewModel: WorkViewModel)

        fun openCastDetails(itemViewHolder: Presenter.ViewHolder, castViewModel: CastViewModel)
    }
}