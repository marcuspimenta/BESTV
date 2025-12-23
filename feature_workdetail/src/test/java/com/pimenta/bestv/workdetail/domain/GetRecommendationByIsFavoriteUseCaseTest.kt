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
import com.pimenta.bestv.model.domain.PageDomainModel
import com.pimenta.bestv.model.domain.WorkDomainModel
import com.pimenta.bestv.model.domain.WorkDomainModel.Type
import com.pimenta.bestv.model.presentation.model.WorkType
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import kotlin.test.assertFailsWith
import org.junit.Test

/**
 * Created by marcus on 23-06-2018.
 */
private const val WORK_ID = 1
private val WORK_PAGE_VIEW_MODEL = PageDomainModel(
    page = 1,
    totalPages = 1,
    results = listOf(
        WorkDomainModel(
            id = 1,
            title = "Title",
            type = Type.MOVIE
        )
    )
)

class GetRecommendationByWorkUseCaseTest {

    private val getRecommendationByMovieUseCase: GetRecommendationByMovieUseCase = mock()
    private val getRecommendationByTvShowUseCase: GetRecommendationByTvShowUseCase = mock()

    private val useCase = GetRecommendationByWorkUseCase(
        getRecommendationByMovieUseCase,
        getRecommendationByTvShowUseCase
    )

    @Test
    fun `should return the right data when loading the recommendations by movie`() = runTest {
        whenever(getRecommendationByMovieUseCase(WORK_ID, 1))
            .thenReturn(WORK_PAGE_VIEW_MODEL)

        val result = useCase(WorkType.MOVIE, WORK_ID, 1)

        assertEquals(WORK_PAGE_VIEW_MODEL, result)
    }

    @Test
    fun `should return an error when loading the recommendations by movie and some exception happens`() = runTest {
        val exception = RuntimeException("Test exception")
        whenever(getRecommendationByMovieUseCase(WORK_ID, 1))
            .thenThrow(exception)

        assertFailsWith<RuntimeException> {
            useCase(WorkType.MOVIE, WORK_ID, 1)
        }
    }

    @Test
    fun `should return the right data when loading the recommendations by tv show`() = runTest {
        whenever(getRecommendationByTvShowUseCase(WORK_ID, 1))
            .thenReturn(WORK_PAGE_VIEW_MODEL)

        val result = useCase(WorkType.TV_SHOW, WORK_ID, 1)

        assertEquals(WORK_PAGE_VIEW_MODEL, result)
    }

    @Test
    fun `should return an error when loading the recommendations by tv show and some exception happens`() = runTest {
        val exception = RuntimeException("Test exception")
        whenever(getRecommendationByTvShowUseCase(WORK_ID, 1))
            .thenThrow(exception)

        assertFailsWith<RuntimeException> {
            useCase(WorkType.TV_SHOW, WORK_ID, 1)
        }
    }
}
