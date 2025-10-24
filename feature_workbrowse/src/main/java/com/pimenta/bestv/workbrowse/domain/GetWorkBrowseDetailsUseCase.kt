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

import com.pimenta.bestv.workbrowse.domain.model.GenreDomainModel
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject

/**
 * Created by marcus on 20-05-2019.
 */
class GetWorkBrowseDetailsUseCase @Inject constructor(
    private val hasFavoriteUseCase: HasFavoriteUseCase,
    private val getMovieGenresUseCase: GetMovieGenresUseCase,
    private val getTvShowGenresUseCase: GetTvShowGenresUseCase
) {

    suspend operator fun invoke(): Triple<Boolean, List<GenreDomainModel>?, List<GenreDomainModel>?> = coroutineScope {
        val hasFavoriteDeferred = async { hasFavoriteUseCase() }
        val movieGenresDeferred = async { getMovieGenresUseCase() }
        val tvShowGenresDeferred = async { getTvShowGenresUseCase() }

        val hasFavoriteMovie = hasFavoriteDeferred.await()
        val movieGenreList = movieGenresDeferred.await()
        val tvShowGenreList = tvShowGenresDeferred.await()

        Triple(hasFavoriteMovie, movieGenreList, tvShowGenreList)
    }
}
