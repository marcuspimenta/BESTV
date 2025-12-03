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
import com.pimenta.bestv.workbrowse.data.repository.TvShowRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.fail
import org.junit.Test

private const val PAGE = 1
private val PAGE_DOMAIN_MODEL = PageDomainModel(
    page = 1,
    totalPages = 10,
    results = listOf(
        WorkDomainModel(
            id = 1,
            title = "Top Rated TV Show",
            originalTitle = "Top Rated TV Show",
            type = WorkDomainModel.Type.TV_SHOW
        )
    )
)

class GetTopRatedTvShowsUseCaseTest {

    private val tvShowRepository: TvShowRepository = mock()
    private val useCase = GetTopRatedTvShowsUseCase(tvShowRepository)

    @Test
    fun `should return top rated tv shows from repository`() = runTest {
        whenever(tvShowRepository.getTopRatedTvShows(PAGE)).thenReturn(PAGE_DOMAIN_MODEL)

        val result = useCase(PAGE)

        assertEquals(PAGE_DOMAIN_MODEL, result)
    }

    @Test
    fun `should throw exception when repository fails`() = runTest {
        whenever(tvShowRepository.getTopRatedTvShows(PAGE)).thenThrow(RuntimeException("Network error"))

        try {
            useCase(PAGE)
            fail("Expected RuntimeException")
        } catch (e: RuntimeException) {
            assertEquals("Network error", e.message)
        }
    }
}
