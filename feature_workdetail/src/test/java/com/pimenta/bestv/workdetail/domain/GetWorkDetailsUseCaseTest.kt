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

import com.pimenta.bestv.model.domain.CastDomainModel
import com.pimenta.bestv.model.domain.PageDomainModel
import com.pimenta.bestv.model.domain.WorkDomainModel
import com.pimenta.bestv.workdetail.domain.model.ReviewDomainModel
import com.pimenta.bestv.workdetail.domain.model.VideoDomainModel
import com.pimenta.bestv.workdetail.domain.model.WatchProviderDomainModel
import com.pimenta.bestv.workdetail.domain.model.WatchProvidersDomainModel
import com.pimenta.bestv.workdetail.domain.model.WorkMetadataDomainModel
import com.pimenta.bestv.workdetail.domain.model.WorkCreditsDomainModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.doSuspendableAnswer
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import kotlin.test.assertFailsWith

/**
 * Created by marcus on 22-08-2019.
 */
private const val WORK_ID = 1
private val WORK_TYPE = WorkDomainModel.Type.MOVIE
private const val INITIAL_FAVORITE = false
private val VIDEO_LIST =
    listOf(
        VideoDomainModel(
            id = "1",
            name = "VideoResponse",
        ),
    )
private val CAST_LIST =
    listOf(
        CastDomainModel(
            id = 1,
            name = "Name",
            character = "Character",
            birthday = "Birthday",
            deathDay = null,
            biography = null,
        ),
    )
private val WORK_CREDITS = WorkCreditsDomainModel(casts = CAST_LIST, crew = emptyList())
private val WORK_PAGE =
    PageDomainModel<WorkDomainModel>(
        page = 0,
        totalPages = 0,
        results = null,
    )
private val REVIEW_PAGE =
    PageDomainModel<ReviewDomainModel>(
        page = 0,
        totalPages = 0,
        results = null,
    )
private const val COUNTRY_CODE = "US"
private val WATCH_PROVIDERS =
    WatchProvidersDomainModel(
        tmdbLink = "https://www.themoviedb.org/movie/1/watch",
        streaming = listOf(
            WatchProviderDomainModel(
                id = 1,
                name = "Netflix",
                logoPath = "/logo.jpg",
                displayPriority = 1,
            ),
        ),
        rent = emptyList(),
        buy = emptyList(),
    )

class GetWorkDetailsUseCaseTest {
    private val checkFavoriteWorkUseCase: CheckFavoriteWorkUseCase = mock()
    private val getVideosUseCase: GetVideosUseCase = mock()
    private val getCastsUseCase: GetCastsUseCase = mock()
    private val getRecommendationByWorkUseCase: GetRecommendationByWorkUseCase = mock()
    private val getSimilarByWorkUseCase: GetSimilarByWorkUseCase = mock()
    private val getReviewByWorkUseCase: GetReviewByWorkUseCase = mock()
    private val getWatchProvidersUseCase: GetWatchProvidersUseCase = mock()
    private val getWorkMetadataUseCase: GetWorkMetadataUseCase = mock()
    private val useCase =
        GetWorkDetailsUseCase(
            checkFavoriteWorkUseCase,
            getVideosUseCase,
            getCastsUseCase,
            getRecommendationByWorkUseCase,
            getSimilarByWorkUseCase,
            getReviewByWorkUseCase,
            getWatchProvidersUseCase,
            getWorkMetadataUseCase,
        )

    @Test
    fun `should return the right data when loading the work details`() =
        runTest {
            whenever(checkFavoriteWorkUseCase(WORK_ID, WORK_TYPE))
                .thenReturn(true)
            whenever(getVideosUseCase(WorkDomainModel.Type.MOVIE, WORK_ID))
                .thenReturn(VIDEO_LIST)
            whenever(getCastsUseCase(WorkDomainModel.Type.MOVIE, WORK_ID))
                .thenReturn(WORK_CREDITS)
            whenever(getRecommendationByWorkUseCase(WorkDomainModel.Type.MOVIE, WORK_ID, 1))
                .thenReturn(WORK_PAGE)
            whenever(getSimilarByWorkUseCase(WorkDomainModel.Type.MOVIE, WORK_ID, 1))
                .thenReturn(WORK_PAGE)
            whenever(getReviewByWorkUseCase(WorkDomainModel.Type.MOVIE, WORK_ID, 1))
                .thenReturn(REVIEW_PAGE)
            whenever(getWatchProvidersUseCase(WorkDomainModel.Type.MOVIE, WORK_ID, COUNTRY_CODE))
                .thenReturn(WATCH_PROVIDERS)
            whenever(getWorkMetadataUseCase(WorkDomainModel.Type.MOVIE, WORK_ID))
                .thenReturn(WorkMetadataDomainModel(status = "Released"))

            val result = useCase(WORK_ID, WORK_TYPE, INITIAL_FAVORITE, COUNTRY_CODE)

            val expected = GetWorkDetailsUseCase.WorkDetailsDomainWrapper(
                isFavorite = true,
                videos = VIDEO_LIST,
                casts = CAST_LIST,
                crew = emptyList(),
                recommended = WORK_PAGE,
                similar = WORK_PAGE,
                reviews = REVIEW_PAGE,
                watchProviders = WATCH_PROVIDERS,
                metadata = WorkMetadataDomainModel(status = "Released"),
            )
            assertEquals(expected, result)
        }

    @Test
    fun `should retain successful sections when credits and similar works fail`() =
        runTest {
            val exception = RuntimeException("Test exception")

            whenever(checkFavoriteWorkUseCase(WORK_ID, WORK_TYPE))
                .thenReturn(true)
            whenever(getVideosUseCase(WorkDomainModel.Type.MOVIE, WORK_ID))
                .thenReturn(VIDEO_LIST)
            whenever(getCastsUseCase(WorkDomainModel.Type.MOVIE, WORK_ID))
                .thenThrow(exception)
            whenever(getRecommendationByWorkUseCase(WorkDomainModel.Type.MOVIE, WORK_ID, 1))
                .thenReturn(WORK_PAGE)
            whenever(getSimilarByWorkUseCase(WorkDomainModel.Type.MOVIE, WORK_ID, 1))
                .thenThrow(exception)
            whenever(getReviewByWorkUseCase(WorkDomainModel.Type.MOVIE, WORK_ID, 1))
                .thenReturn(REVIEW_PAGE)
            whenever(getWatchProvidersUseCase(WorkDomainModel.Type.MOVIE, WORK_ID, COUNTRY_CODE))
                .thenReturn(WATCH_PROVIDERS)

            val result = useCase(WORK_ID, WORK_TYPE, INITIAL_FAVORITE, COUNTRY_CODE)

            assertEquals(true, result.isFavorite)
            assertEquals(VIDEO_LIST, result.videos)
            assertEquals(null, result.casts)
            assertEquals(null, result.crew)
            assertEquals(WORK_PAGE, result.recommended)
            assertEquals(PageDomainModel<WorkDomainModel>(1, 0, emptyList()), result.similar)
            assertEquals(REVIEW_PAGE, result.reviews)
            assertEquals(WATCH_PROVIDERS, result.watchProviders)
        }

    @Test
    fun `should return null watch providers when watch providers fails`() =
        runTest {
            val exception = RuntimeException("Watch providers error")

            whenever(checkFavoriteWorkUseCase(WORK_ID, WORK_TYPE))
                .thenReturn(true)
            whenever(getVideosUseCase(WorkDomainModel.Type.MOVIE, WORK_ID))
                .thenReturn(VIDEO_LIST)
            whenever(getCastsUseCase(WorkDomainModel.Type.MOVIE, WORK_ID))
                .thenReturn(WORK_CREDITS)
            whenever(getRecommendationByWorkUseCase(WorkDomainModel.Type.MOVIE, WORK_ID, 1))
                .thenReturn(WORK_PAGE)
            whenever(getSimilarByWorkUseCase(WorkDomainModel.Type.MOVIE, WORK_ID, 1))
                .thenReturn(WORK_PAGE)
            whenever(getReviewByWorkUseCase(WorkDomainModel.Type.MOVIE, WORK_ID, 1))
                .thenReturn(REVIEW_PAGE)
            whenever(getWatchProvidersUseCase(WorkDomainModel.Type.MOVIE, WORK_ID, COUNTRY_CODE))
                .thenThrow(exception)
            whenever(getWorkMetadataUseCase(WorkDomainModel.Type.MOVIE, WORK_ID))
                .thenReturn(WorkMetadataDomainModel())

            val result = useCase(WORK_ID, WORK_TYPE, INITIAL_FAVORITE, COUNTRY_CODE)

            val expected = GetWorkDetailsUseCase.WorkDetailsDomainWrapper(
                isFavorite = true,
                videos = VIDEO_LIST,
                casts = CAST_LIST,
                crew = emptyList(),
                recommended = WORK_PAGE,
                similar = WORK_PAGE,
                reviews = REVIEW_PAGE,
                watchProviders = null,
                metadata = WorkMetadataDomainModel(),
            )
            assertEquals(expected, result)
        }

    @Test
    fun `should retain successful sections when videos recommendations and reviews fail`() =
        runTest {
            stubSuccessfulRequests()
            whenever(getVideosUseCase(WORK_TYPE, WORK_ID)).thenThrow(RuntimeException())
            whenever(getRecommendationByWorkUseCase(WORK_TYPE, WORK_ID, 1)).thenThrow(RuntimeException())
            whenever(getReviewByWorkUseCase(WORK_TYPE, WORK_ID, 1)).thenThrow(RuntimeException())
            whenever(getCastsUseCase(WORK_TYPE, WORK_ID)).doSuspendableAnswer {
                delay(100)
                WORK_CREDITS
            }

            val result = useCase(WORK_ID, WORK_TYPE, INITIAL_FAVORITE, COUNTRY_CODE)

            assertEquals(null, result.videos)
            assertEquals(PageDomainModel<WorkDomainModel>(1, 0, emptyList()), result.recommended)
            assertEquals(PageDomainModel<ReviewDomainModel>(1, 0, emptyList()), result.reviews)
            assertEquals(CAST_LIST, result.casts)
            assertEquals(WORK_PAGE, result.similar)
            assertEquals(WATCH_PROVIDERS, result.watchProviders)
            assertEquals(WorkMetadataDomainModel(), result.metadata)
        }

    @Test
    fun `should retain known favorite status when favorite lookup fails`() =
        runTest {
            stubSuccessfulRequests()
            val favoriteWork = true
            whenever(checkFavoriteWorkUseCase(WORK_ID, WORK_TYPE)).thenThrow(RuntimeException())

            val result = useCase(WORK_ID, WORK_TYPE, favoriteWork, COUNTRY_CODE)

            assertEquals(true, result.isFavorite)
            assertEquals(VIDEO_LIST, result.videos)
        }

    @Test
    fun `should throw the videos error when all remote requests fail despite a successful favorite lookup`() =
        runTest {
            val exception = IllegalStateException("Videos unavailable")
            stubFailedRequests(videosException = exception, videosDelayMillis = 100)
            doReturn(true).whenever(checkFavoriteWorkUseCase).invoke(WORK_ID, WORK_TYPE)

            val thrown = assertFailsWith<IllegalStateException> {
                useCase(
                    WORK_ID,
                    WORK_TYPE,
                    INITIAL_FAVORITE,
                    COUNTRY_CODE
                )
            }

            assertEquals(exception.message, thrown.message)
        }

    @Test
    fun `should throw the videos error when every request including favorites fails`() =
        runTest {
            val exception = IllegalStateException("Videos unavailable")
            stubFailedRequests(videosException = exception)

            val thrown = assertFailsWith<IllegalStateException> {
                useCase(
                    WORK_ID,
                    WORK_TYPE,
                    INITIAL_FAVORITE,
                    COUNTRY_CODE
                )
            }

            assertEquals(exception.message, thrown.message)
        }

    @Test
    fun `should load details when only videos succeed with an empty list`() =
        runTest {
            stubFailedRequests()
            doReturn(emptyList<VideoDomainModel>()).whenever(getVideosUseCase).invoke(WORK_TYPE, WORK_ID)

            assertEquals(emptyList<VideoDomainModel>(), useCase(WORK_ID, WORK_TYPE, INITIAL_FAVORITE, COUNTRY_CODE).videos)
        }

    @Test
    fun `should load details when only videos succeed with null`() =
        runTest {
            stubFailedRequests()
            doReturn(null).whenever(getVideosUseCase).invoke(WORK_TYPE, WORK_ID)

            assertEquals(null, useCase(WORK_ID, WORK_TYPE, INITIAL_FAVORITE, COUNTRY_CODE).videos)
        }

    @Test
    fun `should load details when only credits succeed`() =
        runTest {
            stubFailedRequests()
            doReturn(WORK_CREDITS).whenever(getCastsUseCase).invoke(WORK_TYPE, WORK_ID)

            val result = useCase(WORK_ID, WORK_TYPE, INITIAL_FAVORITE, COUNTRY_CODE)

            assertEquals(CAST_LIST, result.casts)
            assertEquals(WORK_CREDITS.crew, result.crew)
        }

    @Test
    fun `should load details when only recommendations succeed`() =
        runTest {
            stubFailedRequests()
            doReturn(WORK_PAGE).whenever(getRecommendationByWorkUseCase).invoke(WORK_TYPE, WORK_ID, 1)

            assertEquals(WORK_PAGE, useCase(WORK_ID, WORK_TYPE, INITIAL_FAVORITE, COUNTRY_CODE).recommended)
        }

    @Test
    fun `should load details when only similar works succeed`() =
        runTest {
            stubFailedRequests()
            doReturn(WORK_PAGE).whenever(getSimilarByWorkUseCase).invoke(WORK_TYPE, WORK_ID, 1)

            assertEquals(WORK_PAGE, useCase(WORK_ID, WORK_TYPE, INITIAL_FAVORITE, COUNTRY_CODE).similar)
        }

    @Test
    fun `should load details when only reviews succeed`() =
        runTest {
            stubFailedRequests()
            doReturn(REVIEW_PAGE).whenever(getReviewByWorkUseCase).invoke(WORK_TYPE, WORK_ID, 1)

            assertEquals(REVIEW_PAGE, useCase(WORK_ID, WORK_TYPE, INITIAL_FAVORITE, COUNTRY_CODE).reviews)
        }

    @Test
    fun `should load details when only watch providers succeed`() =
        runTest {
            stubFailedRequests()
            doReturn(WATCH_PROVIDERS).whenever(getWatchProvidersUseCase).invoke(WORK_TYPE, WORK_ID, COUNTRY_CODE)

            assertEquals(WATCH_PROVIDERS, useCase(WORK_ID, WORK_TYPE, INITIAL_FAVORITE, COUNTRY_CODE).watchProviders)
        }

    @Test
    fun `should load details when only watch providers succeed with null`() =
        runTest {
            stubFailedRequests()
            doReturn(null).whenever(getWatchProvidersUseCase).invoke(WORK_TYPE, WORK_ID, COUNTRY_CODE)

            assertEquals(null, useCase(WORK_ID, WORK_TYPE, INITIAL_FAVORITE, COUNTRY_CODE).watchProviders)
        }

    @Test
    fun `should load details when only metadata succeeds`() =
        runTest {
            stubFailedRequests()
            val metadata = WorkMetadataDomainModel()
            doReturn(metadata).whenever(getWorkMetadataUseCase).invoke(WORK_TYPE, WORK_ID)

            assertEquals(metadata, useCase(WORK_ID, WORK_TYPE, INITIAL_FAVORITE, COUNTRY_CODE).metadata)
        }

    @Test
    fun `should keep favorite status when the lookup throws cancellation`() =
        runTest {
            stubSuccessfulRequests()
            whenever(checkFavoriteWorkUseCase(WORK_ID, WORK_TYPE)).thenThrow(CancellationException())

            val result = useCase(WORK_ID, WORK_TYPE, INITIAL_FAVORITE, COUNTRY_CODE)

            assertEquals(INITIAL_FAVORITE, result.isFavorite)
            assertEquals(VIDEO_LIST, result.videos)
        }

    @Test
    fun `should retain other details when credits throw cancellation`() =
        runTest {
            stubSuccessfulRequests()
            whenever(getCastsUseCase(WORK_TYPE, WORK_ID)).thenThrow(CancellationException())

            val result = useCase(WORK_ID, WORK_TYPE, INITIAL_FAVORITE, COUNTRY_CODE)

            assertEquals(null, result.casts)
            assertEquals(null, result.crew)
            assertEquals(VIDEO_LIST, result.videos)
        }

    @Test
    fun `should retain other details when watch providers throw cancellation`() =
        runTest {
            stubSuccessfulRequests()
            whenever(getWatchProvidersUseCase(WORK_TYPE, WORK_ID, COUNTRY_CODE)).thenThrow(CancellationException())

            val result = useCase(WORK_ID, WORK_TYPE, INITIAL_FAVORITE, COUNTRY_CODE)

            assertEquals(null, result.watchProviders)
            assertEquals(VIDEO_LIST, result.videos)
        }

    @Test
    fun `should retain other details when metadata throws cancellation`() =
        runTest {
            stubSuccessfulRequests()
            whenever(getWorkMetadataUseCase(WORK_TYPE, WORK_ID)).thenThrow(CancellationException())

            val result = useCase(WORK_ID, WORK_TYPE, INITIAL_FAVORITE, COUNTRY_CODE)

            assertEquals(null, result.metadata)
            assertEquals(VIDEO_LIST, result.videos)
        }

    private suspend fun stubFailedRequests(
        videosException: RuntimeException = RuntimeException("Videos unavailable"),
        videosDelayMillis: Long = 0,
    ) {
        whenever(checkFavoriteWorkUseCase(WORK_ID, WORK_TYPE)).thenThrow(RuntimeException("Favorites unavailable"))
        whenever(getVideosUseCase(WORK_TYPE, WORK_ID)).doSuspendableAnswer {
            delay(videosDelayMillis)
            throw videosException
        }
        whenever(getCastsUseCase(WORK_TYPE, WORK_ID)).thenThrow(RuntimeException("Credits unavailable"))
        whenever(getRecommendationByWorkUseCase(WORK_TYPE, WORK_ID, 1))
            .thenThrow(RuntimeException("Recommendations unavailable"))
        whenever(getSimilarByWorkUseCase(WORK_TYPE, WORK_ID, 1)).thenThrow(RuntimeException("Similar works unavailable"))
        whenever(getReviewByWorkUseCase(WORK_TYPE, WORK_ID, 1)).thenThrow(RuntimeException("Reviews unavailable"))
        whenever(getWatchProvidersUseCase(WORK_TYPE, WORK_ID, COUNTRY_CODE))
            .thenThrow(RuntimeException("Watch providers unavailable"))
        whenever(getWorkMetadataUseCase(WORK_TYPE, WORK_ID)).thenThrow(RuntimeException("Metadata unavailable"))
    }

    private suspend fun stubSuccessfulRequests() {
        whenever(checkFavoriteWorkUseCase(WORK_ID, WORK_TYPE)).thenReturn(true)
        whenever(getVideosUseCase(WORK_TYPE, WORK_ID)).thenReturn(VIDEO_LIST)
        whenever(getCastsUseCase(WORK_TYPE, WORK_ID)).thenReturn(WORK_CREDITS)
        whenever(getRecommendationByWorkUseCase(WORK_TYPE, WORK_ID, 1)).thenReturn(WORK_PAGE)
        whenever(getSimilarByWorkUseCase(WORK_TYPE, WORK_ID, 1)).thenReturn(WORK_PAGE)
        whenever(getReviewByWorkUseCase(WORK_TYPE, WORK_ID, 1)).thenReturn(REVIEW_PAGE)
        whenever(getWatchProvidersUseCase(WORK_TYPE, WORK_ID, COUNTRY_CODE)).thenReturn(WATCH_PROVIDERS)
        whenever(getWorkMetadataUseCase(WORK_TYPE, WORK_ID)).thenReturn(WorkMetadataDomainModel())
    }

    @Test
    fun `should continue loading when metadata request fails`() =
        runTest {
            whenever(checkFavoriteWorkUseCase(WORK_ID, WORK_TYPE)).thenReturn(true)
            whenever(getVideosUseCase(WorkDomainModel.Type.MOVIE, WORK_ID)).thenReturn(VIDEO_LIST)
            whenever(getCastsUseCase(WorkDomainModel.Type.MOVIE, WORK_ID)).thenReturn(WORK_CREDITS)
            whenever(getRecommendationByWorkUseCase(WorkDomainModel.Type.MOVIE, WORK_ID, 1)).thenReturn(WORK_PAGE)
            whenever(getSimilarByWorkUseCase(WorkDomainModel.Type.MOVIE, WORK_ID, 1)).thenReturn(WORK_PAGE)
            whenever(getReviewByWorkUseCase(WorkDomainModel.Type.MOVIE, WORK_ID, 1)).thenReturn(REVIEW_PAGE)
            whenever(
                getWatchProvidersUseCase(WorkDomainModel.Type.MOVIE, WORK_ID, COUNTRY_CODE)
            ).thenReturn(WATCH_PROVIDERS)
            whenever(getWorkMetadataUseCase(WorkDomainModel.Type.MOVIE, WORK_ID))
                .thenThrow(RuntimeException("Metadata unavailable"))

            val result = useCase(WORK_ID, WORK_TYPE, INITIAL_FAVORITE, COUNTRY_CODE)

            assertEquals(VIDEO_LIST, result.videos)
            assertEquals(WATCH_PROVIDERS, result.watchProviders)
            assertEquals(null, result.metadata)
        }
}
