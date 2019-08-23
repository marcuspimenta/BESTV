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

package com.pimenta.bestv.feature.workdetail.domain

import com.pimenta.bestv.common.presentation.mapper.toMovieDbModel
import com.pimenta.bestv.common.presentation.mapper.toTvShowDbModel
import com.pimenta.bestv.common.presentation.model.WorkType
import com.pimenta.bestv.common.presentation.model.WorkViewModel
import com.pimenta.bestv.data.MediaRepository
import javax.inject.Inject

/**
 * Created by marcus on 23-08-2019.
 */
class SetFavoriteUseCase @Inject constructor(
        private val mediaRepository: MediaRepository
) {

    operator fun invoke(workViewModel: WorkViewModel) =
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
}