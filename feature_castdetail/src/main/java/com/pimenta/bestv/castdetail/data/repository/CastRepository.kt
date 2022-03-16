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

package com.pimenta.bestv.castdetail.data.repository

import com.pimenta.bestv.castdetail.R
import com.pimenta.bestv.castdetail.data.remote.datasource.CastRemoteDataSource
import com.pimenta.bestv.model.data.mapper.toDomainModel
import com.pimenta.bestv.presentation.platform.Resource
import javax.inject.Inject

/**
 * Created by marcus on 29-10-2019.
 */
class CastRepository @Inject constructor(
    private val resource: Resource,
    private val castRemoteDataSource: CastRemoteDataSource
) {

    fun getCastDetails(castId: Int) =
        castRemoteDataSource.getCastDetails(castId)
            .map {
                val source = resource.getStringResource(R.string.source_tmdb)
                it.toDomainModel(source)
            }

    fun getMovieCreditsByCast(castId: Int) =
        castRemoteDataSource.getMovieCreditsByCast(castId)
            .map {
                val source = resource.getStringResource(R.string.source_tmdb)
                it.works?.map { work -> work.toDomainModel(source) }
            }

    fun getTvShowCreditsByCast(castId: Int) =
        castRemoteDataSource.getTvShowCreditsByCast(castId)
            .map {
                val source = resource.getStringResource(R.string.source_tmdb)
                it.works?.map { work -> work.toDomainModel(source) }
            }
}
