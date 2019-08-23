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

package com.pimenta.bestv.common.domain

import com.pimenta.bestv.common.presentation.mapper.toMovieDbModel
import com.pimenta.bestv.common.presentation.mapper.toTvShowDbModel
import com.pimenta.bestv.common.presentation.mapper.toViewModel
import com.pimenta.bestv.common.presentation.model.*
import com.pimenta.bestv.feature.search.domain.UrlEncoderTextUseCase
import com.pimenta.bestv.data.MediaRepository
import io.reactivex.Single
import javax.inject.Inject

/**
 * Created by marcus on 18-04-2019.
 */
class WorkUseCase @Inject constructor(
        private val urlEncoderTextUseCase: UrlEncoderTextUseCase,
        private val mediaRepository: MediaRepository
) {

    fun isFavorite(workViewModel: WorkViewModel) =
            when (workViewModel.type) {
                WorkType.MOVIE -> mediaRepository.isFavoriteMovie(workViewModel.id)
                WorkType.TV_SHOW -> mediaRepository.isFavoriteTvShow(workViewModel.id)
            }

    fun setFavorite(workViewModel: WorkViewModel) =
            when (workViewModel.type) {
                WorkType.MOVIE -> {
                    if (workViewModel.isFavorite) {
                        mediaRepository.deleteFavoriteMovie(workViewModel.toMovieDbModel())
                    } else {
                        mediaRepository.saveFavoriteMovie(workViewModel.toMovieDbModel())
                    }
                }
                WorkType.TV_SHOW -> {
                    if (workViewModel.isFavorite) {
                        mediaRepository.deleteFavoriteTvShow(workViewModel.toTvShowDbModel())
                    } else {
                        mediaRepository.saveFavoriteTvShow(workViewModel.toTvShowDbModel())
                    }
                }
            }

    fun hasFavorite() = mediaRepository.hasFavorite()

    fun getMovieGenres() =
            mediaRepository.getMovieGenres()
                    .map { it.genres?.map { genre -> genre.toViewModel() } }

    fun getTvShowGenres() =
            mediaRepository.getTvShowGenres()
                    .map { it.genres?.map { genre -> genre.toViewModel() } }

    fun getFavorites(): Single<List<WorkViewModel>> =
            mediaRepository.getFavorites()
                    .map { it.map { work -> work.toViewModel() } }

    fun loadWorkByType(page: Int, movieListType: MediaRepository.WorkType) =
            mediaRepository.loadWorkByType(page, movieListType)
                    .map {
                        WorkPageViewModel(
                                it.page,
                                it.totalPages,
                                it.works?.map { work -> work.toViewModel() }
                        )
                    }

    fun getWorkByGenre(genreViewModel: GenreViewModel, page: Int) =
            when (genreViewModel.source) {
                Source.MOVIE -> mediaRepository.getMovieByGenre(genreViewModel.id, page)
                        .map {
                            WorkPageViewModel(
                                    it.page,
                                    it.totalPages,
                                    it.works?.map { work -> work.toViewModel() }
                            )
                        }
                Source.TV_SHOW -> mediaRepository.getTvShowByGenre(genreViewModel.id, page)
                        .map {
                            WorkPageViewModel(
                                    it.page,
                                    it.totalPages,
                                    it.works?.map { work -> work.toViewModel() }
                            )
                        }
            }


    fun searchMoviesByQuery(query: String, page: Int) =
            urlEncoderTextUseCase(query)
                    .flatMap {
                        mediaRepository.searchMoviesByQuery(it, page)
                                .map { moviePage ->
                                    WorkPageViewModel(
                                            moviePage.page,
                                            moviePage.totalPages,
                                            moviePage.works?.map { work -> work.toViewModel() }
                                    )
                                }
                    }

    fun searchTvShowsByQuery(query: String, page: Int) =
            urlEncoderTextUseCase(query)
                    .flatMap {
                        mediaRepository.searchTvShowsByQuery(it, page)
                                .map { tvShowPage ->
                                    WorkPageViewModel(
                                            tvShowPage.page,
                                            tvShowPage.totalPages,
                                            tvShowPage.works?.map { work -> work.toViewModel() }
                                    )
                                }
                    }

}