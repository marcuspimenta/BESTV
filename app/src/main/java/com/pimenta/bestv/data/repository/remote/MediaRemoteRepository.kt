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

package com.pimenta.bestv.data.repository.remote

import com.pimenta.bestv.data.entity.*
import io.reactivex.Single

/**
 * Created by marcus on 08-02-2018.
 */
interface MediaRemoteRepository {

    fun getMovieGenres(): Single<MovieGenreList>

    fun getTvShowGenres(): Single<TvShowGenreList>

    fun getMoviesByGenre(genre: Genre, page: Int): Single<MoviePage>

    fun getMovie(movieId: Int): Movie?

    fun getCastByMovie(movie: Movie): Single<CastList>

    fun getRecommendationByMovie(movie: Movie, page: Int): Single<MoviePage>

    fun getSimilarByMovie(movie: Movie, page: Int): Single<MoviePage>

    fun getVideosByMovie(movie: Movie): Single<VideoList>

    fun getNowPlayingMovies(page: Int): Single<MoviePage>

    fun getPopularMovies(page: Int): Single<MoviePage>

    fun getTopRatedMovies(page: Int): Single<MoviePage>

    fun getUpComingMovies(page: Int): Single<MoviePage>

    fun searchMoviesByQuery(query: String, page: Int): Single<MoviePage>

    fun getCastDetails(cast: Cast): Single<Cast>

    fun getMovieCreditsByCast(cast: Cast): Single<CastMovieList>

    fun getTvShowCreditsByCast(cast: Cast): Single<CastTvShowList>

    fun getTvShowByGenre(genre: Genre, page: Int): Single<TvShowPage>

    fun getAiringTodayTvShows(page: Int): Single<TvShowPage>

    fun getOnTheAirTvShows(page: Int): Single<TvShowPage>

    fun getPopularTvShows(page: Int): Single<TvShowPage>

    fun getTopRatedTvShows(page: Int): Single<TvShowPage>

    fun getTvShow(tvId: Int): TvShow?

    fun getCastByTvShow(tvShow: TvShow): Single<CastList>

    fun getRecommendationByTvShow(tvShow: TvShow, page: Int): Single<TvShowPage>

    fun getSimilarByTvShow(tvShow: TvShow, page: Int): Single<TvShowPage>

    fun getVideosByTvShow(tvShow: TvShow): Single<VideoList>

    fun searchTvShowsByQuery(query: String, page: Int): Single<TvShowPage>
}