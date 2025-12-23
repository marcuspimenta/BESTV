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
import com.pimenta.bestv.model.domain.CastDomainModel
import com.pimenta.bestv.workdetail.data.repository.TvShowRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import kotlin.test.assertFailsWith
import org.junit.Test

/**
 * Created by marcus on 21-10-2019.
 */
private const val WORK_ID = 1
private val CAST_LIST = listOf(
    CastDomainModel(
        id = 1,
        name = "Name",
        character = "Character",
        birthday = "Birthday",
        deathDay = null,
        biography = null
    )
)

class GetCastByTvShowUseCaseTest {

    private val tvShowRepository: TvShowRepository = mock()

    private val useCase = GetCastByTvShowUseCase(
        tvShowRepository
    )

    @Test
    fun `should return the right data when loading the casts by tv show`() = runTest {
        whenever(tvShowRepository.getCastByTvShow(WORK_ID)).thenReturn(CAST_LIST)

        val result = useCase(WORK_ID)

        assertEquals(CAST_LIST, result)
    }

    @Test
    fun `should return an error when some exception happens when loading the casts by tv show`() = runTest {
        val exception = RuntimeException("Test exception")
        whenever(tvShowRepository.getCastByTvShow(WORK_ID)).thenThrow(exception)

        assertFailsWith<RuntimeException> {
            useCase(WORK_ID)
        }
    }
}
