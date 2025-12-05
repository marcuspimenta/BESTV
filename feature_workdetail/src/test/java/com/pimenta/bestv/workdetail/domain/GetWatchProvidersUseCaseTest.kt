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

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import com.pimenta.bestv.model.presentation.model.WorkType
import com.pimenta.bestv.workdetail.domain.model.WatchProviderDomainModel
import com.pimenta.bestv.workdetail.domain.model.WatchProvidersDomainModel
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import kotlin.test.assertFailsWith

private const val WORK_ID = 1
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

class GetWatchProvidersUseCaseTest {

    private val getWatchProvidersByMovieUseCase: GetWatchProvidersByMovieUseCase = mock()
    private val getWatchProvidersByTvShowUseCase: GetWatchProvidersByTvShowUseCase = mock()

    private val useCase = GetWatchProvidersUseCase(
        getWatchProvidersByMovieUseCase,
        getWatchProvidersByTvShowUseCase
    )

    @Test
    fun `should return the right data when loading the watch providers by movie`() = runTest {
        whenever(getWatchProvidersByMovieUseCase(WORK_ID, COUNTRY_CODE))
            .thenReturn(WATCH_PROVIDERS)

        val result = useCase(WorkType.MOVIE, WORK_ID, COUNTRY_CODE)

        assertEquals(WATCH_PROVIDERS, result)
    }

    @Test
    fun `should return an error when loading the watch providers by movie and some exception happens`() = runTest {
        val exception = RuntimeException("Test exception")
        whenever(getWatchProvidersByMovieUseCase(WORK_ID, COUNTRY_CODE))
            .thenThrow(exception)

        assertFailsWith<RuntimeException> {
            useCase(WorkType.MOVIE, WORK_ID, COUNTRY_CODE)
        }
    }

    @Test
    fun `should return the right data when loading the watch providers by tv show`() = runTest {
        whenever(getWatchProvidersByTvShowUseCase(WORK_ID, COUNTRY_CODE))
            .thenReturn(WATCH_PROVIDERS)

        val result = useCase(WorkType.TV_SHOW, WORK_ID, COUNTRY_CODE)

        assertEquals(WATCH_PROVIDERS, result)
    }

    @Test
    fun `should return an error when loading the watch providers by tv show and some exception happens`() = runTest {
        val exception = RuntimeException("Test exception")
        whenever(getWatchProvidersByTvShowUseCase(WORK_ID, COUNTRY_CODE))
            .thenThrow(exception)

        assertFailsWith<RuntimeException> {
            useCase(WorkType.TV_SHOW, WORK_ID, COUNTRY_CODE)
        }
    }
}
