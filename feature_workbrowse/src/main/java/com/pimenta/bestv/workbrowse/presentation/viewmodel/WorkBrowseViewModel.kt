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
import com.pimenta.bestv.presentation.di.annotation.FragmentScope
import com.pimenta.bestv.presentation.presenter.BaseViewModel
import com.pimenta.bestv.route.search.SearchRoute
import com.pimenta.bestv.route.workdetail.WorkDetailsRoute
import com.pimenta.bestv.workbrowse.domain.GetSectionDetailsUseCase
import com.pimenta.bestv.workbrowse.domain.GetWorkBrowseDetailsUseCase
import com.pimenta.bestv.workbrowse.domain.HasFavoriteUseCase
import com.pimenta.bestv.workbrowse.presentation.model.WorkBrowseEffect
import com.pimenta.bestv.workbrowse.presentation.model.WorkBrowseEvent
import com.pimenta.bestv.workbrowse.presentation.model.WorkBrowseState
import com.pimenta.bestv.workbrowse.presentation.model.WorkBrowseState.Section.Movies
import com.pimenta.bestv.workbrowse.presentation.model.WorkBrowseState.Section.Search
import com.pimenta.bestv.workbrowse.presentation.model.WorkBrowseState.Section.TvShows
import com.pimenta.bestv.workbrowse.presentation.model.WorkBrowseState.State.Error
import com.pimenta.bestv.workbrowse.presentation.model.WorkBrowseState.State.Loaded
import com.pimenta.bestv.workbrowse.presentation.model.WorkBrowseState.State.Loading
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

/**
 * ViewModel for the Work Browse screen.
 */
@FragmentScope
class WorkBrowseViewModel @Inject constructor(
    private val getSectionDetailsUseCase: GetSectionDetailsUseCase,
    private val getWorkBrowseDetailsUseCase: GetWorkBrowseDetailsUseCase,
    private val hasFavoriteUseCase: HasFavoriteUseCase,
    private val workDetailsRoute: WorkDetailsRoute,
    private val searchRoute: SearchRoute,
) : BaseViewModel<WorkBrowseState, WorkBrowseEffect>(WorkBrowseState()) {

    init {
        loadData()
    }

    fun handleEvent(event: WorkBrowseEvent) {
        when (event) {
            is WorkBrowseEvent.LoadData -> loadData()
            is WorkBrowseEvent.RetryLoad -> loadData()
            is WorkBrowseEvent.SectionClicked -> handleSectionClicked(event.sectionClickedIndex)
            is WorkBrowseEvent.WorkSelected -> handleWorkSelected(event.work)
            is WorkBrowseEvent.WorkClicked -> handleWorkClicked(event.work)
        }
    }

    private fun loadData() {
        viewModelScope.launch {
            updateState { it.copy(state = Loading) }

            try {
                /*
                val result = getWorkBrowseDetailsUseCase()


                val hasFavorite = result.first
                val movieGenres = result.second?.map { it.toViewModel() }.orEmpty()
                val tvShowGenres = result.third?.map { it.toViewModel() }.orEmpty()

                val sections = buildSections(hasFavorite, movieGenres, tvShowGenres)
                 */

                val sectionDetails = getSectionDetailsUseCase()
                updateState {
                    it.copy(
                        state = Loaded(
                            workSelected = null,
                            selectedSectionIndex = 1,
                            sections = listOf(
                                Search,
                                Movies(sectionDetails.movieSectionDetails),
                                TvShows(sectionDetails.tvSectionDetails)
                            )
                        )
                    )
                }
            } catch (throwable: Throwable) {
                Timber.e(throwable, "Error while loading browse data")
                updateState { it.copy(state = Error) }
            }
        }
    }

    fun checkAndUpdateFavorites() {
        viewModelScope.launch {
            /*try {
                val hasFavorite = hasFavoriteUseCase()
                val currentState = currentState.state as? Loaded ?: return@launch

                val currentHasFavorites = currentState.sections.any { it is Favorites }

                if (hasFavorite != currentHasFavorites) {
                    val updatedSections = currentState.sections.toMutableList().apply {
                        if (hasFavorite) {
                            add(0, Favorites)
                        } else {
                            remove(Favorites)
                        }
                    }

                    updateState {
                        it.copy(state = Loaded(sections = updatedSections))
                    }
                }
            } catch (throwable: Throwable) {
                Timber.e(throwable, "Error while checking favorites")
            }*/
        }
    }

    private fun handleSectionClicked(selectedSectionIndex: Int) {
        val currentState = currentState.state as? Loaded ?: return
        when {
            selectedSectionIndex == 0 -> {
                val intent = searchRoute.buildSearchIntent()
                emitEvent(WorkBrowseEffect.Navigate(intent))
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
        emitEvent(WorkBrowseEffect.Navigate(intent))
    }
}
