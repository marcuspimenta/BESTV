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
import com.pimenta.bestv.workbrowse.presentation.model.GenreViewModel
import com.pimenta.bestv.workbrowse.presentation.model.Source
import com.pimenta.bestv.data.remote.model.remote.MovieGenreListResponse
import com.pimenta.bestv.data.remote.model.remote.MovieGenreResponse
import io.reactivex.Single
import org.junit.Test

/**
 * Created by marcus on 2019-08-26.
 */
private val MOVIE_GENRES = MovieGenreListResponse().apply {
    genres = listOf(
            MovieGenreResponse(
                    id = 2,
                    name = "Action"
            )
    )
}
private val MOVIE_GENRES_VIEW_MODEL = listOf(
        GenreViewModel(
                id = 2,
                name = "Action",
                source = Source.MOVIE
        )
)

class GetMovieGenresUseCaseTest {

    private val mediaRepository: MediaRepository = mock()
    private val useCase = com.pimenta.bestv.workbrowse.domain.GetMovieGenresUseCase(
            mediaRepository
    )

    @Test
    fun `should return the right data when loading the movie genres`() {
        whenever(mediaRepository.getMovieGenres()).thenReturn(Single.just(MOVIE_GENRES))

        useCase()
                .test()
                .assertComplete()
                .assertResult(MOVIE_GENRES_VIEW_MODEL)
    }

    @Test
    fun `should return an error when some exception happens`() {
        whenever(mediaRepository.getMovieGenres()).thenReturn(Single.error(Throwable()))

        useCase()
                .test()
                .assertError(Throwable::class.java)
    }
}