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

package com.pimenta.bestv.search.domain

import com.pimenta.bestv.model.domain.PageDomainModel
import com.pimenta.bestv.model.domain.WorkDomainModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject

/**
 * Created by marcus on 20-05-2019.
 */
class SearchWorksByQueryUseCase @Inject constructor(
    private val urlEncoderTextUseCase: UrlEncoderTextUseCase,
    private val searchMoviesByQueryUseCase: SearchMoviesByQueryUseCase,
    private val searchTvShowsByQueryUseCase: SearchTvShowsByQueryUseCase
) {

    suspend operator fun invoke(query: String): Pair<PageDomainModel<WorkDomainModel>, PageDomainModel<WorkDomainModel>> =
        coroutineScope {
            val urlEncoder = async { urlEncoderTextUseCase(query) }.await()
            val movies = async { searchMoviesByQueryUseCase(urlEncoder, 1) }
            val tvShows = async { searchTvShowsByQueryUseCase(urlEncoder, 1) }
            val results = awaitAll(movies, tvShows)
            results[0] to results[1]
        }
}
