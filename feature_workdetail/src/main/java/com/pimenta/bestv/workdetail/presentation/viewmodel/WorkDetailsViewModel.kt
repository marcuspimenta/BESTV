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

import android.content.Intent
import androidx.core.net.toUri
import androidx.lifecycle.viewModelScope
import com.pimenta.bestv.model.presentation.mapper.toViewModel
import com.pimenta.bestv.model.presentation.model.CastViewModel
import com.pimenta.bestv.model.presentation.model.WorkViewModel
import com.pimenta.bestv.presentation.presenter.BaseViewModel
import com.pimenta.bestv.route.castdetail.CastDetailsRoute
import com.pimenta.bestv.route.workdetail.WorkDetailsRoute
import com.pimenta.bestv.workdetail.domain.GetRecommendationByWorkUseCase
import com.pimenta.bestv.workdetail.domain.GetReviewByWorkUseCase
import com.pimenta.bestv.workdetail.domain.GetSimilarByWorkUseCase
import com.pimenta.bestv.workdetail.domain.GetWorkDetailsUseCase
import com.pimenta.bestv.workdetail.domain.SetFavoriteUseCase
import com.pimenta.bestv.workdetail.presentation.mapper.toViewModel
import com.pimenta.bestv.workdetail.presentation.model.ErrorState
import com.pimenta.bestv.workdetail.presentation.model.PaginationState
import com.pimenta.bestv.workdetail.presentation.model.VideoViewModel
import com.pimenta.bestv.workdetail.presentation.model.WorkDetailsEffect
import com.pimenta.bestv.workdetail.presentation.model.WorkDetailsEvent
import com.pimenta.bestv.workdetail.presentation.model.WorkDetailsState
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

/**
 * ViewModel for the Work Details screen following MVI architecture.
 * Manages the state and handles user events.
 *
 * Created by marcus on 20-10-2025.
 */
class WorkDetailsViewModel @Inject constructor(
    private val work: WorkViewModel,
    private val setFavoriteUseCase: SetFavoriteUseCase,
    private val getWorkDetailsUseCase: GetWorkDetailsUseCase,
    private val getReviewByWorkUseCase: GetReviewByWorkUseCase,
    private val getRecommendationByWorkUseCase: GetRecommendationByWorkUseCase,
    private val getSimilarByWorkUseCase: GetSimilarByWorkUseCase,
    private val workDetailsRoute: WorkDetailsRoute,
    private val castDetailsRoute: CastDetailsRoute,
) : BaseViewModel<WorkDetailsState, WorkDetailsEffect>(WorkDetailsState(work = work)) {

    /**
     * Handle user events
     */
    fun handleEvent(event: WorkDetailsEvent) {
        when (event) {
            is WorkDetailsEvent.LoadData -> loadData()
            is WorkDetailsEvent.ToggleFavorite -> toggleFavorite()
            is WorkDetailsEvent.ReviewItemSelected -> handleReviewItemSelected(event.review)
            is WorkDetailsEvent.RecommendationItemSelected -> handleRecommendationItemSelected(event.work)
            is WorkDetailsEvent.SimilarItemSelected -> handleSimilarItemSelected(event.work)
            is WorkDetailsEvent.WorkClicked -> handleWorkClicked(event.work)
            is WorkDetailsEvent.CastClicked -> handleCastClicked(event.cast)
            is WorkDetailsEvent.VideoClicked -> handleVideoClicked(event.video)
            is WorkDetailsEvent.DismissError -> dismissError()
        }
    }

    private fun loadData() {
        viewModelScope.launch {
            try {
                updateState { it.copy(isLoading = true, error = null) }

                val result = getWorkDetailsUseCase(work)

                updateState {
                    it.copy(
                        isLoading = false,
                        isFavorite = result.isFavorite,
                        videos = result.videos?.map { video -> video.toViewModel() } ?: emptyList(),
                        casts = result.casts?.map { cast -> cast.toViewModel() } ?: emptyList(),
                        recommendedWorks = result.recommended.results?.map { work -> work.toViewModel() } ?: emptyList(),
                        recommendedPagination = PaginationState(
                            currentPage = result.recommended.page,
                            totalPages = result.recommended.totalPages
                        ),
                        similarWorks = result.similar.results?.map { work -> work.toViewModel() } ?: emptyList(),
                        similarPagination = PaginationState(
                            currentPage = result.similar.page,
                            totalPages = result.similar.totalPages
                        ),
                        reviews = result.reviews.results?.map { review -> review.toViewModel() } ?: emptyList(),
                        reviewPagination = PaginationState(
                            currentPage = result.reviews.page,
                            totalPages = result.reviews.totalPages
                        ),
                        error = null
                    )
                }
            } catch (throwable: Throwable) {
                Timber.e(throwable, "Error while loading work details")
                updateState {
                    it.copy(
                        isLoading = false,
                        error = ErrorState.LoadingError("Failed to load work details")
                    )
                }
            }
        }
    }

    private fun toggleFavorite() {
        viewModelScope.launch {
            try {
                val currentFavoriteState = currentState.isFavorite
                val updatedWork = work.copy(isFavorite = currentFavoriteState)

                setFavoriteUseCase(updatedWork)

                val newFavoriteState = !currentFavoriteState
                updateState { it.copy(isFavorite = newFavoriteState) }
            } catch (throwable: Throwable) {
                Timber.e(throwable, "Error while toggling favorite")
                updateState {
                    it.copy(error = ErrorState.FavoriteError("Failed to update favorite status"))
                }
            }
        }
    }

    private fun handleReviewItemSelected(review: com.pimenta.bestv.workdetail.presentation.model.ReviewViewModel) {
        val state = currentState
        val reviewIndex = state.reviews.indexOf(review)

        // Check if we need to load more
        if (reviewIndex < state.reviews.size - 1 || !state.reviewPagination.canLoadMore) {
            return
        }

        updateState {
            it.copy(
                reviewPagination = it.reviewPagination.copy(isLoadingMore = true)
            )
        }

        viewModelScope.launch {
            try {
                val nextPage = state.reviewPagination.currentPage + 1
                val pageResult = getReviewByWorkUseCase(work.type, work.id, nextPage).toViewModel()

                updateState { currentState ->
                    currentState.copy(
                        reviews = currentState.reviews + (pageResult.results ?: emptyList()),
                        reviewPagination = PaginationState(
                            currentPage = pageResult.page,
                            totalPages = pageResult.totalPages,
                            isLoadingMore = false
                        )
                    )
                }
            } catch (throwable: Throwable) {
                Timber.e(throwable, "Error while loading more reviews")
                updateState {
                    it.copy(
                        reviewPagination = it.reviewPagination.copy(isLoadingMore = false),
                        error = ErrorState.PaginationError("Failed to load more reviews")
                    )
                }
            }
        }
    }

    private fun handleRecommendationItemSelected(selectedWork: WorkViewModel) {
        val state = currentState
        val workIndex = state.recommendedWorks.indexOf(selectedWork)

        // Check if we need to load more
        if (workIndex < state.recommendedWorks.size - 1 || !state.recommendedPagination.canLoadMore) {
            return
        }

        updateState {
            it.copy(
                recommendedPagination = it.recommendedPagination.copy(isLoadingMore = true)
            )
        }

        viewModelScope.launch {
            try {
                val nextPage = state.recommendedPagination.currentPage + 1
                val pageResult = getRecommendationByWorkUseCase(work.type, work.id, nextPage).toViewModel()

                updateState { currentState ->
                    currentState.copy(
                        recommendedWorks = currentState.recommendedWorks + (pageResult.results ?: emptyList()),
                        recommendedPagination = PaginationState(
                            currentPage = pageResult.page,
                            totalPages = pageResult.totalPages,
                            isLoadingMore = false
                        )
                    )
                }
            } catch (throwable: Throwable) {
                Timber.e(throwable, "Error while loading more recommendations")
                updateState {
                    it.copy(
                        recommendedPagination = it.recommendedPagination.copy(isLoadingMore = false),
                        error = ErrorState.PaginationError("Failed to load more recommendations")
                    )
                }
            }
        }
    }

    private fun handleSimilarItemSelected(selectedWork: WorkViewModel) {
        val state = currentState
        val workIndex = state.similarWorks.indexOf(selectedWork)

        // Check if we need to load more
        if (workIndex < state.similarWorks.size - 1 || !state.similarPagination.canLoadMore) {
            return
        }

        updateState {
            it.copy(
                similarPagination = it.similarPagination.copy(isLoadingMore = true)
            )
        }

        viewModelScope.launch {
            try {
                val nextPage = state.similarPagination.currentPage + 1
                val pageResult = getSimilarByWorkUseCase(work.type, work.id, nextPage).toViewModel()

                updateState { currentState ->
                    currentState.copy(
                        similarWorks = currentState.similarWorks + (pageResult.results ?: emptyList()),
                        similarPagination = PaginationState(
                            currentPage = pageResult.page,
                            totalPages = pageResult.totalPages,
                            isLoadingMore = false
                        )
                    )
                }
            } catch (throwable: Throwable) {
                Timber.e(throwable, "Error while loading more similar works")
                updateState {
                    it.copy(
                        similarPagination = it.similarPagination.copy(isLoadingMore = false),
                        error = ErrorState.PaginationError("Failed to load more similar works")
                    )
                }
            }
        }
    }

    private fun handleWorkClicked(work: WorkViewModel) {
        val intent = workDetailsRoute.buildWorkDetailIntent(work)
        emitEvent(WorkDetailsEffect.OpenIntent(intent, true))
    }

    private fun handleCastClicked(cast: CastViewModel) {
        val intent = castDetailsRoute.buildCastDetailIntent(cast)
        emitEvent(WorkDetailsEffect.OpenIntent(intent, true))
    }

    private fun handleVideoClicked(video: VideoViewModel) {
        video.youtubeUrl?.let {
            val intent = Intent(Intent.ACTION_VIEW, it.toUri())
            emitEvent(WorkDetailsEffect.OpenIntent(intent, false))
        }
    }

    private fun dismissError() {
        updateState { it.copy(error = null) }
    }
}
