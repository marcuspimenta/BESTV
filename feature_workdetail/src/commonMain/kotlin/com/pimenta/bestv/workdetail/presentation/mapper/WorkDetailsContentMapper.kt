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

package com.pimenta.bestv.workdetail.presentation.mapper

import com.pimenta.bestv.platform.currentLocaleTag
import com.pimenta.bestv.presentation.mapper.toViewModel
import com.pimenta.bestv.presentation.model.PaginationState
import com.pimenta.bestv.presentation.model.WorkViewModel
import com.pimenta.bestv.presentation.platform.ContentSourceLabelProvider
import com.pimenta.bestv.presentation.ui.compose.LazyContentTypes
import com.pimenta.bestv.workdetail.domain.GetWorkDetailsUseCase.WorkDetailsDomainWrapper
import com.pimenta.bestv.workdetail.domain.model.WorkMetadataDomainModel
import com.pimenta.bestv.workdetail.presentation.model.WorkDetailsState.ActionButton.*
import com.pimenta.bestv.workdetail.presentation.model.WorkDetailsState.Content.*
import com.pimenta.bestv.workdetail.presentation.model.WorkDetailsState.State.Loaded

internal fun WorkDetailsDomainWrapper.toLoadedState(work: WorkViewModel, resource: ContentSourceLabelProvider) =
    Loaded(
        indexOfContentToScroll = null,
        contents = listOfNotNull(
            toHeader(work),
            toVideos(),
            toCasts(resource),
            toRecommendedWorks(resource),
            toSimilarWorks(resource),
            toReviews(),
        ),
        error = null,
    )

private fun WorkDetailsDomainWrapper.toHeader(work: WorkViewModel) =
    Header(
        actions = listOfNotNull(
            SaveWork(isFavorite),
            ScrollToVideos.takeIf { videos?.isNotEmpty() == true },
            ScrollToCasts.takeIf { casts?.isNotEmpty() == true },
            ScrollToRecommendedWorks.takeIf { recommended.results?.isNotEmpty() == true },
            ScrollToSimilarWorks.takeIf { similar.results?.isNotEmpty() == true },
            ScrollToReviews.takeIf { reviews.results?.isNotEmpty() == true },
        ),
        watchProviders = watchProviders?.toViewModel()?.takeIf { it.hasAnyProvider },
        metadata = (metadata ?: WorkMetadataDomainModel())
            .toViewModel(currentLocaleTag(), work.originalLanguage)
            .takeIf { it.hasContent },
        crew = crew.orEmpty().map { it.toViewModel() },
    ).takeIf { it.actions.isNotEmpty() }

private fun WorkDetailsDomainWrapper.toVideos() = videos?.let { videos ->
    Videos(
        videos.mapIndexed { index, video ->
            video.toViewModel(fallbackKey = "${LazyContentTypes.INDEX_KEY_PREFIX}$index")
        },
    ).takeIf { it.videos.isNotEmpty() }
}

private fun WorkDetailsDomainWrapper.toCasts(resource: ContentSourceLabelProvider) = casts?.let { casts ->
    Casts(casts.mapNotNull { it.toViewModel(resource) }).takeIf { it.casts.isNotEmpty() }
}

private fun WorkDetailsDomainWrapper.toRecommendedWorks(resource: ContentSourceLabelProvider) =
    recommended.results?.let { works ->
        RecommendedWorks(
            recommended = works.mapNotNull { it.toViewModel(resource) },
            page = PaginationState(currentPage = recommended.page, totalPages = recommended.totalPages),
        ).takeIf { it.recommended.isNotEmpty() }
    }

private fun WorkDetailsDomainWrapper.toSimilarWorks(resource: ContentSourceLabelProvider) =
    similar.results?.let { works ->
        SimilarWorks(
            similar = works.mapNotNull { it.toViewModel(resource) },
            page = PaginationState(currentPage = similar.page, totalPages = similar.totalPages),
        ).takeIf { it.similar.isNotEmpty() }
    }

private fun WorkDetailsDomainWrapper.toReviews() = reviews.results?.let { reviews ->
    Reviews(
        reviews = reviews.map { it.toViewModel() },
        page = PaginationState(currentPage = this.reviews.page, totalPages = this.reviews.totalPages),
    ).takeIf { it.reviews.isNotEmpty() }
}
