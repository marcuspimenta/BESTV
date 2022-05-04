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

import android.content.Intent
import androidx.leanback.widget.Presenter
import com.nhaarman.mockitokotlin2.inOrder
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.only
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.pimenta.bestv.castdetail.domain.GetCastDetailsUseCase
import com.pimenta.bestv.castdetail.presentation.presenter.CastDetailsPresenter
import com.pimenta.bestv.model.domain.CastDomainModel
import com.pimenta.bestv.model.presentation.model.CastViewModel
import com.pimenta.bestv.model.presentation.model.WorkType
import com.pimenta.bestv.model.presentation.model.WorkViewModel
import com.pimenta.bestv.presentation.dispatcher.CoroutineDispatchers
import com.pimenta.bestv.route.workdetail.WorkDetailsRoute
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
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
    private val coroutineDispatchers = CoroutineDispatchers(
        UnconfinedTestDispatcher(),
        UnconfinedTestDispatcher()
    )

    private val presenter = CastDetailsPresenter(
        view,
        getCastDetailsUseCase,
        workDetailsRoute,
        coroutineDispatchers
    )

    @Test
    fun `should load the cast details`() = runTest {
        val result = Triple(CAST_DETAILED_DOMAIN_MODEL, null, null)

        whenever(getCastDetailsUseCase(CAST_VIEW_MODEL.id)).thenReturn(result)

        presenter.loadCastDetails(CAST_VIEW_MODEL)

        inOrder(view) {
            verify(view).onShowProgress()
            verify(view).onCastLoaded(CAST_DETAILED_VIEW_MODEL, null, null)
            verify(view).onHideProgress()
            verifyNoMoreInteractions()
        }
    }

    @Test
    fun `should load nothing if an error happens`() = runTest {
        whenever(getCastDetailsUseCase(CAST_VIEW_MODEL.id)).thenThrow(RuntimeException())

        presenter.loadCastDetails(CAST_VIEW_MODEL)

        inOrder(view) {
            verify(view).onShowProgress()
            verify(view).onErrorCastDetailsLoaded()
            verify(view).onHideProgress()
            verifyNoMoreInteractions()
        }
    }

    @Test
    fun `should open work details when a work is clicked`() = runTest {
        val intent = mock<Intent>()
        val itemViewHolder = mock<Presenter.ViewHolder>()

        whenever(workDetailsRoute.buildWorkDetailIntent(MOVIE_VIEW_MODEL)).thenReturn(intent)

        presenter.workClicked(itemViewHolder, MOVIE_VIEW_MODEL)

        verify(view, only()).openWorkDetails(itemViewHolder, intent)
    }
}
