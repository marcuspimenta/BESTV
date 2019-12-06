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

package com.pimenta.bestv.search.domain

import org.junit.Test

/**
 * Created by marcus on 24-05-2018.
 */
private const val TEXT = "Game of thrones"
private const val TEXT_ENCODED = "Game+of+thrones"

class UrlEncoderTextUseCaseTest {

    private val useCase = UrlEncoderTextUseCase()

    @Test
    fun `should return the right data when encoding a text`() {
        useCase(TEXT)
                .test()
                .assertComplete()
                .assertResult(TEXT_ENCODED)
    }
}