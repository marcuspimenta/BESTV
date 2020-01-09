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

import androidx.leanback.widget.Row
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.check
import com.nhaarman.mockitokotlin2.inOrder
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.only
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.pimenta.bestv.presentation.platform.Resource
import com.pimenta.bestv.presentation.scheduler.RxScheduler
import com.pimenta.bestv.route.Route
import com.pimenta.bestv.route.search.SearchRoute
import com.pimenta.bestv.workbrowse.domain.GetWorkBrowseDetailsUseCase
import com.pimenta.bestv.workbrowse.domain.HasFavoriteUseCase
import com.pimenta.bestv.workbrowse.domain.model.GenreDomainModel
import com.pimenta.bestv.workbrowse.presentation.presenter.WorkBrowsePresenter
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import org.junit.Assert
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

class WorkBrowsePresenterTest {

    private val view: WorkBrowsePresenter.View = mock()
    private val hasFavoriteUseCase: HasFavoriteUseCase = mock()
    private val getWorkBrowseDetailsUseCase: GetWorkBrowseDetailsUseCase = mock()
    private val searchRoute: SearchRoute = mock()
    private val resource: Resource = mock()
    private val rxScheduler: RxScheduler = RxScheduler(
            Schedulers.trampoline(),
            Schedulers.trampoline()
    )

    private val presenter = WorkBrowsePresenter(
            view,
            hasFavoriteUseCase,
            getWorkBrowseDetailsUseCase,
            searchRoute,
            resource,
            rxScheduler
    )

    @Test
    fun `should load the right data when loading the browse details and there is favorite works`() {
        whenever(resource.getStringResource(any()))
                .thenReturn(EMPTY_STRING)
        whenever(getWorkBrowseDetailsUseCase())
                .thenReturn(Single.just(Triple(true, MOVIE_GENRE_DOMAIN_MODELS, TV_SHOW_GENRE_DOMAIN_MODELS)))

        presenter.loadData()

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
    fun `should load the right data when loading the browse details and there is not any favorite work`() {
        whenever(resource.getStringResource(any()))
                .thenReturn(EMPTY_STRING)
        whenever(getWorkBrowseDetailsUseCase())
                .thenReturn(Single.just(Triple(false, MOVIE_GENRE_DOMAIN_MODELS, TV_SHOW_GENRE_DOMAIN_MODELS)))

        presenter.loadData()

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
    fun `should return an error when loading the browse details if an exception happens`() {
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
    fun `should not add the favorite row when it already was added`() {
        whenever(resource.getStringResource(any()))
                .thenReturn(EMPTY_STRING)
        whenever(getWorkBrowseDetailsUseCase())
                .thenReturn(Single.just(Triple(true, MOVIE_GENRE_DOMAIN_MODELS, TV_SHOW_GENRE_DOMAIN_MODELS)))
        whenever(hasFavoriteUseCase()).thenReturn(Single.just(true))

        presenter.loadData()
        presenter.addFavoriteRow(0)

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
    fun `should notify to update the selected position 3 when the favorite row does not exist anymore`() {
        whenever(resource.getStringResource(any()))
                .thenReturn(EMPTY_STRING)
        whenever(getWorkBrowseDetailsUseCase())
                .thenReturn(Single.just(Triple(true, MOVIE_GENRE_DOMAIN_MODELS, TV_SHOW_GENRE_DOMAIN_MODELS)))
        whenever(hasFavoriteUseCase()).thenReturn(Single.just(false))

        presenter.loadData()
        presenter.addFavoriteRow(0)

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
    fun `should add the favorite row when it already was not added`() {
        whenever(resource.getStringResource(any()))
                .thenReturn(EMPTY_STRING)
        whenever(getWorkBrowseDetailsUseCase())
                .thenReturn(Single.just(Triple(false, MOVIE_GENRE_DOMAIN_MODELS, TV_SHOW_GENRE_DOMAIN_MODELS)))
        whenever(hasFavoriteUseCase()).thenReturn(Single.just(true))

        presenter.loadData()
        presenter.addFavoriteRow(0)

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
    fun `should not notify to update the selected position when the favorite row does not exist`() {
        whenever(resource.getStringResource(any()))
                .thenReturn(EMPTY_STRING)
        whenever(getWorkBrowseDetailsUseCase())
                .thenReturn(Single.just(Triple(false, MOVIE_GENRE_DOMAIN_MODELS, TV_SHOW_GENRE_DOMAIN_MODELS)))
        whenever(hasFavoriteUseCase()).thenReturn(Single.just(false))

        presenter.loadData()
        presenter.addFavoriteRow(0)

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
    fun `should not notify if an error happens while checking if has favorite works`() {
        whenever(resource.getStringResource(any()))
                .thenReturn(EMPTY_STRING)
        whenever(getWorkBrowseDetailsUseCase())
                .thenReturn(Single.just(Triple(false, MOVIE_GENRE_DOMAIN_MODELS, TV_SHOW_GENRE_DOMAIN_MODELS)))
        whenever(hasFavoriteUseCase()).thenReturn(Single.error(Throwable()))

        presenter.loadData()
        presenter.addFavoriteRow(0)

        inOrder(view) {
            val captor = argumentCaptor<List<Row>>()

            verify(view).onShowProgress()
            verify(view).onDataLoaded(captor.capture())
            verify(view).onHideProgress()

            verifyNoMoreInteractions()
        }
    }

    @Test
    fun `should refresh the rows when the selected position was updated`() {
        whenever(resource.getStringResource(any()))
                .thenReturn(EMPTY_STRING)
        whenever(getWorkBrowseDetailsUseCase())
                .thenReturn(Single.just(Triple(true, MOVIE_GENRE_DOMAIN_MODELS, TV_SHOW_GENRE_DOMAIN_MODELS)))
        whenever(hasFavoriteUseCase()).thenReturn(Single.just(false))

        presenter.loadData()
        presenter.addFavoriteRow(0)
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
    fun `should not refresh the rows when the selected position was not updated`() {
        whenever(resource.getStringResource(any()))
                .thenReturn(EMPTY_STRING)
        whenever(getWorkBrowseDetailsUseCase())
                .thenReturn(Single.just(Triple(false, MOVIE_GENRE_DOMAIN_MODELS, TV_SHOW_GENRE_DOMAIN_MODELS)))
        whenever(hasFavoriteUseCase()).thenReturn(Single.just(false))

        presenter.loadData()
        presenter.addFavoriteRow(0)
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
        val route: Route = mock()

        whenever(searchRoute.buildSearchRoute()).thenReturn(route)

        presenter.searchClicked()

        verify(view, only()).openSearch(route)
    }
}
