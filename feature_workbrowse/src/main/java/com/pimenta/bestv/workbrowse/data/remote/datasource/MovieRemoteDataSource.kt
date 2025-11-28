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

import com.pimenta.bestv.model.data.remote.MovieResponse
import com.pimenta.bestv.workbrowse.data.remote.api.MovieTmdbApi
import timber.log.Timber

/**
 * Created by marcus on 20-10-2019.
 */
class MovieRemoteDataSource(
    private val tmdbApiKey: String,
    private val tmdbFilterLanguage: String,
    private val movieTmdbApi: MovieTmdbApi
) {

    suspend fun getMovie(movieId: Int): MovieResponse? =
        try {
            movieTmdbApi.getMovie(movieId, tmdbApiKey, tmdbFilterLanguage)
        } catch (e: Exception) {
            Timber.e(e, "Error while getting a movie")
            null
        }

    suspend fun getMoviesByGenre(genreId: Int, page: Int) =
        movieTmdbApi.getMoviesByGenre(genreId, tmdbApiKey, tmdbFilterLanguage, false, page)

    suspend fun getNowPlayingMovies(page: Int) =
        movieTmdbApi.getNowPlayingMovies(tmdbApiKey, tmdbFilterLanguage, page)

    suspend fun getPopularMovies(page: Int) =
        movieTmdbApi.getPopularMovies(tmdbApiKey, tmdbFilterLanguage, page)

    suspend fun getTopRatedMovies(page: Int) =
        movieTmdbApi.getTopRatedMovies(tmdbApiKey, tmdbFilterLanguage, page)

    suspend fun getUpComingMovies(page: Int) =
        movieTmdbApi.getUpComingMovies(tmdbApiKey, tmdbFilterLanguage, page)
}
