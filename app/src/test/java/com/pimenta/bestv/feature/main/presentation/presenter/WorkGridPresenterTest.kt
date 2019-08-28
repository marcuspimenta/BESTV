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

import com.nhaarman.mockitokotlin2.*
import com.pimenta.bestv.common.presentation.model.*
import com.pimenta.bestv.data.MediaRepository
import com.pimenta.bestv.feature.main.domain.GetFavoritesUseCase
import com.pimenta.bestv.feature.main.domain.GetWorkByGenreUseCase
import com.pimenta.bestv.feature.main.domain.LoadWorkByTypeUseCase
import com.pimenta.bestv.scheduler.RxSchedulerTest
import io.reactivex.Single
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.TestScheduler
import org.junit.Test
import java.util.concurrent.TimeUnit

/**
 * Created by marcus on 2019-08-26.
 */
private val MOVIE_GENRE = GenreViewModel(
        id = 1,
        source = Source.MOVIE
)
private val MOVIE_VIEW_MODEL = WorkViewModel(
        id = 1,
        title = "Batman",
        originalTitle = "Batman",
        type = WorkType.MOVIE
)
private val TV_SHOW_VIEW_MODEL = WorkViewModel(
        id = 1,
        title = "Arrow",
        originalTitle = "Arrow",
        type = WorkType.TV_SHOW
)
private val MOVIE_PAGE_VIEW_MODEL = WorkPageViewModel(
        page = 1,
        totalPages = 1,
        works = listOf(
                MOVIE_VIEW_MODEL
        )
)
private val EMPTY_PAGE_VIEW_MODEL = WorkPageViewModel(
        page = 1,
        totalPages = 1
)

class WorkGridPresenterTest {

    private val view: WorkGridPresenter.View = mock()
    private val getFavoritesUseCase: GetFavoritesUseCase = mock()
    private val getWorkByGenreUseCase: GetWorkByGenreUseCase = mock()
    private val loadWorkByTypeUseCase: LoadWorkByTypeUseCase = mock()

    private val presenter = WorkGridPresenter(
            view,
            getFavoritesUseCase,
            getWorkByGenreUseCase,
            loadWorkByTypeUseCase,
            RxSchedulerTest()
    )

    @Test
    fun `should show the favorite works when loading them`() {
        whenever(getFavoritesUseCase()).thenReturn(Single.just(listOf(MOVIE_VIEW_MODEL)))

        presenter.loadWorksByType(MediaRepository.WorkType.FAVORITES_MOVIES)

        inOrder(view) {
            verify(view).onShowProgress()
            verify(view).onHideProgress()
            verify(view).onWorksLoaded(listOf(MOVIE_VIEW_MODEL))
        }
        verifyNoMoreInteractions(view)
    }

    @Test
    fun `should show an error message if an error happens while loading the favorite works`() {
        whenever(getFavoritesUseCase()).thenReturn(Single.error(Throwable()))

        presenter.loadWorksByType(MediaRepository.WorkType.FAVORITES_MOVIES)

        inOrder(view) {
            verify(view).onShowProgress()
            verify(view).onHideProgress()
            verify(view).onErrorWorksLoaded()
        }
        verifyNoMoreInteractions(view)
    }

    @Test
    fun `should show the works when loading them by type`() {
        whenever(loadWorkByTypeUseCase(1, MediaRepository.WorkType.NOW_PLAYING_MOVIES)).thenReturn(Single.just(MOVIE_PAGE_VIEW_MODEL))

        presenter.loadWorksByType(MediaRepository.WorkType.NOW_PLAYING_MOVIES)

        inOrder(view) {
            verify(view).onShowProgress()
            verify(view).onHideProgress()
            verify(view).onWorksLoaded(listOf(MOVIE_VIEW_MODEL))
        }
        verifyNoMoreInteractions(view)
    }

    @Test
    fun `should show an error message if an error happens while loading the works by type`() {
        whenever(loadWorkByTypeUseCase(1, MediaRepository.WorkType.NOW_PLAYING_MOVIES)).thenReturn(Single.error(Throwable()))

        presenter.loadWorksByType(MediaRepository.WorkType.NOW_PLAYING_MOVIES)

        inOrder(view) {
            verify(view).onShowProgress()
            verify(view).onHideProgress()
            verify(view).onErrorWorksLoaded()
        }
        verifyNoMoreInteractions(view)
    }

    @Test
    fun `should not show any work if any work is found when loading the works by type`() {
        whenever(loadWorkByTypeUseCase(1, MediaRepository.WorkType.NOW_PLAYING_MOVIES)).thenReturn(Single.just(EMPTY_PAGE_VIEW_MODEL))

        presenter.loadWorksByType(MediaRepository.WorkType.NOW_PLAYING_MOVIES)

        inOrder(view) {
            verify(view).onShowProgress()
            verify(view).onHideProgress()
        }
        verifyNoMoreInteractions(view)
    }

    @Test
    fun `should load the works by genre`() {
        whenever(getWorkByGenreUseCase(MOVIE_GENRE, 1)).thenReturn(Single.just(MOVIE_PAGE_VIEW_MODEL))

        presenter.loadWorkByGenre(MOVIE_GENRE)

        inOrder(view) {
            verify(view).onShowProgress()
            verify(view).onHideProgress()
            verify(view).onWorksLoaded(listOf(MOVIE_VIEW_MODEL))
        }
        verifyNoMoreInteractions(view)
    }

    @Test
    fun `should show an error message when an error happens while loading the works by genre`() {
        whenever(getWorkByGenreUseCase(MOVIE_GENRE, 1)).thenReturn(Single.error(Throwable()))

        presenter.loadWorkByGenre(MOVIE_GENRE)

        inOrder(view) {
            verify(view).onShowProgress()
            verify(view).onHideProgress()
            verify(view).onErrorWorksLoaded()
        }
        verifyNoMoreInteractions(view)
    }

    @Test
    fun `should wait some time and then return the view model to the view`() {
        val testScheduler = TestScheduler()
        RxJavaPlugins.setComputationSchedulerHandler { testScheduler }

        presenter.countTimerLoadBackdropImage(MOVIE_VIEW_MODEL)

        testScheduler.advanceTimeBy(300L, TimeUnit.MILLISECONDS)

        verify(view).loadBackdropImage(MOVIE_VIEW_MODEL)
    }

    @Test
    fun `should return the right view model to the view if the counter is called before the first one finishes`() {
        val testScheduler = TestScheduler()
        RxJavaPlugins.setComputationSchedulerHandler { testScheduler }

        presenter.countTimerLoadBackdropImage(MOVIE_VIEW_MODEL)

        testScheduler.advanceTimeBy(100L, TimeUnit.MILLISECONDS)

        presenter.countTimerLoadBackdropImage(TV_SHOW_VIEW_MODEL)

        testScheduler.advanceTimeBy(300L, TimeUnit.MILLISECONDS)

        verify(view, times(1)).loadBackdropImage(TV_SHOW_VIEW_MODEL)
    }

}