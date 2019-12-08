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

package com.pimenta.bestv.workbrowse.domain

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import com.pimenta.bestv.model.domain.WorkDomainModel
import com.pimenta.bestv.model.domain.WorkPageDomainModel
import com.pimenta.bestv.workbrowse.data.repository.MovieRepository
import io.reactivex.Single
import org.junit.Test

/**
 * Created by marcus on 2019-10-21.
 */
private const val GENRE_ID = 1
private const val PAGE = 1
private val MOVIE_PAGE_DOMAIN_MODEL = WorkPageDomainModel(
        page = 1,
        totalPages = 1,
        works = listOf(
                WorkDomainModel(
                        id = 1,
                        title = "Batman",
                        originalTitle = "Batman",
                        type = WorkDomainModel.Type.MOVIE
                )
        )
)

class GetMovieByGenreUseCaseTest {

    private val movieRepository: MovieRepository = mock()

    private val useCase = GetMovieByGenreUseCase(
            movieRepository
    )

    @Test
    fun `should return the right data when loading the movies by genre`() {
        whenever(movieRepository.getMoviesByGenre(GENRE_ID, PAGE))
                .thenReturn(Single.just(MOVIE_PAGE_DOMAIN_MODEL))

        useCase(GENRE_ID, PAGE)
                .test()
                .assertComplete()
                .assertResult(MOVIE_PAGE_DOMAIN_MODEL)
    }

    @Test
    fun `should return an error when some exception happens`() {
        whenever(movieRepository.getMoviesByGenre(GENRE_ID, PAGE))
                .thenReturn(Single.error(Throwable()))

        useCase(GENRE_ID, PAGE)
                .test()
                .assertError(Throwable::class.java)
    }
}