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

package com.pimenta.bestv.workbrowse.domain

import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject

/**
 * Created by marcus on 23-08-2019.
 */
class HasFavoriteUseCase @Inject constructor(
    private val getFavoriteMoviesUseCase: GetFavoriteMoviesUseCase,
    private val getFavoriteTvShowsUseCase: GetFavoriteTvShowsUseCase
) {

    suspend operator fun invoke(): Boolean = coroutineScope {
        val favoriteMoviesDeferred = async { getFavoriteMoviesUseCase() }
        val favoriteTvShowsDeferred = async { getFavoriteTvShowsUseCase() }

        val favoriteMovies = favoriteMoviesDeferred.await()
        val favoriteTvShows = favoriteTvShowsDeferred.await()

        favoriteMovies.isNotEmpty() || favoriteTvShows.isNotEmpty()
    }
}
