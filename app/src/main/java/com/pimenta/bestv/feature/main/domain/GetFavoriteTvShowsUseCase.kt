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

package com.pimenta.bestv.feature.main.domain

import com.pimenta.bestv.common.presentation.model.WorkViewModel
import java.util.*
import javax.inject.Inject

/**
 * Created by marcus on 13-10-2019.
 */
class GetFavoriteTvShowsUseCase @Inject constructor(
        private val getFavoriteTvShowIdsUseCase: GetFavoriteTvShowIdsUseCase,
        private val getTvShowUseCase: GetTvShowUseCase
) {

    operator fun invoke() =
            getFavoriteTvShowIdsUseCase()
                    .map {
                        val tvShows = ArrayList<WorkViewModel>()
                        it.forEach { tvShowDbModel ->
                            getTvShowUseCase(tvShowDbModel.id)?.let { tvShowViewModel ->
                                tvShowViewModel.isFavorite = true
                                tvShows.add(tvShowViewModel)
                            }
                        }
                        tvShows.toList()
                    }
}