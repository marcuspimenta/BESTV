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
import com.pimenta.bestv.model.domain.PageDomainModel
import com.pimenta.bestv.model.domain.WorkDomainModel
import com.pimenta.bestv.workbrowse.presentation.model.GenreViewModel
import com.pimenta.bestv.workbrowse.presentation.model.Source
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Test

/**
 * Created by marcus on 2019-08-26.
 */
private val MOVIE_GENRE = GenreViewModel(
    id = 1,
    source = Source.MOVIE
)
private val MOVIE_PAGE_DOMAIN_MODEL = PageDomainModel(
    page = 1,
    totalPages = 1,
    results = listOf(
        WorkDomainModel(
            id = 1,
            title = "Batman",
            originalTitle = "Batman",
            type = WorkDomainModel.Type.MOVIE
        )
    )
)
private val TV_SHOW_GENRE = GenreViewModel(
    id = 1,
    source = Source.TV_SHOW
)
private val TV_SHOW_PAGE_DOMAIN_MODEL = PageDomainModel(
    page = 1,
    totalPages = 1,
    results = listOf(
        WorkDomainModel(
            id = 1,
            title = "Batman",
            originalTitle = "Batman",
            type = WorkDomainModel.Type.TV_SHOW
        )
    )
)

class GetWorkByGenreUseCaseTest {

    private val getMovieByGenreUseCase: GetMovieByGenreUseCase = mock()
    private val getTvShowByGenreUseCase: GetTvShowByGenreUseCase = mock()
    private val useCase = GetWorkByGenreUseCase(
        getMovieByGenreUseCase,
        getTvShowByGenreUseCase
    )

    @Test
    fun `should return the right data when loading a movie page`() = runTest {
        whenever(getMovieByGenreUseCase(MOVIE_GENRE.id, 1))
            .thenReturn(MOVIE_PAGE_DOMAIN_MODEL)

        val result = useCase(MOVIE_GENRE.id, MOVIE_GENRE.source, 1)

        Assert.assertEquals(MOVIE_PAGE_DOMAIN_MODEL, result)
    }

    @Test
    fun `should return an error when some exception happens when loading a movie page`() = runTest {
        whenever(getMovieByGenreUseCase(MOVIE_GENRE.id, 1))
            .thenThrow(RuntimeException())

        try {
            useCase(MOVIE_GENRE.id, MOVIE_GENRE.source, 1)
            Assert.fail("Expected exception")
        } catch (e: RuntimeException) {
            // Expected
        }
    }

    @Test
    fun `should return the right data when loading a tv show page`() = runTest {
        whenever(getTvShowByGenreUseCase(TV_SHOW_GENRE.id, 1))
            .thenReturn(TV_SHOW_PAGE_DOMAIN_MODEL)

        val result = useCase(TV_SHOW_GENRE.id, TV_SHOW_GENRE.source, 1)

        Assert.assertEquals(TV_SHOW_PAGE_DOMAIN_MODEL, result)
    }

    @Test
    fun `should return an error when some exception happens when loading a tv show page`() = runTest {
        whenever(getTvShowByGenreUseCase(TV_SHOW_GENRE.id, 1))
            .thenThrow(RuntimeException())

        try {
            useCase(TV_SHOW_GENRE.id, TV_SHOW_GENRE.source, 1)
            Assert.fail("Expected exception")
        } catch (e: RuntimeException) {
            // Expected
        }
    }
}
