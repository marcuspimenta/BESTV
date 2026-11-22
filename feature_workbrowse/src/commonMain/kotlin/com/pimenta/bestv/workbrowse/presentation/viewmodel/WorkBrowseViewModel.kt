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

package com.pimenta.bestv.workbrowse.presentation.viewmodel

import androidx.lifecycle.viewModelScope
import com.pimenta.bestv.presentation.model.WorkViewModel
import com.pimenta.bestv.presentation.mapper.toViewModel
import com.pimenta.bestv.presentation.viewmodel.BaseViewModel
import com.pimenta.bestv.presentation.platform.ContentSourceLabelProvider
import com.pimenta.bestv.presentation.route.search.SearchRoute
import com.pimenta.bestv.presentation.route.WorkDetailsDestination
import com.pimenta.bestv.workbrowse.domain.GetBrowseDataUseCase
import com.pimenta.bestv.workbrowse.domain.ObserveFavoritesUseCase
import com.pimenta.bestv.workbrowse.domain.model.BrowseSectionDetails
import com.pimenta.bestv.workbrowse.presentation.model.WorkBrowseEffect
import com.pimenta.bestv.workbrowse.presentation.model.WorkBrowseEvent
import com.pimenta.bestv.workbrowse.presentation.model.WorkBrowseState
import com.pimenta.bestv.workbrowse.presentation.model.WorkBrowseState.State.Error
import com.pimenta.bestv.workbrowse.presentation.model.WorkBrowseState.State.Loaded
import com.pimenta.bestv.workbrowse.presentation.model.WorkBrowseState.State.Loading
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import com.pimenta.bestv.logging.logError

/**
 * ViewModel for the Work Browse screen.
 */
class WorkBrowseViewModel(
    private val getBrowseDataUseCase: GetBrowseDataUseCase,
    private val observeFavoritesUseCase: ObserveFavoritesUseCase,
    // private val loadRecommendationUseCase: LoadRecommendationUseCase,
    private val searchRoute: SearchRoute,
    private val resource: ContentSourceLabelProvider,
) : BaseViewModel<WorkBrowseState, WorkBrowseEffect>(
    WorkBrowseState(),
) {
    init {
        observeFavorites()
        handleLoadData()
    }

    fun handleEvent(event: WorkBrowseEvent) {
        when (event) {
            is WorkBrowseEvent.BackClicked -> handleBackClicked()
            is WorkBrowseEvent.RetryLoad -> handleLoadData()
            is WorkBrowseEvent.SplashAnimationFinished -> handleSplashAnimationFinished()
            is WorkBrowseEvent.SectionClicked -> handleSectionClicked(event.sectionClickedIndex)
            is WorkBrowseEvent.WorkSelected -> handleWorkSelected(event.work)
            is WorkBrowseEvent.WorkClicked -> handleWorkClicked(event.work)
        }
    }

    /*private fun loadRecommendations() {
        viewModelScope.launch {
            try {
                loadRecommendationUseCase()
            } catch (cancellation: CancellationException) {
                throw cancellation
            } catch (exception: Exception) {
                logError(exception, "Error while loading the recommendations")
            }
        }
    }*/

    private fun handleBackClicked() = emitEffect(WorkBrowseEffect.CloseScreen)

    private fun observeFavorites() {
        val favorites = flow { emitAll(observeFavoritesUseCase()) }
            .onStart { emit(emptyList()) }
            .catch { throwable ->
                when (throwable) {
                    is CancellationException -> throw throwable
                    is Exception -> {
                        logError(throwable, "Error while observing favorite data")
                        emit(emptyList())
                    }
                    else -> throw throwable
                }
            }

        viewModelScope.launch {
            try {
                favorites.collect { works ->
                    val favoriteWorks = works.mapNotNull { it.toViewModel(resource) }
                    updateLoadedFavorites(favoriteWorks)
                }
            } catch (cancellation: CancellationException) {
                throw cancellation
            } catch (exception: Exception) {
                logError(exception, "Error while observing favorite data")
            }
        }
    }

    private fun handleLoadData() {
        if (currentState.state !is Loaded) {
            updateState { current ->
                val isSplashAnimationFinished = (current.state as? Loading)?.isSplashAnimationFinished ?: false
                current.copy(state = Loading(isSplashAnimationFinished))
            }
        }

        viewModelScope.launch {
            try {
                val sectionsDetails = getBrowseDataUseCase()
                awaitSplashAnimationFinished()
                displayBrowseSections(sectionsDetails, currentState.latestFavorites)
            } catch (cancellation: CancellationException) {
                throw cancellation
            } catch (exception: Exception) {
                logError(exception, "Error while loading browse data")
                awaitSplashAnimationFinished()
                if (currentState.state !is Loaded) updateState { it.copy(state = Error) }
            }
        }
    }

    private suspend fun awaitSplashAnimationFinished() {
        state.first { browseState ->
            val loadingState = browseState.state as? Loading
            loadingState == null || loadingState.isSplashAnimationFinished
        }
    }

    private fun displayBrowseSections(sectionsDetails: BrowseSectionDetails, favorites: List<WorkViewModel>) {
        updateState { current -> current.withBrowseSections(sectionsDetails, favorites, resource) }
    }

    private fun updateLoadedFavorites(favorites: List<WorkViewModel>) {
        updateState { current -> current.withFavorites(favorites) }
    }

    private fun handleSplashAnimationFinished() {
        updateState { current ->
            val loadingState = current.state as? Loading ?: return@updateState current
            current.copy(state = loadingState.copy(isSplashAnimationFinished = true))
        }
    }

    private fun handleSectionClicked(selectedSectionIndex: Int) {
        val currentState = currentState.state as? Loaded ?: return
        when {
            selectedSectionIndex == 0 -> {
                emitEffect(WorkBrowseEffect.Navigate(searchRoute.buildSearchDestination()))
            }

            else -> {
                updateState {
                    it.copy(
                        state = currentState.copy(
                            selectedSectionIndex = selectedSectionIndex,
                        ),
                    )
                }
            }
        }
    }

    private fun handleWorkSelected(work: WorkViewModel) {
        val currentState = currentState.state as? Loaded ?: return
        updateState {
            it.copy(state = currentState.copy(workSelected = work))
        }
    }

    private fun handleWorkClicked(work: WorkViewModel) {
        emitEffect(WorkBrowseEffect.Navigate(WorkDetailsDestination.create(work)))
    }
}
