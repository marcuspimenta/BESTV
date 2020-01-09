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
import com.pimenta.bestv.workdetail.data.repository.TvShowRepository
import com.pimenta.bestv.workdetail.domain.model.VideoDomainModel
import io.reactivex.Single
import org.junit.Test

/**
 * Created by marcus on 22-10-2018.
 */
private const val TV_SHOW_ID = 1
private val VIDEO_LIST = listOf(
        VideoDomainModel(
                id = "1",
                name = "VideoResponse"
        )
)

class GetVideosByTvShowUseCaseTest {

    private val tvShowRepository: TvShowRepository = mock()

    private val useCase = GetVideosByTvShowUseCase(
            tvShowRepository
    )

    @Test
    fun `should return the right data when loading the videos`() {
        whenever(tvShowRepository.getVideosByTvShow(TV_SHOW_ID))
                .thenReturn(Single.just(VIDEO_LIST))

        useCase(TV_SHOW_ID)
                .test()
                .assertComplete()
                .assertResult(VIDEO_LIST)
    }

    @Test
    fun `should return an error when some exception happens`() {
        whenever(tvShowRepository.getVideosByTvShow(TV_SHOW_ID))
                .thenReturn(Single.error(Throwable()))

        useCase(TV_SHOW_ID)
                .test()
                .assertError(Throwable::class.java)
    }
}
