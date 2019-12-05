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
import com.pimenta.bestv.model.domain.CastDomainModel
import com.pimenta.bestv.model.domain.WorkPageDomainModel
import com.pimenta.bestv.model.presentation.model.WorkType
import com.pimenta.bestv.model.presentation.model.WorkViewModel
import com.pimenta.bestv.presentation.kotlin.Quadruple
import com.pimenta.bestv.workdetail.domain.model.VideoDomainModel
import io.reactivex.Single
import org.junit.Test

/**
 * Created by marcus on 22-08-2019.
 */
private val WORK = WorkViewModel(
        id = 1,
        type = WorkType.MOVIE
)
private val VIDEO_LIST = listOf(
        VideoDomainModel(
                id = "1",
                name = "VideoResponse"
        )
)
private val CAST_LIST = listOf(
        CastDomainModel(
                id = 1,
                name = "Name",
                character = "Character",
                birthday = "Birthday",
                deathDay = null,
                biography = null
        )
)
private val WORK_PAGE = WorkPageDomainModel(
        page = 0,
        totalPages = 0,
        works = null
)

class GetWorkDetailsUseCaseTest {

    private val getVideosUseCase: GetVideosUseCase = mock()
    private val getCastsUseCase: GetCastsUseCase = mock()
    private val getRecommendationByWorkUseCase: GetRecommendationByWorkUseCase = mock()
    private val getSimilarByWorkUseCase: GetSimilarByWorkUseCase = mock()
    private val useCase = GetWorkDetailsUseCase(
            getVideosUseCase,
            getCastsUseCase,
            getRecommendationByWorkUseCase,
            getSimilarByWorkUseCase
    )

    @Test
    fun `should return the right data when loading the work details`() {
        whenever(getVideosUseCase(WorkType.MOVIE, WORK.id))
                .thenReturn(Single.just(VIDEO_LIST))
        whenever(getCastsUseCase(WorkType.MOVIE, WORK.id))
                .thenReturn(Single.just(CAST_LIST))
        whenever(getRecommendationByWorkUseCase(WorkType.MOVIE, WORK.id, 1))
                .thenReturn(Single.just(WORK_PAGE))
        whenever(getSimilarByWorkUseCase(WorkType.MOVIE, WORK.id, 1))
                .thenReturn(Single.just(WORK_PAGE))

        useCase(WORK)
                .test()
                .assertComplete()
                .assertResult(Quadruple(VIDEO_LIST, CAST_LIST, WORK_PAGE, WORK_PAGE))
    }

    @Test
    fun `should return an error when some exception happens`() {
        whenever(getVideosUseCase(WorkType.MOVIE, WORK.id))
                .thenReturn(Single.just(VIDEO_LIST))
        whenever(getCastsUseCase(WorkType.MOVIE, WORK.id))
                .thenReturn(Single.error(Throwable()))
        whenever(getRecommendationByWorkUseCase(WorkType.MOVIE, WORK.id, 1))
                .thenReturn(Single.just(WORK_PAGE))
        whenever(getSimilarByWorkUseCase(WorkType.MOVIE, WORK.id, 1))
                .thenReturn(Single.error(Throwable()))

        useCase(WORK)
                .test()
                .assertError(Throwable::class.java)
    }
}