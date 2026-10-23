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

import com.pimenta.bestv.model.domain.WorkDomainModel
import com.pimenta.bestv.workdetail.domain.repository.MovieRepository
import com.pimenta.bestv.workdetail.domain.repository.TvShowRepository

/**
 * Created by marcus on 23-08-2019.
 */
class SetFavoriteUseCase(
    private val movieRepository: MovieRepository,
    private val tvShowRepository: TvShowRepository,
) {
    suspend operator fun invoke(workId: Int, workType: WorkDomainModel.Type, desiredFavoriteState: Boolean) {
        when (workType) {
            WorkDomainModel.Type.MOVIE -> {
                if (desiredFavoriteState) {
                    movieRepository.saveFavoriteMovie(workId)
                } else {
                    movieRepository.deleteFavoriteMovie(workId)
                }
            }
            WorkDomainModel.Type.TV_SHOW -> {
                if (desiredFavoriteState) {
                    tvShowRepository.saveFavoriteTvShow(workId)
                } else {
                    tvShowRepository.deleteFavoriteTvShow(workId)
                }
            }
        }
    }
}
