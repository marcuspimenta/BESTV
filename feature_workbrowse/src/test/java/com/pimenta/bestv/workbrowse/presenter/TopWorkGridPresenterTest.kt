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

package com.pimenta.bestv.workbrowse.presenter

import android.content.Intent
import androidx.leanback.widget.Presenter
import com.nhaarman.mockitokotlin2.inOrder
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.only
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.pimenta.bestv.model.domain.PageDomainModel
import com.pimenta.bestv.model.domain.WorkDomainModel
import com.pimenta.bestv.model.presentation.model.WorkType
import com.pimenta.bestv.model.presentation.model.WorkViewModel
import com.pimenta.bestv.presentation.dispatcher.CoroutineDispatchers
import com.pimenta.bestv.route.workdetail.WorkDetailsRoute
import com.pimenta.bestv.workbrowse.domain.LoadWorkByTypeUseCase
import com.pimenta.bestv.workbrowse.presentation.model.TopWorkTypeViewModel
import com.pimenta.bestv.workbrowse.presentation.presenter.TopWorkGridPresenter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

/**
 * Created by marcus on 2019-08-26.
 */
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
private val MOVIE_DOMAIN_MODEL = WorkDomainModel(
    id = 1,
    title = "Batman",
    originalTitle = "Batman",
    type = WorkDomainModel.Type.MOVIE
)
private val MOVIE_PAGE_DOMAIN_MODEL = PageDomainModel(
    page = 1,
    totalPages = 1,
    results = listOf(
        MOVIE_DOMAIN_MODEL
    )
)
private val EMPTY_PAGE_VIEW_MODEL = PageDomainModel<WorkDomainModel>(
    page = 1,
    totalPages = 1
)

@OptIn(ExperimentalCoroutinesApi::class)
class TopWorkGridPresenterTest {

    private val testDispatcher = StandardTestDispatcher()
    private val view: TopWorkGridPresenter.View = mock()
    private val loadWorkByTypeUseCase: LoadWorkByTypeUseCase = mock()
    private val workDetailsRoute: WorkDetailsRoute = mock()
    private val coroutineDispatchers = CoroutineDispatchers(
        testDispatcher,
        testDispatcher
    )

    private val presenter = TopWorkGridPresenter(
        view,
        loadWorkByTypeUseCase,
        workDetailsRoute,
        coroutineDispatchers
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `should show the works when loading them by type`() = runTest {
        whenever(loadWorkByTypeUseCase(1, TopWorkTypeViewModel.NOW_PLAYING_MOVIES))
            .thenReturn(MOVIE_PAGE_DOMAIN_MODEL)

        presenter.loadWorkPageByType(TopWorkTypeViewModel.NOW_PLAYING_MOVIES)
        advanceUntilIdle()

        inOrder(view) {
            verify(view).onShowProgress()
            verify(view).onWorksLoaded(listOf(MOVIE_VIEW_MODEL))
            verify(view).onHideProgress()
            verifyNoMoreInteractions()
        }
    }

    @Test
    fun `should show an error message if an error happens while loading the works by type`() = runTest {
        whenever(loadWorkByTypeUseCase(1, TopWorkTypeViewModel.NOW_PLAYING_MOVIES))
            .thenThrow(RuntimeException())

        presenter.loadWorkPageByType(TopWorkTypeViewModel.NOW_PLAYING_MOVIES)
        advanceUntilIdle()

        inOrder(view) {
            verify(view).onShowProgress()
            verify(view).onErrorWorksLoaded()
            verify(view).onHideProgress()
            verifyNoMoreInteractions()
        }
    }

    @Test
    fun `should not show any work if any work is found when loading the works by type`() = runTest {
        whenever(loadWorkByTypeUseCase(1, TopWorkTypeViewModel.NOW_PLAYING_MOVIES))
            .thenReturn(EMPTY_PAGE_VIEW_MODEL)

        presenter.loadWorkPageByType(TopWorkTypeViewModel.NOW_PLAYING_MOVIES)
        advanceUntilIdle()

        inOrder(view) {
            verify(view).onShowProgress()
            verify(view).onHideProgress()
            verifyNoMoreInteractions()
        }
    }

    @Test
    fun `should wait some time and then return the view model to the view`() = runTest {
        presenter.countTimerLoadBackdropImage(MOVIE_VIEW_MODEL)

        advanceTimeBy(300L)
        runCurrent()

        verify(view).loadBackdropImage(MOVIE_VIEW_MODEL)
    }

    @Test
    fun `should return the right view model to the view if the counter is called before the first one finishes`() = runTest {
        presenter.countTimerLoadBackdropImage(MOVIE_VIEW_MODEL)

        advanceTimeBy(100L)
        runCurrent()

        presenter.countTimerLoadBackdropImage(TV_SHOW_VIEW_MODEL)

        advanceTimeBy(300L)
        runCurrent()

        verify(view, times(1)).loadBackdropImage(TV_SHOW_VIEW_MODEL)
    }

    @Test
    fun `should open work details when a work is clicked`() {
        val intent: Intent = mock()
        val itemViewHolder: Presenter.ViewHolder = mock()

        whenever(workDetailsRoute.buildWorkDetailIntent(MOVIE_VIEW_MODEL)).thenReturn(intent)

        presenter.workClicked(itemViewHolder, MOVIE_VIEW_MODEL)

        verify(view, only()).openWorkDetails(itemViewHolder, intent)
    }
}
