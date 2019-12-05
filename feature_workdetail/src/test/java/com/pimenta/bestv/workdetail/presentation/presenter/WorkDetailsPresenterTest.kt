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
import com.nhaarman.mockitokotlin2.*
import com.pimenta.bestv.model.domain.WorkDomainModel
import com.pimenta.bestv.model.domain.WorkPageDomainModel
import com.pimenta.bestv.model.presentation.mapper.toViewModel
import com.pimenta.bestv.model.presentation.model.CastViewModel
import com.pimenta.bestv.model.presentation.model.WorkType
import com.pimenta.bestv.model.presentation.model.WorkViewModel
import com.pimenta.bestv.presentation.kotlin.Quadruple
import com.pimenta.bestv.presentation.scheduler.RxScheduler
import com.pimenta.bestv.route.Route
import com.pimenta.bestv.route.castdetail.CastDetailsRoute
import com.pimenta.bestv.route.workdetail.WorkDetailsRoute
import com.pimenta.bestv.workdetail.domain.GetRecommendationByWorkUseCase
import com.pimenta.bestv.workdetail.domain.GetSimilarByWorkUseCase
import com.pimenta.bestv.workdetail.domain.GetWorkDetailsUseCase
import com.pimenta.bestv.workdetail.domain.SetFavoriteUseCase
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import org.junit.Test

/**
 * Created by marcus on 24-06-2019.
 */
private val RECOMMENDED_PAGE = WorkPageDomainModel(
        page = 1,
        totalPages = 1,
        works = listOf(
                WorkDomainModel(
                        id = 2,
                        title = "Recommended movie",
                        type = WorkDomainModel.Type.MOVIE
                )
        )
)
private val SIMILAR_PAGE = WorkPageDomainModel(
        page = 1,
        totalPages = 1,
        works = listOf(
                WorkDomainModel(
                        id = 3,
                        title = "Similar movie",
                        type = WorkDomainModel.Type.MOVIE
                )
        )
)
private val CAST_DETAILED_VIEW_MODEL = CastViewModel(
        id = 1,
        name = "Carlos",
        character = "Batman",
        birthday = "1990-07-13"
)
private val MOVIE_VIEW_MODEL = WorkViewModel(
        id = 1,
        title = "Batman",
        originalTitle = "Batman",
        type = WorkType.MOVIE
)

private fun aWorkViewModel(favorite: Boolean = false) = WorkViewModel(
        id = 1,
        title = "Title",
        type = WorkType.MOVIE,
        isFavorite = favorite
)

class WorkDetailsPresenterTest {

    private val view: WorkDetailsPresenter.View = mock()
    private val setFavoriteUseCase: SetFavoriteUseCase = mock()
    private val getRecommendationByWorkUseCase: GetRecommendationByWorkUseCase = mock()
    private val getSimilarByWorkUseCase: GetSimilarByWorkUseCase = mock()
    private val getWorkDetailsUseCase: GetWorkDetailsUseCase = mock()
    private val workDetailsRoute: WorkDetailsRoute = mock()
    private val castDetailsRoute: CastDetailsRoute = mock()
    private val rxSchedulerTest = RxScheduler(
            Schedulers.trampoline(),
            Schedulers.trampoline()
    )

    private val presenter = WorkDetailsPresenter(
            view,
            setFavoriteUseCase,
            getRecommendationByWorkUseCase,
            getSimilarByWorkUseCase,
            getWorkDetailsUseCase,
            workDetailsRoute,
            castDetailsRoute,
            rxSchedulerTest
    )

    @Test
    fun `should set a work as favorite`() {
        val workViewModel = aWorkViewModel()

        whenever(setFavoriteUseCase(workViewModel))
                .thenReturn(Completable.complete())

        presenter.setFavorite(workViewModel)

        verify(view, only()).onResultSetFavoriteMovie(true)
    }

    @Test
    fun `should remove a work from favorite`() {
        val workViewModel = aWorkViewModel(favorite = true)

        whenever(setFavoriteUseCase(workViewModel))
                .thenReturn(Completable.complete())

        presenter.setFavorite(workViewModel)

        verify(view, only()).onResultSetFavoriteMovie(false)
    }

    @Test
    fun `should return false if a error happens while setting a work as favorite`() {
        val workViewModel = aWorkViewModel(favorite = true)

        whenever(setFavoriteUseCase(workViewModel))
                .thenReturn(Completable.error(Throwable()))

        presenter.setFavorite(workViewModel)

        verify(view, only()).onResultSetFavoriteMovie(false)
    }

    @Test
    fun `should return the right data when loading the work details`() {
        val workViewModel = aWorkViewModel()
        val recommendedWorks = RECOMMENDED_PAGE.works
                ?.map { it.toViewModel() }
                ?: emptyList()
        val similarWorks = SIMILAR_PAGE.works
                ?.map { it.toViewModel() }
                ?: emptyList()

        whenever(getWorkDetailsUseCase(workViewModel))
                .thenReturn(Single.just(Quadruple(null, null, RECOMMENDED_PAGE, SIMILAR_PAGE)))

        presenter.loadDataByWork(workViewModel)

        inOrder(view) {
            verify(view).onShowProgress()
            verify(view).onDataLoaded(false, null, null, recommendedWorks, similarWorks)
            verify(view).onHideProgress()
            verifyNoMoreInteractions()
        }
    }

    @Test
    fun `should show an error message if a error happens while loading the work details`() {
        val workViewModel = aWorkViewModel()

        whenever(getWorkDetailsUseCase(workViewModel))
                .thenReturn(Single.error(Throwable()))

        presenter.loadDataByWork(workViewModel)

        inOrder(view) {
            verify(view).onShowProgress()
            verify(view).onErrorWorkDetailsLoaded()
            verify(view).onHideProgress()
            verifyNoMoreInteractions()
        }
    }

    @Test
    fun `should load the recommended works and show them`() {
        val workViewModel = aWorkViewModel()

        whenever(getRecommendationByWorkUseCase(workViewModel.type, workViewModel.id, 1))
                .thenReturn(Single.just(RECOMMENDED_PAGE))

        presenter.loadRecommendationByWork(workViewModel)

        val recommendedWorks = RECOMMENDED_PAGE.works
                ?.map { it.toViewModel() }
                ?: emptyList()

        verify(view, only()).onRecommendationLoaded(recommendedWorks)
    }

    @Test
    fun `should not show any data when a error happens while loading the recommended works`() {
        val workViewModel = aWorkViewModel()

        whenever(getRecommendationByWorkUseCase(workViewModel.type, workViewModel.id, 1))
                .thenReturn(Single.error(Throwable()))

        presenter.loadRecommendationByWork(workViewModel)

        verifyZeroInteractions(view)
    }

    @Test
    fun `should load the similar works and show them`() {
        val workViewModel = aWorkViewModel()

        whenever(getSimilarByWorkUseCase(workViewModel.type, workViewModel.id, 1))
                .thenReturn(Single.just(SIMILAR_PAGE))

        presenter.loadSimilarByWork(workViewModel)

        val similarWorks = SIMILAR_PAGE.works
                ?.map { it.toViewModel() }
                ?: emptyList()

        verify(view, only()).onSimilarLoaded(similarWorks)
    }

    @Test
    fun `should not show any data when a error happens while loading the similar works`() {
        val workViewModel = aWorkViewModel()

        whenever(getSimilarByWorkUseCase(workViewModel.type, workViewModel.id, 1))
                .thenReturn(Single.error(Throwable()))

        presenter.loadSimilarByWork(workViewModel)

        verifyZeroInteractions(view)
    }

    @Test
    fun `should open work details when a work is clicked`() {
        val itemViewHolder: Presenter.ViewHolder = mock()
        val route: Route = mock()

        whenever(workDetailsRoute.buildWorkDetailRoute(MOVIE_VIEW_MODEL))
                .thenReturn(route)

        presenter.workClicked(itemViewHolder, MOVIE_VIEW_MODEL)

        verify(view, only()).openWorkDetails(itemViewHolder, route)
    }

    @Test
    fun `should open cast details when a cast is clicked`() {
        val itemViewHolder: Presenter.ViewHolder = mock()
        val route: Route = mock()

        whenever(castDetailsRoute.buildCastDetailRoute(CAST_DETAILED_VIEW_MODEL))
                .thenReturn(route)

        presenter.castClicked(itemViewHolder, CAST_DETAILED_VIEW_MODEL)

        verify(view, only()).openCastDetails(itemViewHolder, route)
    }
}