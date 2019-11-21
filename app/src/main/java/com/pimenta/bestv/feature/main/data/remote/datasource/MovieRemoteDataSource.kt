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

package com.pimenta.bestv.feature.main.data.remote.datasource

import com.pimenta.bestv.BuildConfig
import com.pimenta.bestv.common.data.model.remote.MovieResponse
import com.pimenta.bestv.feature.main.data.remote.api.MovieTmdbApi
import timber.log.Timber
import java.io.IOException
import javax.inject.Inject

/**
 * Created by marcus on 20-10-2019.
 */
class MovieRemoteDataSource @Inject constructor(
    private val movieTmdbApi: MovieTmdbApi
) {

    fun getMovie(movieId: Int): MovieResponse? =
            try {
                movieTmdbApi.getMovie(movieId, BuildConfig.TMDB_API_KEY, BuildConfig.TMDB_FILTER_LANGUAGE).execute().body()
            } catch (e: IOException) {
                Timber.e(e, "Error while getting a movie")
                null
            }

    fun getMoviesByGenre(genreId: Int, page: Int) =
            movieTmdbApi.getMoviesByGenre(genreId, BuildConfig.TMDB_API_KEY, BuildConfig.TMDB_FILTER_LANGUAGE, false, page)

    fun getNowPlayingMovies(page: Int) =
            movieTmdbApi.getNowPlayingMovies(BuildConfig.TMDB_API_KEY, BuildConfig.TMDB_FILTER_LANGUAGE, page)

    fun getPopularMovies(page: Int) =
            movieTmdbApi.getPopularMovies(BuildConfig.TMDB_API_KEY, BuildConfig.TMDB_FILTER_LANGUAGE, page)

    fun getTopRatedMovies(page: Int) =
            movieTmdbApi.getTopRatedMovies(BuildConfig.TMDB_API_KEY, BuildConfig.TMDB_FILTER_LANGUAGE, page)

    fun getUpComingMovies(page: Int) =
            movieTmdbApi.getUpComingMovies(BuildConfig.TMDB_API_KEY, BuildConfig.TMDB_FILTER_LANGUAGE, page)
}