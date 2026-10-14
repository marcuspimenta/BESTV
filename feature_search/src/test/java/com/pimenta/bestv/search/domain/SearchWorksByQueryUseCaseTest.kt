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

package com.pimenta.bestv.search.domain

import com.pimenta.bestv.model.domain.PageDomainModel
import com.pimenta.bestv.model.domain.WorkDomainModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.mockito.kotlin.doSuspendableAnswer
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import kotlin.test.assertFailsWith

/**
 * Created by marcus on 24-05-2018.
 */
private const val QUERY = "Batman"
private const val QUERY_ENCODED = "Batman"
private val MOVIE_PAGE_VIEW_MODEL =
    PageDomainModel(
        page = 1,
        totalPages = 10,
        results = listOf(
            WorkDomainModel(
                id = 1,
                title = "Batman",
                originalTitle = "Batman",
                type = WorkDomainModel.Type.MOVIE,
            ),
        ),
    )

private val TV_SHOW_PAGE_VIEW_MODEL =
    PageDomainModel(
        page = 1,
        totalPages = 10,
        results = listOf(
            WorkDomainModel(
                id = 1,
                title = "Batman",
                originalTitle = "Batman",
                type = WorkDomainModel.Type.TV_SHOW,
            ),
        ),
    )

class SearchWorksByQueryUseCaseTest {
    private val urlEncoderTextUseCase: UrlEncoderTextUseCase = mock()
    private val searchMoviesByQueryUseCase: SearchMoviesByQueryUseCase = mock()
    private val searchTvShowsByQueryUseCase: SearchTvShowsByQueryUseCase = mock()
    private val useCase =
        SearchWorksByQueryUseCase(
            urlEncoderTextUseCase,
            searchMoviesByQueryUseCase,
            searchTvShowsByQueryUseCase,
        )

    @Test
    fun `should return the right data when searching works by query`() =
        runTest {
            val expected = MOVIE_PAGE_VIEW_MODEL to TV_SHOW_PAGE_VIEW_MODEL

            whenever(urlEncoderTextUseCase(QUERY)).thenReturn(QUERY_ENCODED)
            whenever(searchMoviesByQueryUseCase(QUERY, 1)).thenReturn(MOVIE_PAGE_VIEW_MODEL)
            whenever(searchTvShowsByQueryUseCase(QUERY, 1)).thenReturn(TV_SHOW_PAGE_VIEW_MODEL)

            val result = useCase(QUERY)
            assertEquals(result, expected)
        }

    @Test
    fun `should return tv shows when the movie search fails`() =
        runTest {
            whenever(urlEncoderTextUseCase(QUERY)).thenReturn(QUERY_ENCODED)
            whenever(searchMoviesByQueryUseCase(QUERY_ENCODED, 1)).thenThrow(RuntimeException("Movie search failed"))
            whenever(searchTvShowsByQueryUseCase(QUERY_ENCODED, 1)).doSuspendableAnswer {
                delay(100)
                TV_SHOW_PAGE_VIEW_MODEL
            }

            val result = useCase(QUERY)

            assertEquals(PageDomainModel<WorkDomainModel>(1, 0, emptyList()), result.first)
            assertEquals(TV_SHOW_PAGE_VIEW_MODEL, result.second)
        }

    @Test
    fun `should return movies when the tv show search fails`() =
        runTest {
            whenever(urlEncoderTextUseCase(QUERY)).thenReturn(QUERY_ENCODED)
            whenever(searchMoviesByQueryUseCase(QUERY_ENCODED, 1)).doSuspendableAnswer {
                delay(100)
                MOVIE_PAGE_VIEW_MODEL
            }
            whenever(searchTvShowsByQueryUseCase(QUERY_ENCODED, 1)).thenThrow(RuntimeException("TV search failed"))

            val result = useCase(QUERY)

            assertEquals(MOVIE_PAGE_VIEW_MODEL, result.first)
            assertEquals(PageDomainModel<WorkDomainModel>(1, 0, emptyList()), result.second)
        }

    @Test
    fun `should propagate movie search cancellation`() =
        runTest {
            whenever(urlEncoderTextUseCase(QUERY)).thenReturn(QUERY_ENCODED)
            whenever(searchMoviesByQueryUseCase(QUERY_ENCODED, 1)).thenThrow(CancellationException())
            whenever(searchTvShowsByQueryUseCase(QUERY_ENCODED, 1)).thenReturn(TV_SHOW_PAGE_VIEW_MODEL)

            assertFailsWith<CancellationException> { useCase(QUERY) }
        }

    @Test
    fun `should propagate tv show search cancellation`() =
        runTest {
            whenever(urlEncoderTextUseCase(QUERY)).thenReturn(QUERY_ENCODED)
            whenever(searchMoviesByQueryUseCase(QUERY_ENCODED, 1)).thenReturn(MOVIE_PAGE_VIEW_MODEL)
            whenever(searchTvShowsByQueryUseCase(QUERY_ENCODED, 1)).thenThrow(CancellationException())

            assertFailsWith<CancellationException> { useCase(QUERY) }
        }

    @Test
    fun `should cancel both searches when the caller is cancelled`() =
        runTest {
            val moviesStarted = CompletableDeferred<Unit>()
            val tvShowsStarted = CompletableDeferred<Unit>()
            var moviesCancelled = false
            var tvShowsCancelled = false
            whenever(urlEncoderTextUseCase(QUERY)).thenReturn(QUERY_ENCODED)
            whenever(searchMoviesByQueryUseCase(QUERY_ENCODED, 1)).doSuspendableAnswer {
                moviesStarted.complete(Unit)
                try {
                    awaitCancellation()
                } finally {
                    moviesCancelled = true
                }
            }
            whenever(searchTvShowsByQueryUseCase(QUERY_ENCODED, 1)).doSuspendableAnswer {
                tvShowsStarted.complete(Unit)
                try {
                    awaitCancellation()
                } finally {
                    tvShowsCancelled = true
                }
            }

            val search = launch { useCase(QUERY) }
            moviesStarted.await()
            tvShowsStarted.await()
            search.cancelAndJoin()

            assertTrue(moviesCancelled)
            assertTrue(tvShowsCancelled)
        }

    @Test
    fun `should return an error when both searches fail`() =
        runTest {
            whenever(urlEncoderTextUseCase(QUERY)).thenReturn(QUERY_ENCODED)
            whenever(searchMoviesByQueryUseCase(QUERY, 1)).thenThrow(RuntimeException())
            whenever(searchTvShowsByQueryUseCase(QUERY, 1)).thenThrow(RuntimeException())

            assertFailsWith<RuntimeException> {
                useCase(QUERY)
            }
        }
}
