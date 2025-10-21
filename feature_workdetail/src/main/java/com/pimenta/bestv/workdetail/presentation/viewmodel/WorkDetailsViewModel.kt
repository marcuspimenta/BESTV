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

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pimenta.bestv.model.presentation.mapper.toViewModel
import com.pimenta.bestv.model.presentation.model.WorkViewModel
import com.pimenta.bestv.workdetail.domain.GetRecommendationByWorkUseCase
import com.pimenta.bestv.workdetail.domain.GetReviewByWorkUseCase
import com.pimenta.bestv.workdetail.domain.GetSimilarByWorkUseCase
import com.pimenta.bestv.workdetail.domain.GetWorkDetailsUseCase
import com.pimenta.bestv.workdetail.domain.SetFavoriteUseCase
import com.pimenta.bestv.workdetail.presentation.mapper.toViewModel
import com.pimenta.bestv.workdetail.presentation.model.ErrorState
import com.pimenta.bestv.workdetail.presentation.model.PaginationState
import com.pimenta.bestv.workdetail.presentation.model.WorkDetailsEffect
import com.pimenta.bestv.workdetail.presentation.model.WorkDetailsEvent
import com.pimenta.bestv.workdetail.presentation.model.WorkDetailsState
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

/**
 * ViewModel for the Work Details screen following MVI architecture.
 * Manages the state and handles user events.
 */
class WorkDetailsViewModel @Inject constructor(
    private val work: WorkViewModel,
    private val setFavoriteUseCase: SetFavoriteUseCase,
    private val getWorkDetailsUseCase: GetWorkDetailsUseCase,
    private val getReviewByWorkUseCase: GetReviewByWorkUseCase,
    private val getRecommendationByWorkUseCase: GetRecommendationByWorkUseCase,
    private val getSimilarByWorkUseCase: GetSimilarByWorkUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(WorkDetailsState(work = work))
    val state: StateFlow<WorkDetailsState> = _state.asStateFlow()

    private val _effects = Channel<WorkDetailsEffect>(Channel.BUFFERED)
    val effects = _effects.receiveAsFlow()

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
                _state.update { it.copy(isLoading = true, error = null) }

                val result = getWorkDetailsUseCase(work)

                val data = WorkDetailsData(
                    isFavorite = result.isFavorite,
                    videos = result.videos?.map { it.toViewModel() } ?: emptyList(),
                    casts = result.casts?.map { it.toViewModel() } ?: emptyList(),
                    recommendedWorks = result.recommended.results?.map { it.toViewModel() } ?: emptyList(),
                    recommendedPagination = PaginationState(
                        currentPage = result.recommended.page,
                        totalPages = result.recommended.totalPages
                    ),
                    similarWorks = result.similar.results?.map { it.toViewModel() } ?: emptyList(),
                    similarPagination = PaginationState(
                        currentPage = result.similar.page,
                        totalPages = result.similar.totalPages
                    ),
                    reviews = result.reviews.results?.map { it.toViewModel() } ?: emptyList(),
                    reviewPagination = PaginationState(
                        currentPage = result.reviews.page,
                        totalPages = result.reviews.totalPages
                    )
                )

                _state.update {
                    it.copy(
                        isLoading = false,
                        isFavorite = data.isFavorite,
                        videos = data.videos,
                        casts = data.casts,
                        recommendedWorks = data.recommendedWorks,
                        recommendedPagination = data.recommendedPagination,
                        similarWorks = data.similarWorks,
                        similarPagination = data.similarPagination,
                        reviews = data.reviews,
                        reviewPagination = data.reviewPagination,
                        error = null
                    )
                }
            } catch (throwable: Throwable) {
                Timber.e(throwable, "Error while loading work details")
                _state.update {
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
                val currentFavoriteState = _state.value.isFavorite
                val updatedWork = work.copy(isFavorite = currentFavoriteState)

                setFavoriteUseCase(updatedWork)

                val newFavoriteState = !currentFavoriteState
                _state.update { it.copy(isFavorite = newFavoriteState) }
                sendEffect(WorkDetailsEffect.ShowFavoriteSuccess(newFavoriteState))
            } catch (throwable: Throwable) {
                Timber.e(throwable, "Error while toggling favorite")
                _state.update {
                    it.copy(error = ErrorState.FavoriteError("Failed to update favorite status"))
                }
            }
        }
    }

    private fun handleReviewItemSelected(review: com.pimenta.bestv.workdetail.presentation.model.ReviewViewModel) {
        val currentState = _state.value
        val reviewIndex = currentState.reviews.indexOf(review)

        // Check if we need to load more
        if (reviewIndex < currentState.reviews.size - 1 || !currentState.reviewPagination.canLoadMore) {
            return
        }

        _state.update {
            it.copy(
                reviewPagination = it.reviewPagination.copy(isLoadingMore = true)
            )
        }

        viewModelScope.launch {
            try {
                val nextPage = currentState.reviewPagination.currentPage + 1
                val pageResult = getReviewByWorkUseCase(work.type, work.id, nextPage).toViewModel()

                _state.update { currentState ->
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
                _state.update {
                    it.copy(
                        reviewPagination = it.reviewPagination.copy(isLoadingMore = false),
                        error = ErrorState.PaginationError("Failed to load more reviews")
                    )
                }
            }
        }
    }

    private fun handleRecommendationItemSelected(selectedWork: WorkViewModel) {
        val currentState = _state.value
        val workIndex = currentState.recommendedWorks.indexOf(selectedWork)

        // Check if we need to load more
        if (workIndex < currentState.recommendedWorks.size - 1 || !currentState.recommendedPagination.canLoadMore) {
            return
        }

        _state.update {
            it.copy(
                recommendedPagination = it.recommendedPagination.copy(isLoadingMore = true)
            )
        }

        viewModelScope.launch {
            try {
                val nextPage = currentState.recommendedPagination.currentPage + 1
                val pageResult = getRecommendationByWorkUseCase(work.type, work.id, nextPage).toViewModel()

                _state.update { currentState ->
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
                _state.update {
                    it.copy(
                        recommendedPagination = it.recommendedPagination.copy(isLoadingMore = false),
                        error = ErrorState.PaginationError("Failed to load more recommendations")
                    )
                }
            }
        }
    }

    private fun handleSimilarItemSelected(selectedWork: WorkViewModel) {
        val currentState = _state.value
        val workIndex = currentState.similarWorks.indexOf(selectedWork)

        // Check if we need to load more
        if (workIndex < currentState.similarWorks.size - 1 || !currentState.similarPagination.canLoadMore) {
            return
        }

        _state.update {
            it.copy(
                similarPagination = it.similarPagination.copy(isLoadingMore = true)
            )
        }

        viewModelScope.launch {
            try {
                val nextPage = currentState.similarPagination.currentPage + 1
                val pageResult = getSimilarByWorkUseCase(work.type, work.id, nextPage).toViewModel()

                _state.update { currentState ->
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
                _state.update {
                    it.copy(
                        similarPagination = it.similarPagination.copy(isLoadingMore = false),
                        error = ErrorState.PaginationError("Failed to load more similar works")
                    )
                }
            }
        }
    }

    private fun handleWorkClicked(work: WorkViewModel) {
        sendEffect(WorkDetailsEffect.NavigateToWorkDetails(work))
    }

    private fun handleCastClicked(cast: com.pimenta.bestv.model.presentation.model.CastViewModel) {
        sendEffect(WorkDetailsEffect.NavigateToCastDetails(cast))
    }

    private fun handleVideoClicked(video: com.pimenta.bestv.workdetail.presentation.model.VideoViewModel) {
        sendEffect(WorkDetailsEffect.OpenVideo(video))
    }

    private fun dismissError() {
        _state.update { it.copy(error = null) }
    }

    private fun sendEffect(effect: WorkDetailsEffect) {
        _effects.trySend(effect)
    }

    /**
     * Internal data class for mapping domain results
     */
    private data class WorkDetailsData(
        val isFavorite: Boolean,
        val videos: List<com.pimenta.bestv.workdetail.presentation.model.VideoViewModel>,
        val casts: List<com.pimenta.bestv.model.presentation.model.CastViewModel>,
        val recommendedWorks: List<WorkViewModel>,
        val recommendedPagination: PaginationState,
        val similarWorks: List<WorkViewModel>,
        val similarPagination: PaginationState,
        val reviews: List<com.pimenta.bestv.workdetail.presentation.model.ReviewViewModel>,
        val reviewPagination: PaginationState
    )
}
