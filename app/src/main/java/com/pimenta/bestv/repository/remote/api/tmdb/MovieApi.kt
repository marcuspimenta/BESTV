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

package com.pimenta.bestv.repository.remote.api.tmdb

import com.pimenta.bestv.repository.entity.CastList
import com.pimenta.bestv.repository.entity.Movie
import com.pimenta.bestv.repository.entity.MoviePage
import com.pimenta.bestv.repository.entity.VideoList

import io.reactivex.Single
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Created by marcus on 11-02-2018.
 */
interface MovieApi {

    @GET("discover/movie")
    fun getMoviesByGenre(
            @Query("with_genres") genreId: Int,
            @Query("api_key") apiKey: String,
            @Query("language") language: String,
            @Query("include_adult") includeAdult: Boolean,
            @Query("page") page: Int
    ): Single<MoviePage>

    @GET("movie/{movie_id}")
    fun getMovie(
            @Path("movie_id") movie_id: Int,
            @Query("api_key") apiKey: String,
            @Query("language") language: String
    ): Call<Movie>

    @GET("movie/{movie_id}/credits")
    fun getCastByMovie(
            @Path("movie_id") movie_id: Int,
            @Query("api_key") apiKey: String,
            @Query("language") language: String
    ): Single<CastList>

    @GET("movie/{movie_id}/recommendations")
    fun getRecommendationByMovie(
            @Path("movie_id") movie_id: Int,
            @Query("api_key") apiKey: String,
            @Query("language") language: String,
            @Query("page") page: Int
    ): Single<MoviePage>

    @GET("movie/{movie_id}/similar")
    fun getSimilarByMovie(
            @Path("movie_id") movie_id: Int,
            @Query("api_key") apiKey: String,
            @Query("language") language: String,
            @Query("page") page: Int
    ): Single<MoviePage>

    @GET("movie/{movie_id}/videos")
    fun getVideosByMovie(
            @Path("movie_id") movie_id: Int,
            @Query("api_key") apiKey: String,
            @Query("language") language: String
    ): Single<VideoList>

    @GET("movie/now_playing")
    fun getNowPlayingMovies(
            @Query("api_key") apiKey: String,
            @Query("language") language: String,
            @Query("page") page: Int
    ): Single<MoviePage>

    @GET("movie/popular")
    fun getPopularMovies(
            @Query("api_key") apiKey: String,
            @Query("language") language: String,
            @Query("page") page: Int
    ): Single<MoviePage>

    @GET("movie/top_rated")
    fun getTopRatedMovies(
            @Query("api_key") apiKey: String,
            @Query("language") language: String,
            @Query("page") page: Int
    ): Single<MoviePage>

    @GET("movie/upcoming")
    fun getUpComingMovies(
            @Query("api_key") apiKey: String,
            @Query("language") language: String,
            @Query("page") page: Int
    ): Single<MoviePage>

    @GET("search/movie")
    fun searchMoviesByQuery(
            @Query("api_key") apiKey: String,
            @Query("query") query: String,
            @Query("language") language: String,
            @Query("page") page: Int
    ): Single<MoviePage>

}