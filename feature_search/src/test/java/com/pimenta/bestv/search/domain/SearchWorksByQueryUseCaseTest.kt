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

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import com.pimenta.bestv.model.domain.PageDomainModel
import com.pimenta.bestv.model.domain.WorkDomainModel
import io.reactivex.Single
import org.junit.Test

/**
 * Created by marcus on 24-05-2018.
 */
private const val QUERY = "Batman"
private const val QUERY_ENCODED = "Batman"
private val MOVIE_PAGE_VIEW_MODEL = PageDomainModel(
    page = 1,
    totalPages = 10,
    results = listOf(
        WorkDomainModel(
            id = 1,
            title = "Batman",
            originalTitle = "Batman",
            type = WorkDomainModel.Type.MOVIE
        )
    )
)

private val TV_SHOW_PAGE_VIEW_MODEL = PageDomainModel(
    page = 1,
    totalPages = 10,
    results = listOf(
        WorkDomainModel(
            id = 1,
            title = "Batman",
            originalTitle = "Batman",
            type = WorkDomainModel.Type.TV_SHOW
        )
    )
)

class SearchWorksByQueryUseCaseTest {

    private val urlEncoderTextUseCase: UrlEncoderTextUseCase = mock()
    private val searchMoviesByQueryUseCase: SearchMoviesByQueryUseCase = mock()
    private val searchTvShowsByQueryUseCase: SearchTvShowsByQueryUseCase = mock()
    private val useCase = SearchWorksByQueryUseCase(
        urlEncoderTextUseCase,
        searchMoviesByQueryUseCase,
        searchTvShowsByQueryUseCase
    )

    @Test
    fun `should return the right data when searching works by query`() {
        val result = MOVIE_PAGE_VIEW_MODEL to TV_SHOW_PAGE_VIEW_MODEL

        whenever(urlEncoderTextUseCase(QUERY)).thenReturn(Single.just(QUERY_ENCODED))
        whenever(searchMoviesByQueryUseCase(QUERY, 1)).thenReturn(Single.just(MOVIE_PAGE_VIEW_MODEL))
        whenever(searchTvShowsByQueryUseCase(QUERY, 1)).thenReturn(Single.just(TV_SHOW_PAGE_VIEW_MODEL))

        useCase(QUERY)
            .test()
            .assertComplete()
            .assertResult(result)
    }

    @Test
    fun `should return an error when some exception happens`() {
        whenever(urlEncoderTextUseCase(QUERY)).thenReturn(Single.just(QUERY_ENCODED))
        whenever(searchMoviesByQueryUseCase(QUERY, 1)).thenReturn(Single.error(Throwable()))
        whenever(searchTvShowsByQueryUseCase(QUERY, 1)).thenReturn(Single.error(Throwable()))

        useCase(QUERY)
            .test()
            .assertError(Throwable::class.java)
    }
}
