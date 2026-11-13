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

package com.pimenta.bestv.workbrowse.data.repository

import com.pimenta.bestv.workbrowse.data.remote.datasource.GenreRemoteDataSource
import com.pimenta.bestv.workbrowse.data.remote.mapper.toDomainModel
import com.pimenta.bestv.workbrowse.domain.model.GenreDomainModel
import com.pimenta.bestv.workbrowse.domain.repository.GenreRepository as GenreRepositoryContract
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

/**
 * Created by marcus on 20-10-2019.
 */
class GenreRepository(
    private val genreRemoteDataSource: GenreRemoteDataSource,
    private val ioDispatcher: CoroutineDispatcher,
) : GenreRepositoryContract {
    override suspend fun getMovieGenres(): List<GenreDomainModel> = withContext(ioDispatcher) {
        val response = genreRemoteDataSource.getMovieGenres()
        val genres = response.genres.orEmpty().map { genre -> genre.toDomainModel() }
        genres
    }

    override suspend fun getTvShowGenres(): List<GenreDomainModel> = withContext(ioDispatcher) {
        val response = genreRemoteDataSource.getTvShowGenres()
        val genres = response.genres.orEmpty().map { genre -> genre.toDomainModel() }
        genres
    }
}
