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

import com.pimenta.bestv.common.presentation.mapper.toViewModel
import com.pimenta.bestv.data.MediaDataSource
import javax.inject.Inject

/**
 * Created by marcus on 24-10-2019.
 */
class GetNowPlayingMoviesUseCase @Inject constructor(
        private val mediaDataSource: MediaDataSource
) {

    operator fun invoke(page: Int) =
            mediaDataSource.getNowPlayingMovies(page)
                    .map { it.toViewModel() }
}