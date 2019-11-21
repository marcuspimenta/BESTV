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

package com.pimenta.bestv.feature.workdetail.domain

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import com.pimenta.bestv.common.presentation.model.VideoViewModel
import com.pimenta.bestv.common.data.model.remote.VideoListResponse
import com.pimenta.bestv.common.data.model.remote.VideoResponse
import io.reactivex.Single
import org.junit.Test

/**
 * Created by marcus on 22-10-2018.
 */
private const val MOVIE_ID = 1
private val VIDEO_LIST = VideoListResponse(
        id = 1,
        videos = listOf(
                VideoResponse(
                        id = "1",
                        name = "VideoResponse"
                )
        )
)
private val VIDEO_VIEW_MODELS = listOf(
        VideoViewModel(
                id = "1",
                name = "VideoResponse"
        )
)

class GetVideosByMovieUseCaseTest {

    private val mediaRepository: MediaRepository = mock()

    private val useCase = GetVideosByMovieUseCase(
            mediaRepository
    )

    @Test
    fun `should return the right data when loading the videos`() {
        whenever(mediaRepository.getVideosByMovie(MOVIE_ID)).thenReturn(Single.just(VIDEO_LIST))

        useCase(MOVIE_ID)
                .test()
                .assertComplete()
                .assertResult(VIDEO_VIEW_MODELS)
    }

    @Test
    fun `should return an error when some exception happens`() {
        whenever(mediaRepository.getVideosByMovie(MOVIE_ID)).thenReturn(Single.error(Throwable()))

        useCase(MOVIE_ID)
                .test()
                .assertError(Throwable::class.java)
    }
}