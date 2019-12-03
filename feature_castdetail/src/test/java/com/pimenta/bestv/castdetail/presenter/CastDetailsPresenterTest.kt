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

package com.pimenta.bestv.castdetail.presenter

import androidx.leanback.widget.Presenter
import com.nhaarman.mockitokotlin2.*
import com.pimenta.bestv.castdetail.domain.GetCastDetailsUseCase
import com.pimenta.bestv.castdetail.presentation.presenter.CastDetailsPresenter
import com.pimenta.bestv.model.domain.CastDomainModel
import com.pimenta.bestv.model.presentation.model.CastViewModel
import com.pimenta.bestv.model.presentation.model.WorkType
import com.pimenta.bestv.model.presentation.model.WorkViewModel
import com.pimenta.bestv.presentation.scheduler.RxScheduler
import com.pimenta.bestv.route.Route
import com.pimenta.bestv.route.workdetail.WorkDetailsRoute
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import org.junit.Test

/**
 * Created by marcus on 23-05-2018.
 */
private val CAST_VIEW_MODEL = CastViewModel(
        id = 1
)
private val CAST_DETAILED_DOMAIN_MODEL = CastDomainModel(
        id = 1,
        name = "Carlos",
        character = "Batman",
        birthday = "1990-07-13"
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

class CastDetailsPresenterTest {

    private val view: CastDetailsPresenter.View = mock()
    private val getCastDetailsUseCase: GetCastDetailsUseCase = mock()
    private val workDetailsRoute: WorkDetailsRoute = mock()
    private val rxSchedulerTest = RxScheduler(
            Schedulers.trampoline(),
            Schedulers.trampoline()
    )

    private val presenter = CastDetailsPresenter(
            view,
            getCastDetailsUseCase,
            workDetailsRoute,
            rxSchedulerTest
    )

    @Test
    fun `should load the cast details`() {
        val result = Triple(CAST_DETAILED_DOMAIN_MODEL, null, null)

        whenever(getCastDetailsUseCase(CAST_VIEW_MODEL.id)).thenReturn(Single.just(result))

        presenter.loadCastDetails(CAST_VIEW_MODEL)

        inOrder(view) {
            verify(view).onShowProgress()
            verify(view).onCastLoaded(CAST_DETAILED_VIEW_MODEL, null, null)
            verify(view).onHideProgress()
            verifyNoMoreInteractions()
        }
    }

    @Test
    fun `should load nothing if an error happens`() {
        whenever(getCastDetailsUseCase(CAST_VIEW_MODEL.id)).thenReturn(Single.error(Throwable()))

        presenter.loadCastDetails(CAST_VIEW_MODEL)

        inOrder(view) {
            verify(view).onShowProgress()
            verify(view).onErrorCastDetailsLoaded()
            verify(view).onHideProgress()
            verifyNoMoreInteractions()
        }
    }

    @Test
    fun `should open work details when a work is clicked`() {
        val route = mock<Route>()
        val itemViewHolder = mock<Presenter.ViewHolder>()

        whenever(workDetailsRoute.buildWorkDetailRoute(MOVIE_VIEW_MODEL)).thenReturn(route)

        presenter.workClicked(itemViewHolder, MOVIE_VIEW_MODEL)

        verify(view, only()).openWorkDetails(itemViewHolder, route)
    }
}