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

package com.pimenta.bestv.workdetail.domain

import org.mockito.kotlin.mock
import org.mockito.kotlin.only
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import com.pimenta.bestv.model.presentation.mapper.toMovieDbModel
import com.pimenta.bestv.model.presentation.mapper.toTvShowDbModel
import com.pimenta.bestv.model.presentation.model.WorkType
import com.pimenta.bestv.model.presentation.model.WorkViewModel
import com.pimenta.bestv.workdetail.data.repository.MovieRepository
import com.pimenta.bestv.workdetail.data.repository.TvShowRepository
import kotlinx.coroutines.test.runTest
import org.junit.Test

/**
 * Created by marcus on 2019-08-28.
 */
private val MOVIE_VIEW_MODEL = WorkViewModel(
    id = 1,
    title = "Batman",
    originalTitle = "Batman",
    originalLanguage = "en",
    overview = "A superhero movie",
    source = "tmdb",
    backdropUrl = "https://image.tmdb.org/t/p/original/backdrop.jpg",
    posterUrl = "https://image.tmdb.org/t/p/original/poster.jpg",
    releaseDate = "Jan 01, 2023",
    type = WorkType.MOVIE,
    voteAverage = 8.0f
)
private val TV_SHOW_VIEW_MODEL = WorkViewModel(
    id = 1,
    title = "Batman",
    originalTitle = "Batman",
    originalLanguage = "en",
    overview = "A superhero TV show",
    source = "tmdb",
    backdropUrl = "https://image.tmdb.org/t/p/original/backdrop.jpg",
    posterUrl = "https://image.tmdb.org/t/p/original/poster.jpg",
    releaseDate = "Jan 01, 2023",
    type = WorkType.TV_SHOW,
    voteAverage = 8.0f
)

class SetFavoriteUseCaseTest {

    private val movieRepository: MovieRepository = mock()
    private val tvShowRepository: TvShowRepository = mock()

    private val useCase = SetFavoriteUseCase(
        movieRepository,
        tvShowRepository
    )

    @Test
    fun `should save a movie as favorite is it is not favorite`() = runTest {
        val dbModel = MOVIE_VIEW_MODEL.toMovieDbModel()

        whenever(movieRepository.saveFavoriteMovie(dbModel))
            .thenReturn(Unit)

        useCase(MOVIE_VIEW_MODEL)

        verify(movieRepository, only()).saveFavoriteMovie(dbModel)
    }

    @Test
    fun `should delete a movie as favorite is it is favorite`() = runTest {
        val movieViewModel = MOVIE_VIEW_MODEL.copy(isFavorite = true)
        val dbModel = movieViewModel.toMovieDbModel()

        whenever(movieRepository.deleteFavoriteMovie(dbModel))
            .thenReturn(Unit)

        useCase(movieViewModel)

        verify(movieRepository, only()).deleteFavoriteMovie(dbModel)
    }

    @Test
    fun `should save a tv show as favorite is it is not favorite`() = runTest {
        val dbModel = TV_SHOW_VIEW_MODEL.toTvShowDbModel()

        whenever(tvShowRepository.saveFavoriteTvShow(dbModel))
            .thenReturn(Unit)

        useCase(TV_SHOW_VIEW_MODEL)

        verify(tvShowRepository, only()).saveFavoriteTvShow(dbModel)
    }

    @Test
    fun `should delete a tv show as favorite is it is favorite`() = runTest {
        val tvShowViewModel = TV_SHOW_VIEW_MODEL.copy(isFavorite = true)
        val dbModel = tvShowViewModel.toTvShowDbModel()

        whenever(tvShowRepository.deleteFavoriteTvShow(dbModel))
            .thenReturn(Unit)

        useCase(tvShowViewModel)

        verify(tvShowRepository, only()).deleteFavoriteTvShow(dbModel)
    }
}
