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

package com.pimenta.bestv.common.usecase

import com.pimenta.bestv.common.presentation.mapper.toViewModel
import com.pimenta.bestv.common.presentation.model.WorkPageViewModel
import com.pimenta.bestv.common.presentation.model.WorkViewModel
import com.pimenta.bestv.data.repository.MediaRepository
import com.pimenta.bestv.data.entity.Genre
import com.pimenta.bestv.data.entity.Work
import com.pimenta.bestv.feature.search.usecase.UrlEncoderTextUseCase
import io.reactivex.Single
import javax.inject.Inject

/**
 * Created by marcus on 18-04-2019.
 */
class WorkUseCase @Inject constructor(
        private val urlEncoderTextUseCase: UrlEncoderTextUseCase,
        private val mediaRepository: MediaRepository
) {

    fun isFavorite(work: Work) = mediaRepository.isFavorite(work)

    fun saveFavorite(work: Work) = mediaRepository.saveFavorite(work)

    fun deleteFavorite(work: Work) = mediaRepository.deleteFavorite(work)

    fun hasFavorite() = mediaRepository.hasFavorite()

    fun getMovieGenres() = mediaRepository.getMovieGenres()

    fun getTvShowGenres() = mediaRepository.getTvShowGenres()

    fun getFavorites(): Single<List<WorkViewModel>> =
            mediaRepository.getFavorites()
                    .map { it.map { work -> work.toViewModel() } }

    fun loadWorkByType(page: Int, movieListType: MediaRepository.WorkType): Single<WorkPageViewModel> =
            mediaRepository.loadWorkByType(page, movieListType)
                    .map {
                        WorkPageViewModel(
                                it.page,
                                it.totalPages,
                                it.works?.map { work -> work.toViewModel() }
                        )
                    }

    fun getWorkByGenre(genre: Genre, page: Int): Single<WorkPageViewModel> =
            mediaRepository.getWorkByGenre(genre, page)
                    .map {
                        WorkPageViewModel(
                                it.page,
                                it.totalPages,
                                it.works?.map { work -> work.toViewModel() }
                        )
                    }

    fun searchMoviesByQuery(query: String, page: Int): Single<WorkPageViewModel> =
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

    fun searchTvShowsByQuery(query: String, page: Int): Single<WorkPageViewModel> =
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