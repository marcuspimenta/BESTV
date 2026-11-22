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

package com.pimenta.bestv.workdetail.presentation.viewmodel

import com.pimenta.bestv.presentation.extension.replaceFirst
import com.pimenta.bestv.presentation.model.PageViewModel
import com.pimenta.bestv.presentation.model.PaginationState
import com.pimenta.bestv.presentation.model.WorkViewModel
import com.pimenta.bestv.workdetail.presentation.model.ErrorType.PaginationError
import com.pimenta.bestv.workdetail.presentation.model.ReviewViewModel
import com.pimenta.bestv.workdetail.presentation.model.WorkDetailsState
import com.pimenta.bestv.workdetail.presentation.model.WorkDetailsState.Content
import com.pimenta.bestv.workdetail.presentation.model.WorkDetailsState.Content.RecommendedWorks
import com.pimenta.bestv.workdetail.presentation.model.WorkDetailsState.Content.Reviews
import com.pimenta.bestv.workdetail.presentation.model.WorkDetailsState.Content.SimilarWorks
import com.pimenta.bestv.workdetail.presentation.model.WorkDetailsState.State.Loaded

internal fun WorkDetailsState.reviewsPaginationStarted() =
    updateContent<Reviews> { copy(page = page.copy(isLoadingMore = true)) }

internal fun WorkDetailsState.reviewsPaginationSucceeded(result: PageViewModel<ReviewViewModel>) =
    updateContent<Reviews> {
        copy(reviews = reviews + result.results, page = page.afterLoading(result))
    }

internal fun WorkDetailsState.reviewsPaginationFailed() =
    updateContent<Reviews>(error = true) { copy(page = page.copy(isLoadingMore = false)) }

internal fun WorkDetailsState.recommendationsPaginationStarted() =
    updateContent<RecommendedWorks> { copy(page = page.copy(isLoadingMore = true)) }

internal fun WorkDetailsState.recommendationsPaginationSucceeded(result: PageViewModel<WorkViewModel>) =
    updateContent<RecommendedWorks> {
        copy(recommended = recommended + result.results, page = page.afterLoading(result))
    }

internal fun WorkDetailsState.recommendationsPaginationFailed() =
    updateContent<RecommendedWorks>(error = true) { copy(page = page.copy(isLoadingMore = false)) }

internal fun WorkDetailsState.similarPaginationStarted() =
    updateContent<SimilarWorks> { copy(page = page.copy(isLoadingMore = true)) }

internal fun WorkDetailsState.similarPaginationSucceeded(result: PageViewModel<WorkViewModel>) =
    updateContent<SimilarWorks> {
        copy(similar = similar + result.results, page = page.afterLoading(result))
    }

internal fun WorkDetailsState.similarPaginationFailed() =
    updateContent<SimilarWorks>(error = true) { copy(page = page.copy(isLoadingMore = false)) }

private fun PaginationState.afterLoading(result: PageViewModel<*>) =
    copy(currentPage = result.page, totalPages = result.totalPages, isLoadingMore = false)

private inline fun <reified T : Content> WorkDetailsState.updateContent(
    error: Boolean = false,
    crossinline transform: T.() -> T,
): WorkDetailsState {
    val loaded = state as Loaded
    return copy(
        state = loaded.copy(
            contents = loaded.contents.replaceFirst<T> { it.transform() },
            error = if (error) PaginationError else loaded.error,
        ),
    )
}
