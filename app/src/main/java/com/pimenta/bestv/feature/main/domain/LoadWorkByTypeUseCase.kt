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

import com.pimenta.bestv.model.presentation.model.TopWorkTypeViewModel
import javax.inject.Inject

/**
 * Created by marcus on 23-08-2019.
 */
class LoadWorkByTypeUseCase @Inject constructor(
    private val getFavoritesUseCase: GetFavoritesUseCase,
    private val getNowPlayingMoviesUseCase: GetNowPlayingMoviesUseCase,
    private val getPopularMoviesUseCase: GetPopularMoviesUseCase,
    private val getTopRatedMoviesUseCase: GetTopRatedMoviesUseCase,
    private val getUpComingMoviesUseCase: GetUpComingMoviesUseCase,
    private val getAiringTodayTvShowsUseCase: GetAiringTodayTvShowsUseCase,
    private val getOnTheAirTvShowsUseCase: GetOnTheAirTvShowsUseCase,
    private val getPopularTvShowsUseCase: GetPopularTvShowsUseCase,
    private val getTopRatedTvShowsUseCase: GetTopRatedTvShowsUseCase
) {

    operator fun invoke(page: Int, topWorkTypeViewModel: TopWorkTypeViewModel) =
            when (topWorkTypeViewModel) {
                TopWorkTypeViewModel.FAVORITES_MOVIES -> getFavoritesUseCase()
                TopWorkTypeViewModel.NOW_PLAYING_MOVIES -> getNowPlayingMoviesUseCase(page)
                TopWorkTypeViewModel.POPULAR_MOVIES -> getPopularMoviesUseCase(page)
                TopWorkTypeViewModel.TOP_RATED_MOVIES -> getTopRatedMoviesUseCase(page)
                TopWorkTypeViewModel.UP_COMING_MOVIES -> getUpComingMoviesUseCase(page)
                TopWorkTypeViewModel.AIRING_TODAY_TV_SHOWS -> getAiringTodayTvShowsUseCase(page)
                TopWorkTypeViewModel.ON_THE_AIR_TV_SHOWS -> getOnTheAirTvShowsUseCase(page)
                TopWorkTypeViewModel.POPULAR_TV_SHOWS -> getPopularTvShowsUseCase(page)
                TopWorkTypeViewModel.TOP_RATED_TV_SHOWS -> getTopRatedTvShowsUseCase(page)
            }
}