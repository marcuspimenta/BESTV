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

package com.pimenta.bestv.workdetail.domain

import com.pimenta.bestv.model.domain.CastDomainModel
import com.pimenta.bestv.model.domain.PageDomainModel
import com.pimenta.bestv.model.domain.WorkDomainModel
import com.pimenta.bestv.model.presentation.model.WorkViewModel
import com.pimenta.bestv.workdetail.domain.model.ReviewDomainModel
import com.pimenta.bestv.workdetail.domain.model.VideoDomainModel
import io.reactivex.Single
import io.reactivex.functions.Function6
import javax.inject.Inject

/**
 * Created by marcus on 20-05-2019.
 */
class GetWorkDetailsUseCase @Inject constructor(
    private val checkFavoriteWorkUseCase: CheckFavoriteWorkUseCase,
    private val getVideosUseCase: GetVideosUseCase,
    private val getCastsUseCase: GetCastsUseCase,
    private val getRecommendationByWorkUseCase: GetRecommendationByWorkUseCase,
    private val getSimilarByWorkUseCase: GetSimilarByWorkUseCase,
    private val getReviewByWorkUseCase: GetReviewByWorkUseCase
) {

    operator fun invoke(workViewModel: WorkViewModel): Single<WorkDetailsDomainWrapper> =
        Single.zip(
            checkFavoriteWorkUseCase(workViewModel),
            getVideosUseCase(workViewModel.type, workViewModel.id),
            getCastsUseCase(workViewModel.type, workViewModel.id),
            getRecommendationByWorkUseCase(workViewModel.type, workViewModel.id, 1),
            getSimilarByWorkUseCase(workViewModel.type, workViewModel.id, 1),
            getReviewByWorkUseCase(workViewModel.type, workViewModel.id, 1),
            Function6 { isFavorite, videos, casts, recommended, similar, reviews ->
                WorkDetailsDomainWrapper(isFavorite, videos, casts, recommended, similar, reviews)
            }
        )

    data class WorkDetailsDomainWrapper(
        val isFavorite: Boolean,
        val videos: List<VideoDomainModel>?,
        val casts: List<CastDomainModel>?,
        val recommended: PageDomainModel<WorkDomainModel>,
        val similar: PageDomainModel<WorkDomainModel>,
        val reviews: PageDomainModel<ReviewDomainModel>
    )
}
