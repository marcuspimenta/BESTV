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
import org.mockito.kotlin.whenever
import com.pimenta.bestv.workdetail.data.repository.MovieRepository
import com.pimenta.bestv.workdetail.domain.model.WatchProviderDomainModel
import com.pimenta.bestv.workdetail.domain.model.WatchProvidersDomainModel
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import kotlin.test.assertFailsWith

private const val MOVIE_ID = 1
private const val COUNTRY_CODE = "US"
private val WATCH_PROVIDERS = WatchProvidersDomainModel(
    tmdbLink = "https://www.themoviedb.org/movie/1/watch",
    streaming = listOf(
        WatchProviderDomainModel(
            id = 1,
            name = "Netflix",
            logoPath = "/logo.jpg",
            displayPriority = 1
        )
    ),
    rent = emptyList(),
    buy = emptyList()
)

class GetWatchProvidersByMovieUseCaseTest {

    private val movieRepository: MovieRepository = mock()

    private val useCase = GetWatchProvidersByMovieUseCase(
        movieRepository
    )

    @Test
    fun `should return the right data when loading the watch providers`() = runTest {
        whenever(movieRepository.getWatchProvidersByMovie(MOVIE_ID, COUNTRY_CODE))
            .thenReturn(WATCH_PROVIDERS)

        val result = useCase(MOVIE_ID, COUNTRY_CODE)

        assertEquals(WATCH_PROVIDERS, result)
    }

    @Test
    fun `should return an error when some exception happens`() = runTest {
        val exception = RuntimeException("Test exception")
        whenever(movieRepository.getWatchProvidersByMovie(MOVIE_ID, COUNTRY_CODE))
            .thenThrow(exception)

        assertFailsWith<RuntimeException> {
            useCase(MOVIE_ID, COUNTRY_CODE)
        }
    }
}
