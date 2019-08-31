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

import com.nhaarman.mockitokotlin2.*
import com.pimenta.bestv.common.kotlin.Quintuple
import com.pimenta.bestv.common.presentation.model.WorkPageViewModel
import com.pimenta.bestv.common.presentation.model.WorkType
import com.pimenta.bestv.common.presentation.model.WorkViewModel
import com.pimenta.bestv.feature.workdetail.domain.GetRecommendationByWorkUseCase
import com.pimenta.bestv.feature.workdetail.domain.GetSimilarByWorkUseCase
import com.pimenta.bestv.feature.workdetail.domain.GetWorkDetailsUseCase
import com.pimenta.bestv.feature.workdetail.domain.SetFavoriteUseCase
import com.pimenta.bestv.scheduler.RxScheduler
import com.pimenta.bestv.scheduler.RxSchedulerTest
import io.reactivex.Completable
import io.reactivex.Single
import org.junit.Test

/**
 * Created by marcus on 24-06-2019.
 */
private val RECOMMENDED_PAGE = WorkPageViewModel(
        page = 1,
        totalPages = 1,
        works = listOf(
                WorkViewModel(
                        id = 2,
                        title = "Recommended movie",
                        type = WorkType.MOVIE
                )
        )
)
private val SIMILAR_PAGE = WorkPageViewModel(
        page = 1,
        totalPages = 1,
        works = listOf(
                WorkViewModel(
                        id = 3,
                        title = "Similar movie",
                        type = WorkType.MOVIE
                )
        )
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
    private val rxScheduler: RxScheduler = RxSchedulerTest()
    private val presenter = WorkDetailsPresenter(
            view,
            setFavoriteUseCase,
            getRecommendationByWorkUseCase,
            getSimilarByWorkUseCase,
            getWorkDetailsUseCase,
            rxScheduler
    )

    @Test
    fun `should set a work as favorite`() {
        val workViewModel = aWorkViewModel()

        whenever(setFavoriteUseCase(workViewModel)).thenReturn(Completable.complete())

        presenter.setFavorite(workViewModel)

        verify(view, only()).onResultSetFavoriteMovie(true)
    }

    @Test
    fun `should remove a work from favorite`() {
        val workViewModel = aWorkViewModel(favorite = true)

        whenever(setFavoriteUseCase(workViewModel)).thenReturn(Completable.complete())

        presenter.setFavorite(workViewModel)

        verify(view, only()).onResultSetFavoriteMovie(false)
    }

    @Test
    fun `should return false if a error happens while setting a work as favorite`() {
        val workViewModel = aWorkViewModel(favorite = true)

        whenever(setFavoriteUseCase(workViewModel)).thenReturn(Completable.error(Throwable()))

        presenter.setFavorite(workViewModel)

        verify(view, only()).onResultSetFavoriteMovie(false)
    }

    @Test
    fun `should return the right data when loading the work details`() {
        val workViewModel = aWorkViewModel()

        whenever(getWorkDetailsUseCase(workViewModel))
                .thenReturn(Single.just(Quintuple(true, null, null, RECOMMENDED_PAGE, SIMILAR_PAGE)))

        presenter.loadDataByWork(workViewModel)

        val recommendedWorks = mutableListOf<WorkViewModel>().apply {
            RECOMMENDED_PAGE.works?.let {
                addAll(it)
            }
        }
        val similarWorks = mutableListOf<WorkViewModel>().apply {
            SIMILAR_PAGE.works?.let {
                addAll(it)
            }
        }

        inOrder(view) {
            verify(view).onShowProgress()
            verify(view).onDataLoaded(true, null, null, recommendedWorks, similarWorks)
            verify(view).onHideProgress()
        }
        verifyNoMoreInteractions(view)
    }

    @Test
    fun `should show an error message if a error happens while loading the work details`() {
        val workViewModel = aWorkViewModel()

        whenever(getWorkDetailsUseCase(workViewModel)).thenReturn(Single.error(Throwable()))

        presenter.loadDataByWork(workViewModel)

        inOrder(view) {
            verify(view).onShowProgress()
            verify(view).onErrorWorkDetailsLoaded()
            verify(view).onHideProgress()
        }
        verifyNoMoreInteractions(view)
    }

    @Test
    fun `should load the recommended works and show them`() {
        val workViewModel = aWorkViewModel()

        whenever(getRecommendationByWorkUseCase(workViewModel.type, workViewModel.id, 1)).thenReturn(Single.just(RECOMMENDED_PAGE))

        presenter.loadRecommendationByWork(workViewModel)

        val recommendedWorks = mutableListOf<WorkViewModel>().apply {
            RECOMMENDED_PAGE.works?.let {
                addAll(it)
            }
        }

        verify(view, only()).onRecommendationLoaded(recommendedWorks)
    }

    @Test
    fun `should not show any data when a error happens while loading the recommended works`() {
        val workViewModel = aWorkViewModel()

        whenever(getRecommendationByWorkUseCase(workViewModel.type, workViewModel.id, 1)).thenReturn(Single.error(Throwable()))

        presenter.loadRecommendationByWork(workViewModel)

        verifyZeroInteractions(view)
    }

    @Test
    fun `should load the similar works and show them`() {
        val workViewModel = aWorkViewModel()

        whenever(getSimilarByWorkUseCase(workViewModel.type, workViewModel.id, 1)).thenReturn(Single.just(SIMILAR_PAGE))

        presenter.loadSimilarByWork(workViewModel)

        val similarWorks = mutableListOf<WorkViewModel>().apply {
            SIMILAR_PAGE.works?.let {
                addAll(it)
            }
        }

        verify(view, only()).onSimilarLoaded(similarWorks)
    }

    @Test
    fun `should not show any data when a error happens while loading the similar works`() {
        val workViewModel = aWorkViewModel()

        whenever(getSimilarByWorkUseCase(workViewModel.type, workViewModel.id, 1)).thenReturn(Single.error(Throwable()))

        presenter.loadSimilarByWork(workViewModel)

        verifyZeroInteractions(view)
    }
}