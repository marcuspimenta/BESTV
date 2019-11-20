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

package com.pimenta.bestv.data

import com.pimenta.bestv.common.data.model.local.MovieDbModel
import com.pimenta.bestv.common.data.model.local.TvShowDbModel
import com.pimenta.bestv.common.domain.model.WorkDomainModel
import io.reactivex.Completable
import io.reactivex.Single

/**
 * Created by marcus on 05-03-2018.
 */
interface MediaRepository {

    fun saveFavoriteMovie(movieDbModel: MovieDbModel): Completable

    fun saveFavoriteTvShow(tvShowDbModel: TvShowDbModel): Completable

    fun deleteFavoriteMovie(movieDbModel: MovieDbModel): Completable

    fun deleteFavoriteTvShow(tvShowDbModel: TvShowDbModel): Completable

    fun getFavoriteMovies(): Single<List<WorkDomainModel>>

    fun getFavoriteTvShows(): Single<List<WorkDomainModel>>
}