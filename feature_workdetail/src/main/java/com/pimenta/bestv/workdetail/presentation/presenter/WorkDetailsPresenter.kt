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

package com.pimenta.bestv.workdetail.presentation.presenter

import androidx.leanback.widget.Presenter
import com.pimenta.bestv.model.presentation.mapper.toViewModel
import com.pimenta.bestv.model.presentation.model.CastViewModel
import com.pimenta.bestv.model.presentation.model.WorkViewModel
import com.pimenta.bestv.presentation.di.annotation.FragmentScope
import com.pimenta.bestv.presentation.extension.addTo
import com.pimenta.bestv.presentation.kotlin.Quadruple
import com.pimenta.bestv.presentation.presenter.AutoDisposablePresenter
import com.pimenta.bestv.presentation.scheduler.RxScheduler
import com.pimenta.bestv.route.Route
import com.pimenta.bestv.route.castdetail.CastDetailsRoute
import com.pimenta.bestv.route.workdetail.WorkDetailsRoute
import com.pimenta.bestv.workdetail.domain.GetRecommendationByWorkUseCase
import com.pimenta.bestv.workdetail.domain.GetSimilarByWorkUseCase
import com.pimenta.bestv.workdetail.domain.GetWorkDetailsUseCase
import com.pimenta.bestv.workdetail.domain.SetFavoriteUseCase
import com.pimenta.bestv.workdetail.presentation.mapper.toViewModel
import com.pimenta.bestv.workdetail.presentation.model.VideoViewModel
import javax.inject.Inject
import timber.log.Timber

/**
 * Created by marcus on 07-02-2018.
 */
@FragmentScope
class WorkDetailsPresenter @Inject constructor(
    private val view: View,
    private val setFavoriteUseCase: SetFavoriteUseCase,
    private val getRecommendationByWorkUseCase: GetRecommendationByWorkUseCase,
    private val getSimilarByWorkUseCase: GetSimilarByWorkUseCase,
    private val getWorkDetailsUseCase: GetWorkDetailsUseCase,
    private val workDetailsRoute: WorkDetailsRoute,
    private val castDetailsRoute: CastDetailsRoute,
    private val rxScheduler: RxScheduler
) : AutoDisposablePresenter() {

    private var recommendedPage = 0
    private var totalRecommendedPage = 0
    private var similarPage = 0
    private var totalSimilarPage = 0
    private val recommendedWorks by lazy { mutableListOf<WorkViewModel>() }
    private val similarWorks by lazy { mutableListOf<WorkViewModel>() }

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
                .observeOn(rxScheduler.computationScheduler)
                .map { movieInfo ->
                    val videoViewModes = movieInfo.first?.map { it.toViewModel() }
                    val castViewModels = movieInfo.second?.map { it.toViewModel() }
                    val recommendationPageViewModel = movieInfo.third.toViewModel()
                    val similarPageViewModel = movieInfo.fourth.toViewModel()
                    Quadruple(videoViewModes, castViewModels, recommendationPageViewModel, similarPageViewModel)
                }
                .observeOn(rxScheduler.mainScheduler)
                .doOnSubscribe { view.onShowProgress() }
                .doFinally { view.onHideProgress() }
                .subscribe({ movieInfo ->
                    with(movieInfo.third) {
                        recommendedPage = page
                        totalRecommendedPage = totalPages
                        works?.let {
                            recommendedWorks.addAll(it)
                        }
                    }

                    with(movieInfo.fourth) {
                        similarPage = page
                        totalSimilarPage = totalPages
                        works?.let {
                            similarWorks.addAll(it)
                        }
                    }

                    view.onDataLoaded(
                            workViewModel.isFavorite,
                            movieInfo.first,
                            movieInfo.second,
                            recommendedWorks,
                            similarWorks
                    )
                }, { throwable ->
                    Timber.e(throwable, "Error while loading data by work")
                    view.onErrorWorkDetailsLoaded()
                }).addTo(compositeDisposable)
    }

    fun loadRecommendationByWork(workViewModel: WorkViewModel) {
        if (recommendedPage != 0 && recommendedPage + 1 > totalRecommendedPage) {
            return
        }

        getRecommendationByWorkUseCase(workViewModel.type, workViewModel.id, recommendedPage + 1)
                .subscribeOn(rxScheduler.ioScheduler)
                .observeOn(rxScheduler.computationScheduler)
                .map { it.toViewModel() }
                .observeOn(rxScheduler.mainScheduler)
                .subscribe({ moviePage ->
                    with(moviePage) {
                        recommendedPage = page
                        totalRecommendedPage = totalPages
                        works?.let {
                            recommendedWorks.addAll(it)
                            view.onRecommendationLoaded(recommendedWorks)
                        }
                    }
                }, { throwable ->
                    Timber.e(throwable, "Error while loading recommendations by work")
                }).addTo(compositeDisposable)
    }

    fun loadSimilarByWork(workViewModel: WorkViewModel) {
        if (similarPage != 0 && similarPage + 1 > totalSimilarPage) {
            return
        }

        getSimilarByWorkUseCase(workViewModel.type, workViewModel.id, similarPage + 1)
                .subscribeOn(rxScheduler.ioScheduler)
                .observeOn(rxScheduler.computationScheduler)
                .map { it.toViewModel() }
                .observeOn(rxScheduler.mainScheduler)
                .subscribe({ moviePage ->
                    with(moviePage) {
                        similarPage = page
                        totalSimilarPage = totalPages
                        works?.let {
                            similarWorks.addAll(it)
                            view.onSimilarLoaded(similarWorks)
                        }
                    }
                }, { throwable ->
                    Timber.e(throwable, "Error while loading similar by work")
                }).addTo(compositeDisposable)
    }

    fun workClicked(itemViewHolder: Presenter.ViewHolder, workViewModel: WorkViewModel) {
        val route = workDetailsRoute.buildWorkDetailRoute(workViewModel)
        view.openWorkDetails(itemViewHolder, route)
    }

    fun castClicked(itemViewHolder: Presenter.ViewHolder, castViewModel: CastViewModel) {
        val route = castDetailsRoute.buildCastDetailRoute(castViewModel)
        view.openCastDetails(itemViewHolder, route)
    }

    interface View {

        fun onShowProgress()

        fun onHideProgress()

        fun onResultSetFavoriteMovie(isFavorite: Boolean)

        fun onDataLoaded(isFavorite: Boolean, videos: List<VideoViewModel>?, casts: List<CastViewModel>?, recommendedWorks: List<WorkViewModel>, similarWorks: List<WorkViewModel>)

        fun onRecommendationLoaded(works: List<WorkViewModel>)

        fun onSimilarLoaded(works: List<WorkViewModel>)

        fun onErrorWorkDetailsLoaded()

        fun openWorkDetails(itemViewHolder: Presenter.ViewHolder, route: Route)

        fun openCastDetails(itemViewHolder: Presenter.ViewHolder, route: Route)
    }
}
