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
import com.pimenta.bestv.workdetail.domain.model.WatchProvidersDomainModel
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

/**
 * Created by marcus on 20-05-2019.
 */
class GetWorkDetailsUseCase(
    private val checkFavoriteWorkUseCase: CheckFavoriteWorkUseCase,
    private val getVideosUseCase: GetVideosUseCase,
    private val getCastsUseCase: GetCastsUseCase,
    private val getRecommendationByWorkUseCase: GetRecommendationByWorkUseCase,
    private val getSimilarByWorkUseCase: GetSimilarByWorkUseCase,
    private val getReviewByWorkUseCase: GetReviewByWorkUseCase,
    private val getWatchProvidersUseCase: GetWatchProvidersUseCase
) {

    suspend operator fun invoke(workViewModel: WorkViewModel, countryCode: String): WorkDetailsDomainWrapper = coroutineScope {
        val isFavoriteDeferred = async { checkFavoriteWorkUseCase(workViewModel) }
        val videosDeferred = async { getVideosUseCase(workViewModel.type, workViewModel.id) }
        val castsDeferred = async { getCastsUseCase(workViewModel.type, workViewModel.id) }
        val recommendedDeferred = async { getRecommendationByWorkUseCase(workViewModel.type, workViewModel.id, 1) }
        val similarDeferred = async { getSimilarByWorkUseCase(workViewModel.type, workViewModel.id, 1) }
        val reviewsDeferred = async { getReviewByWorkUseCase(workViewModel.type, workViewModel.id, 1) }
        val watchProvidersDeferred = async {
            runCatching { getWatchProvidersUseCase(workViewModel.type, workViewModel.id, countryCode) }.getOrNull()
        }

        WorkDetailsDomainWrapper(
            isFavorite = isFavoriteDeferred.await(),
            videos = videosDeferred.await(),
            casts = castsDeferred.await(),
            recommended = recommendedDeferred.await(),
            similar = similarDeferred.await(),
            reviews = reviewsDeferred.await(),
            watchProviders = watchProvidersDeferred.await()
        )
    }

    data class WorkDetailsDomainWrapper(
        val isFavorite: Boolean,
        val videos: List<VideoDomainModel>?,
        val casts: List<CastDomainModel>?,
        val recommended: PageDomainModel<WorkDomainModel>,
        val similar: PageDomainModel<WorkDomainModel>,
        val reviews: PageDomainModel<ReviewDomainModel>,
        val watchProviders: WatchProvidersDomainModel?
    )
}
