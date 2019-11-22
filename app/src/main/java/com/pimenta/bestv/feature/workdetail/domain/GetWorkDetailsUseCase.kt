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

import com.pimenta.bestv.model.domain.CastDomainModel
import com.pimenta.bestv.model.domain.VideoDomainModel
import com.pimenta.bestv.model.domain.WorkPageDomainModel
import com.pimenta.bestv.common.kotlin.Quadruple
import com.pimenta.bestv.model.presentation.model.WorkViewModel
import io.reactivex.Single
import io.reactivex.functions.Function4
import javax.inject.Inject

/**
 * Created by marcus on 20-05-2019.
 */
class GetWorkDetailsUseCase @Inject constructor(
    private val getVideosUseCase: GetVideosUseCase,
    private val getCastsUseCase: GetCastsUseCase,
    private val getRecommendationByWorkUseCase: GetRecommendationByWorkUseCase,
    private val getSimilarByWorkUseCase: GetSimilarByWorkUseCase
) {

    operator fun invoke(workViewModel: WorkViewModel): Single<Quadruple<List<VideoDomainModel>?, List<CastDomainModel>?, WorkPageDomainModel, WorkPageDomainModel>> =
            Single.zip(
                    getVideosUseCase(workViewModel.type, workViewModel.id),
                    getCastsUseCase(workViewModel.type, workViewModel.id),
                    getRecommendationByWorkUseCase(workViewModel.type, workViewModel.id, 1),
                    getSimilarByWorkUseCase(workViewModel.type, workViewModel.id, 1),
                    Function4 { videos, casts, recommendedMovies, similarMovies ->
                        Quadruple(videos, casts, recommendedMovies, similarMovies)
                    }
            )
}