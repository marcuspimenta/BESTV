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

import com.nhaarman.mockitokotlin2.*
import com.pimenta.bestv.common.kotlin.Quintuple
import com.pimenta.bestv.common.presentation.model.WorkPageViewModel
import com.pimenta.bestv.common.presentation.model.WorkType
import com.pimenta.bestv.common.presentation.model.WorkViewModel
import com.pimenta.bestv.common.usecase.WorkUseCase
import com.pimenta.bestv.feature.workdetail.usecase.GetRecommendationByWorkUseCase
import com.pimenta.bestv.feature.workdetail.usecase.GetSimilarByWorkUseCase
import com.pimenta.bestv.feature.workdetail.usecase.GetWorkDetailsUseCase
import com.pimenta.bestv.scheduler.RxScheduler
import com.pimenta.bestv.scheduler.RxSchedulerTest
import io.reactivex.Single
import org.junit.Test

/**
 * Created by marcus on 24-06-2019.
 */
class WorkDetailsPresenterTest {

    private val view: WorkDetailsPresenter.View = mock()
    private val workUseCase: WorkUseCase = mock()
    private val getRecommendationByWorkUseCase: GetRecommendationByWorkUseCase = mock()
    private val getSimilarByWorkUseCase: GetSimilarByWorkUseCase = mock()
    private val getWorkDetailsUseCase: GetWorkDetailsUseCase = mock()
    private val rxScheduler: RxScheduler = RxSchedulerTest()
    private val presenter = WorkDetailsPresenter(
            view,
            workUseCase,
            getRecommendationByWorkUseCase,
            getSimilarByWorkUseCase,
            getWorkDetailsUseCase,
            rxScheduler
    )

    @Test
    fun `should set a work as favorite`() {
        val workViewModel = aWorkViewModel()

        whenever(workUseCase.setFavorite(any())).thenReturn(Single.just(true))

        presenter.setFavorite(workViewModel)

        verify(view).onResultSetFavoriteMovie(true)
    }

    @Test
    fun `should remove a work from favorite`() {
        val workViewModel = aWorkViewModel(favorite = true)

        whenever(workUseCase.setFavorite(any())).thenReturn(Single.just(true))

        presenter.setFavorite(workViewModel)

        verify(view).onResultSetFavoriteMovie(false)
    }

    @Test
    fun `should return false if a error happens while setting a work as favorite`() {
        val workViewModel = aWorkViewModel(favorite = true)

        whenever(workUseCase.setFavorite(any())).thenReturn(Single.error(Throwable()))

        presenter.setFavorite(workViewModel)

        verify(view).onResultSetFavoriteMovie(false)
    }

    @Test
    fun `should return the right data when loading the work details`() {
        val workViewModel = aWorkViewModel()

        whenever(getWorkDetailsUseCase(any()))
                .thenReturn(Single.just(Quintuple(true, null, null, recommendedPage, similarPage)))

        presenter.loadDataByWork(workViewModel)

        val recommendedWorks = mutableListOf<WorkViewModel>().apply {
            recommendedPage.works?.let {
                addAll(it)
            }
        }
        val similarWorks = mutableListOf<WorkViewModel>().apply {
            similarPage.works?.let {
                addAll(it)
            }
        }

        verify(view).onShowProgress()
        verify(view).onDataLoaded(true, null, null, recommendedWorks, similarWorks)
        verify(view).onHideProgress()
    }

    @Test
    fun `should show an error message if a error happens while loading the work details`() {
        val workViewModel = aWorkViewModel()

        whenever(getWorkDetailsUseCase(any())).thenReturn(Single.error(Throwable()))

        presenter.loadDataByWork(workViewModel)

        verify(view).onShowProgress()
        verify(view).onHideProgress()
        verify(view).onErrorWorkDetailsLoaded()
    }

    @Test
    fun `should load the recommended works and show them`() {
        val workViewModel = aWorkViewModel()

        whenever(getRecommendationByWorkUseCase(any(), any())).thenReturn(Single.just(recommendedPage))


        presenter.loadRecommendationByWork(workViewModel)

        val recommendedWorks = mutableListOf<WorkViewModel>().apply {
            recommendedPage.works?.let {
                addAll(it)
            }
        }

        verify(view).onRecommendationLoaded(recommendedWorks)
    }

    @Test
    fun `should not show any data when a error happens while loading the recommended works`() {
        val workViewModel = aWorkViewModel()

        whenever(getRecommendationByWorkUseCase(any(), any())).thenReturn(Single.error(Throwable()))

        presenter.loadRecommendationByWork(workViewModel)

        verify(view, never()).onRecommendationLoaded(mutableListOf())
    }

    @Test
    fun `should load the similar works and show them`() {
        val workViewModel = aWorkViewModel()

        whenever(getSimilarByWorkUseCase(any(), any())).thenReturn(Single.just(similarPage))

        presenter.loadSimilarByWork(workViewModel)

        val similarWorks = mutableListOf<WorkViewModel>().apply {
            similarPage.works?.let {
                addAll(it)
            }
        }

        verify(view).onSimilarLoaded(similarWorks)
    }

    @Test
    fun `should not show any data when a error happens while loading the similar works`() {
        val workViewModel = aWorkViewModel()

        whenever(getSimilarByWorkUseCase(any(), any())).thenReturn(Single.error(Throwable()))

        presenter.loadSimilarByWork(workViewModel)

        verify(view, never()).onSimilarLoaded(mutableListOf())
    }

    companion object {
        private val recommendedPage = WorkPageViewModel(
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

        private val similarPage = WorkPageViewModel(
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
    }

}