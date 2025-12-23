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
import com.pimenta.bestv.model.domain.PageDomainModel
import com.pimenta.bestv.model.domain.WorkDomainModel
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Test

/**
 * Created by marcus on 2019-08-23.
 */
private val WORK_DOMAIN_MODEL = WorkDomainModel(
    id = 1,
    title = "Batman",
    originalTitle = "Batman",
    type = WorkDomainModel.Type.MOVIE
)
private val WORK_PAGE_DOMAIN_MODEL = PageDomainModel(
    page = 1,
    totalPages = 1,
    results = listOf(WORK_DOMAIN_MODEL)
)

class GetFavoritesUseCaseTest {

    private val getFavoriteMoviesUseCase: GetFavoriteMoviesUseCase = mock()
    private val getFavoriteTvShowsUseCase: GetFavoriteTvShowsUseCase = mock()

    private val useCase = GetFavoritesUseCase(
        getFavoriteMoviesUseCase,
        getFavoriteTvShowsUseCase
    )

    @Test
    fun `should return the right data when loading the favorites`() = runTest {
        whenever(getFavoriteMoviesUseCase()).thenReturn(listOf(WORK_DOMAIN_MODEL))
        whenever(getFavoriteTvShowsUseCase()).thenReturn(emptyList())

        val result = useCase()

        Assert.assertEquals(WORK_PAGE_DOMAIN_MODEL, result)
    }

    @Test
    fun `should return an error when some exception happens`() = runTest {
        whenever(getFavoriteMoviesUseCase()).thenReturn(listOf(WORK_DOMAIN_MODEL))
        whenever(getFavoriteTvShowsUseCase()).thenThrow(RuntimeException())

        try {
            useCase()
            Assert.fail("Expected exception")
        } catch (e: RuntimeException) {
            // Expected
        }
    }
}
