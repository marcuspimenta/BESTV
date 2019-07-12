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

package com.pimenta.bestv.data.repository

import com.pimenta.bestv.R
import com.pimenta.bestv.data.entity.*
import io.reactivex.Completable
import io.reactivex.Single

/**
 * Created by marcus on 05-03-2018.
 */
interface MediaRepository {

    fun getFavorites(): Single<List<Work>>

    fun getMovieGenres(): Single<MovieGenreList>

    fun getTvShowGenres(): Single<TvShowGenreList>

    fun isFavorite(work: Work): Single<Boolean>

    fun hasFavorite(): Single<Boolean>

    fun saveFavorite(work: Work): Completable

    fun deleteFavorite(work: Work): Completable

    fun loadWorkByType(page: Int, movieListType: WorkType): Single<out WorkPage<*>>

    fun getWorkByGenre(genre: Genre, page: Int): Single<out WorkPage<*>>

    fun getCastByWork(work: Work): Single<CastList>

    fun getRecommendationByWork(work: Work, page: Int): Single<out WorkPage<*>>

    fun getSimilarByWork(work: Work, page: Int): Single<out WorkPage<*>>

    fun getVideosByWork(work: Work): Single<VideoList>

    fun searchMoviesByQuery(query: String, page: Int): Single<MoviePage>

    fun searchTvShowsByQuery(query: String, page: Int): Single<TvShowPage>

    fun getCastDetails(cast: Cast): Single<Cast>

    fun getMovieCreditsByCast(cast: Cast): Single<CastMovieList>

    fun getTvShowCreditsByCast(cast: Cast): Single<CastTvShowList>

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