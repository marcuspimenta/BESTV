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

package com.pimenta.bestv.castdetail.domain

import com.pimenta.bestv.model.domain.CastDomainModel
import com.pimenta.bestv.model.domain.WorkDomainModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

/**
 * Created by marcus on 15-04-2019.
 */
class GetCastDetailsUseCase(
    private val getCastPersonalDetails: GetCastPersonalDetails,
    private val getMovieCreditsByCastUseCase: GetMovieCreditsByCastUseCase,
    private val getTvShowCreditsByCastUseCase: GetTvShowCreditsByCastUseCase
) {

    suspend operator fun invoke(castId: Int): Triple<CastDomainModel, List<WorkDomainModel>?, List<WorkDomainModel>?> =
        coroutineScope {
            val castViewModel = async { getCastPersonalDetails(castId) }
            val castMovieList = async { getMovieCreditsByCastUseCase(castId) }
            val castTvShowList = async { getTvShowCreditsByCastUseCase(castId) }
            awaitAll(castViewModel, castMovieList, castTvShowList)
            Triple(castViewModel.await(), castMovieList.await(), castTvShowList.await())
        }
}
