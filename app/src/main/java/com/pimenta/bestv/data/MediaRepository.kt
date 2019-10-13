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

import com.pimenta.bestv.R
import com.pimenta.bestv.common.presentation.model.WorkViewModel
import com.pimenta.bestv.data.local.entity.MovieDbModel
import com.pimenta.bestv.data.local.entity.TvShowDbModel
import com.pimenta.bestv.data.remote.entity.*
import io.reactivex.Completable
import io.reactivex.Single

/**
 * Created by marcus on 05-03-2018.
 */
interface MediaRepository {

    fun hasFavorite(): Single<Boolean>

    fun isFavoriteMovie(movieId: Int): Single<Boolean>

    fun isFavoriteTvShow(tvShowId: Int): Single<Boolean>

    fun saveFavoriteMovie(movieDbModel: MovieDbModel): Completable

    fun saveFavoriteTvShow(tvShowDbModel: TvShowDbModel): Completable

    fun deleteFavoriteMovie(movieDbModel: MovieDbModel): Completable

    fun deleteFavoriteTvShow(tvShowDbModel: TvShowDbModel): Completable

    fun getFavorites(): Single<List<WorkResponse>>

    fun getMovieGenres(): Single<MovieGenreListResponse>

    fun getTvShowGenres(): Single<TvShowGenreListResponse>

    fun loadWorkByType(page: Int, movieListType: WorkType): Single<out WorkPageResponse<*>>

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

    fun searchMoviesByQuery(query: String, page: Int): Single<MoviePageResponse>

    fun searchTvShowsByQuery(query: String, page: Int): Single<TvShowPageResponse>

    fun getCastDetails(castId: Int): Single<CastResponse>

    fun getMovieCreditsByCast(castId: Int): Single<CastMovieListResponse>

    fun getTvShowCreditsByCast(castId: Int): Single<CastTvShowListResponse>

    fun loadRecommendations(works: List<WorkViewModel>?): Completable

    enum class WorkType(val resource: Int) {
        FAVORITES_MOVIES(R.string.favorites),
        NOW_PLAYING_MOVIES(R.string.now_playing),
        POPULAR_MOVIES(R.string.popular),
        TOP_RATED_MOVIES(R.string.top_rated),
        UP_COMING_MOVIES(R.string.up_coming),
        AIRING_TODAY_TV_SHOWS(R.string.airing_today),
        ON_THE_AIR_TV_SHOWS(R.string.on_the_air),
        POPULAR_TV_SHOWS(R.string.popular),
        TOP_RATED_TV_SHOWS(R.string.top_rated);
    }
}