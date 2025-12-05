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
import com.pimenta.bestv.model.domain.PageDomainModel
import com.pimenta.bestv.model.domain.WorkDomainModel
import com.pimenta.bestv.model.presentation.model.WorkType
import com.pimenta.bestv.model.presentation.model.WorkViewModel
import com.pimenta.bestv.workdetail.domain.model.ReviewDomainModel
import com.pimenta.bestv.workdetail.domain.model.VideoDomainModel
import com.pimenta.bestv.workdetail.domain.model.WatchProviderDomainModel
import com.pimenta.bestv.workdetail.domain.model.WatchProvidersDomainModel
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import kotlin.test.assertFailsWith

/**
 * Created by marcus on 22-08-2019.
 */
private val WORK = WorkViewModel(
    id = 1,
    title = "Test Movie",
    originalTitle = "Test Movie",
    originalLanguage = "en",
    overview = "A test movie",
    source = "tmdb",
    backdropUrl = "https://image.tmdb.org/t/p/original/backdrop.jpg",
    posterUrl = "https://image.tmdb.org/t/p/original/poster.jpg",
    releaseDate = "Jan 01, 2023",
    type = WorkType.MOVIE,
    voteAverage = 8.0f
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
private val WORK_PAGE = PageDomainModel<WorkDomainModel>(
    page = 0,
    totalPages = 0,
    results = null
)
private val REVIEW_PAGE = PageDomainModel<ReviewDomainModel>(
    page = 0,
    totalPages = 0,
    results = null
)
private const val COUNTRY_CODE = "US"
private val WATCH_PROVIDERS = WatchProvidersDomainModel(
    tmdbLink = "https://www.themoviedb.org/movie/1/watch",
    streaming = listOf(
        WatchProviderDomainModel(
            id = 1,
            name = "Netflix",
            logoPath = "/logo.jpg",
            displayPriority = 1
        )
    ),
    rent = emptyList(),
    buy = emptyList()
)

class GetWorkDetailsUseCaseTest {

    private val checkFavoriteWorkUseCase: CheckFavoriteWorkUseCase = mock()
    private val getVideosUseCase: GetVideosUseCase = mock()
    private val getCastsUseCase: GetCastsUseCase = mock()
    private val getRecommendationByWorkUseCase: GetRecommendationByWorkUseCase = mock()
    private val getSimilarByWorkUseCase: GetSimilarByWorkUseCase = mock()
    private val getReviewByWorkUseCase: GetReviewByWorkUseCase = mock()
    private val getWatchProvidersUseCase: GetWatchProvidersUseCase = mock()
    private val useCase = GetWorkDetailsUseCase(
        checkFavoriteWorkUseCase,
        getVideosUseCase,
        getCastsUseCase,
        getRecommendationByWorkUseCase,
        getSimilarByWorkUseCase,
        getReviewByWorkUseCase,
        getWatchProvidersUseCase
    )

    @Test
    fun `should return the right data when loading the work details`() = runTest {
        whenever(checkFavoriteWorkUseCase(WORK))
            .thenReturn(true)
        whenever(getVideosUseCase(WorkType.MOVIE, WORK.id))
            .thenReturn(VIDEO_LIST)
        whenever(getCastsUseCase(WorkType.MOVIE, WORK.id))
            .thenReturn(CAST_LIST)
        whenever(getRecommendationByWorkUseCase(WorkType.MOVIE, WORK.id, 1))
            .thenReturn(WORK_PAGE)
        whenever(getSimilarByWorkUseCase(WorkType.MOVIE, WORK.id, 1))
            .thenReturn(WORK_PAGE)
        whenever(getReviewByWorkUseCase(WorkType.MOVIE, WORK.id, 1))
            .thenReturn(REVIEW_PAGE)
        whenever(getWatchProvidersUseCase(WorkType.MOVIE, WORK.id, COUNTRY_CODE))
            .thenReturn(WATCH_PROVIDERS)

        val result = useCase(WORK, COUNTRY_CODE)

        val expected = GetWorkDetailsUseCase.WorkDetailsDomainWrapper(
            isFavorite = true,
            videos = VIDEO_LIST,
            casts = CAST_LIST,
            recommended = WORK_PAGE,
            similar = WORK_PAGE,
            reviews = REVIEW_PAGE,
            watchProviders = WATCH_PROVIDERS
        )
        assertEquals(expected, result)
    }

    @Test
    fun `should return an error when some exception happens`() = runTest {
        val exception = RuntimeException("Test exception")

        whenever(checkFavoriteWorkUseCase(WORK))
            .thenReturn(true)
        whenever(getVideosUseCase(WorkType.MOVIE, WORK.id))
            .thenReturn(VIDEO_LIST)
        whenever(getCastsUseCase(WorkType.MOVIE, WORK.id))
            .thenThrow(exception)
        whenever(getRecommendationByWorkUseCase(WorkType.MOVIE, WORK.id, 1))
            .thenReturn(WORK_PAGE)
        whenever(getSimilarByWorkUseCase(WorkType.MOVIE, WORK.id, 1))
            .thenThrow(exception)
        whenever(getReviewByWorkUseCase(WorkType.MOVIE, WORK.id, 1))
            .thenReturn(REVIEW_PAGE)
        whenever(getWatchProvidersUseCase(WorkType.MOVIE, WORK.id, COUNTRY_CODE))
            .thenReturn(WATCH_PROVIDERS)

        assertFailsWith<RuntimeException> {
            useCase(WORK, COUNTRY_CODE)
        }
    }

    @Test
    fun `should return null watch providers when watch providers fails`() = runTest {
        val exception = RuntimeException("Watch providers error")

        whenever(checkFavoriteWorkUseCase(WORK))
            .thenReturn(true)
        whenever(getVideosUseCase(WorkType.MOVIE, WORK.id))
            .thenReturn(VIDEO_LIST)
        whenever(getCastsUseCase(WorkType.MOVIE, WORK.id))
            .thenReturn(CAST_LIST)
        whenever(getRecommendationByWorkUseCase(WorkType.MOVIE, WORK.id, 1))
            .thenReturn(WORK_PAGE)
        whenever(getSimilarByWorkUseCase(WorkType.MOVIE, WORK.id, 1))
            .thenReturn(WORK_PAGE)
        whenever(getReviewByWorkUseCase(WorkType.MOVIE, WORK.id, 1))
            .thenReturn(REVIEW_PAGE)
        whenever(getWatchProvidersUseCase(WorkType.MOVIE, WORK.id, COUNTRY_CODE))
            .thenThrow(exception)

        val result = useCase(WORK, COUNTRY_CODE)

        val expected = GetWorkDetailsUseCase.WorkDetailsDomainWrapper(
            isFavorite = true,
            videos = VIDEO_LIST,
            casts = CAST_LIST,
            recommended = WORK_PAGE,
            similar = WORK_PAGE,
            reviews = REVIEW_PAGE,
            watchProviders = null
        )
        assertEquals(expected, result)
    }
}
