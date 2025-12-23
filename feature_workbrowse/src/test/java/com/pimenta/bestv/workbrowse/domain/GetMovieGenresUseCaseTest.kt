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

import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import com.pimenta.bestv.workbrowse.data.repository.GenreRepository
import com.pimenta.bestv.workbrowse.domain.model.GenreDomainModel
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Test

/**
 * Created by marcus on 2019-08-26.
 */
private val MOVIE_GENRES_DOMAIN_MODEL = listOf(
    GenreDomainModel(
        id = 2,
        name = "Action",
        source = GenreDomainModel.Source.MOVIE
    )
)

class GetMovieGenresUseCaseTest {

    private val genreRepository: GenreRepository = mock()
    private val useCase = GetMovieGenresUseCase(
        genreRepository
    )

    @Test
    fun `should return the right data when loading the movie genres`() = runTest {
        whenever(genreRepository.getMovieGenres())
            .thenReturn(MOVIE_GENRES_DOMAIN_MODEL)

        val result = useCase()

        Assert.assertEquals(MOVIE_GENRES_DOMAIN_MODEL, result)
    }

    @Test
    fun `should return an error when some exception happens`() = runTest {
        whenever(genreRepository.getMovieGenres())
            .thenThrow(RuntimeException())

        try {
            useCase()
            Assert.fail("Expected exception")
        } catch (e: RuntimeException) {
            // Expected
        }
    }
}
