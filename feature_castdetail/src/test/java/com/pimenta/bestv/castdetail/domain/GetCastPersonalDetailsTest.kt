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
import com.pimenta.bestv.castdetail.data.repository.CastRepository
import com.pimenta.bestv.model.domain.CastDomainModel
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import kotlin.test.assertFailsWith

/**
 * Created by marcus on 23-05-2018.
 */
private const val CAST_ID = 1
private val CAST_DETAILED = CastDomainModel(
    id = 1,
    name = "Carlos",
    character = "Batman",
    birthday = "1990-07-13"
)

class GetCastPersonalDetailsTest {

    private val castRepository: CastRepository = mock()

    private val useCase = GetCastPersonalDetails(
        castRepository
    )

    @Test
    fun `should return the right data when loading the cast personal details`() = runTest {
        whenever(castRepository.getCastDetails(CAST_ID)).thenReturn(CAST_DETAILED)

        val result = useCase(CAST_ID)
        assertEquals(result, CAST_DETAILED)
    }

    @Test
    fun `should return an error when some exception happens`() = runTest {
        whenever(castRepository.getCastDetails(CAST_ID)).thenThrow(RuntimeException())

        assertFailsWith<RuntimeException> {
            useCase(CAST_ID)
        }
    }
}
