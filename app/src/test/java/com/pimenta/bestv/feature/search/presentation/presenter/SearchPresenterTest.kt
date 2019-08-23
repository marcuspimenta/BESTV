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

package com.pimenta.bestv.feature.search.presentation.presenter

import com.nhaarman.mockitokotlin2.*
import com.pimenta.bestv.common.presentation.model.WorkPageViewModel
import com.pimenta.bestv.common.presentation.model.WorkType
import com.pimenta.bestv.common.presentation.model.WorkViewModel
import com.pimenta.bestv.common.domain.WorkUseCase
import com.pimenta.bestv.feature.search.domain.SearchWorksByQueryUseCase
import com.pimenta.bestv.scheduler.RxScheduler
import com.pimenta.bestv.scheduler.RxSchedulerTest
import io.reactivex.Single
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.TestScheduler
import org.junit.Test
import java.util.concurrent.TimeUnit

/**
 * Created by marcus on 24-05-2018.
 */
class SearchPresenterTest {

    private val view: SearchPresenter.View = mock()
    private val workUseCase: WorkUseCase = mock()
    private val searchWorksByQueryUseCase: SearchWorksByQueryUseCase = mock()
    private val rxScheduler: RxScheduler = RxSchedulerTest()

    private val presenter = SearchPresenter(
            view,
            workUseCase,
            searchWorksByQueryUseCase,
            rxScheduler
    )

    @Test
    fun `should search the works by query`() {
        val result = Pair(aMoviePageViewModel, aTvShowPageViewModel)

        whenever(searchWorksByQueryUseCase(any())).thenReturn(Single.just(result))

        presenter.searchWorksByQuery(query)

        verify(view).onShowProgress()
        verify(view).onResultLoaded(aMovieList, aTvShowList)
        verify(view).onHideProgress()
    }

    @Test
    fun `should return null if an error happens while searching the works by query`() {
        whenever(searchWorksByQueryUseCase(any())).thenReturn(Single.error(Throwable()))

        presenter.searchWorksByQuery(query)

        verify(view).onHideProgress()
        verify(view).onErrorSearch()
    }

    @Test
    fun `should not search the works when the query is null`() {
        whenever(searchWorksByQueryUseCase(any())).thenReturn(Single.error(Throwable()))

        presenter.searchWorksByQuery(null)

        verify(view).onHideProgress()
        verify(view).onClear()
    }

    @Test
    fun `should not search the works when the query is empty`() {
        whenever(searchWorksByQueryUseCase(any())).thenReturn(Single.error(Throwable()))

        presenter.searchWorksByQuery("")

        verify(view).onHideProgress()
        verify(view).onClear()
    }

    @Test
    fun `should return the right data when loading the movies`() {
        whenever(workUseCase.searchMoviesByQuery(any(), any())).thenReturn(Single.just(aMoviePageViewModel))

        presenter.loadMovies()

        verify(view).onMoviesLoaded(aMovieList)
    }

    @Test
    fun `should return null if an error happens while loading the movies`() {
        whenever(workUseCase.searchMoviesByQuery(any(), any())).thenReturn(Single.error(Throwable()))

        presenter.loadMovies()

        verifyZeroInteractions(view)
    }

    @Test
    fun `should return the right data when loading the tv shows`() {
        whenever(workUseCase.searchTvShowsByQuery(any(), any())).thenReturn(Single.just(aTvShowPageViewModel))

        presenter.loadTvShows()

        verify(view).onTvShowsLoaded(aTvShowList)
    }

    @Test
    fun `should return null if an error happens while loading the tv shows`() {
        whenever(workUseCase.searchTvShowsByQuery(any(), any())).thenReturn(Single.error(Throwable()))

        presenter.loadTvShows()

        verifyZeroInteractions(view)
    }

    @Test
    fun `should wait some time and then return the view model to the view`() {
        val testScheduler = TestScheduler()
        RxJavaPlugins.setComputationSchedulerHandler { testScheduler }

        presenter.countTimerLoadBackdropImage(aWorkViewModel)

        testScheduler.advanceTimeBy(300L, TimeUnit.MILLISECONDS)

        verify(view).loadBackdropImage(aWorkViewModel)
    }

    @Test
    fun `should return the right view model to the view if the counter is called before the first one finishes`() {
        val testScheduler = TestScheduler()
        RxJavaPlugins.setComputationSchedulerHandler { testScheduler }

        presenter.countTimerLoadBackdropImage(aWorkViewModel)

        testScheduler.advanceTimeBy(100L, TimeUnit.MILLISECONDS)

        presenter.countTimerLoadBackdropImage(otherWorkViewModel)

        testScheduler.advanceTimeBy(300L, TimeUnit.MILLISECONDS)

        verify(view).loadBackdropImage(otherWorkViewModel)
    }

    companion object {

        private const val query = "Batman"

        private val aWorkViewModel = WorkViewModel(
                id = 1,
                title = "Game of thrones",
                originalTitle = "Game of thrones",
                type = WorkType.TV_SHOW
        )

        private val otherWorkViewModel = WorkViewModel(
                id = 1,
                title = "Arrow",
                originalTitle = "Arrow",
                type = WorkType.TV_SHOW
        )

        private val aMovieList = listOf(
                WorkViewModel(
                        id = 1,
                        title = "Batman",
                        originalTitle = "Batman",
                        type = WorkType.MOVIE
                )
        )

        private val aMoviePageViewModel = WorkPageViewModel(
                page = 1,
                totalPages = 10,
                works = aMovieList
        )

        private val aTvShowList = listOf(
                WorkViewModel(
                        id = 1,
                        title = "Batman",
                        originalTitle = "Batman",
                        type = WorkType.TV_SHOW
                )
        )

        private val aTvShowPageViewModel = WorkPageViewModel(
                page = 1,
                totalPages = 10,
                works = aTvShowList
        )

    }
}