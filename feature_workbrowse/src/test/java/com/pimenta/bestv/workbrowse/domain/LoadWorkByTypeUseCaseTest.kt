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

package com.pimenta.bestv.workbrowse.domain

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import com.pimenta.bestv.model.domain.PageDomainModel
import com.pimenta.bestv.model.domain.WorkDomainModel
import com.pimenta.bestv.workbrowse.presentation.model.TopWorkTypeViewModel
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Test

/**
 * Created by marcus on 2019-08-26.
 */
private val MOVIE_PAGE_DOMAIN_MODEL = PageDomainModel(
    page = 1,
    totalPages = 1,
    results = listOf(
        WorkDomainModel(
            id = 1,
            title = "Batman",
            originalTitle = "Batman",
            type = WorkDomainModel.Type.MOVIE
        )
    )
)

class LoadWorkByTypeUseCaseTest {

    private val getFavoritesUseCase: GetFavoritesUseCase = mock()
    private val getNowPlayingMoviesUseCase: GetNowPlayingMoviesUseCase = mock()
    private val getPopularMoviesUseCase: GetPopularMoviesUseCase = mock()
    private val getTopRatedMoviesUseCase: GetTopRatedMoviesUseCase = mock()
    private val getUpComingMoviesUseCase: GetUpComingMoviesUseCase = mock()
    private val getAiringTodayTvShowsUseCase: GetAiringTodayTvShowsUseCase = mock()
    private val getOnTheAirTvShowsUseCase: GetOnTheAirTvShowsUseCase = mock()
    private val getPopularTvShowsUseCase: GetPopularTvShowsUseCase = mock()
    private val getTopRatedTvShowsUseCase: GetTopRatedTvShowsUseCase = mock()
    private val useCase = LoadWorkByTypeUseCase(
        getFavoritesUseCase,
        getNowPlayingMoviesUseCase,
        getPopularMoviesUseCase,
        getTopRatedMoviesUseCase,
        getUpComingMoviesUseCase,
        getAiringTodayTvShowsUseCase,
        getOnTheAirTvShowsUseCase,
        getPopularTvShowsUseCase,
        getTopRatedTvShowsUseCase
    )

    @Test
    fun `should return the right data when loading the works by type`() = runTest {
        whenever(getNowPlayingMoviesUseCase(1))
            .thenReturn(MOVIE_PAGE_DOMAIN_MODEL)

        val result = useCase(1, TopWorkTypeViewModel.NOW_PLAYING_MOVIES)

        Assert.assertEquals(MOVIE_PAGE_DOMAIN_MODEL, result)
    }

    @Test
    fun `should return an error when some exception happens`() = runTest {
        whenever(getNowPlayingMoviesUseCase(1))
            .thenThrow(RuntimeException())

        try {
            useCase(1, TopWorkTypeViewModel.NOW_PLAYING_MOVIES)
            Assert.fail("Expected exception")
        } catch (e: RuntimeException) {
            // Expected
        }
    }
}
