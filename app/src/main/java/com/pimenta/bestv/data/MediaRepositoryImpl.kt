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

import com.pimenta.bestv.common.data.mapper.toDomainModel
import com.pimenta.bestv.common.data.model.local.MovieDbModel
import com.pimenta.bestv.common.data.model.local.TvShowDbModel
import com.pimenta.bestv.common.domain.model.WorkDomainModel
import com.pimenta.bestv.common.presentation.model.WorkViewModel
import com.pimenta.bestv.data.local.MediaLocalRepository
import com.pimenta.bestv.data.local.provider.RecommendationProvider
import com.pimenta.bestv.data.remote.MediaRemoteRepository
import io.reactivex.Completable
import io.reactivex.Single
import javax.inject.Inject

/**
 * Created by marcus on 05-03-2018.
 */
class MediaRepositoryImpl @Inject constructor(
    private val mediaLocalRepository: MediaLocalRepository,
    private val mediaRemoteRepository: MediaRemoteRepository,
    private val recommendationProvider: RecommendationProvider
) : MediaRepository {

    override fun saveFavoriteMovie(movieDbModel: MovieDbModel): Completable =
            mediaLocalRepository.saveFavoriteMovie(movieDbModel)

    override fun saveFavoriteTvShow(tvShowDbModel: TvShowDbModel): Completable =
            mediaLocalRepository.saveFavoriteTvShow(tvShowDbModel)

    override fun deleteFavoriteMovie(movieDbModel: MovieDbModel): Completable =
            mediaLocalRepository.deleteFavoriteMovie(movieDbModel)

    override fun deleteFavoriteTvShow(tvShowDbModel: TvShowDbModel): Completable =
            mediaLocalRepository.deleteFavoriteTvShow(tvShowDbModel)

    override fun getFavoriteMovies(): Single<List<WorkDomainModel>> =
            mediaLocalRepository.getMovies()
                    .map {
                        val movies = mutableListOf<WorkDomainModel>()
                        it.forEach { movieDbModel ->
                            mediaRemoteRepository.getMovie(movieDbModel.id)?.let { work ->
                                work.isFavorite = true
                                movies.add(work.toDomainModel())
                            }
                        }
                        movies.toList()
                    }

    override fun getFavoriteTvShows(): Single<List<WorkDomainModel>> =
            mediaLocalRepository.getTvShows()
                    .map {
                        val tvShows = mutableListOf<WorkDomainModel>()
                        it.forEach { tvShowDbModel ->
                            mediaRemoteRepository.getTvShow(tvShowDbModel.id)?.let { work ->
                                work.isFavorite = true
                                tvShows.add(work.toDomainModel())
                            }
                        }
                        tvShows.toList()
                    }

    override fun loadRecommendations(works: List<WorkViewModel>?): Completable =
            recommendationProvider.loadRecommendations(works)
}