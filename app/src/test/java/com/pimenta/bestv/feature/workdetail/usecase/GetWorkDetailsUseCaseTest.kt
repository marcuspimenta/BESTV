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

package com.pimenta.bestv.feature.workdetail.usecase

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import com.pimenta.bestv.common.kotlin.Quintuple
import com.pimenta.bestv.common.presentation.model.CastViewModel
import com.pimenta.bestv.common.presentation.model.VideoViewModel
import com.pimenta.bestv.common.presentation.model.WorkPageViewModel
import com.pimenta.bestv.common.usecase.WorkUseCase
import com.pimenta.bestv.data.entity.Movie
import io.reactivex.Single
import org.junit.Test

class GetWorkDetailsUseCaseTest {

    private val workUseCase: WorkUseCase = mock()
    private val getVideosUseCase: GetVideosUseCase = mock()
    private val getCastsUseCase: GetCastsUseCase = mock()
    private val getRecommendationByWorkUseCase: GetRecommendationByWorkUseCase = mock()
    private val getSimilarByWorkUseCase: GetSimilarByWorkUseCase = mock()
    private val useCase = GetWorkDetailsUseCase(
            workUseCase,
            getVideosUseCase,
            getCastsUseCase,
            getRecommendationByWorkUseCase,
            getSimilarByWorkUseCase
    )


    @Test
    fun `should return the right data when loading the work details`() {
        whenever(workUseCase.isFavorite((any()))).thenReturn(Single.just(true))
        whenever(getVideosUseCase(any())).thenReturn(Single.just(videoViewModels))
        whenever(getCastsUseCase(any())).thenReturn(Single.just(castViewModels))
        whenever(getRecommendationByWorkUseCase(any(), any())).thenReturn(Single.just(workPageViewModel))
        whenever(getSimilarByWorkUseCase(any(), any())).thenReturn(Single.just(workPageViewModel))

        useCase(movie)
                .test()
                .assertComplete()
                .assertResult(Quintuple(true, videoViewModels, castViewModels, workPageViewModel, workPageViewModel))
    }

    @Test
    fun `should return an error when some exception happens`() {
        whenever(workUseCase.isFavorite((any()))).thenReturn(Single.just(true))
        whenever(getVideosUseCase(any())).thenReturn(Single.just(videoViewModels))
        whenever(getCastsUseCase(any())).thenReturn(Single.error(Throwable()))
        whenever(getRecommendationByWorkUseCase(any(), any())).thenReturn(Single.just(workPageViewModel))
        whenever(getSimilarByWorkUseCase(any(), any())).thenReturn(Single.error(Throwable()))

        useCase(movie)
                .test()
                .assertError(Throwable::class.java)
    }

    companion object {

        private val movie = Movie(
                id = 1
        )

        private val videoViewModels = listOf(
                VideoViewModel(
                        id = "1",
                        name = "Video"
                )
        )

        private val castViewModels = listOf(
                CastViewModel(
                        id = 1,
                        name = "Name",
                        character = "Character",
                        birthday = "Birthday",
                        deathDay = null,
                        biography = null
                )
        )

        private val workPageViewModel = WorkPageViewModel(
                page = 0,
                totalPages = 0,
                works = null
        )
    }
}