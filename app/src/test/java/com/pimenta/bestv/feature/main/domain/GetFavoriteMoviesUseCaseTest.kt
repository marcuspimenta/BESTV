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
import com.pimenta.bestv.common.presentation.model.WorkType
import com.pimenta.bestv.common.presentation.model.WorkViewModel
import com.pimenta.bestv.data.MediaRepository
import com.pimenta.bestv.data.local.entity.MovieDbModel
import com.pimenta.bestv.data.remote.entity.MovieResponse
import io.reactivex.Single
import org.junit.Test

/**
 * Created by marcus on 15-10-2019.
 */
private val MOVIE_DB = MovieDbModel(
        id = 1
)
private val MOVIE_RESPONSE = MovieResponse(
        id = 1,
        title = "Batman",
        originalTitle = "Batman"
)
private val MOVIE_VIEW_MODEL = WorkViewModel(
        id = 1,
        title = "Batman",
        originalTitle = "Batman",
        isFavorite = true,
        type = WorkType.MOVIE
)

class GetFavoriteMoviesUseCaseTest {

    private val mediaRepository: MediaRepository = mock()

    private val useCase = GetFavoriteMoviesUseCase(
            mediaRepository
    )

    @Test
    fun `should return the right data when loading the favorites movies`() {
        whenever(mediaRepository.getFavoriteMovies()).thenReturn(Single.just(listOf(MOVIE_DB)))
        whenever(mediaRepository.getMovie(MOVIE_DB.id)).thenReturn(MOVIE_RESPONSE)

        useCase()
                .test()
                .assertComplete()
                .assertResult(listOf(MOVIE_VIEW_MODEL))
    }

    @Test
    fun `should return an error when some exception happens`() {
        whenever(mediaRepository.getFavoriteMovies()).thenReturn(Single.error(Throwable()))

        useCase()
                .test()
                .assertError(Throwable::class.java)
    }
}