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

package com.pimenta.bestv.repository;

import android.support.annotation.StringRes;

import com.pimenta.bestv.BesTV;
import com.pimenta.bestv.R;
import com.pimenta.bestv.repository.entity.Cast;
import com.pimenta.bestv.repository.entity.CastList;
import com.pimenta.bestv.repository.entity.Genre;
import com.pimenta.bestv.repository.entity.GenreList;
import com.pimenta.bestv.repository.entity.Movie;
import com.pimenta.bestv.repository.entity.MoviePage;
import com.pimenta.bestv.repository.entity.TvShow;
import com.pimenta.bestv.repository.entity.TvShowPage;
import com.pimenta.bestv.repository.entity.VideoList;
import com.pimenta.bestv.repository.entity.WorkPage;

import java.util.List;

import io.reactivex.Single;

/**
 * Created by marcus on 05-03-2018.
 */
public interface MediaRepository {

    /**
     * Checks if the {@link Movie} is favorite
     *
     * @return          {@code true} if yes, {@code false} otherwise
     */
    boolean isFavorite(Movie movie);

    /**
     * Checks if there is any {@link Movie} saved as favorite
     *
     * @return          {@code true} if there any {@link Movie} saved as favorite,
     *                  {@code false} otherwise
     */
    boolean hasFavoriteMovie();

    /**
     * Saves a {@link Movie} as favorites
     *
     * @param movie     {@link Movie} to be saved as favorite
     * @return          {@code true} if the {@link Movie} was saved with success,
     *                  {@code false} otherwise
     */
    boolean saveFavoriteMovie(Movie movie);

    /**
     * Deletes a {@link Movie} from favorites
     *
     * @param movie     {@link Movie} to be deleted from favorite
     * @return          {@code true} if the {@link Movie} was deleted with success,
     *                  {@code false} otherwise
     */
    boolean deleteFavoriteMovie(Movie movie);

    /**
     * Gets the favorites {@link List<Movie>}
     *
     * @return          Favorite {@link Single<List<Movie>>}
     */
    Single<List<Movie>> getFavoriteMovies();

    /**
     * Loads the {@link MoviePage} by {@link WorkType}
     *
     * @param page              Page to be loaded
     * @param movieListType     {@link WorkType}
     * @return                  {@link Single<MoviePage>}
     */
    Single<? extends WorkPage> loadWorkByType(int page, WorkType movieListType);

    /**
     * Gets the {@link GenreList} available at TMDb
     *
     * @return {@link Single<GenreList>}
     */
    Single<GenreList> getMovieGenres();

    /**
     * Gets the {@link List<Movie>} by the {@link Genre}
     *
     * @param genre {@link Genre} to search the {@link List<Movie>}
     * @param page  Specify which page to query. Minimum: 1, maximum: 1000,
     *              default: 1
     *
     * @return {@link Single<MoviePage>}
     */
    Single<MoviePage> getMoviesByGenre(Genre genre, int page);

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
     * @return {@link Single<MoviePage>}
     */
    Single<MoviePage> getRecommendationByMovie(Movie movie, int page);

    /**
     * Gets a list of similar movies. This is not the same as the
     * "Recommendation" system you see on the website. These items
     * are assembled by looking at keywords and genres.
     *
     * @param movie {@link Movie} to search the {@link List<Movie>}
     * @param page  Specify which page to query. Minimum: 1, maximum: 1000,
     *              default: 1
     *
     * @return {@link Single<MoviePage>}
     */
    Single<MoviePage> getSimilarByMovie(Movie movie, int page);

    /**
     * Gets the videos from a movie
     *
     * @param movie {@link Movie}
     *
     * @return      {@link Single<VideoList>}
     */
    Single<VideoList> getVideosByMovie(Movie movie);

    /**
     * Gets the now playing {@link MoviePage}
     *
     * @param page Specify which page to query. Minimum: 1, maximum: 1000,
     *             default: 1
     *
     * @return {@link Single<MoviePage>}
     */
    Single<MoviePage> getNowPlayingMovies(int page);

    /**
     * Gets the popular {@link MoviePage}
     *
     * @param page Specify which page to query. Minimum: 1, maximum: 1000,
     *             default: 1
     *
     * @return {@link Single<MoviePage>}
     */
    Single<MoviePage> getPopularMovies(int page);

    /**
     * Gets the top rated {@link MoviePage}
     *
     * @param page Specify which page to query. Minimum: 1, maximum: 1000,
     *             default: 1
     *
     * @return {@link Single<MoviePage>}
     */
    Single<MoviePage> getTopRatedMovies(int page);

    /**
     * Gets the up coming {@link MoviePage}
     *
     * @param page Specify which page to query. Minimum: 1, maximum: 1000,
     *             default: 1
     *
     * @return {@link Single<MoviePage>}
     */
    Single<MoviePage> getUpComingMovies(int page);

    /**
     * Searches the movies by a query
     *
     * @param query     Query to search the movies
     * @param page      Specify which page to query. Minimum: 1, maximum: 1000,
     *                  default: 1
     *
     * @return          {@link Single<MoviePage>}
     */
    Single<MoviePage> searchMoviesByQuery(String query, int page);

    /**
     * Gets the {@link Cast} details by the {@link Cast}
     *
     * @param cast {@link Cast} to search
     *
     * @return {@link Single<Cast>}
     */
    Single<Cast> getCastDetails(Cast cast);

    /**
     * Gets the {@link GenreList} available at TMDb
     *
     * @return {@link Single<GenreList>}
     */
    Single<GenreList> getTvShowGenres();

    /**
     * Gets the {@link List<Movie>} by the {@link Genre}
     *
     * @param genre {@link Genre} to search the {@link List<TvShow>}
     * @param page  Specify which page to query. Minimum: 1, maximum: 1000,
     *              default: 1
     *
     * @return {@link Single<TvShowPage>}
     */
    Single<TvShowPage> getTvShowByGenre(Genre genre, int page);

    /**
     * Represents the movie list type
     */
    enum WorkType {
        FAVORITES_MOVIES(R.string.favorites),
        NOW_PLAYING_MOVIES(R.string.now_playing),
        POPULAR_MOVIES(R.string.popular),
        TOP_RATED_MOVIES(R.string.top_rated),
        UP_COMING_MOVIES(R.string.up_coming),
        AIRING_TODAY_TV_SHOWS(R.string.airing_today),
        ON_THE_AIR_TV_SHOWS(R.string.on_the_air),
        POPULAR_TV_SHOWS(R.string.popular),
        TOP_RATED_TV_SHOWS(R.string.top_rated);

        private String mName;

        WorkType(@StringRes int nameResource) {
            mName = BesTV.get().getString(nameResource);
        }

        public String getName() {
            return mName;
        }
    }

}