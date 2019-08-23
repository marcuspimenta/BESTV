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
import com.pimenta.bestv.common.presentation.model.GenreViewModel
import com.pimenta.bestv.common.presentation.model.Source
import com.pimenta.bestv.common.domain.WorkUseCase
import io.reactivex.Single
import org.junit.Test

/**
 * Created by marcus on 28-05-2019.
 */
class GetWorkBrowseDetailsUseCaseTest {

    private val workUseCase: WorkUseCase = mock()

    private val useCase = GetWorkBrowseDetailsUseCase(
            workUseCase
    )

    @Test
    fun `should return the right data when loading the browse details`() {
        whenever(workUseCase.hasFavorite()).thenReturn(Single.just(true))
        whenever(workUseCase.getMovieGenres()).thenReturn(Single.just(moveGenres))
        whenever(workUseCase.getTvShowGenres()).thenReturn(Single.just(tvShowGenres))

        useCase()
                .test()
                .assertComplete()
                .assertResult(Triple(true, moveGenres, tvShowGenres))
    }

    @Test
    fun `should return an error when some exception happens`() {
        whenever(workUseCase.hasFavorite()).thenReturn(Single.just(true))
        whenever(workUseCase.getMovieGenres()).thenReturn(Single.error(Throwable()))
        whenever(workUseCase.getTvShowGenres()).thenReturn(Single.just(tvShowGenres))

        useCase()
                .test()
                .assertError(Throwable::class.java)
    }

    companion object {

        private val moveGenres = listOf(
                GenreViewModel(
                        id = 1,
                        name = "Action",
                        source = Source.MOVIE
                )
        )

        private val tvShowGenres = listOf(
                GenreViewModel(
                        id = 2,
                        name = "Action",
                        source = Source.TV_SHOW
                )
        )
    }
}