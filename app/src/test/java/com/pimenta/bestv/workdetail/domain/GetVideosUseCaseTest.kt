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
import com.pimenta.bestv.workdetail.presentation.model.VideoViewModel
import com.pimenta.bestv.model.presentation.model.WorkType
import io.reactivex.Single
import org.junit.Test

/**
 * Created by marcus on 23-06-2018.
 */
private const val MOVIE_ID = 1
private val VIDEO_VIEW_MODELS = listOf(
        VideoViewModel(
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
    fun `should return the right data when loading the videos by movie`() {
        whenever(getVideosByMovieUseCase(MOVIE_ID)).thenReturn(Single.just(VIDEO_VIEW_MODELS))

        useCase(WorkType.MOVIE, MOVIE_ID)
                .test()
                .assertComplete()
                .assertResult(VIDEO_VIEW_MODELS)
    }

    @Test
    fun `should return an error when loading the videos by movie abd some exception happens`() {
        whenever(getVideosByMovieUseCase(MOVIE_ID)).thenReturn(Single.error(Throwable()))

        useCase(WorkType.MOVIE, MOVIE_ID)
                .test()
                .assertError(Throwable::class.java)
    }

    @Test
    fun `should return the right data when loading the videos by tv show`() {
        whenever(getVideosByTvShowUseCase(MOVIE_ID)).thenReturn(Single.just(VIDEO_VIEW_MODELS))

        useCase(WorkType.TV_SHOW, MOVIE_ID)
                .test()
                .assertComplete()
                .assertResult(VIDEO_VIEW_MODELS)
    }

    @Test
    fun `should return an error when loading the videos by tv show abd some exception happens`() {
        whenever(getVideosByTvShowUseCase(MOVIE_ID)).thenReturn(Single.error(Throwable()))

        useCase(WorkType.TV_SHOW, MOVIE_ID)
                .test()
                .assertError(Throwable::class.java)
    }
}