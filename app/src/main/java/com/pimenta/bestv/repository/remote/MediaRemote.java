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

package com.pimenta.bestv.repository.remote;

import com.pimenta.bestv.repository.entity.Cast;
import com.pimenta.bestv.repository.entity.CastList;
import com.pimenta.bestv.repository.entity.Genre;
import com.pimenta.bestv.repository.entity.GenreList;
import com.pimenta.bestv.repository.entity.Movie;
import com.pimenta.bestv.repository.entity.MovieList;
import com.pimenta.bestv.repository.entity.VideoList;

import java.util.List;

import io.reactivex.Single;

/**
 * Created by marcus on 08-02-2018.
 */
public interface MediaRemote {

    /**
     * Gets the {@link GenreList} available at TMDb
     *
     * @return {@link Single<GenreList>}
     */
    Single<GenreList> getGenres();

    /**
     * Gets the {@link List<Movie>} by the {@link Genre}
     *
     * @param genre {@link Genre} to search the {@link List<Movie>}
     * @param page  Specify which page to query. Minimum: 1, maximum: 1000,
     *              default: 1
     *
     * @return {@link Single<MovieList>}
     */
    Single<MovieList> getMoviesByGenre(Genre genre, int page);

    /**
     * Gets the {@link Movie} by the ID
     *
     * @param movieId   Movie ID
     * @return          {@link Movie}
     */
    Movie getMovie(int movieId);

    /**
     * Gets the {@link CastList} by the {@link Movie}
     *
     * @param movie {@link Movie} to search the {@link List<CastList>}
     *
     * @return {@link Single<CastList>}
     */
    Single<CastList> getCastByMovie(Movie movie);

    /**
     * Gets a list of recommended movies for a movie.
     *
     * @param movie {@link Movie} to search the {@link List<Movie>}
     * @param page  Specify which page to query. Minimum: 1, maximum: 1000,
     *              default: 1
     *
     * @return {@link Single<MovieList>}
     */
    Single<MovieList> getRecommendationByMovie(Movie movie, int page);

    /**
     * Gets a list of similar movies. This is not the same as the
     * "Recommendation" system you see on the website. These items
     * are assembled by looking at keywords and genres.
     *
     * @param movie {@link Movie} to search the {@link List<Movie>}
     * @param page  Specify which page to query. Minimum: 1, maximum: 1000,
     *              default: 1
     *
     * @return {@link Single<MovieList>}
     */
    Single<MovieList> getSimilarByMovie(Movie movie, int page);

    /**
     * Gets the videos from a movie
     *
     * @param movie {@link Movie}
     *
     * @return      {@link Single<VideoList>}
     */
    Single<VideoList> getVideosByMovie(Movie movie);

    /**
     * Gets the now playing {@link MovieList}
     *
     * @param page Specify which page to query. Minimum: 1, maximum: 1000,
     *             default: 1
     *
     * @return {@link Single<MovieList>}
     */
    Single<MovieList> getNowPlayingMovies(int page);

    /**
     * Gets the popular {@link MovieList}
     *
     * @param page Specify which page to query. Minimum: 1, maximum: 1000,
     *             default: 1
     *
     * @return {@link Single<MovieList>}
     */
    Single<MovieList> getPopularMovies(int page);

    /**
     * Gets the top rated {@link MovieList}
     *
     * @param page Specify which page to query. Minimum: 1, maximum: 1000,
     *             default: 1
     *
     * @return {@link Single<MovieList>}
     */
    Single<MovieList> getTopRatedMovies(int page);

    /**
     * Gets the up coming {@link MovieList}
     *
     * @param page Specify which page to query. Minimum: 1, maximum: 1000,
     *             default: 1
     *
     * @return {@link Single<MovieList>}
     */
    Single<MovieList> getUpComingMovies(int page);


    /**
     * Searches the movies by a query
     *
     * @param query     Query to search the movies
     * @param page      Specify which page to query. Minimum: 1, maximum: 1000,
     *                  default: 1
     *
     * @return          {@link Single<MovieList>}
     */
    Single<MovieList> searchMoviesByQuery(String query, int page);

    /**
     * Gets the {@link Cast} details by the {@link Cast}
     *
     * @param cast {@link Cast} to search
     *
     * @return {@link Single<Cast>}
     */
    Single<Cast> getCastDetails(Cast cast);
}