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

package com.pimenta.bestv.feature.castdetail.presenter

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.pimenta.bestv.common.presentation.model.CastViewModel
import com.pimenta.bestv.feature.castdetail.usecase.GetCastDetailsUseCase
import com.pimenta.bestv.scheduler.RxSchedulerTest
import io.reactivex.Single
import org.junit.Test

/**
 * Created by marcus on 23-05-2018.
 */
class CastDetailsPresenterTest {

    private val view: CastDetailsPresenter.View = mock()
    private val getCastDetailsUseCase: GetCastDetailsUseCase = mock()
    private val rxSchedulerTest: RxSchedulerTest = RxSchedulerTest()

    private val presenter = CastDetailsPresenter(
            view,
            getCastDetailsUseCase,
            rxSchedulerTest
    )

    @Test
    fun `should load the cast details`() {
        val result = Triple(aCastDetailedViewModel, null, null)

        whenever(getCastDetailsUseCase(any())).thenReturn(Single.just(result))

        presenter.loadCastDetails(aCastViewModel)

        verify(view).onCastLoaded(result.first, result.second, result.third)
    }

    @Test
    fun `should load nothing if an error happens`() {
        whenever(getCastDetailsUseCase(any())).thenReturn(Single.error(Throwable()))

        presenter.loadCastDetails(aCastViewModel)

        verify(view).onErrorCastDetailsLoaded()
    }

    companion object {

        private val aCastViewModel = CastViewModel(
                id = 1
        )

        private val aCastDetailedViewModel = CastViewModel(
                id = 1,
                name = "Carlos",
                character = "Batman",
                birthday = "1990-07-13"
        )

    }
}