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
import timber.log.Timber
import java.io.IOException
import javax.inject.Inject
import javax.inject.Named

/**
 * Created by marcus on 20-10-2019.
 */
class MovieRemoteDataSource @Inject constructor(
    @Named("tmdbApiKey") private val tmdbApiKey: String,
    @Named("tmdbFilterLanguage") private val tmdbFilterLanguage: String,
    private val movieTmdbApi: MovieTmdbApi
) {

    fun getMovie(movieId: Int) =
        try {
            movieTmdbApi.getMovie(movieId, tmdbApiKey, tmdbFilterLanguage).execute().body()
        } catch (e: IOException) {
            Timber.e(e, "Error while getting a movie")
            null
        }

    fun getMoviesByGenre(genreId: Int, page: Int) =
        movieTmdbApi.getMoviesByGenre(genreId, tmdbApiKey, tmdbFilterLanguage, false, page)

    fun getNowPlayingMovies(page: Int) =
        movieTmdbApi.getNowPlayingMovies(tmdbApiKey, tmdbFilterLanguage, page)

    fun getPopularMovies(page: Int) =
        movieTmdbApi.getPopularMovies(tmdbApiKey, tmdbFilterLanguage, page)

    fun getTopRatedMovies(page: Int) =
        movieTmdbApi.getTopRatedMovies(tmdbApiKey, tmdbFilterLanguage, page)

    fun getUpComingMovies(page: Int) =
        movieTmdbApi.getUpComingMovies(tmdbApiKey, tmdbFilterLanguage, page)
}
