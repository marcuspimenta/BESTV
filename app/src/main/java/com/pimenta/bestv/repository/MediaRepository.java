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
import com.pimenta.bestv.repository.entity.CastMovieList;
import com.pimenta.bestv.repository.entity.CastTvShowList;
import com.pimenta.bestv.repository.entity.Genre;
import com.pimenta.bestv.repository.entity.Movie;
import com.pimenta.bestv.repository.entity.MovieGenreList;
import com.pimenta.bestv.repository.entity.MoviePage;
import com.pimenta.bestv.repository.entity.TvShowGenreList;
import com.pimenta.bestv.repository.entity.TvShowPage;
import com.pimenta.bestv.repository.entity.VideoList;
import com.pimenta.bestv.repository.entity.Work;
import com.pimenta.bestv.repository.entity.WorkPage;

import java.util.List;

import io.reactivex.Single;

/**
 * Created by marcus on 05-03-2018.
 */
public interface MediaRepository {

    /**
     * Checks if the {@link Work} is favorite
     *
     * @param work {@link Work} to verify if is favorite
     *
     * @return {@code true} if yes, {@code false} otherwise
     */
    boolean isFavorite(Work work);

    /**
     * Checks if there is any {@link Work} saved as favorite
     *
     * @return {@link Single<Boolean>}
     */
    Single<Boolean> hasFavorite();

    /**
     * Saves a {@link Work} as favorites
     *
     * @param work {@link Work} to be saved as favorite
     *
     * @return {@code true} if the {@link Work} was saved with success,
     * {@code false} otherwise
     */
    boolean saveFavorite(Work work);

    /**
     * Deletes a {@link Work} from favorites
     *
     * @param work {@link Work} to be deleted from favorite
     *
     * @return {@code true} if the {@link Work} was deleted with success,
     * {@code false} otherwise
     */
    boolean deleteFavorite(Work work);

    /**
     * Gets the favorites {@link List<Work>}
     *
     * @return Favorite {@link Single<List<Work>>}
     */
    Single<List<Work>> getFavorites();

    /**
     * Loads the {@link MoviePage} by {@link WorkType}
     *
     * @param page          Page to be loaded
     * @param movieListType {@link WorkType}
     *
     * @return {@link Single<MoviePage>}
     */
    Single<? extends WorkPage> loadWorkByType(int page, WorkType movieListType);

    /**
     * Gets the {@link MovieGenreList} available at TMDb
     *
     * @return {@link Single<MovieGenreList>}
     */
    Single<MovieGenreList> getMovieGenres();

    /**
     * Gets the {@link List<Work>} by the {@link Genre}
     *
     * @param genre {@link Genre} to search the {@link List<Movie>}
     * @param page  Specify which page to query. Minimum: 1, maximum: 1000,
     *              default: 1
     *
     * @return {@link Single<? extends WorkPage>}
     */
    Single<? extends WorkPage> getWorkByGenre(Genre genre, int page);

    /**
     * Gets the {@link Movie} by the ID
     *
     * @param movieId Movie ID
     *
     * @return {@link Movie}
     */
    Movie getMovie(int movieId);

    /**
     * Gets the {@link CastList} by the {@link Work}
     *
     * @param work {@link Work} to search the {@link List<CastList>}
     *
     * @return {@link Single<CastList>}
     */
    Single<CastList> getCastByWork(Work work);

    /**
     * Gets a list of recommended movies for a work.
     *
     * @param work {@link Work} to search the {@link List<Work>}
     * @param page Specify which page to query. Minimum: 1, maximum: 1000,
     *             default: 1
     *
     * @return {@link Single<? extends WorkPage>}
     */
    Single<? extends WorkPage> getRecommendationByWork(Work work, int page);

    /**
     * Gets a list of similar works. This is not the same as the
     * "Recommendation" system you see on the website. These items
     * are assembled by looking at keywords and genres.
     *
     * @param work {@link Work} to search the {@link List<Work>}
     * @param page Specify which page to query. Minimum: 1, maximum: 1000,
     *             default: 1
     *
     * @return {@link Single<? extends WorkPage>}
     */
    Single<? extends WorkPage> getSimilarByWork(Work work, int page);

    /**
     * Gets the {@link VideoList} by the {@link Work}
     *
     * @param work {@link Work} to search the {@link List<VideoList>}
     *
     * @return {@link Single<VideoList>}
     */
    Single<VideoList> getVideosByWork(Work work);

    /**
     * Searches the movies by a query
     *
     * @param query Query to search the movies
     * @param page  Specify which page to query. Minimum: 1, maximum: 1000,
     *              default: 1
     *
     * @return {@link Single<MoviePage>}
     */
    Single<MoviePage> searchMoviesByQuery(String query, int page);

    /**
     * Searches the tv shows by a query
     *
     * @param query Query to search the movies
     * @param page  Specify which page to query. Minimum: 1, maximum: 1000,
     *              default: 1
     *
     * @return {@link Single<TvShowPage>}
     */
    Single<TvShowPage> searchTvShowsByQuery(String query, int page);

    /**
     * Gets the {@link Cast} details by the {@link Cast}
     *
     * @param cast {@link Cast} to search
     *
     * @return {@link Single<Cast>}
     */
    Single<Cast> getCastDetails(Cast cast);

    /**
     * Gets the {@link CastMovieList} details by the {@link Cast}
     *
     * @param cast {@link Cast} to search
     *
     * @return {@link Single<CastMovieList>}
     */
    Single<CastMovieList> getMovieCreditsByCast(Cast cast);

    /**
     * Gets the {@link CastTvShowList} details by the {@link Cast}
     *
     * @param cast {@link Cast} to search
     *
     * @return {@link Single<CastTvShowList>}
     */
    Single<CastTvShowList> getTvShowCreditsByCast(Cast cast);

    /**
     * Gets the {@link TvShowGenreList} available at TMDb
     *
     * @return {@link Single<TvShowGenreList>}
     */
    Single<TvShowGenreList> getTvShowGenres();

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