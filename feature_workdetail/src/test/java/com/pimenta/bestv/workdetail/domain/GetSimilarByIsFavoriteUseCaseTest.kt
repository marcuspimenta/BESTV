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
import com.pimenta.bestv.model.domain.WorkDomainModel
import com.pimenta.bestv.model.domain.WorkPageDomainModel
import com.pimenta.bestv.model.presentation.model.WorkType
import io.reactivex.Single
import org.junit.Test

/**
 * Created by marcus on 23-06-2018.
 */
private const val WORK_ID = 1
private val WORK_PAGE = WorkPageDomainModel(
        page = 1,
        totalPages = 1,
        works = listOf(
                WorkDomainModel(
                        id = 1,
                        title = "Title"
                )
        )
)

class GetSimilarByWorkUseCaseTest {

    private val getSimilarByMovieUseCase: GetSimilarByMovieUseCase = mock()
    private val getSimilarByTvShowUseCase: GetSimilarByTvShowUseCase = mock()

    private val useCase = GetSimilarByWorkUseCase(
            getSimilarByMovieUseCase,
            getSimilarByTvShowUseCase
    )

    @Test
    fun `should return the right data when loading the similar works by movie`() {
        whenever(getSimilarByMovieUseCase(WORK_ID, 1))
                .thenReturn(Single.just(WORK_PAGE))

        useCase(WorkType.MOVIE, WORK_ID, 1)
                .test()
                .assertComplete()
                .assertResult(WORK_PAGE)
    }

    @Test
    fun `should return an error when loading the similar works by movie and some exception happens`() {
        whenever(getSimilarByMovieUseCase(WORK_ID, 1))
                .thenReturn(Single.error(Throwable()))

        useCase(WorkType.MOVIE, WORK_ID, 1)
                .test()
                .assertError(Throwable::class.java)
    }

    @Test
    fun `should return the right data when loading the similar works by tv show`() {
        whenever(getSimilarByTvShowUseCase(WORK_ID, 1))
                .thenReturn(Single.just(WORK_PAGE))

        useCase(WorkType.TV_SHOW, WORK_ID, 1)
                .test()
                .assertComplete()
                .assertResult(WORK_PAGE)
    }

    @Test
    fun `should return an error when loading the similar works by tv show and some exception happens`() {
        whenever(getSimilarByTvShowUseCase(WORK_ID, 1))
                .thenReturn(Single.error(Throwable()))

        useCase(WorkType.TV_SHOW, WORK_ID, 1)
                .test()
                .assertError(Throwable::class.java)
    }
}
