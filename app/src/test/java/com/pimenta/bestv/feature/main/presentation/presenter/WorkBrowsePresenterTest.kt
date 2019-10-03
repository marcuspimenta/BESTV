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
import com.pimenta.bestv.common.presentation.model.GenreViewModel
import com.pimenta.bestv.common.presentation.model.Source
import com.pimenta.bestv.feature.main.domain.GetWorkBrowseDetailsUseCase
import com.pimenta.bestv.feature.main.domain.HasFavoriteUseCase
import com.pimenta.bestv.scheduler.RxScheduler
import com.pimenta.bestv.scheduler.RxSchedulerTest
import io.reactivex.Single
import org.junit.Test

/**
 * Created by marcus on 28-05-2019.
 */
private val MOVIE_GENRES = listOf(
        GenreViewModel(
                id = 1,
                name = "Action",
                source = Source.MOVIE
        )
)
private val TV_SHOW_GENRES = listOf(
        GenreViewModel(
                id = 2,
                name = "Action",
                source = Source.TV_SHOW
        )
)

class WorkBrowsePresenterTest {

    private val view: WorkBrowsePresenter.View = mock()
    private val hasFavoriteUseCase: HasFavoriteUseCase = mock()
    private val getWorkBrowseDetailsUseCase: GetWorkBrowseDetailsUseCase = mock()
    private val rxScheduler: RxScheduler = RxSchedulerTest()

    private val presenter = WorkBrowsePresenter(
            view,
            hasFavoriteUseCase,
            getWorkBrowseDetailsUseCase,
            rxScheduler
    )

    @Test
    fun `should return true if there is some favorite works`() {
        whenever(hasFavoriteUseCase()).thenReturn(Single.just(true))

        presenter.hasFavorite()

        verify(view).onHasFavorite(true)
        verifyNoMoreInteractions(view)
    }

    @Test
    fun `should return false if there is not any favorite works`() {
        whenever(hasFavoriteUseCase()).thenReturn(Single.just(false))

        presenter.hasFavorite()

        verify(view).onHasFavorite(false)
        verifyNoMoreInteractions(view)
    }

    @Test
    fun `should return false if an exception happens while checking if there is some favorite works`() {
        whenever(hasFavoriteUseCase()).thenReturn(Single.error(Throwable()))

        presenter.hasFavorite()

        verify(view).onHasFavorite(false)
        verifyNoMoreInteractions(view)
    }

    @Test
    fun `should load the right data when loading the browse details`() {
        whenever(getWorkBrowseDetailsUseCase()).thenReturn(Single.just(Triple(true, MOVIE_GENRES, TV_SHOW_GENRES)))

        presenter.loadData()

        inOrder(view) {
            verify(view).onShowProgress()
            verify(view).onDataLoaded(true, MOVIE_GENRES, TV_SHOW_GENRES)
            verify(view).onHideProgress()
            verifyNoMoreInteractions()
        }
    }

    @Test
    fun `should return null when loading the browse details if an exception happens`() {
        whenever(getWorkBrowseDetailsUseCase()).thenReturn(Single.error(Throwable()))

        presenter.loadData()

        inOrder(view) {
            verify(view).onShowProgress()
            verify(view).onErrorDataLoaded()
            verify(view).onHideProgress()
            verifyNoMoreInteractions()
        }
    }

    @Test
    fun `should open the search view when click in the search icon`() {
        presenter.searchClicked()

        verify(view, only()).openSearchView()
    }
}