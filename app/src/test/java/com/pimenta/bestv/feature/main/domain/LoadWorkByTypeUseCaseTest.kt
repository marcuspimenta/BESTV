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

package com.pimenta.bestv.feature.main.domain

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import com.pimenta.bestv.model.presentation.model.TopWorkTypeViewModel
import com.pimenta.bestv.model.presentation.model.WorkPageViewModel
import com.pimenta.bestv.model.presentation.model.WorkType
import com.pimenta.bestv.model.presentation.model.WorkViewModel
import io.reactivex.Single
import org.junit.Test

/**
 * Created by marcus on 2019-08-26.
 */
private val MOVIE_PAGE_VIEW_MODEL = WorkPageViewModel(
        page = 1,
        totalPages = 1,
        works = listOf(
                WorkViewModel(
                        id = 1,
                        title = "Batman",
                        originalTitle = "Batman",
                        type = WorkType.MOVIE
                )
        )
)

class LoadWorkByTypeUseCaseTest {

    private val getNowPlayingMoviesUseCase: GetNowPlayingMoviesUseCase = mock()
    private val getPopularMoviesUseCase: GetPopularMoviesUseCase = mock()
    private val getTopRatedMoviesUseCase: GetTopRatedMoviesUseCase = mock()
    private val getUpComingMoviesUseCase: GetUpComingMoviesUseCase = mock()
    private val getAiringTodayTvShowsUseCase: GetAiringTodayTvShowsUseCase = mock()
    private val getOnTheAirTvShowsUseCase: GetOnTheAirTvShowsUseCase = mock()
    private val getPopularTvShowsUseCase: GetPopularTvShowsUseCase = mock()
    private val getTopRatedTvShowsUseCase: GetTopRatedTvShowsUseCase = mock()

    private val useCase = LoadWorkByTypeUseCase(
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
    fun `should return the right data when loading the works by type`() {
        whenever(getNowPlayingMoviesUseCase(1)).thenReturn(Single.just(MOVIE_PAGE_VIEW_MODEL))

        useCase(1, TopWorkTypeViewModel.NOW_PLAYING_MOVIES)
                .test()
                .assertComplete()
                .assertResult(MOVIE_PAGE_VIEW_MODEL)
    }

    @Test
    fun `should return an error when some exception happens`() {
        whenever(getNowPlayingMoviesUseCase(1)).thenReturn(Single.error(Throwable()))

        useCase(1, TopWorkTypeViewModel.NOW_PLAYING_MOVIES)
                .test()
                .assertError(Throwable::class.java)
    }
}