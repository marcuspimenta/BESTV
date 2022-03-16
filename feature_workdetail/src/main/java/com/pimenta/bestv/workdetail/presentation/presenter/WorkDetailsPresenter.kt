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
import com.pimenta.bestv.model.presentation.model.PageViewModel
import com.pimenta.bestv.model.presentation.model.WorkViewModel
import com.pimenta.bestv.presentation.di.annotation.FragmentScope
import com.pimenta.bestv.presentation.extension.addTo
import com.pimenta.bestv.presentation.presenter.AutoDisposablePresenter
import com.pimenta.bestv.presentation.scheduler.RxScheduler
import com.pimenta.bestv.route.Route
import com.pimenta.bestv.route.castdetail.CastDetailsRoute
import com.pimenta.bestv.route.workdetail.WorkDetailsRoute
import com.pimenta.bestv.workdetail.domain.GetRecommendationByWorkUseCase
import com.pimenta.bestv.workdetail.domain.GetReviewByWorkUseCase
import com.pimenta.bestv.workdetail.domain.GetSimilarByWorkUseCase
import com.pimenta.bestv.workdetail.domain.GetWorkDetailsUseCase
import com.pimenta.bestv.workdetail.domain.SetFavoriteUseCase
import com.pimenta.bestv.workdetail.presentation.mapper.toViewModel
import com.pimenta.bestv.workdetail.presentation.model.ReviewViewModel
import com.pimenta.bestv.workdetail.presentation.model.VideoViewModel
import timber.log.Timber
import javax.inject.Inject

/**
 * Created by marcus on 07-02-2018.
 */
@FragmentScope
class WorkDetailsPresenter @Inject constructor(
    private val view: View,
    private val workViewModel: WorkViewModel,
    private val setFavoriteUseCase: SetFavoriteUseCase,
    private val getRecommendationByWorkUseCase: GetRecommendationByWorkUseCase,
    private val getSimilarByWorkUseCase: GetSimilarByWorkUseCase,
    private val getReviewByWorkUseCase: GetReviewByWorkUseCase,
    private val getWorkDetailsUseCase: GetWorkDetailsUseCase,
    private val workDetailsRoute: WorkDetailsRoute,
    private val castDetailsRoute: CastDetailsRoute,
    private val rxScheduler: RxScheduler
) : AutoDisposablePresenter() {

    private var recommendedPage = 0
    private var totalRecommendedPage = 0
    private var similarPage = 0
    private var totalSimilarPage = 0
    private var reviewPage = 0
    private var totalReviewPage = 0
    private val recommendedWorks by lazy { mutableListOf<WorkViewModel>() }
    private val similarWorks by lazy { mutableListOf<WorkViewModel>() }
    private val reviews by lazy { mutableListOf<ReviewViewModel>() }

    fun setFavorite() {
        setFavoriteUseCase(workViewModel)
            .subscribeOn(rxScheduler.ioScheduler)
            .observeOn(rxScheduler.mainScheduler)
            .subscribe({
                workViewModel.isFavorite = !workViewModel.isFavorite
                view.resultSetFavoriteMovie(workViewModel.isFavorite)
            }, { throwable ->
                Timber.e(throwable, "Error while settings the work as favorite")
                view.resultSetFavoriteMovie(false)
            }).addTo(compositeDisposable)
    }

    fun loadData() {
        getWorkDetailsUseCase(workViewModel)
            .subscribeOn(rxScheduler.ioScheduler)
            .observeOn(rxScheduler.computationScheduler)
            .map { it.toViewModel() }
            .observeOn(rxScheduler.mainScheduler)
            .doOnSubscribe { view.showProgress() }
            .doFinally { view.hideProgress() }
            .subscribe({ result ->
                workViewModel.isFavorite = result.isFavorite

                with(result.recommended) {
                    recommendedPage = page
                    totalRecommendedPage = totalPages
                    results?.let {
                        recommendedWorks.addAll(it)
                    }
                }

                with(result.similar) {
                    similarPage = page
                    totalSimilarPage = totalPages
                    results?.let {
                        similarWorks.addAll(it)
                    }
                }

                with(result.reviews) {
                    reviewPage = page
                    totalReviewPage = totalPages
                    results?.let {
                        reviews.addAll(it)
                    }
                }

                view.dataLoaded(
                    workViewModel.isFavorite,
                    reviews,
                    result.videos,
                    result.casts,
                    recommendedWorks,
                    similarWorks
                )
            }, { throwable ->
                Timber.e(throwable, "Error while loading data by work")
                view.errorWorkDetailsLoaded()
            }).addTo(compositeDisposable)
    }

    fun reviewItemSelected(reviewViewModel: ReviewViewModel) {
        if ((reviews.indexOf(reviewViewModel) < reviews.size - 1) ||
            (reviewPage != 0 && reviewPage + 1 > totalReviewPage)
        ) {
            return
        }

        getReviewByWorkUseCase(workViewModel.type, workViewModel.id, recommendedPage + 1)
            .subscribeOn(rxScheduler.ioScheduler)
            .observeOn(rxScheduler.computationScheduler)
            .map { it.toViewModel() }
            .observeOn(rxScheduler.mainScheduler)
            .subscribe({ result ->
                with(result) {
                    reviewPage = page
                    totalReviewPage = totalPages
                    results?.let {
                        reviews.addAll(it)
                        view.reviewLoaded(reviews)
                    }
                }
            }, { throwable ->
                Timber.e(throwable, "Error while loading reviews by work")
            }).addTo(compositeDisposable)
    }

    fun recommendationItemSelected(recommendedWorkViewModel: WorkViewModel) {
        if ((recommendedWorks.indexOf(recommendedWorkViewModel) < recommendedWorks.size - 1) ||
            (recommendedPage != 0 && recommendedPage + 1 > totalRecommendedPage)
        ) {
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
                    results?.let {
                        recommendedWorks.addAll(it)
                        view.recommendationLoaded(recommendedWorks)
                    }
                }
            }, { throwable ->
                Timber.e(throwable, "Error while loading recommendations by work")
            }).addTo(compositeDisposable)
    }

    fun similarItemSelected(similarWorkViewModel: WorkViewModel) {
        if ((similarWorks.indexOf(similarWorkViewModel) < similarWorks.size - 1) ||
            (similarPage != 0 && similarPage + 1 > totalSimilarPage)
        ) {
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
                    results?.let {
                        similarWorks.addAll(it)
                        view.similarLoaded(similarWorks)
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

    fun videoClicked(videoViewModel: VideoViewModel) {
        view.openVideo(videoViewModel)
    }

    private fun GetWorkDetailsUseCase.WorkDetailsDomainWrapper.toViewModel() = WorkDetailsViewModelWrapper(
        isFavorite,
        videos?.map { it.toViewModel() },
        casts?.map { it.toViewModel() },
        recommended.toViewModel(),
        similar.toViewModel(),
        reviews.toViewModel()
    )

    data class WorkDetailsViewModelWrapper(
        val isFavorite: Boolean,
        val videos: List<VideoViewModel>?,
        val casts: List<CastViewModel>?,
        val recommended: PageViewModel<WorkViewModel>,
        val similar: PageViewModel<WorkViewModel>,
        val reviews: PageViewModel<ReviewViewModel>
    )

    interface View {

        fun showProgress()

        fun hideProgress()

        fun resultSetFavoriteMovie(isFavorite: Boolean)

        fun dataLoaded(
            isFavorite: Boolean,
            reviews: List<ReviewViewModel>?,
            videos: List<VideoViewModel>?,
            casts: List<CastViewModel>?,
            recommendedWorks: List<WorkViewModel>,
            similarWorks: List<WorkViewModel>
        )

        fun reviewLoaded(reviews: List<ReviewViewModel>)

        fun recommendationLoaded(works: List<WorkViewModel>)

        fun similarLoaded(works: List<WorkViewModel>)

        fun errorWorkDetailsLoaded()

        fun openWorkDetails(itemViewHolder: Presenter.ViewHolder, route: Route)

        fun openCastDetails(itemViewHolder: Presenter.ViewHolder, route: Route)

        fun openVideo(videoViewModel: VideoViewModel)
    }
}
