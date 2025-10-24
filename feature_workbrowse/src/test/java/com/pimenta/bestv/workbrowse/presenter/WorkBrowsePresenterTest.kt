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
import androidx.leanback.widget.Row
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.check
import com.nhaarman.mockitokotlin2.inOrder
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.only
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.pimenta.bestv.presentation.dispatcher.CoroutineDispatchers
import com.pimenta.bestv.presentation.platform.Resource
import com.pimenta.bestv.route.search.SearchRoute
import com.pimenta.bestv.workbrowse.domain.GetWorkBrowseDetailsUseCase
import com.pimenta.bestv.workbrowse.domain.HasFavoriteUseCase
import com.pimenta.bestv.workbrowse.domain.model.GenreDomainModel
import com.pimenta.bestv.workbrowse.presentation.presenter.WorkBrowsePresenter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test

/**
 * Created by marcus on 28-05-2019.
 */
private const val EMPTY_STRING = ""
private val MOVIE_GENRE_DOMAIN_MODELS = listOf(
    GenreDomainModel(
        id = 1,
        name = "Action",
        source = GenreDomainModel.Source.MOVIE
    )
)
private val TV_SHOW_GENRE_DOMAIN_MODELS = listOf(
    GenreDomainModel(
        id = 2,
        name = "Action",
        source = GenreDomainModel.Source.TV_SHOW
    )
)

@OptIn(ExperimentalCoroutinesApi::class)
class WorkBrowsePresenterTest {

    private val testDispatcher = StandardTestDispatcher()
    private val view: WorkBrowsePresenter.View = mock()
    private val hasFavoriteUseCase: HasFavoriteUseCase = mock()
    private val getWorkBrowseDetailsUseCase: GetWorkBrowseDetailsUseCase = mock()
    private val searchRoute: SearchRoute = mock()
    private val resource: Resource = mock()
    private val coroutineDispatchers = CoroutineDispatchers(
        testDispatcher,
        testDispatcher
    )

    private val presenter = WorkBrowsePresenter(
        view,
        hasFavoriteUseCase,
        getWorkBrowseDetailsUseCase,
        searchRoute,
        resource,
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
    fun `should load the right data when loading the browse details and there is favorite works`() = runTest {
        whenever(resource.getStringResource(any()))
            .thenReturn(EMPTY_STRING)
        whenever(getWorkBrowseDetailsUseCase())
            .thenReturn(Triple(true, MOVIE_GENRE_DOMAIN_MODELS, TV_SHOW_GENRE_DOMAIN_MODELS))

        presenter.loadData()
        advanceUntilIdle()

        inOrder(view) {
            verify(view).onShowProgress()
            verify(view).onDataLoaded(
                check {
                    Assert.assertEquals(it.size, 17)
                }
            )
            verify(view).onHideProgress()
            verifyNoMoreInteractions()
        }
    }

    @Test
    fun `should load the right data when loading the browse details and there is not any favorite work`() = runTest {
        whenever(resource.getStringResource(any()))
            .thenReturn(EMPTY_STRING)
        whenever(getWorkBrowseDetailsUseCase())
            .thenReturn(Triple(false, MOVIE_GENRE_DOMAIN_MODELS, TV_SHOW_GENRE_DOMAIN_MODELS))

        presenter.loadData()
        advanceUntilIdle()

        inOrder(view) {
            verify(view).onShowProgress()
            verify(view).onDataLoaded(
                check {
                    Assert.assertEquals(it.size, 16)
                }
            )
            verify(view).onHideProgress()
            verifyNoMoreInteractions()
        }
    }

    @Test
    fun `should return an error when loading the browse details if an exception happens`() = runTest {
        whenever(getWorkBrowseDetailsUseCase()).thenThrow(RuntimeException())

        presenter.loadData()
        advanceUntilIdle()

        inOrder(view) {
            verify(view).onShowProgress()
            verify(view).onErrorDataLoaded()
            verify(view).onHideProgress()
            verifyNoMoreInteractions()
        }
    }

    @Test
    fun `should not add the favorite row when it already was added`() = runTest {
        whenever(resource.getStringResource(any()))
            .thenReturn(EMPTY_STRING)
        whenever(getWorkBrowseDetailsUseCase())
            .thenReturn(Triple(true, MOVIE_GENRE_DOMAIN_MODELS, TV_SHOW_GENRE_DOMAIN_MODELS))
        whenever(hasFavoriteUseCase()).thenReturn(true)

        presenter.loadData()
        advanceUntilIdle()
        presenter.addFavoriteRow(0)
        advanceUntilIdle()

        inOrder(view) {
            verify(view).onShowProgress()
            verify(view).onDataLoaded(
                check {
                    Assert.assertEquals(it.size, 17)
                }
            )
            verify(view).onHideProgress()
            verifyNoMoreInteractions()
        }
    }

    @Test
    fun `should notify to update the selected position 3 when the favorite row does not exist anymore`() = runTest {
        whenever(resource.getStringResource(any()))
            .thenReturn(EMPTY_STRING)
        whenever(getWorkBrowseDetailsUseCase())
            .thenReturn(Triple(true, MOVIE_GENRE_DOMAIN_MODELS, TV_SHOW_GENRE_DOMAIN_MODELS))
        whenever(hasFavoriteUseCase()).thenReturn(false)

        presenter.loadData()
        advanceUntilIdle()
        presenter.addFavoriteRow(0)
        advanceUntilIdle()

        inOrder(view) {
            val captor = argumentCaptor<List<Row>>()

            verify(view).onShowProgress()
            verify(view).onDataLoaded(captor.capture())
            verify(view).onHideProgress()
            verify(view).onUpdateSelectedPosition(3)
            verifyNoMoreInteractions()
        }
    }

    @Test
    fun `should add the favorite row when it already was not added`() = runTest {
        whenever(resource.getStringResource(any()))
            .thenReturn(EMPTY_STRING)
        whenever(getWorkBrowseDetailsUseCase())
            .thenReturn(Triple(false, MOVIE_GENRE_DOMAIN_MODELS, TV_SHOW_GENRE_DOMAIN_MODELS))
        whenever(hasFavoriteUseCase()).thenReturn(true)

        presenter.loadData()
        advanceUntilIdle()
        presenter.addFavoriteRow(0)
        advanceUntilIdle()

        inOrder(view) {
            val captor = argumentCaptor<List<Row>>()

            verify(view).onShowProgress()
            verify(view).onDataLoaded(captor.capture())
            verify(view).onHideProgress()
            verify(view).onDataLoaded(captor.capture())

            verifyNoMoreInteractions()
        }
    }

    @Test
    fun `should not notify to update the selected position when the favorite row does not exist`() = runTest {
        whenever(resource.getStringResource(any()))
            .thenReturn(EMPTY_STRING)
        whenever(getWorkBrowseDetailsUseCase())
            .thenReturn(Triple(false, MOVIE_GENRE_DOMAIN_MODELS, TV_SHOW_GENRE_DOMAIN_MODELS))
        whenever(hasFavoriteUseCase()).thenReturn(false)

        presenter.loadData()
        advanceUntilIdle()
        presenter.addFavoriteRow(0)
        advanceUntilIdle()

        inOrder(view) {
            verify(view).onShowProgress()
            verify(view).onDataLoaded(
                check {
                    Assert.assertEquals(it.size, 16)
                }
            )
            verify(view).onHideProgress()
            verifyNoMoreInteractions()
        }
    }

    @Test
    fun `should not notify if an error happens while checking if has favorite works`() = runTest {
        whenever(resource.getStringResource(any()))
            .thenReturn(EMPTY_STRING)
        whenever(getWorkBrowseDetailsUseCase())
            .thenReturn(Triple(false, MOVIE_GENRE_DOMAIN_MODELS, TV_SHOW_GENRE_DOMAIN_MODELS))
        whenever(hasFavoriteUseCase()).thenThrow(RuntimeException())

        presenter.loadData()
        advanceUntilIdle()
        presenter.addFavoriteRow(0)
        advanceUntilIdle()

        inOrder(view) {
            val captor = argumentCaptor<List<Row>>()

            verify(view).onShowProgress()
            verify(view).onDataLoaded(captor.capture())
            verify(view).onHideProgress()

            verifyNoMoreInteractions()
        }
    }

    @Test
    fun `should refresh the rows when the selected position was updated`() = runTest {
        whenever(resource.getStringResource(any()))
            .thenReturn(EMPTY_STRING)
        whenever(getWorkBrowseDetailsUseCase())
            .thenReturn(Triple(true, MOVIE_GENRE_DOMAIN_MODELS, TV_SHOW_GENRE_DOMAIN_MODELS))
        whenever(hasFavoriteUseCase()).thenReturn(false)

        presenter.loadData()
        advanceUntilIdle()
        presenter.addFavoriteRow(0)
        advanceUntilIdle()
        presenter.refreshRows()

        inOrder(view) {
            val captor = argumentCaptor<List<Row>>()

            verify(view).onShowProgress()
            verify(view).onDataLoaded(captor.capture())
            verify(view).onHideProgress()
            verify(view).onUpdateSelectedPosition(3)
            verify(view).onDataLoaded(captor.capture())
            verifyNoMoreInteractions()
        }
    }

    @Test
    fun `should not refresh the rows when the selected position was not updated`() = runTest {
        whenever(resource.getStringResource(any()))
            .thenReturn(EMPTY_STRING)
        whenever(getWorkBrowseDetailsUseCase())
            .thenReturn(Triple(false, MOVIE_GENRE_DOMAIN_MODELS, TV_SHOW_GENRE_DOMAIN_MODELS))
        whenever(hasFavoriteUseCase()).thenReturn(false)

        presenter.loadData()
        advanceUntilIdle()
        presenter.addFavoriteRow(0)
        advanceUntilIdle()
        presenter.refreshRows()

        inOrder(view) {
            verify(view).onShowProgress()
            verify(view).onDataLoaded(
                check {
                    Assert.assertEquals(it.size, 16)
                }
            )
            verify(view).onHideProgress()
            verifyNoMoreInteractions()
        }
    }

    @Test
    fun `should open the search view when click in the search icon`() {
        val intent: Intent = mock()

        whenever(searchRoute.buildSearchIntent()).thenReturn(intent)

        presenter.searchClicked()

        verify(view, only()).openSearch(intent)
    }
}
