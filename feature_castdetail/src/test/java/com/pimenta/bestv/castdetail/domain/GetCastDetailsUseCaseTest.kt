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

package com.pimenta.bestv.castdetail.domain

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import com.pimenta.bestv.model.domain.CastDomainModel
import com.pimenta.bestv.model.domain.WorkDomainModel
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import kotlin.test.assertFailsWith

/**
 * Created by marcus on 23-05-2018.
 */
private const val CAST_ID = 1
private val CAST_DETAILED = CastDomainModel(
    id = CAST_ID,
    name = "Carlos",
    character = "Batman",
    birthday = "1990-07-13"
)
private val EXPECTED_RESULT = Triple<CastDomainModel, List<WorkDomainModel>?, List<WorkDomainModel>?>(
    CAST_DETAILED,
    emptyList(),
    emptyList()
)

class GetCastDetailsUseCaseTest {

    private val getCastPersonalDetails: GetCastPersonalDetails = mock()
    private val getMovieCreditsByCastUseCase: GetMovieCreditsByCastUseCase = mock()
    private val getTvShowCreditsByCastUseCase: GetTvShowCreditsByCastUseCase = mock()

    private val useCase = GetCastDetailsUseCase(
        getCastPersonalDetails,
        getMovieCreditsByCastUseCase,
        getTvShowCreditsByCastUseCase
    )

    @Test
    fun `should return the correct data when load the cast details`() = runTest {
        whenever(getCastPersonalDetails(CAST_ID)).thenReturn(CAST_DETAILED)
        whenever(getMovieCreditsByCastUseCase(CAST_ID)).thenReturn(emptyList())
        whenever(getTvShowCreditsByCastUseCase(CAST_ID)).thenReturn(emptyList())

        val result = useCase(CAST_ID)
        assertEquals(result, EXPECTED_RESULT)
    }

    @Test
    fun `should return an error when some exception happens`() = runTest {
        whenever(getCastPersonalDetails(CAST_ID)).thenReturn(CAST_DETAILED)
        whenever(getMovieCreditsByCastUseCase(CAST_ID)).thenThrow(RuntimeException())
        whenever(getTvShowCreditsByCastUseCase(CAST_ID)).thenReturn(emptyList())

        assertFailsWith<RuntimeException> {
            useCase(CAST_ID)
        }
    }
}
