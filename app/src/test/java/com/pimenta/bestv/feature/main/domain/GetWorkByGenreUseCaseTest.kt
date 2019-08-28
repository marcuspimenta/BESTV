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
import com.pimenta.bestv.common.presentation.model.*
import com.pimenta.bestv.data.MediaRepository
import com.pimenta.bestv.data.remote.entity.MoviePageResponse
import com.pimenta.bestv.data.remote.entity.MovieResponse
import com.pimenta.bestv.data.remote.entity.TvShowPageResponse
import com.pimenta.bestv.data.remote.entity.TvShowResponse
import io.reactivex.Single
import org.junit.Test

/**
 * Created by marcus on 2019-08-26.
 */
private val MOVIE_GENRE = GenreViewModel(
        id = 1,
        source = Source.MOVIE
)
private val WORK_PAGE = MoviePageResponse().apply {
    page = 1
    totalPages = 1
    works = listOf(
            MovieResponse(
                    id = 1,
                    title = "Batman",
                    originalTitle = "Batman"
            )
    )
}
private val MOVIE_PAGE_VIEW_MODEL = WorkPageViewModel(
        page = 1,
        totalPages = 1,
        works = listOf(
                WorkViewModel(
                        id = 1,
                        title = "Batman",
                        originalTitle = "Batman",
                        type = WorkType.MOVIE
                )
        )
)
private val TV_SHOW_GENRE = GenreViewModel(
        id = 1,
        source = Source.TV_SHOW
)
private val TV_SHOW_PAGE = TvShowPageResponse().apply {
    page = 1
    totalPages = 1
    works = listOf(
            TvShowResponse(
                    id = 1,
                    title = "Batman",
                    originalTitle = "Batman"
            )
    )
}
private val TV_SHOW_PAGE_VIEW_MODEL = WorkPageViewModel(
        page = 1,
        totalPages = 1,
        works = listOf(
                WorkViewModel(
                        id = 1,
                        title = "Batman",
                        originalTitle = "Batman",
                        type = WorkType.TV_SHOW
                )
        )
)

class GetWorkByGenreUseCaseTest {

    private val mediaRepository: MediaRepository = mock()
    private val useCase = GetWorkByGenreUseCase(
            mediaRepository
    )

    @Test
    fun `should return the right data when loading a movie page`() {
        whenever(mediaRepository.getMovieByGenre(MOVIE_GENRE.id, 1)).thenReturn(Single.just(WORK_PAGE))

        useCase(MOVIE_GENRE, 1)
                .test()
                .assertComplete()
                .assertResult(MOVIE_PAGE_VIEW_MODEL)
    }

    @Test
    fun `should return an error when some exception happens when loading a movie page`() {
        whenever(mediaRepository.getMovieByGenre(MOVIE_GENRE.id, 1)).thenReturn(Single.error(Throwable()))

        useCase(MOVIE_GENRE, 1)
                .test()
                .assertError(Throwable::class.java)
    }

    @Test
    fun `should return the right data when loading a tv show page`() {
        whenever(mediaRepository.getTvShowByGenre(TV_SHOW_GENRE.id, 1)).thenReturn(Single.just(TV_SHOW_PAGE))

        useCase(TV_SHOW_GENRE, 1)
                .test()
                .assertComplete()
                .assertResult(TV_SHOW_PAGE_VIEW_MODEL)
    }

    @Test
    fun `should return an error when some exception happens when loading a tv show page`() {
        whenever(mediaRepository.getTvShowByGenre(TV_SHOW_GENRE.id, 1)).thenReturn(Single.error(Throwable()))

        useCase(TV_SHOW_GENRE, 1)
                .test()
                .assertError(Throwable::class.java)
    }

}