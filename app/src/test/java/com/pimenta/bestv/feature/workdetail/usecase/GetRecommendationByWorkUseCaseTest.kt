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

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import com.pimenta.bestv.common.presentation.model.WorkPageViewModel
import com.pimenta.bestv.common.presentation.model.WorkType
import com.pimenta.bestv.common.presentation.model.WorkViewModel
import com.pimenta.bestv.repository.MediaRepository
import com.pimenta.bestv.repository.remote.entity.MoviePageResponse
import com.pimenta.bestv.repository.remote.entity.MovieResponse
import io.reactivex.Single
import org.junit.Test

/**
 * Created by marcus on 23-06-2018.
 */
private const val MOVIE_ID = 1
private val WORK_PAGE = MoviePageResponse().apply {
    page = 1
    totalPages = 1
    works = listOf(
            MovieResponse(
                    id = 1,
                    title = "Title"
            )
    )
}
private val WORK_PAGE_VIEW_MODEL = WorkPageViewModel(
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

class GetRecommendationByWorkUseCaseTest {

    private val mediaRepository: MediaRepository = mock()
    private val useCase = GetRecommendationByWorkUseCase(mediaRepository)

    @Test
    fun `should return the right data when loading the recommendations`() {
        whenever(mediaRepository.getRecommendationByMovie(MOVIE_ID, 1)).thenReturn(Single.just(WORK_PAGE))

        useCase(WorkType.MOVIE, MOVIE_ID, 1)
                .test()
                .assertComplete()
                .assertResult(WORK_PAGE_VIEW_MODEL)
    }

    @Test
    fun `should return an error when some exception happens`() {
        whenever(mediaRepository.getRecommendationByMovie(MOVIE_ID, 1)).thenReturn(Single.error(Throwable()))

        useCase(WorkType.MOVIE, MOVIE_ID, 1)
                .test()
                .assertError(Throwable::class.java)
    }
}