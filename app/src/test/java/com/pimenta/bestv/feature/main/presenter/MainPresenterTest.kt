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

package com.pimenta.bestv.feature.main.presenter

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.pimenta.bestv.common.usecase.LoadRecommendationUseCase
import io.reactivex.Completable
import org.junit.Test

/**
 * Created by marcus on 22-05-2018.
 */
class MainPresenterTest {

    val loadRecommendationUseCase: LoadRecommendationUseCase = mock()

    private val presenter = MainPresenter(loadRecommendationUseCase)

    @Test
    fun `should do nothing when load the recommendations`() {
        whenever(loadRecommendationUseCase()).thenReturn(Completable.complete())

        presenter.loadRecommendations()

        verify(loadRecommendationUseCase).invoke()
    }

    @Test
    fun `should do nothing when load the recommendations and an error happens`() {
        whenever(loadRecommendationUseCase()).thenReturn(Completable.error(Throwable()))

        presenter.loadRecommendations()

        verify(loadRecommendationUseCase).invoke()
    }
}
