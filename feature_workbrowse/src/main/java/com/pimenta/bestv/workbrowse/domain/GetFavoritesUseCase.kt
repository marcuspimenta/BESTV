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

import com.pimenta.bestv.model.domain.PageDomainModel
import com.pimenta.bestv.model.domain.WorkDomainModel
import io.reactivex.Single
import io.reactivex.functions.BiFunction
import javax.inject.Inject

/**
 * Created by marcus on 23-08-2019.
 */
class GetFavoritesUseCase @Inject constructor(
    private val getFavoriteMoviesUseCase: GetFavoriteMoviesUseCase,
    private val getFavoriteTvShowsUseCase: GetFavoriteTvShowsUseCase
) {

    operator fun invoke(): Single<PageDomainModel<WorkDomainModel>> =
            Single.zip(
                    getFavoriteMoviesUseCase(),
                    getFavoriteTvShowsUseCase(),
                    BiFunction { favoriteMovies, favoriteTvShows ->
                        PageDomainModel(
                                page = 1,
                                totalPages = 1,
                                works = favoriteMovies + favoriteTvShows
                        )
                    }
            )
}
