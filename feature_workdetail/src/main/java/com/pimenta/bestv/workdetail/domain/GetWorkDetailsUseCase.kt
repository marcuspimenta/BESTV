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
import com.pimenta.bestv.model.domain.emptyPage
import com.pimenta.bestv.model.extension.asyncCatching
import com.pimenta.bestv.model.presentation.model.WorkViewModel
import com.pimenta.bestv.workdetail.domain.model.ReviewDomainModel
import com.pimenta.bestv.workdetail.domain.model.CrewDomainModel
import com.pimenta.bestv.workdetail.domain.model.VideoDomainModel
import com.pimenta.bestv.workdetail.domain.model.WorkMetadataDomainModel
import com.pimenta.bestv.workdetail.domain.model.WatchProvidersDomainModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.awaitAll
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
    private val getWatchProvidersUseCase: GetWatchProvidersUseCase,
    private val getWorkMetadataUseCase: GetWorkMetadataUseCase,
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    suspend operator fun invoke(
        workViewModel: WorkViewModel,
        countryCode: String,
    ) = coroutineScope {
        val isFavoriteDeferred = asyncCatching { checkFavoriteWorkUseCase(workViewModel) }
        val videosDeferred = asyncCatching { getVideosUseCase(workViewModel.type, workViewModel.id) }
        val creditsDeferred = asyncCatching { getCastsUseCase(workViewModel.type, workViewModel.id) }
        val recommendedDeferred = asyncCatching { getRecommendationByWorkUseCase(workViewModel.type, workViewModel.id, 1) }
        val similarDeferred = asyncCatching { getSimilarByWorkUseCase(workViewModel.type, workViewModel.id, 1) }
        val reviewsDeferred = asyncCatching { getReviewByWorkUseCase(workViewModel.type, workViewModel.id, 1) }
        val watchProvidersDeferred = asyncCatching {
            getWatchProvidersUseCase(workViewModel.type, workViewModel.id, countryCode)
        }
        val metadataDeferred = asyncCatching { getWorkMetadataUseCase(workViewModel.type, workViewModel.id) }

        val results = awaitAll(
            isFavoriteDeferred,
            videosDeferred,
            creditsDeferred,
            recommendedDeferred,
            similarDeferred,
            reviewsDeferred,
            watchProvidersDeferred,
            metadataDeferred,
        )
        // The local favorite lookup must not mask a complete remote failure.
        val remoteResults = results.drop(1)
        if (remoteResults.all { it.isFailure }) {
            remoteResults.first().getOrThrow()
        }

        // awaitAll guarantees completion before reading the typed results.
        val credits = creditsDeferred.getCompleted().getOrNull()
        WorkDetailsDomainWrapper(
            isFavorite = isFavoriteDeferred.getCompleted().getOrNull() ?: workViewModel.isFavorite,
            videos = videosDeferred.getCompleted().getOrNull(),
            casts = credits?.casts,
            crew = credits?.crew,
            recommended = recommendedDeferred.getCompleted().getOrElse { emptyPage() },
            similar = similarDeferred.getCompleted().getOrElse { emptyPage() },
            reviews = reviewsDeferred.getCompleted().getOrElse { emptyPage() },
            watchProviders = watchProvidersDeferred.getCompleted().getOrNull(),
            metadata = metadataDeferred.getCompleted().getOrNull(),
        )
    }

    data class WorkDetailsDomainWrapper(
        val isFavorite: Boolean,
        val videos: List<VideoDomainModel>?,
        val casts: List<CastDomainModel>?,
        val recommended: PageDomainModel<WorkDomainModel>,
        val similar: PageDomainModel<WorkDomainModel>,
        val reviews: PageDomainModel<ReviewDomainModel>,
        val watchProviders: WatchProvidersDomainModel?,
        val metadata: WorkMetadataDomainModel? = null,
        val crew: List<CrewDomainModel>? = null,
    )
}
