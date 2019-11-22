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

package com.pimenta.bestv.feature.main.domain

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import com.pimenta.bestv.model.presentation.model.WorkType
import com.pimenta.bestv.model.presentation.model.WorkViewModel
import io.reactivex.Single
import org.junit.Test

/**
 * Created by marcus on 2019-08-26.
 */
private val WORK_VIEW_MODEL = WorkViewModel(
        id = 1,
        title = "Batman",
        originalTitle = "Batman",
        type = WorkType.MOVIE
)

class HasFavoriteUseCaseTest {

    private val getFavoriteMoviesUseCase: GetFavoriteMoviesUseCase = mock()
    private val getFavoriteTvShowsUseCase: GetFavoriteTvShowsUseCase = mock()

    private val useCase = HasFavoriteUseCase(
            getFavoriteMoviesUseCase,
            getFavoriteTvShowsUseCase
    )

    @Test
    fun `should return true if there is at least one favorite work`() {
        whenever(getFavoriteMoviesUseCase()).thenReturn(Single.just(listOf(WORK_VIEW_MODEL)))
        whenever(getFavoriteTvShowsUseCase()).thenReturn(Single.just(emptyList()))

        useCase()
                .test()
                .assertComplete()
                .assertResult(true)
    }

    @Test
    fun `should return false if there is no favorite work`() {
        whenever(getFavoriteMoviesUseCase()).thenReturn(Single.just(emptyList()))
        whenever(getFavoriteTvShowsUseCase()).thenReturn(Single.just(emptyList()))

        useCase()
                .test()
                .assertComplete()
                .assertResult(false)
    }

    @Test
    fun `should return an error when some exception happens`() {
        whenever(getFavoriteMoviesUseCase()).thenReturn(Single.just(listOf(WORK_VIEW_MODEL)))
        whenever(getFavoriteTvShowsUseCase()).thenReturn(Single.error(Throwable()))

        useCase()
                .test()
                .assertError(Throwable::class.java)
    }
}