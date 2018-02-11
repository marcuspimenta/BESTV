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

package com.pimenta.bestv.api.tmdb;

import com.pimenta.bestv.models.MovieList;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by marcus on 11-02-2018.
 */
public interface MovieApi {

    @GET("movie/now_playing")
    Call<MovieList> getNowPlayingMovies(@Query("api_key") String apiKey, @Query("language") String language, @Query("region") String region);

    @GET("movie/popular")
    Call<MovieList> getPopularMovies(@Query("api_key") String apiKey, @Query("language") String language);

    @GET("movie/top_rated")
    Call<MovieList> getTopRatedMovies(@Query("api_key") String apiKey, @Query("language") String language);

    @GET("movie/upcoming")
    Call<MovieList> getUpComingMovies(@Query("api_key") String apiKey, @Query("language") String language);

}