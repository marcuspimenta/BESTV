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

package com.pimenta.bestv.repository.remote

import com.pimenta.bestv.repository.entity.Cast
import com.pimenta.bestv.repository.entity.CastList
import com.pimenta.bestv.repository.entity.CastMovieList
import com.pimenta.bestv.repository.entity.CastTvShowList
import com.pimenta.bestv.repository.entity.Genre
import com.pimenta.bestv.repository.entity.GenreList
import com.pimenta.bestv.repository.entity.Movie
import com.pimenta.bestv.repository.entity.MovieGenreList
import com.pimenta.bestv.repository.entity.MoviePage
import com.pimenta.bestv.repository.entity.TvShowGenreList
import com.pimenta.bestv.repository.entity.TvShowPage
import com.pimenta.bestv.repository.entity.VideoList
import com.pimenta.bestv.repository.entity.TvShow

import io.reactivex.Single

/**
 * Created by marcus on 08-02-2018.
 */
interface MediaRemoteRepository {

    /**
     * Gets the [GenreList] available at TMDb
     *
     * @return [<]
     */
    fun getMovieGenres(): Single<MovieGenreList>

    /**
     * Gets the [GenreList] available at TMDb
     *
     * @return [<]
     */
    fun getTvShowGenres(): Single<TvShowGenreList>

    /**
     * Gets the [<] by the [Genre]
     *
     * @param genre [Genre] to search the [<]
     * @param page  Specify which page to query. Minimum: 1, maximum: 1000,
     * default: 1
     *
     * @return [<]
     */
    fun getMoviesByGenre(genre: Genre, page: Int): Single<MoviePage>

    /**
     * Gets the [Movie] by the ID
     *
     * @param movieId Movie ID
     *
     * @return [Movie]
     */
    fun getMovie(movieId: Int): Movie?

    /**
     * Gets the [CastList] by the [Movie]
     *
     * @param movie [Movie] to search the [<]
     *
     * @return [<]
     */
    fun getCastByMovie(movie: Movie): Single<CastList>

    /**
     * Gets a list of recommended movies for a movie.
     *
     * @param movie [Movie] to search the [<]
     * @param page  Specify which page to query. Minimum: 1, maximum: 1000,
     * default: 1
     *
     * @return [<]
     */
    fun getRecommendationByMovie(movie: Movie, page: Int): Single<MoviePage>

    /**
     * Gets a list of similar movies. This is not the same as the
     * "Recommendation" system you see on the website. These items
     * are assembled by looking at keywords and genres.
     *
     * @param movie [Movie] to search the [<]
     * @param page  Specify which page to query. Minimum: 1, maximum: 1000,
     * default: 1
     *
     * @return [<]
     */
    fun getSimilarByMovie(movie: Movie, page: Int): Single<MoviePage>

    /**
     * Gets the videos from a movie
     *
     * @param movie [Movie]
     *
     * @return [<]
     */
    fun getVideosByMovie(movie: Movie): Single<VideoList>

    /**
     * Gets the now playing [MoviePage]
     *
     * @param page Specify which page to query. Minimum: 1, maximum: 1000,
     * default: 1
     *
     * @return [<]
     */
    fun getNowPlayingMovies(page: Int): Single<MoviePage>

    /**
     * Gets the popular [MoviePage]
     *
     * @param page Specify which page to query. Minimum: 1, maximum: 1000,
     * default: 1
     *
     * @return [<]
     */
    fun getPopularMovies(page: Int): Single<MoviePage>

    /**
     * Gets the top rated [MoviePage]
     *
     * @param page Specify which page to query. Minimum: 1, maximum: 1000,
     * default: 1
     *
     * @return [<]
     */
    fun getTopRatedMovies(page: Int): Single<MoviePage>

    /**
     * Gets the up coming [MoviePage]
     *
     * @param page Specify which page to query. Minimum: 1, maximum: 1000,
     * default: 1
     *
     * @return [<]
     */
    fun getUpComingMovies(page: Int): Single<MoviePage>


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
     * Gets the [<] by the [Genre]
     *
     * @param genre [Genre] to search the [<]
     * @param page  Specify which page to query. Minimum: 1, maximum: 1000,
     * default: 1
     *
     * @return [<]
     */
    fun getTvShowByGenre(genre: Genre, page: Int): Single<TvShowPage>

    /**
     * Gets the airing today [TvShowPage]
     *
     * @param page Specify which page to query. Minimum: 1, maximum: 1000,
     * default: 1
     *
     * @return [<]
     */
    fun getAiringTodayTvShows(page: Int): Single<TvShowPage>

    /**
     * Gets the on the air [TvShowPage]
     *
     * @param page Specify which page to query. Minimum: 1, maximum: 1000,
     * default: 1
     *
     * @return [<]
     */
    fun getOnTheAirTvShows(page: Int): Single<TvShowPage>

    /**
     * Gets the popular [TvShowPage]
     *
     * @param page Specify which page to query. Minimum: 1, maximum: 1000,
     * default: 1
     *
     * @return [<]
     */
    fun getPopularTvShows(page: Int): Single<TvShowPage>

    /**
     * Gets the top rated [TvShowPage]
     *
     * @param page Specify which page to query. Minimum: 1, maximum: 1000,
     * default: 1
     *
     * @return [<]
     */
    fun getTopRatedTvShows(page: Int): Single<TvShowPage>

    /**
     * Gets the [TvShow] by the ID
     *
     * @param tvId TvShow ID
     *
     * @return [TvShow]
     */
    fun getTvShow(tvId: Int): TvShow?

    /**
     * Gets the [CastList] by the [TvShow]
     *
     * @param tvShow [TvShow] to search the [<]
     *
     * @return [<]
     */
    fun getCastByTvShow(tvShow: TvShow): Single<CastList>

    /**
     * Gets a list of recommended tv shows for a tv shows.
     *
     * @param tvShow [TvShow] to search the [<]
     * @param page   Specify which page to query. Minimum: 1, maximum: 1000,
     * default: 1
     *
     * @return [<]
     */
    fun getRecommendationByTvShow(tvShow: TvShow, page: Int): Single<TvShowPage>

    /**
     * Gets a list of similar tv shows. This is not the same as the
     * "Recommendation" system you see on the website. These items
     * are assembled by looking at keywords and genres.
     *
     * @param tvShow [TvShow] to search the [<]
     * @param page   Specify which page to query. Minimum: 1, maximum: 1000,
     * default: 1
     *
     * @return [<]
     */
    fun getSimilarByTvShow(tvShow: TvShow, page: Int): Single<TvShowPage>

    /**
     * Gets the videos from a tv show
     *
     * @param tvShow [TvShow]
     *
     * @return [<]
     */
    fun getVideosByTvShow(tvShow: TvShow): Single<VideoList>

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
}