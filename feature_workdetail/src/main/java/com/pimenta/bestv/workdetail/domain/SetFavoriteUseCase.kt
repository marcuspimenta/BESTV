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

package com.pimenta.bestv.workdetail.domain

import com.pimenta.bestv.model.presentation.mapper.toMovieDbModel
import com.pimenta.bestv.model.presentation.mapper.toTvShowDbModel
import com.pimenta.bestv.model.presentation.model.WorkType
import com.pimenta.bestv.model.presentation.model.WorkViewModel
import com.pimenta.bestv.workdetail.data.repository.MovieRepository
import com.pimenta.bestv.workdetail.data.repository.TvShowRepository
import javax.inject.Inject

/**
 * Created by marcus on 23-08-2019.
 */
class SetFavoriteUseCase @Inject constructor(
    private val movieRepository: MovieRepository,
    private val tvShowRepository: TvShowRepository
) {

    suspend operator fun invoke(workViewModel: WorkViewModel) {
        when (workViewModel.type) {
            WorkType.MOVIE -> {
                if (workViewModel.isFavorite) {
                    movieRepository.deleteFavoriteMovie(workViewModel.toMovieDbModel())
                } else {
                    movieRepository.saveFavoriteMovie(workViewModel.toMovieDbModel())
                }
            }
            WorkType.TV_SHOW -> {
                if (workViewModel.isFavorite) {
                    tvShowRepository.deleteFavoriteTvShow(workViewModel.toTvShowDbModel())
                } else {
                    tvShowRepository.saveFavoriteTvShow(workViewModel.toTvShowDbModel())
                }
            }
        }
    }
}
