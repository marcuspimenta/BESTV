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

import com.pimenta.bestv.common.kotlin.Quintuple
import com.pimenta.bestv.common.presentation.model.CastViewModel
import com.pimenta.bestv.common.presentation.model.VideoViewModel
import com.pimenta.bestv.common.presentation.model.WorkPageViewModel
import com.pimenta.bestv.common.presentation.model.WorkViewModel
import com.pimenta.bestv.common.domain.WorkUseCase
import io.reactivex.Single
import io.reactivex.functions.Function5
import javax.inject.Inject

/**
 * Created by marcus on 20-05-2019.
 */
class GetWorkDetailsUseCase @Inject constructor(
        private val workUseCase: WorkUseCase,
        private val getVideosUseCase: GetVideosUseCase,
        private val getCastsUseCase: GetCastsUseCase,
        private val getRecommendationByWorkUseCase: GetRecommendationByWorkUseCase,
        private val getSimilarByWorkUseCase: GetSimilarByWorkUseCase
) {

    operator fun invoke(workViewModel: WorkViewModel): Single<Quintuple<Boolean, List<VideoViewModel>?, List<CastViewModel>?, WorkPageViewModel, WorkPageViewModel>> =
            Single.zip(
                    workUseCase.isFavorite(workViewModel),
                    getVideosUseCase(workViewModel.type, workViewModel.id),
                    getCastsUseCase(workViewModel.type, workViewModel.id),
                    getRecommendationByWorkUseCase(workViewModel.type, workViewModel.id, 1),
                    getSimilarByWorkUseCase(workViewModel.type, workViewModel.id, 1),
                    Function5 { isFavorite, casts, recommendedMovies, similarMovies, videos ->
                        Quintuple(isFavorite, casts, recommendedMovies, similarMovies, videos)
                    }
            )
}