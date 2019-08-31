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
import com.pimenta.bestv.common.presentation.model.WorkType
import com.pimenta.bestv.common.presentation.model.WorkViewModel
import com.pimenta.bestv.data.MediaRepository
import io.reactivex.Single
import org.junit.Test

/**
 * Created by marcus on 2019-08-28.
 */
private val MOVIE_VIEW_MODEL = WorkViewModel(
        id = 1,
        title = "Batman",
        originalTitle = "Batman",
        type = WorkType.MOVIE
)
private val TV_SHOW_VIEW_MODEL = WorkViewModel(
        id = 1,
        title = "Batman",
        originalTitle = "Batman",
        type = WorkType.TV_SHOW
)

class IsFavoriteUseCaseTest {

    private val mediaRepository: MediaRepository = mock()
    private val useCase = IsFavoriteUseCase(
            mediaRepository
    )

    @Test
    fun `should return true if a movie is favorite`() {
        whenever(mediaRepository.isFavoriteMovie(MOVIE_VIEW_MODEL.id))
                .thenReturn(Single.just(true))

        useCase(MOVIE_VIEW_MODEL)
                .test()
                .assertComplete()
                .assertResult(true)
    }

    @Test
    fun `should return an error when some exception happens when checking if a movie is favorite`() {
        whenever(mediaRepository.isFavoriteMovie(MOVIE_VIEW_MODEL.id))
                .thenReturn(Single.error(Throwable()))

        useCase(MOVIE_VIEW_MODEL)
                .test()
                .assertError(Throwable::class.java)
    }

    @Test
    fun `should return true if a tv show is favorite`() {
        whenever(mediaRepository.isFavoriteTvShow(TV_SHOW_VIEW_MODEL.id))
                .thenReturn(Single.just(true))

        useCase(TV_SHOW_VIEW_MODEL)
                .test()
                .assertComplete()
                .assertResult(true)
    }

    @Test
    fun `should return an error when some exception happens when checking if a tv show is favorite`() {
        whenever(mediaRepository.isFavoriteTvShow(TV_SHOW_VIEW_MODEL.id))
                .thenReturn(Single.error(Throwable()))

        useCase(TV_SHOW_VIEW_MODEL)
                .test()
                .assertError(Throwable::class.java)
    }
}