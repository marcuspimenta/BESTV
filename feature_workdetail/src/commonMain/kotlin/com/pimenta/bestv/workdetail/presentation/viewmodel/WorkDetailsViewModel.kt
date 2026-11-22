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

import androidx.lifecycle.viewModelScope
import com.pimenta.bestv.presentation.mapper.toViewModel
import com.pimenta.bestv.presentation.model.CastViewModel
import com.pimenta.bestv.presentation.model.PageViewModel
import com.pimenta.bestv.presentation.model.PaginationState
import com.pimenta.bestv.presentation.model.WorkViewModel
import com.pimenta.bestv.presentation.extension.firstOf
import com.pimenta.bestv.presentation.extension.replaceFirst
import com.pimenta.bestv.presentation.viewmodel.BaseViewModel
import com.pimenta.bestv.presentation.platform.ContentSourceLabelProvider
import com.pimenta.bestv.presentation.route.castdetail.CastDetailsRoute
import com.pimenta.bestv.presentation.route.WorkDetailsDestination
import com.pimenta.bestv.workdetail.domain.GetRecommendationByWorkUseCase
import com.pimenta.bestv.workdetail.domain.GetReviewByWorkUseCase
import com.pimenta.bestv.workdetail.domain.GetSimilarByWorkUseCase
import com.pimenta.bestv.workdetail.domain.GetWorkDetailsUseCase
import com.pimenta.bestv.workdetail.domain.SetFavoriteUseCase
import com.pimenta.bestv.workdetail.presentation.mapper.toViewModel
import com.pimenta.bestv.workdetail.presentation.mapper.toDomainType
import com.pimenta.bestv.workdetail.presentation.mapper.toLoadedState
import com.pimenta.bestv.workdetail.presentation.model.ErrorType
import com.pimenta.bestv.workdetail.presentation.model.ErrorType.FavoriteError
import com.pimenta.bestv.workdetail.presentation.model.VideoViewModel
import com.pimenta.bestv.workdetail.presentation.model.WorkDetailsEffect
import com.pimenta.bestv.workdetail.presentation.model.WorkDetailsEvent
import com.pimenta.bestv.workdetail.presentation.model.WorkDetailsEvent.ActionButtonClicked
import com.pimenta.bestv.workdetail.presentation.model.WorkDetailsEvent.CastClicked
import com.pimenta.bestv.workdetail.presentation.model.WorkDetailsEvent.ClearScrollIndex
import com.pimenta.bestv.workdetail.presentation.model.WorkDetailsEvent.DismissError
import com.pimenta.bestv.workdetail.presentation.model.WorkDetailsEvent.LoadData
import com.pimenta.bestv.workdetail.presentation.model.WorkDetailsEvent.LoadMoreRecommendations
import com.pimenta.bestv.workdetail.presentation.model.WorkDetailsEvent.LoadMoreReviews
import com.pimenta.bestv.workdetail.presentation.model.WorkDetailsEvent.LoadMoreSimilar
import com.pimenta.bestv.workdetail.presentation.model.WorkDetailsEvent.ShowError
import com.pimenta.bestv.workdetail.presentation.model.WorkDetailsEvent.VideoClicked
import com.pimenta.bestv.workdetail.presentation.model.WorkDetailsEvent.WorkClicked
import com.pimenta.bestv.workdetail.presentation.model.WorkDetailsState
import com.pimenta.bestv.workdetail.presentation.model.WorkDetailsState.ActionButton
import com.pimenta.bestv.workdetail.presentation.model.WorkDetailsState.ActionButton.SaveWork
import com.pimenta.bestv.workdetail.presentation.model.WorkDetailsState.Content.Header
import com.pimenta.bestv.workdetail.presentation.model.WorkDetailsState.Content.RecommendedWorks
import com.pimenta.bestv.workdetail.presentation.model.WorkDetailsState.Content.Reviews
import com.pimenta.bestv.workdetail.presentation.model.WorkDetailsState.Content.SimilarWorks
import com.pimenta.bestv.workdetail.presentation.model.WorkDetailsState.State.Error
import com.pimenta.bestv.workdetail.presentation.model.WorkDetailsState.State.Loaded
import com.pimenta.bestv.workdetail.presentation.model.WorkDetailsState.State.Loading
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import com.pimenta.bestv.logging.logError
import com.pimenta.bestv.platform.currentCountryCode

/**
 * ViewModel for the Work Details screen following MVI architecture.
 * Manages the state and handles user events.
 *
 * Created by marcus on 20-10-2025.
 */
class WorkDetailsViewModel(
    private val work: WorkViewModel,
    private val setFavoriteUseCase: SetFavoriteUseCase,
    private val getWorkDetailsUseCase: GetWorkDetailsUseCase,
    private val getReviewByWorkUseCase: GetReviewByWorkUseCase,
    private val getRecommendationByWorkUseCase: GetRecommendationByWorkUseCase,
    private val getSimilarByWorkUseCase: GetSimilarByWorkUseCase,
    private val castDetailsRoute: CastDetailsRoute,
    private val resource: ContentSourceLabelProvider,
) : BaseViewModel<WorkDetailsState, WorkDetailsEffect>(
    WorkDetailsState(work, Loading),
) {
    fun handleEvent(event: WorkDetailsEvent) {
        when (event) {
            is LoadData -> loadData()
            is ActionButtonClicked -> actionButtonClicked(event.action)
            is LoadMoreReviews -> loadMoreReviews()
            is LoadMoreRecommendations -> loadMoreRecommendations()
            is LoadMoreSimilar -> loadMoreSimilar()
            is WorkClicked -> handleWorkClicked(event.work)
            is CastClicked -> handleCastClicked(event.cast)
            is VideoClicked -> handleVideoClicked(event.video)
            is ShowError -> showError(event.error)
            is DismissError -> dismissError()
            is ClearScrollIndex -> clearScrollIndex()
        }
    }

    private fun loadData() {
        if (currentState.state !is Loading) return

        viewModelScope.launch {
            try {
                updateState { it.copy(state = Loading) }

                val countryCode = currentCountryCode().takeIf(String::isNotEmpty) ?: "US"
                val result = getWorkDetailsUseCase(work.id, work.type.toDomainType(), work.isFavorite, countryCode)

                updateState { it.copy(state = result.toLoadedState(work, resource)) }
            } catch (exception: CancellationException) {
                throw exception
            } catch (exception: Exception) {
                logError(exception, "Error while loading work details")
                updateState {
                    it.copy(state = Error(ErrorType.LoadingError))
                }
            }
        }
    }

    private fun actionButtonClicked(actionButton: ActionButton) {
        when (actionButton) {
            is SaveWork -> toggleFavorite()
            else -> {
                updateState {
                    it.copy(
                        state = (it.state as Loaded).copy(
                            indexOfContentToScroll = it.state.contents
                                .firstOf<Header>()
                                .actions
                                .indexOf(actionButton),
                        ),
                    )
                }
            }
        }
    }

    private fun toggleFavorite() {
        viewModelScope.launch {
            try {
                val currentFavoriteState = (currentState.state as Loaded)
                    .contents
                    .firstOf<Header>()
                    .actions
                    .firstOf<SaveWork>()
                val newFavoriteState = !currentFavoriteState.isFavorite
                setFavoriteUseCase(work.id, work.type.toDomainType(), newFavoriteState)

                updateState { currentState ->
                    currentState.copy(
                        state = (currentState.state as Loaded).copy(
                            contents = currentState.state.contents.replaceFirst<Header> {
                                it.copy(
                                    actions = it.actions.replaceFirst<SaveWork> {
                                        it.copy(isFavorite = newFavoriteState)
                                    },
                                )
                            },
                        ),
                    )
                }
            } catch (cancellation: CancellationException) {
                throw cancellation
            } catch (exception: Exception) {
                logError(exception, "Error while toggling favorite")

                updateState { currentState ->
                    currentState.copy(
                        state = (currentState.state as Loaded).copy(
                            error = FavoriteError,
                        ),
                    )
                }
            }
        }
    }

    private fun loadMoreReviews() = loadMoreContent(
        page = (currentState.state as Loaded).contents.firstOf<Reviews>().page,
        errorMessage = "Error while loading more reviews",
        loadPage = { page -> getReviewByWorkUseCase(work.type.toDomainType(), work.id, page).toViewModel() },
        onStarted = WorkDetailsState::reviewsPaginationStarted,
        onSuccess = WorkDetailsState::reviewsPaginationSucceeded,
        onFailure = WorkDetailsState::reviewsPaginationFailed,
    )

    private fun loadMoreRecommendations() = loadMoreContent(
        page = (currentState.state as Loaded).contents.firstOf<RecommendedWorks>().page,
        errorMessage = "Error while loading more recommendations",
        loadPage = { page ->
            getRecommendationByWorkUseCase(work.type.toDomainType(), work.id, page).toViewModel(resource)
        },
        onStarted = WorkDetailsState::recommendationsPaginationStarted,
        onSuccess = WorkDetailsState::recommendationsPaginationSucceeded,
        onFailure = WorkDetailsState::recommendationsPaginationFailed,
    )

    private fun loadMoreSimilar() = loadMoreContent(
        page = (currentState.state as Loaded).contents.firstOf<SimilarWorks>().page,
        errorMessage = "Error while loading more similar works",
        loadPage = { page -> getSimilarByWorkUseCase(work.type.toDomainType(), work.id, page).toViewModel(resource) },
        onStarted = WorkDetailsState::similarPaginationStarted,
        onSuccess = WorkDetailsState::similarPaginationSucceeded,
        onFailure = WorkDetailsState::similarPaginationFailed,
    )

    private fun <T> loadMoreContent(
        page: PaginationState,
        errorMessage: String,
        loadPage: suspend (Int) -> PageViewModel<T>,
        onStarted: (WorkDetailsState) -> WorkDetailsState,
        onSuccess: (WorkDetailsState, PageViewModel<T>) -> WorkDetailsState,
        onFailure: (WorkDetailsState) -> WorkDetailsState,
    ) {
        if (!page.canLoadMore || page.isLoadingMore) return
        updateState(onStarted)
        viewModelScope.launch {
            try {
                val result = loadPage(page.currentPage + 1)
                updateState { state -> onSuccess(state, result) }
            } catch (cancellation: CancellationException) {
                throw cancellation
            } catch (exception: Exception) {
                logError(exception, errorMessage)
                updateState(onFailure)
            }
        }
    }

    private fun handleWorkClicked(work: WorkViewModel) {
        emitEffect(WorkDetailsEffect.Navigate(WorkDetailsDestination.create(work)))
    }

    private fun handleCastClicked(cast: CastViewModel) {
        emitEffect(WorkDetailsEffect.Navigate(castDetailsRoute.buildCastDetailsDestination(cast.id)))
    }

    private fun handleVideoClicked(video: VideoViewModel) {
        video.youtubeUrl?.let {
            emitEffect(WorkDetailsEffect.OpenExternalUrl(it))
        }
    }

    private fun showError(error: ErrorType) {
        updateState { currentState ->
            val loadedState = currentState.state as? Loaded ?: return@updateState currentState
            currentState.copy(
                state = loadedState.copy(error = error),
            )
        }
    }

    private fun dismissError() {
        updateState { currentState ->
            val loadedState = currentState.state as? Loaded ?: return@updateState currentState
            currentState.copy(
                state = loadedState.copy(error = null),
            )
        }
    }

    private fun clearScrollIndex() {
        updateState { currentState ->
            currentState.copy(
                state = (currentState.state as Loaded).copy(
                    indexOfContentToScroll = null,
                ),
            )
        }
    }
}
