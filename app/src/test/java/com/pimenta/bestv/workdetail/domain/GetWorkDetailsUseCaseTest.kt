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
import com.pimenta.bestv.common.kotlin.Quintuple
import com.pimenta.bestv.model.presentation.model.CastViewModel
import com.pimenta.bestv.workdetail.presentation.model.VideoViewModel
import com.pimenta.bestv.model.presentation.model.WorkPageViewModel
import com.pimenta.bestv.model.presentation.model.WorkType
import io.reactivex.Single
import org.junit.Test

/**
 * Created by marcus on 22-08-2019.
 */
private val WORK_VIEW_MODEL = com.pimenta.bestv.model.presentation.model.WorkViewModel(
        id = 1,
        type = WorkType.MOVIE
)
private val VIDEO_VIEW_MODELS = listOf(
        VideoViewModel(
                id = "1",
                name = "VideoResponse"
        )
)
private val CAST_VIEW_MODELS = listOf(
        CastViewModel(
                id = 1,
                name = "Name",
                character = "Character",
                birthday = "Birthday",
                deathDay = null,
                biography = null
        )
)
private val WORK_PAGE_VIEW_MODEL = WorkPageViewModel(
        page = 0,
        totalPages = 0,
        works = null
)

class GetWorkDetailsUseCaseTest {

    private val isFavoriteUseCase: IsFavoriteUseCase = mock()
    private val getVideosUseCase: GetVideosUseCase = mock()
    private val getCastsUseCase: GetCastsUseCase = mock()
    private val getRecommendationByWorkUseCase: GetRecommendationByWorkUseCase = mock()
    private val getSimilarByWorkUseCase: GetSimilarByWorkUseCase = mock()
    private val useCase = GetWorkDetailsUseCase(
            isFavoriteUseCase,
            getVideosUseCase,
            getCastsUseCase,
            getRecommendationByWorkUseCase,
            getSimilarByWorkUseCase
    )

    @Test
    fun `should return the right data when loading the work details`() {
        whenever(isFavoriteUseCase(WORK_VIEW_MODEL))
                .thenReturn(Single.just(true))
        whenever(getVideosUseCase(WorkType.MOVIE, WORK_VIEW_MODEL.id))
                .thenReturn(Single.just(VIDEO_VIEW_MODELS))
        whenever(getCastsUseCase(WorkType.MOVIE, WORK_VIEW_MODEL.id))
                .thenReturn(Single.just(CAST_VIEW_MODELS))
        whenever(getRecommendationByWorkUseCase(WorkType.MOVIE, WORK_VIEW_MODEL.id, 1))
                .thenReturn(Single.just(WORK_PAGE_VIEW_MODEL))
        whenever(getSimilarByWorkUseCase(WorkType.MOVIE, WORK_VIEW_MODEL.id, 1))
                .thenReturn(Single.just(WORK_PAGE_VIEW_MODEL))

        useCase(WORK_VIEW_MODEL)
                .test()
                .assertComplete()
                .assertResult(Quintuple(true, VIDEO_VIEW_MODELS, CAST_VIEW_MODELS, WORK_PAGE_VIEW_MODEL, WORK_PAGE_VIEW_MODEL))
    }

    @Test
    fun `should return an error when some exception happens`() {
        whenever(isFavoriteUseCase(WORK_VIEW_MODEL))
                .thenReturn(Single.just(true))
        whenever(getVideosUseCase(WorkType.MOVIE, WORK_VIEW_MODEL.id))
                .thenReturn(Single.just(VIDEO_VIEW_MODELS))
        whenever(getCastsUseCase(WorkType.MOVIE, WORK_VIEW_MODEL.id))
                .thenReturn(Single.error(Throwable()))
        whenever(getRecommendationByWorkUseCase(WorkType.MOVIE, WORK_VIEW_MODEL.id, 1))
                .thenReturn(Single.just(WORK_PAGE_VIEW_MODEL))
        whenever(getSimilarByWorkUseCase(WorkType.MOVIE, WORK_VIEW_MODEL.id, 1))
                .thenReturn(Single.error(Throwable()))

        useCase(WORK_VIEW_MODEL)
                .test()
                .assertError(Throwable::class.java)
    }
}