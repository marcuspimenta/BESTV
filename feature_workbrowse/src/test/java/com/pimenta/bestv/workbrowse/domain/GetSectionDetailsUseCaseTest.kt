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

import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import com.pimenta.bestv.model.domain.PageDomainModel
import com.pimenta.bestv.model.domain.WorkDomainModel
import com.pimenta.bestv.workbrowse.domain.model.GenreDomainModel
import com.pimenta.bestv.workbrowse.presentation.model.Source
import com.pimenta.bestv.workbrowse.presentation.model.TopWorkTypeViewModel
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test

private val WORK_DOMAIN_MODEL = WorkDomainModel(
    id = 1,
    title = "Batman",
    originalTitle = "Batman",
    originalLanguage = "en",
    overview = "A superhero movie",
    source = "tmdb",
    backdropPath = "/backdrop.jpg",
    posterPath = "/poster.jpg",
    releaseDate = "2023-01-01",
    type = WorkDomainModel.Type.MOVIE,
    voteAverage = 8.0f
)

private val MOVIE_PAGE = PageDomainModel(
    page = 1,
    totalPages = 5,
    results = listOf(WORK_DOMAIN_MODEL)
)

private val TV_SHOW_PAGE = PageDomainModel(
    page = 1,
    totalPages = 3,
    results = listOf(WORK_DOMAIN_MODEL.copy(id = 2, type = WorkDomainModel.Type.TV_SHOW))
)

private val FAVORITES_PAGE = PageDomainModel(
    page = 1,
    totalPages = 1,
    results = listOf(WORK_DOMAIN_MODEL.copy(id = 3, isFavorite = true))
)

private val MOVIE_GENRE = GenreDomainModel(id = 28, name = "Action", source = GenreDomainModel.Source.MOVIE)
private val TV_SHOW_GENRE = GenreDomainModel(id = 10759, name = "Action & Adventure", source = GenreDomainModel.Source.TV_SHOW)

class GetSectionDetailsUseCaseTest {

    private val getMovieGenresUseCase: GetMovieGenresUseCase = mock()
    private val getTvShowGenresUseCase: GetTvShowGenresUseCase = mock()
    private val getWorkByGenreUseCase: GetWorkByGenreUseCase = mock()
    private val loadWorkByTypeUseCase: LoadWorkByTypeUseCase = mock()

    private val useCase = GetSectionDetailsUseCase(
        getMovieGenresUseCase,
        getTvShowGenresUseCase,
        getWorkByGenreUseCase,
        loadWorkByTypeUseCase
    )

    @Test
    fun `getAllSections should return movie, tv show and favorites sections`() = runTest {
        // Setup movie types
        whenever(loadWorkByTypeUseCase(eq(1), any<TopWorkTypeViewModel>())).thenReturn(MOVIE_PAGE)

        // Setup genres
        whenever(getMovieGenresUseCase()).thenReturn(listOf(MOVIE_GENRE))
        whenever(getTvShowGenresUseCase()).thenReturn(listOf(TV_SHOW_GENRE))
        whenever(getWorkByGenreUseCase(eq(MOVIE_GENRE.id), eq(Source.MOVIE), eq(1))).thenReturn(MOVIE_PAGE)
        whenever(getWorkByGenreUseCase(eq(TV_SHOW_GENRE.id), eq(Source.TV_SHOW), eq(1))).thenReturn(TV_SHOW_PAGE)

        val result = useCase.getAllSections()

        assertTrue(result.movieSectionDetails.isNotEmpty())
        assertTrue(result.tvSectionDetails.isNotEmpty())
    }

    @Test
    fun `getAllSections should return empty lists when no data available`() = runTest {
        val emptyPage = PageDomainModel<WorkDomainModel>(page = 1, totalPages = 1, results = emptyList())

        whenever(loadWorkByTypeUseCase(eq(1), any<TopWorkTypeViewModel>())).thenReturn(emptyPage)
        whenever(getMovieGenresUseCase()).thenReturn(emptyList())
        whenever(getTvShowGenresUseCase()).thenReturn(emptyList())

        val result = useCase.getAllSections()

        assertTrue(result.movieSectionDetails.isEmpty())
        assertTrue(result.tvSectionDetails.isEmpty())
    }

    @Test
    fun `getFavoriteSections should return favorites when available`() = runTest {
        whenever(loadWorkByTypeUseCase(eq(1), eq(TopWorkTypeViewModel.FAVORITES_MOVIES))).thenReturn(FAVORITES_PAGE)

        val result = useCase.getFavoriteSections()

        assertEquals(1, result.size)
    }

    @Test
    fun `getFavoriteSections should return empty list when no favorites`() = runTest {
        val emptyPage = PageDomainModel<WorkDomainModel>(page = 1, totalPages = 1, results = emptyList())
        whenever(loadWorkByTypeUseCase(eq(1), eq(TopWorkTypeViewModel.FAVORITES_MOVIES))).thenReturn(emptyPage)

        val result = useCase.getFavoriteSections()

        assertTrue(result.isEmpty())
    }

    @Test
    fun `getAllSections should throw exception when loading fails`() = runTest {
        whenever(loadWorkByTypeUseCase(eq(1), any<TopWorkTypeViewModel>())).thenThrow(RuntimeException("Network error"))

        try {
            useCase.getAllSections()
            fail("Expected RuntimeException")
        } catch (e: RuntimeException) {
            assertEquals("Network error", e.message)
        }
    }
}
