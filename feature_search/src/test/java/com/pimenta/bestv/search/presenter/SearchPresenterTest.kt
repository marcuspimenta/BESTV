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

package com.pimenta.bestv.search.presenter

import android.content.Intent
import androidx.leanback.widget.Presenter
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.inOrder
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.only
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyZeroInteractions
import com.nhaarman.mockitokotlin2.whenever
import com.pimenta.bestv.model.domain.PageDomainModel
import com.pimenta.bestv.model.domain.WorkDomainModel
import com.pimenta.bestv.model.presentation.model.WorkType
import com.pimenta.bestv.model.presentation.model.WorkViewModel
import com.pimenta.bestv.presentation.scheduler.RxScheduler
import com.pimenta.bestv.route.workdetail.WorkDetailsRoute
import com.pimenta.bestv.search.domain.SearchMoviesByQueryUseCase
import com.pimenta.bestv.search.domain.SearchTvShowsByQueryUseCase
import com.pimenta.bestv.search.domain.SearchWorksByQueryUseCase
import com.pimenta.bestv.search.presentation.presenter.SearchPresenter
import io.reactivex.Single
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.Schedulers
import io.reactivex.schedulers.TestScheduler
import org.junit.Test
import java.util.concurrent.TimeUnit

/**
 * Created by marcus on 24-05-2018.
 */
private const val QUERY = "Batman"
private val WORK_VIEW_MODEL = WorkViewModel(
    id = 1,
    title = "Game of thrones",
    originalTitle = "Game of thrones",
    type = WorkType.TV_SHOW
)
private val OTHER_WORK_VIEW_MODEL = WorkViewModel(
    id = 1,
    title = "Arrow",
    originalTitle = "Arrow",
    type = WorkType.TV_SHOW
)
private val MOVIE_VIEW_MODEL_LIST = listOf(
    WorkViewModel(
        id = 1,
        title = "Batman",
        originalTitle = "Batman",
        type = WorkType.MOVIE
    )
)
private val MOVIE_DOMAIN_MODEL_LIST = listOf(
    WorkDomainModel(
        id = 1,
        title = "Batman",
        originalTitle = "Batman",
        type = WorkDomainModel.Type.MOVIE
    )
)
private val MOVIE_PAGE_DOMAIN_MODEL = PageDomainModel(
    page = 1,
    totalPages = 10,
    results = MOVIE_DOMAIN_MODEL_LIST
)
private val TV_SHOW_VIEW_MODEL_LIST = listOf(
    WorkViewModel(
        id = 1,
        title = "Batman",
        originalTitle = "Batman",
        type = WorkType.TV_SHOW
    )
)
private val TV_SHOW_DOMAIN_MODEL_LIST = listOf(
    WorkDomainModel(
        id = 1,
        title = "Batman",
        originalTitle = "Batman",
        type = WorkDomainModel.Type.TV_SHOW
    )
)
private val TV_SHOW_PAGE_DOMAIN_MODEL = PageDomainModel(
    page = 1,
    totalPages = 10,
    results = TV_SHOW_DOMAIN_MODEL_LIST
)

class SearchPresenterTest {

    private val view: SearchPresenter.View = mock()
    private val searchWorksByQueryUseCase: SearchWorksByQueryUseCase = mock()
    private val searchMoviesByQueryUseCase: SearchMoviesByQueryUseCase = mock()
    private val searchTvShowsByQueryUseCase: SearchTvShowsByQueryUseCase = mock()
    private val workDetailsRoute: WorkDetailsRoute = mock()
    private val rxSchedulerTest = RxScheduler(
        Schedulers.trampoline(),
        Schedulers.trampoline(),
        Schedulers.trampoline()
    )

    private val presenter = SearchPresenter(
        view,
        searchWorksByQueryUseCase,
        searchMoviesByQueryUseCase,
        searchTvShowsByQueryUseCase,
        workDetailsRoute,
        rxSchedulerTest
    )

    @Test
    fun `should search the works by query`() {
        val result = MOVIE_PAGE_DOMAIN_MODEL to TV_SHOW_PAGE_DOMAIN_MODEL

        whenever(searchWorksByQueryUseCase(QUERY)).thenReturn(Single.just(result))

        presenter.searchWorksByQuery(QUERY)

        inOrder(view) {
            verify(view).onShowProgress()
            verify(view).onResultLoaded(MOVIE_VIEW_MODEL_LIST, TV_SHOW_VIEW_MODEL_LIST)
            verify(view).onHideProgress()
            verifyNoMoreInteractions()
        }
    }

    @Test
    fun `should return null if an error happens while searching the works by query`() {
        whenever(searchWorksByQueryUseCase(QUERY)).thenReturn(Single.error(Throwable()))

        presenter.searchWorksByQuery(QUERY)

        inOrder(view) {
            verify(view).onShowProgress()
            verify(view).onErrorSearch()
            verify(view).onHideProgress()
            verifyNoMoreInteractions()
        }
    }

    @Test
    fun `should not search the works when the query is null`() {
        presenter.searchWorksByQuery(null)

        inOrder(view) {
            verify(view).onHideProgress()
            verify(view).onClear()
            verifyNoMoreInteractions()
        }
    }

    @Test
    fun `should not search the works when the query is empty`() {
        presenter.searchWorksByQuery("")

        inOrder(view) {
            verify(view).onHideProgress()
            verify(view).onClear()
            verifyNoMoreInteractions()
        }
    }

    @Test
    fun `should return the right data when loading the movies`() {
        whenever(searchMoviesByQueryUseCase(any(), any()))
            .thenReturn(Single.just(MOVIE_PAGE_DOMAIN_MODEL))

        presenter.loadMovies()

        verify(view, only()).onMoviesLoaded(MOVIE_VIEW_MODEL_LIST)
    }

    @Test
    fun `should return null if an error happens while loading the movies`() {
        whenever(searchMoviesByQueryUseCase(any(), any())).thenReturn(Single.error(Throwable()))

        presenter.loadMovies()

        verifyZeroInteractions(view)
    }

    @Test
    fun `should return the right data when loading the tv shows`() {
        whenever(searchTvShowsByQueryUseCase(any(), any()))
            .thenReturn(Single.just(TV_SHOW_PAGE_DOMAIN_MODEL))

        presenter.loadTvShows()

        verify(view, only()).onTvShowsLoaded(TV_SHOW_VIEW_MODEL_LIST)
    }

    @Test
    fun `should return null if an error happens while loading the tv shows`() {
        whenever(searchTvShowsByQueryUseCase(any(), any())).thenReturn(Single.error(Throwable()))

        presenter.loadTvShows()

        verifyZeroInteractions(view)
    }

    @Test
    fun `should wait some time and then return the view model to the view`() {
        val testScheduler = TestScheduler()
        RxJavaPlugins.setComputationSchedulerHandler { testScheduler }

        presenter.countTimerLoadBackdropImage(WORK_VIEW_MODEL)

        testScheduler.advanceTimeBy(300L, TimeUnit.MILLISECONDS)

        verify(view, only()).loadBackdropImage(WORK_VIEW_MODEL)
    }

    @Test
    fun `should return the right view model to the view if the counter is called before the first one finishes`() {
        val testScheduler = TestScheduler()
        RxJavaPlugins.setComputationSchedulerHandler { testScheduler }

        presenter.countTimerLoadBackdropImage(WORK_VIEW_MODEL)

        testScheduler.advanceTimeBy(100L, TimeUnit.MILLISECONDS)

        presenter.countTimerLoadBackdropImage(OTHER_WORK_VIEW_MODEL)

        testScheduler.advanceTimeBy(300L, TimeUnit.MILLISECONDS)

        verify(view, only()).loadBackdropImage(OTHER_WORK_VIEW_MODEL)
    }

    @Test
    fun `should open work details when a work is clicked`() {
        val itemViewHolder: Presenter.ViewHolder = mock()
        val intent: Intent = mock()

        whenever(workDetailsRoute.buildWorkDetailIntent(WORK_VIEW_MODEL))
            .thenReturn(intent)

        presenter.workClicked(itemViewHolder, WORK_VIEW_MODEL)

        verify(view, only()).openWorkDetails(itemViewHolder, intent)
    }
}
