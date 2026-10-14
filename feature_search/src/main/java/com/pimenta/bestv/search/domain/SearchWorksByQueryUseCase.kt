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
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

/**
 * Created by marcus on 20-05-2019.
 */
class SearchWorksByQueryUseCase(
    private val urlEncoderTextUseCase: UrlEncoderTextUseCase,
    private val searchMoviesByQueryUseCase: SearchMoviesByQueryUseCase,
    private val searchTvShowsByQueryUseCase: SearchTvShowsByQueryUseCase,
) {
    suspend operator fun invoke(query: String) = coroutineScope {
        val encodedQuery = urlEncoderTextUseCase(query)
        val movies = async { searchSafely { searchMoviesByQueryUseCase(encodedQuery, 1) } }
        val tvShows = async { searchSafely { searchTvShowsByQueryUseCase(encodedQuery, 1) } }

        val movieResult = movies.await()
        val tvShowResult = tvShows.await()

        if (movieResult.isFailure && tvShowResult.isFailure) {
            movieResult.getOrThrow()
        }

        movieResult.getOrElse { emptyPage() } to tvShowResult.getOrElse { emptyPage() }
    }

    private suspend fun searchSafely(
        search: suspend () -> PageDomainModel<WorkDomainModel>,
    ) = try {
        Result.success(search())
    } catch (exception: CancellationException) {
        throw exception
    } catch (exception: Exception) {
        Result.failure(exception)
    }

    private fun emptyPage() = PageDomainModel<WorkDomainModel>(page = 1, totalPages = 0, results = emptyList())
}
