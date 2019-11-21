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

package com.pimenta.bestv.feature.workdetail.domain

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import io.reactivex.Single
import org.junit.Test

/**
 * Created by marcus on 23-10-2018.
 */
private const val MOVIE_ID = 1

class IsFavoriteMovieUseCaseTest {

    private val mediaRepository: MediaRepository = mock()

    private val useCase = IsFavoriteMovieUseCase(
            mediaRepository
    )

    @Test
    fun `should return true if a movie is favorite`() {
        whenever(mediaRepository.isFavoriteMovie(MOVIE_ID)).thenReturn(Single.just(true))

        useCase(MOVIE_ID)
                .test()
                .assertComplete()
                .assertResult(true)
    }

    @Test
    fun `should return false if a movie is not favorite`() {
        whenever(mediaRepository.isFavoriteMovie(MOVIE_ID)).thenReturn(Single.just(false))

        useCase(MOVIE_ID)
                .test()
                .assertComplete()
                .assertResult(false)
    }

    @Test
    fun `should return an error when some exception happens when checking if a movie is favorite`() {
        whenever(mediaRepository.isFavoriteMovie(MOVIE_ID)).thenReturn(Single.error(Throwable()))

        useCase(MOVIE_ID)
                .test()
                .assertError(Throwable::class.java)
    }
}