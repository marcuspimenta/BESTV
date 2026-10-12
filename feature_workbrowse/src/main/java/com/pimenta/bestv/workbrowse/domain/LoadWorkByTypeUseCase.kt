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

import com.pimenta.bestv.workbrowse.domain.model.BrowseCategoryKey

/**
 * Created by marcus on 23-08-2019.
 */
class LoadWorkByTypeUseCase(
    private val getFavoritesUseCase: GetFavoritesUseCase,
    private val getNowPlayingMoviesUseCase: GetNowPlayingMoviesUseCase,
    private val getPopularMoviesUseCase: GetPopularMoviesUseCase,
    private val getTopRatedMoviesUseCase: GetTopRatedMoviesUseCase,
    private val getUpComingMoviesUseCase: GetUpComingMoviesUseCase,
    private val getAiringTodayTvShowsUseCase: GetAiringTodayTvShowsUseCase,
    private val getOnTheAirTvShowsUseCase: GetOnTheAirTvShowsUseCase,
    private val getPopularTvShowsUseCase: GetPopularTvShowsUseCase,
    private val getTopRatedTvShowsUseCase: GetTopRatedTvShowsUseCase,
) {
    suspend operator fun invoke(
        page: Int,
        category: BrowseCategoryKey,
    ) = when (category) {
        BrowseCategoryKey.FAVORITES_MOVIES -> getFavoritesUseCase()
        BrowseCategoryKey.NOW_PLAYING_MOVIES -> getNowPlayingMoviesUseCase(page)
        BrowseCategoryKey.POPULAR_MOVIES -> getPopularMoviesUseCase(page)
        BrowseCategoryKey.TOP_RATED_MOVIES -> getTopRatedMoviesUseCase(page)
        BrowseCategoryKey.UP_COMING_MOVIES -> getUpComingMoviesUseCase(page)
        BrowseCategoryKey.AIRING_TODAY_TV_SHOWS -> getAiringTodayTvShowsUseCase(page)
        BrowseCategoryKey.ON_THE_AIR_TV_SHOWS -> getOnTheAirTvShowsUseCase(page)
        BrowseCategoryKey.POPULAR_TV_SHOWS -> getPopularTvShowsUseCase(page)
        BrowseCategoryKey.TOP_RATED_TV_SHOWS -> getTopRatedTvShowsUseCase(page)
    }
}
