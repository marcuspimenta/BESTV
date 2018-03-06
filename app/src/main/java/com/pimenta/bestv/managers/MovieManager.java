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

package com.pimenta.bestv.managers;

import com.pimenta.bestv.models.Movie;

import java.util.List;

/**
 * Created by marcus on 05-03-2018.
 */
public interface MovieManager {

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
     * @return          Favorite {@link List<Movie>}
     */
    List<Movie> getFavoriteMovies();

}