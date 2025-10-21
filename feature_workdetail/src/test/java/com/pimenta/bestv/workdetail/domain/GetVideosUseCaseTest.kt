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
import com.pimenta.bestv.workdetail.domain.model.VideoDomainModel
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import kotlin.test.assertFailsWith

/**
 * Created by marcus on 23-06-2018.
 */
private const val MOVIE_ID = 1
private val VIDEO_LIST = listOf(
    VideoDomainModel(
        id = "1",
        name = "VideoResponse"
    )
)

class GetVideosUseCaseTest {

    private val getVideosByMovieUseCase: GetVideosByMovieUseCase = mock()
    private val getVideosByTvShowUseCase: GetVideosByTvShowUseCase = mock()

    private val useCase = GetVideosUseCase(
        getVideosByMovieUseCase,
        getVideosByTvShowUseCase
    )

    @Test
    fun `should return the right data when loading the videos by movie`() = runTest {
        whenever(getVideosByMovieUseCase(MOVIE_ID)).thenReturn(VIDEO_LIST)

        val result = useCase(WorkType.MOVIE, MOVIE_ID)

        assertEquals(VIDEO_LIST, result)
    }

    @Test
    fun `should return an error when loading the videos by movie abd some exception happens`() = runTest {
        val exception = RuntimeException("Test exception")
        whenever(getVideosByMovieUseCase(MOVIE_ID)).thenThrow(exception)

        assertFailsWith<RuntimeException> {
            useCase(WorkType.MOVIE, MOVIE_ID)
        }
    }

    @Test
    fun `should return the right data when loading the videos by tv show`() = runTest {
        whenever(getVideosByTvShowUseCase(MOVIE_ID)).thenReturn(VIDEO_LIST)

        val result = useCase(WorkType.TV_SHOW, MOVIE_ID)

        assertEquals(VIDEO_LIST, result)
    }

    @Test
    fun `should return an error when loading the videos by tv show abd some exception happens`() = runTest {
        val exception = RuntimeException("Test exception")
        whenever(getVideosByTvShowUseCase(MOVIE_ID)).thenThrow(exception)

        assertFailsWith<RuntimeException> {
            useCase(WorkType.TV_SHOW, MOVIE_ID)
        }
    }
}
