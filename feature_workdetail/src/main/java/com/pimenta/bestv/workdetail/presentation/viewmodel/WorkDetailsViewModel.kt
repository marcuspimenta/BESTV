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
import com.pimenta.bestv.presentation.extension.firstOf
import com.pimenta.bestv.presentation.extension.replaceFirst
import com.pimenta.bestv.presentation.presenter.BaseViewModel
import com.pimenta.bestv.route.castdetail.CastDetailsRoute
import com.pimenta.bestv.route.workdetail.WorkDetailsRoute
import com.pimenta.bestv.workdetail.domain.GetRecommendationByWorkUseCase
import com.pimenta.bestv.workdetail.domain.GetReviewByWorkUseCase
import com.pimenta.bestv.workdetail.domain.GetSimilarByWorkUseCase
import com.pimenta.bestv.workdetail.domain.GetWorkDetailsUseCase
import com.pimenta.bestv.workdetail.domain.SetFavoriteUseCase
import com.pimenta.bestv.workdetail.presentation.mapper.toViewModel
import com.pimenta.bestv.workdetail.presentation.model.ErrorType
import com.pimenta.bestv.workdetail.presentation.model.ErrorType.FavoriteError
import com.pimenta.bestv.workdetail.presentation.model.ErrorType.PaginationError
import com.pimenta.bestv.workdetail.presentation.model.PaginationState
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
import com.pimenta.bestv.workdetail.presentation.model.WorkDetailsState.ActionButton.ScrollToCasts
import com.pimenta.bestv.workdetail.presentation.model.WorkDetailsState.ActionButton.ScrollToRecommendedWorks
import com.pimenta.bestv.workdetail.presentation.model.WorkDetailsState.ActionButton.ScrollToReviews
import com.pimenta.bestv.workdetail.presentation.model.WorkDetailsState.ActionButton.ScrollToSimilarWorks
import com.pimenta.bestv.workdetail.presentation.model.WorkDetailsState.ActionButton.ScrollToVideos
import com.pimenta.bestv.workdetail.presentation.model.WorkDetailsState.Content.Casts
import com.pimenta.bestv.workdetail.presentation.model.WorkDetailsState.Content.Header
import com.pimenta.bestv.workdetail.presentation.model.WorkDetailsState.Content.RecommendedWorks
import com.pimenta.bestv.workdetail.presentation.model.WorkDetailsState.Content.Reviews
import com.pimenta.bestv.workdetail.presentation.model.WorkDetailsState.Content.SimilarWorks
import com.pimenta.bestv.workdetail.presentation.model.WorkDetailsState.Content.Videos
import com.pimenta.bestv.workdetail.presentation.model.WorkDetailsState.State.Error
import com.pimenta.bestv.workdetail.presentation.model.WorkDetailsState.State.Loaded
import com.pimenta.bestv.workdetail.presentation.model.WorkDetailsState.State.Loading
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
) : BaseViewModel<WorkDetailsState, WorkDetailsEffect>(WorkDetailsState(work, Loading)) {

    /**
     * Handle user events
     */
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
        viewModelScope.launch {
            try {
                updateState { it.copy(state = Loading) }

                val result = getWorkDetailsUseCase(work)

                updateState {
                    it.copy(
                        state = Loaded(
                            contents = listOfNotNull(
                                Header(
                                    listOfNotNull(
                                        SaveWork(result.isFavorite),
                                        ScrollToVideos.takeIf { result.videos?.isNotEmpty() == true },
                                        ScrollToCasts.takeIf { result.casts?.isNotEmpty() == true },
                                        ScrollToRecommendedWorks.takeIf { result.recommended.results?.isNotEmpty() == true },
                                        ScrollToSimilarWorks.takeIf { result.similar.results?.isNotEmpty() == true },
                                        ScrollToReviews.takeIf { result.reviews.results?.isNotEmpty() == true },
                                    )
                                ),
                                result.videos?.let {
                                    Videos(it.map { video -> video.toViewModel() })
                                        .takeIf { it.videos.isNotEmpty() }
                                },
                                result.casts?.let {
                                    it.mapNotNull { cast -> cast.toViewModel() }.let { casts ->
                                        Casts(casts).takeIf { casts.isNotEmpty() }
                                    }
                                },
                                result.recommended.results?.let {
                                    it.mapNotNull { work -> work.toViewModel() }.let { recos ->
                                        RecommendedWorks(
                                            recommended = recos,
                                            page = PaginationState(
                                                currentPage = result.recommended.page,
                                                totalPages = result.recommended.totalPages
                                            )
                                        ).takeIf { recos.isNotEmpty() }
                                    }
                                },
                                result.similar.results?.let {
                                    it.mapNotNull { work -> work.toViewModel() }.let { similar ->
                                        SimilarWorks(
                                            similar = similar,
                                            page = PaginationState(
                                                currentPage = result.similar.page,
                                                totalPages = result.similar.totalPages
                                            )
                                        ).takeIf { similar.isNotEmpty() }
                                    }
                                },
                                result.reviews.results?.let {
                                    Reviews(
                                        reviews = it.map { work -> work.toViewModel() },
                                        page = PaginationState(
                                            currentPage = result.reviews.page,
                                            totalPages = result.reviews.totalPages
                                        )
                                    ).takeIf { it.reviews.isNotEmpty() }
                                }
                            )
                        )
                    )
                }
            } catch (throwable: Throwable) {
                Timber.e(throwable, "Error while loading work details")
                updateState {
                    it.copy(state = Error())
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
                                .firstOf<Header>().actions.indexOf(actionButton)
                        )
                    )
                }
            }
        }
    }

    private fun toggleFavorite() {
        viewModelScope.launch {
            try {
                val currentFavoriteState = (currentState.state as Loaded).contents
                    .firstOf<Header>().actions.firstOf<SaveWork>()
                val updatedWork = work.copy(isFavorite = currentFavoriteState.isFavorite)

                setFavoriteUseCase(updatedWork)

                val newFavoriteState = !currentFavoriteState.isFavorite
                updateState { currentState ->
                    currentState.copy(
                        state = (currentState.state as Loaded).copy(
                            contents = currentState.state.contents.replaceFirst<Header> {
                                it.copy(
                                    actions = it.actions.replaceFirst<SaveWork> {
                                        it.copy(isFavorite = newFavoriteState)
                                    }
                                )
                            }
                        )
                    )
                }
            } catch (throwable: Throwable) {
                Timber.e(throwable, "Error while toggling favorite")

                updateState { currentState ->
                    currentState.copy(
                        state = (currentState.state as Loaded).copy(
                            error = FavoriteError
                        )
                    )
                }
            }
        }
    }

    private fun loadMoreReviews() {
        val reviews = (currentState.state as Loaded).contents.firstOf<Reviews>()

        // Check if we can load more and are not already loading
        if (!reviews.page.canLoadMore || reviews.page.isLoadingMore) {
            return
        }

        updateState { currentState ->
            currentState.copy(
                state = (currentState.state as Loaded).copy(
                    contents = currentState.state.contents.replaceFirst<Reviews> {
                        it.copy(page = it.page.copy(isLoadingMore = true))
                    }
                )
            )
        }

        viewModelScope.launch {
            try {
                val nextPage = reviews.page.currentPage + 1
                val pageResult = getReviewByWorkUseCase(work.type, work.id, nextPage)
                    .toViewModel()

                updateState { currentState ->
                    currentState.copy(
                        state = (currentState.state as Loaded).copy(
                            contents = currentState.state.contents.replaceFirst<Reviews> {
                                it.copy(
                                    reviews = it.reviews + pageResult.results.orEmpty(),
                                    page = it.page.copy(
                                        currentPage = pageResult.page,
                                        totalPages = pageResult.totalPages,
                                        isLoadingMore = false
                                    )
                                )
                            }
                        )
                    )
                }
            } catch (throwable: Throwable) {
                Timber.e(throwable, "Error while loading more reviews")

                updateState { currentState ->
                    currentState.copy(
                        state = (currentState.state as Loaded).copy(
                            contents = currentState.state.contents.replaceFirst<Reviews> {
                                it.copy(page = it.page.copy(isLoadingMore = false))
                            },
                            error = PaginationError
                        )
                    )
                }
            }
        }
    }

    private fun loadMoreRecommendations() {
        val recommendedWorks = (currentState.state as Loaded).contents.firstOf<RecommendedWorks>()

        // Check if we can load more and are not already loading
        if (!recommendedWorks.page.canLoadMore || recommendedWorks.page.isLoadingMore) {
            return
        }

        updateState { currentState ->
            currentState.copy(
                state = (currentState.state as Loaded).copy(
                    contents = currentState.state.contents.replaceFirst<RecommendedWorks> {
                        it.copy(page = it.page.copy(isLoadingMore = true))
                    }
                )
            )
        }

        viewModelScope.launch {
            try {
                val nextPage = recommendedWorks.page.currentPage + 1
                val pageResult = getRecommendationByWorkUseCase(work.type, work.id, nextPage)
                        .toViewModel()

                updateState { currentState ->
                    currentState.copy(
                        state = (currentState.state as Loaded).copy(
                            contents = currentState.state.contents.replaceFirst<RecommendedWorks> {
                                it.copy(
                                    recommended = it.recommended + pageResult.results.orEmpty(),
                                    page = it.page.copy(
                                        currentPage = pageResult.page,
                                        totalPages = pageResult.totalPages,
                                        isLoadingMore = false
                                    )
                                )
                            }
                        )
                    )
                }
            } catch (throwable: Throwable) {
                Timber.e(throwable, "Error while loading more recommendations")

                updateState { currentState ->
                    currentState.copy(
                        state = (currentState.state as Loaded).copy(
                            contents = currentState.state.contents.replaceFirst<RecommendedWorks> {
                                it.copy(page = it.page.copy(isLoadingMore = false))
                            },
                            error = PaginationError
                        )
                    )
                }
            }
        }
    }

    private fun loadMoreSimilar() {
        val similarWorks = (currentState.state as Loaded).contents.firstOf<SimilarWorks>()

        // Check if we can load more and are not already loading
        if (!similarWorks.page.canLoadMore || similarWorks.page.isLoadingMore) {
            return
        }

        updateState { currentState ->
            currentState.copy(
                state = (currentState.state as Loaded).copy(
                    contents = currentState.state.contents.replaceFirst<SimilarWorks> {
                        it.copy(page = it.page.copy(isLoadingMore = true))
                    }
                )
            )
        }

        viewModelScope.launch {
            try {
                val nextPage = similarWorks.page.currentPage + 1
                val pageResult = getSimilarByWorkUseCase(work.type, work.id, nextPage)
                    .toViewModel()

                updateState { currentState ->
                    currentState.copy(
                        state = (currentState.state as Loaded).copy(
                            contents = currentState.state.contents.replaceFirst<SimilarWorks> {
                                it.copy(
                                    similar = it.similar + pageResult.results.orEmpty(),
                                    page = it.page.copy(
                                        currentPage = pageResult.page,
                                        totalPages = pageResult.totalPages,
                                        isLoadingMore = false
                                    )
                                )
                            }
                        )
                    )
                }
            } catch (throwable: Throwable) {
                Timber.e(throwable, "Error while loading more similar works")

                updateState { currentState ->
                    currentState.copy(
                        state = (currentState.state as Loaded).copy(
                            contents = currentState.state.contents.replaceFirst<SimilarWorks> {
                                it.copy(page = it.page.copy(isLoadingMore = false))
                            },
                            error = PaginationError
                        )
                    )
                }
            }
        }
    }

    private fun handleWorkClicked(work: WorkViewModel) {
        val intent = workDetailsRoute.buildWorkDetailIntent(work)
        emitEffect(WorkDetailsEffect.OpenIntent(intent))
    }

    private fun handleCastClicked(cast: CastViewModel) {
        val intent = castDetailsRoute.buildCastDetailIntent(cast)
        emitEffect(WorkDetailsEffect.OpenIntent(intent))
    }

    private fun handleVideoClicked(video: VideoViewModel) {
        video.youtubeUrl?.let {
            val intent = Intent(Intent.ACTION_VIEW, it.toUri())
            emitEffect(WorkDetailsEffect.OpenIntent(intent))
        }
    }

    private fun showError(error: ErrorType) {
        updateState { currentState ->
            val loadedState = currentState.state as? Loaded ?: return@updateState currentState
            currentState.copy(
                state = loadedState.copy(error = error)
            )
        }
    }

    private fun dismissError() {
        updateState { currentState ->
            val loadedState = currentState.state as? Loaded ?: return@updateState currentState
            currentState.copy(
                state = loadedState.copy(error = null)
            )
        }
    }

    private fun clearScrollIndex() {
        updateState { currentState ->
            currentState.copy(
                state = (currentState.state as Loaded).copy(
                    indexOfContentToScroll = null
                )
            )
        }
    }
}
