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
import com.pimenta.bestv.model.presentation.model.WorkViewModel
import com.pimenta.bestv.presentation.presenter.BaseViewModel
import com.pimenta.bestv.route.search.SearchRoute
import com.pimenta.bestv.route.workdetail.WorkDetailsRoute
import com.pimenta.bestv.workbrowse.domain.GetSectionDetailsUseCase
import com.pimenta.bestv.workbrowse.presentation.model.ContentSection
import com.pimenta.bestv.workbrowse.presentation.model.WorkBrowseEffect
import com.pimenta.bestv.workbrowse.presentation.model.WorkBrowseEvent
import com.pimenta.bestv.workbrowse.presentation.model.WorkBrowseState
import com.pimenta.bestv.workbrowse.presentation.model.WorkBrowseState.Section.Favorites
import com.pimenta.bestv.workbrowse.presentation.model.WorkBrowseState.Section.Movies
import com.pimenta.bestv.workbrowse.presentation.model.WorkBrowseState.Section.Search
import com.pimenta.bestv.workbrowse.presentation.model.WorkBrowseState.Section.TvShows
import com.pimenta.bestv.workbrowse.presentation.model.WorkBrowseState.State.Error
import com.pimenta.bestv.workbrowse.presentation.model.WorkBrowseState.State.Loaded
import com.pimenta.bestv.workbrowse.presentation.model.WorkBrowseState.State.Loading
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

/**
 * ViewModel for the Work Browse screen.
 */
class WorkBrowseViewModel @Inject constructor(
    private val getSectionDetailsUseCase: GetSectionDetailsUseCase,
    //private val loadRecommendationUseCase: LoadRecommendationUseCase,
    private val workDetailsRoute: WorkDetailsRoute,
    private val searchRoute: SearchRoute,
) : BaseViewModel<WorkBrowseState, WorkBrowseEffect>(WorkBrowseState()) {

    init {
        handleLoadData()
    }

    fun handleEvent(event: WorkBrowseEvent) {
        when (event) {
            is WorkBrowseEvent.BackClicked -> handleBackClicked()
            is WorkBrowseEvent.LoadData -> handleLoadData()
            is WorkBrowseEvent.ScreenResumed -> handleScreenResumed()
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
            } catch (throwable: Throwable) {
                Timber.e(throwable, "Error while loading the recommendations")
            }
        }
    }*/

    private fun handleBackClicked() = emitEffect(WorkBrowseEffect.CloseScreen)

    private fun handleLoadData() {
        viewModelScope.launch {
            updateState { it.copy(state = Loading(false)) }

            try {
                val sectionDetails = getSectionDetailsUseCase.getAllSections()

                // Wait for splash animation to finish reactively
                state.first { (it.state as? Loading)?.isSplashAnimationFinished == true }

                updateState {
                    it.copy(
                        state = Loaded(
                        workSelected = null,
                        selectedSectionIndex = 1,
                        sections = listOfNotNull(
                            Search,
                            Movies(sectionDetails.movieSectionDetails).takeIf { it.content.isNotEmpty() },
                            TvShows(sectionDetails.tvSectionDetails).takeIf { it.content.isNotEmpty() },
                            Favorites(sectionDetails.favoriteSectionDetails).takeIf { it.content.isNotEmpty() })
                        )
                    )
                }
            } catch (throwable: Throwable) {
                Timber.e(throwable, "Error while loading browse data")
                updateState { it.copy(state = Error) }
            }
        }
    }

    private fun handleScreenResumed() {
        val currentState = currentState.state as? Loaded ?: return
        viewModelScope.launch {
            try {
                val favorites = getSectionDetailsUseCase.getFavoriteSections()
                val updatedSections = updateSectionsWithFavorites(currentState.sections, favorites)

                updateState {
                    it.copy(
                        state = currentState.copy(
                            selectedSectionIndex = currentState.selectedSectionIndex.coerceAtMost(updatedSections.lastIndex),
                            sections = updatedSections
                        )
                    )
                }
            } catch (throwable: Throwable) {
                Timber.e(throwable, "Error while checking favorites")
            }
        }
    }

    private fun updateSectionsWithFavorites(
        sections: List<WorkBrowseState.Section>,
        favorites: List<ContentSection>
    ): List<WorkBrowseState.Section> {
        val hasFavorites = favorites.isNotEmpty()
        val favoritesIndex = sections.indexOfFirst { it is Favorites }

        return when {
            hasFavorites && favoritesIndex >= 0 -> sections.toMutableList().apply { set(favoritesIndex, Favorites(favorites)) }
            hasFavorites -> sections + Favorites(favorites)
            favoritesIndex >= 0 -> sections.filterNot { it is Favorites }
            else -> sections
        }
    }

    private fun handleSplashAnimationFinished() {
        val currentState = currentState.state as? Loading ?: return
        updateState { it.copy(state = currentState.copy(isSplashAnimationFinished = true)) }
    }

    private fun handleSectionClicked(selectedSectionIndex: Int) {
        val currentState = currentState.state as? Loaded ?: return
        when {
            selectedSectionIndex == 0 -> {
                val intent = searchRoute.buildSearchIntent()
                emitEffect(WorkBrowseEffect.Navigate(intent))
            }

            else -> {
                updateState {
                    it.copy(
                        state = currentState.copy(
                            selectedSectionIndex = selectedSectionIndex
                        )
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
        val intent = workDetailsRoute.buildWorkDetailIntent(work)
        emitEffect(WorkBrowseEffect.Navigate(intent))
    }
}
