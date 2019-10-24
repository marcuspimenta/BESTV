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

import com.pimenta.bestv.data.MediaDataSource
import javax.inject.Inject

/**
 * Created by marcus on 23-08-2019.
 */
class LoadWorkByTypeUseCase @Inject constructor(
        private val mediaDataSource: MediaDataSource
) {

    operator fun invoke(page: Int, movieListType: MediaDataSource.WorkType) =
            mediaDataSource.loadWorkByType(page, movieListType)
                    .map { it.toViewModel() }

    /*override fun loadWorkByType(page: Int, movieListType: MediaDataSource.WorkType): Single<out WorkPageResponse<*>> =
            when (movieListType) {
                MediaDataSource.WorkType.NOW_PLAYING_MOVIES -> mediaRemoteRepository.getNowPlayingMovies(page)
                MediaDataSource.WorkType.POPULAR_MOVIES -> mediaRemoteRepository.getPopularMovies(page)
                MediaDataSource.WorkType.TOP_RATED_MOVIES -> mediaRemoteRepository.getTopRatedMovies(page)
                MediaDataSource.WorkType.UP_COMING_MOVIES -> mediaRemoteRepository.getUpComingMovies(page)
                MediaDataSource.WorkType.AIRING_TODAY_TV_SHOWS -> mediaRemoteRepository.getAiringTodayTvShows(page)
                MediaDataSource.WorkType.ON_THE_AIR_TV_SHOWS -> mediaRemoteRepository.getOnTheAirTvShows(page)
                MediaDataSource.WorkType.POPULAR_TV_SHOWS -> mediaRemoteRepository.getPopularTvShows(page)
                MediaDataSource.WorkType.TOP_RATED_TV_SHOWS -> mediaRemoteRepository.getTopRatedTvShows(page)
                else -> Single.error(Throwable())
            }*/
}