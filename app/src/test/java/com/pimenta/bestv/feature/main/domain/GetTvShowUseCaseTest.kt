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

package com.pimenta.bestv.feature.main.domain

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import com.pimenta.bestv.model.presentation.model.WorkType
import com.pimenta.bestv.model.presentation.model.WorkViewModel
import com.pimenta.bestv.data.remote.model.remote.TvShowResponse
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Created by marcus on 2019-10-21.
 */
private const val TV_SHOW_ID = 1
private val TV_SHOW_RESPONSE = TvShowResponse(
        id = TV_SHOW_ID,
        title = "Batman",
        originalTitle = "Batman"
)
private val TV_SHOW_VIEW_MODEL = WorkViewModel(
        id = 1,
        title = "Batman",
        originalTitle = "Batman",
        type = WorkType.TV_SHOW
)

class GetTvShowUseCaseTest {

    private val mediaRepository: MediaRepository = mock()

    private val useCase = GetTvShowUseCase(
            mediaRepository
    )

    @Test
    fun `should return the right data when loading a tv show`() {
        whenever(mediaRepository.getTvShow(TV_SHOW_ID)).thenReturn(TV_SHOW_RESPONSE)

        val tvShowResponse = useCase(TV_SHOW_ID)

        assertEquals(tvShowResponse, TV_SHOW_VIEW_MODEL)
    }

    @Test
    fun `should return null when no tv show is found`() {
        whenever(mediaRepository.getTvShow(TV_SHOW_ID)).thenReturn(null)

        val tvShowResponse = useCase(TV_SHOW_ID)

        assertEquals(tvShowResponse, null)
    }
}