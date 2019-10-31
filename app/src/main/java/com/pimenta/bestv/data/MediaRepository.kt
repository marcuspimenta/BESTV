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

package com.pimenta.bestv.data

import com.pimenta.bestv.common.data.model.local.MovieDbModel
import com.pimenta.bestv.common.data.model.local.TvShowDbModel
import com.pimenta.bestv.common.data.model.remote.*
import com.pimenta.bestv.common.presentation.model.WorkViewModel
import io.reactivex.Completable
import io.reactivex.Single

/**
 * Created by marcus on 05-03-2018.
 */
interface MediaRepository {

    fun isFavoriteMovie(movieId: Int): Single<Boolean>

    fun isFavoriteTvShow(tvShowId: Int): Single<Boolean>

    fun saveFavoriteMovie(movieDbModel: MovieDbModel): Completable

    fun saveFavoriteTvShow(tvShowDbModel: TvShowDbModel): Completable

    fun deleteFavoriteMovie(movieDbModel: MovieDbModel): Completable

    fun deleteFavoriteTvShow(tvShowDbModel: TvShowDbModel): Completable

    fun getFavoriteMovies(): Single<List<MovieResponse>>

    fun getFavoriteTvShows(): Single<List<TvShowResponse>>

    fun getMovieGenres(): Single<MovieGenreListResponse>

    fun getTvShowGenres(): Single<TvShowGenreListResponse>

    fun getMovieByGenre(genreId: Int, page: Int): Single<MoviePageResponse>

    fun getTvShowByGenre(genreId: Int, page: Int): Single<TvShowPageResponse>

    fun getCastByMovie(workId: Int): Single<CastListResponse>

    fun getCastByTvShow(workId: Int): Single<CastListResponse>

    fun getRecommendationByMovie(workId: Int, page: Int): Single<MoviePageResponse>

    fun getRecommendationByTvShow(workId: Int, page: Int): Single<TvShowPageResponse>

    fun getSimilarByMovie(workId: Int, page: Int): Single<MoviePageResponse>

    fun getSimilarByTvShow(workId: Int, page: Int): Single<TvShowPageResponse>

    fun getVideosByMovie(workId: Int): Single<VideoListResponse>

    fun getVideosByTvShow(workId: Int): Single<VideoListResponse>

    fun loadRecommendations(works: List<WorkViewModel>?): Completable

    fun getNowPlayingMovies(page: Int): Single<MoviePageResponse>

    fun getPopularMovies(page: Int): Single<MoviePageResponse>

    fun getTopRatedMovies(page: Int): Single<MoviePageResponse>

    fun getUpComingMovies(page: Int): Single<MoviePageResponse>

    fun getAiringTodayTvShows(page: Int): Single<TvShowPageResponse>

    fun getOnTheAirTvShows(page: Int): Single<TvShowPageResponse>

    fun getPopularTvShows(page: Int): Single<TvShowPageResponse>

    fun getTopRatedTvShows(page: Int): Single<TvShowPageResponse>
}