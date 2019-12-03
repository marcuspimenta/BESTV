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

package com.pimenta.bestv.search.data.remote.datasource

import com.pimenta.bestv.search.data.remote.api.SearchMovieTmdbApi
import javax.inject.Inject
import javax.inject.Named

/**
 * Created by marcus on 29-10-2019.
 */
class MovieRemoteDataSource @Inject constructor(
    @Named("tmdbApiKey") private val tmdbApiKey: String,
    @Named("tmdbFilterLanguage") private val tmdbFilterLanguage: String,
    private val searchMovieTmdbApi: SearchMovieTmdbApi
) {

    fun searchMoviesByQuery(query: String, page: Int) =
            searchMovieTmdbApi.searchMoviesByQuery(tmdbApiKey, query, tmdbFilterLanguage, page)
}