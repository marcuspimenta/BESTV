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

package com.pimenta.bestv.workbrowse.data.remote.datasource

import com.pimenta.bestv.workbrowse.data.remote.api.MovieTmdbApi

/**
 * Created by marcus on 20-10-2019.
 */
class MovieRemoteDataSource(
    private val tmdbFilterLanguage: String,
    private val movieTmdbApi: MovieTmdbApi,
) {
    suspend fun getMoviesByGenre(
        genreId: Int,
        page: Int,
    ) = movieTmdbApi.getMoviesByGenre(genreId, tmdbFilterLanguage, false, page)

    suspend fun getNowPlayingMovies(page: Int) = movieTmdbApi.getNowPlayingMovies(tmdbFilterLanguage, page)

    suspend fun getPopularMovies(page: Int) = movieTmdbApi.getPopularMovies(tmdbFilterLanguage, page)

    suspend fun getTopRatedMovies(page: Int) = movieTmdbApi.getTopRatedMovies(tmdbFilterLanguage, page)

    suspend fun getUpComingMovies(page: Int) = movieTmdbApi.getUpComingMovies(tmdbFilterLanguage, page)
}
