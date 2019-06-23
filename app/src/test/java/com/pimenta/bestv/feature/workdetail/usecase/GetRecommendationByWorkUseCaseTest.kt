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
import com.pimenta.bestv.common.presentation.model.WorkPageViewModel
import com.pimenta.bestv.common.presentation.model.WorkType
import com.pimenta.bestv.common.presentation.model.WorkViewModel
import com.pimenta.bestv.data.entity.Movie
import com.pimenta.bestv.data.entity.MoviePage
import com.pimenta.bestv.data.repository.MediaRepository
import io.reactivex.Single
import org.junit.Test

/**
 * Created by marcus on 23-06-2018.
 */
class GetRecommendationByWorkUseCaseTest {

    private val mediaRepository: MediaRepository = mock()
    private val useCase = GetRecommendationByWorkUseCase(mediaRepository)

    @Test
    fun `should return the right data when loading the recommendations`() {
        whenever(mediaRepository.getRecommendationByWork(any(), any())).thenReturn(Single.just(workPage))

        useCase(movie, 1)
                .test()
                .assertComplete()
                .assertResult(workPageViewModel)
    }

    @Test
    fun `should return an error when some exception happens`() {
        whenever(mediaRepository.getRecommendationByWork(any(), any())).thenReturn(Single.error(Throwable()))

        useCase(movie, 1)
                .test()
                .assertError(Throwable::class.java)
    }

    companion object {

        private val movie = Movie(
                id = 1
        )

        private val workPage = MoviePage().apply {
            page = 1
            totalPages = 1
            works = listOf(
                    Movie(
                            id = 1,
                            title = "Title"
                    )
            )
        }

        private val workPageViewModel = WorkPageViewModel(
                page = 1,
                totalPages = 1,
                works = listOf(
                        WorkViewModel(
                                id = 1,
                                title = "Title",
                                type = WorkType.MOVIE
                        )
                )
        )
    }
}