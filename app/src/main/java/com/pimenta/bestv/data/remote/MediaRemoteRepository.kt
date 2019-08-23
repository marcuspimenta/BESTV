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

package com.pimenta.bestv.data.remote

import com.pimenta.bestv.data.remote.entity.*
import io.reactivex.Single

/**
 * Created by marcus on 08-02-2018.
 */
interface MediaRemoteRepository {

    fun getMovieGenres(): Single<MovieGenreListResponse>

    fun getTvShowGenres(): Single<TvShowGenreListResponse>

    fun getMoviesByGenre(genreId: Int, page: Int): Single<MoviePageResponse>

    fun getMovie(movieId: Int): MovieResponse?

    fun getCastByMovie(movieId: Int): Single<CastListResponse>

    fun getRecommendationByMovie(movieId: Int, page: Int): Single<MoviePageResponse>

    fun getSimilarByMovie(movieId: Int, page: Int): Single<MoviePageResponse>

    fun getVideosByMovie(movieId: Int): Single<VideoListResponse>

    fun getNowPlayingMovies(page: Int): Single<MoviePageResponse>

    fun getPopularMovies(page: Int): Single<MoviePageResponse>

    fun getTopRatedMovies(page: Int): Single<MoviePageResponse>

    fun getUpComingMovies(page: Int): Single<MoviePageResponse>

    fun searchMoviesByQuery(query: String, page: Int): Single<MoviePageResponse>

    fun getCastDetails(castId: Int): Single<CastResponse>

    fun getMovieCreditsByCast(castId: Int): Single<CastMovieListResponse>

    fun getTvShowCreditsByCast(castId: Int): Single<CastTvShowListResponse>

    fun getTvShowByGenre(genreId: Int, page: Int): Single<TvShowPageResponse>

    fun getAiringTodayTvShows(page: Int): Single<TvShowPageResponse>

    fun getOnTheAirTvShows(page: Int): Single<TvShowPageResponse>

    fun getPopularTvShows(page: Int): Single<TvShowPageResponse>

    fun getTopRatedTvShows(page: Int): Single<TvShowPageResponse>

    fun getTvShow(tvId: Int): TvShowResponse?

    fun getCastByTvShow(tvShowId: Int): Single<CastListResponse>

    fun getRecommendationByTvShow(tvShowId: Int, page: Int): Single<TvShowPageResponse>

    fun getSimilarByTvShow(tvShowId: Int, page: Int): Single<TvShowPageResponse>

    fun getVideosByTvShow(tvShowId: Int): Single<VideoListResponse>

    fun searchTvShowsByQuery(query: String, page: Int): Single<TvShowPageResponse>
}