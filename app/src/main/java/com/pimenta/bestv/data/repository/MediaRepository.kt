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

    /**
     * Gets the favorites [<]
     *
     * @return Favorite [&lt;][<]
     */
    fun getFavorites(): Single<List<Work>>

    /**
     * Gets the [MovieGenreList] available at TMDb
     *
     * @return [<]
     */
    fun getMovieGenres(): Single<MovieGenreList>

    /**
     * Gets the [TvShowGenreList] available at TMDb
     *
     * @return [<]
     */
    fun getTvShowGenres(): Single<TvShowGenreList>

    /**
     * Checks if the [Work] is favorite
     *
     * @param work [Work] to verify if is favorite
     *
     * @return [<]
     */
    fun isFavorite(work: Work): Single<Boolean>

    /**
     * Checks if there is any [Work] saved as favorite
     *
     * @return [<]
     */
    fun hasFavorite(): Single<Boolean>

    /**
     * Saves a [Work] as favorites
     *
     * @param work [Work] to be saved as favorite
     *
     * @return [<]
     */
    fun saveFavorite(work: Work): Completable

    /**
     * Deletes a [Work] from favorites
     *
     * @param work [Work] to be deleted from favorite
     *
     * @return [<]
     */
    fun deleteFavorite(work: Work): Completable

    /**
     * Loads the [MoviePage] by [WorkType]
     *
     * @param page          Page to be loaded
     * @param movieListType [WorkType]
     *
     * @return [<]
     */
    fun loadWorkByType(page: Int, movieListType: WorkType): Single<out WorkPage<*>>

    /**
     * Gets the [<] by the [Genre]
     *
     * @param genre [Genre] to search the [<]
     * @param page  Specify which page to query. Minimum: 1, maximum: 1000,
     * default: 1
     *
     * @return [?][<]
     */
    fun getWorkByGenre(genre: Genre, page: Int): Single<out WorkPage<*>>

    /**
     * Gets the [CastList] by the [Work]
     *
     * @param work [Work] to search the [<]
     *
     * @return [<]
     */
    fun getCastByWork(work: Work): Single<CastList>

    /**
     * Gets a list of recommended movies for a work.
     *
     * @param work [Work] to search the [<]
     * @param page Specify which page to query. Minimum: 1, maximum: 1000,
     * default: 1
     *
     * @return [?][<]
     */
    fun getRecommendationByWork(work: Work, page: Int): Single<out WorkPage<*>>

    /**
     * Gets a list of similar works. This is not the same as the
     * "Recommendation" system you see on the website. These items
     * are assembled by looking at keywords and genres.
     *
     * @param work [Work] to search the [<]
     * @param page Specify which page to query. Minimum: 1, maximum: 1000,
     * default: 1
     *
     * @return [?][<]
     */
    fun getSimilarByWork(work: Work, page: Int): Single<out WorkPage<*>>

    /**
     * Gets the [VideoList] by the [Work]
     *
     * @param work [Work] to search the [<]
     *
     * @return [<]
     */
    fun getVideosByWork(work: Work): Single<VideoList>

    /**
     * Searches the movies by a query
     *
     * @param query Query to search the movies
     * @param page  Specify which page to query. Minimum: 1, maximum: 1000,
     * default: 1
     *
     * @return [<]
     */
    fun searchMoviesByQuery(query: String, page: Int): Single<MoviePage>

    /**
     * Searches the tv shows by a query
     *
     * @param query Query to search the movies
     * @param page  Specify which page to query. Minimum: 1, maximum: 1000,
     * default: 1
     *
     * @return [<]
     */
    fun searchTvShowsByQuery(query: String, page: Int): Single<TvShowPage>

    /**
     * Gets the [Cast] details by the [Cast]
     *
     * @param cast [Cast] to search
     *
     * @return [<]
     */
    fun getCastDetails(cast: Cast): Single<Cast>

    /**
     * Gets the [CastMovieList] details by the [Cast]
     *
     * @param cast [Cast] to search
     *
     * @return [<]
     */
    fun getMovieCreditsByCast(cast: Cast): Single<CastMovieList>

    /**
     * Gets the [CastTvShowList] details by the [Cast]
     *
     * @param cast [Cast] to search
     *
     * @return [<]
     */
    fun getTvShowCreditsByCast(cast: Cast): Single<CastTvShowList>

    /**
     * Represents the movie list type
     */
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