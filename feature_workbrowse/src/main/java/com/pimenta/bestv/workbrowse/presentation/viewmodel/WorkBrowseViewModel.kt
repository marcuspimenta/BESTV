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
import com.pimenta.bestv.model.presentation.model.PageViewModel
import com.pimenta.bestv.model.presentation.model.WorkViewModel
import com.pimenta.bestv.presentation.mapper.toViewModel
import com.pimenta.bestv.presentation.model.PaginationState
import com.pimenta.bestv.presentation.presenter.BaseViewModel
import com.pimenta.bestv.presentation.platform.DeviceCapabilities
import com.pimenta.bestv.presentation.platform.Resource
import com.pimenta.bestv.route.search.SearchRoute
import com.pimenta.bestv.route.workdetail.WorkDetailsRoute
import com.pimenta.bestv.workbrowse.domain.GetSectionDetailsUseCase
import com.pimenta.bestv.workbrowse.domain.GetWorkByGenreUseCase
import com.pimenta.bestv.workbrowse.domain.LoadWorkByTypeUseCase
import com.pimenta.bestv.workbrowse.domain.model.BrowseCategoryKey
import com.pimenta.bestv.workbrowse.domain.model.GenreDomainModel
import com.pimenta.bestv.workbrowse.presentation.mapper.toViewModels
import com.pimenta.bestv.workbrowse.presentation.model.ContentSection
import com.pimenta.bestv.workbrowse.presentation.model.ContentSection.Genre
import com.pimenta.bestv.workbrowse.presentation.model.ContentSection.TopContent
import com.pimenta.bestv.workbrowse.presentation.model.Source
import com.pimenta.bestv.workbrowse.presentation.model.TopWorkTypeViewModel
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
import com.pimenta.bestv.workbrowse.presentation.model.lazyListKey
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import timber.log.Timber

/**
 * ViewModel for the Work Browse screen.
 */
class WorkBrowseViewModel(
    private val getSectionDetailsUseCase: GetSectionDetailsUseCase,
    private val getWorkByGenreUseCase: GetWorkByGenreUseCase,
    private val loadWorkByTypeUseCase: LoadWorkByTypeUseCase,
    // private val loadRecommendationUseCase: LoadRecommendationUseCase,
    private val workDetailsRoute: WorkDetailsRoute,
    private val searchRoute: SearchRoute,
    private val resource: Resource,
    private val deviceCapabilities: DeviceCapabilities,
) : BaseViewModel<WorkBrowseState, WorkBrowseEffect>(
    WorkBrowseState(isMobileDevice = deviceCapabilities.isMobileDevice),
) {
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
            is WorkBrowseEvent.LoadMore -> handleLoadMore(event.section)
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
                val movieSections = sectionDetails.movieSections.toViewModels(resource)
                val tvShowSections = sectionDetails.tvShowSections.toViewModels(resource)
                val favoriteSections = sectionDetails.favoriteSections.toViewModels(resource)

                // Wait for splash animation to finish reactively
                state.first { (it.state as? Loading)?.isSplashAnimationFinished == true }

                updateState {
                    it.copy(
                        state = Loaded(
                            workSelected = null,
                            selectedSectionIndex = 1,
                            sections = listOfNotNull(
                                Search,
                                Movies(movieSections).takeIf { it.content.isNotEmpty() },
                                TvShows(tvShowSections).takeIf { it.content.isNotEmpty() },
                                Favorites(favoriteSections).takeIf { it.content.isNotEmpty() },
                            ),
                        ),
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
                val favorites = getSectionDetailsUseCase.getFavoriteSections().toViewModels(resource)
                val updatedSections = updateSectionsWithFavorites(currentState.sections, favorites)

                updateState {
                    it.copy(
                        state = currentState.copy(
                            selectedSectionIndex = currentState.selectedSectionIndex.coerceAtMost(
                                updatedSections.lastIndex
                            ),
                            sections = updatedSections,
                        ),
                    )
                }
            } catch (throwable: Throwable) {
                Timber.e(throwable, "Error while checking favorites")
            }
        }
    }

    private fun updateSectionsWithFavorites(
        sections: List<WorkBrowseState.Section>,
        favorites: List<ContentSection>,
    ): List<WorkBrowseState.Section> {
        val hasFavorites = favorites.isNotEmpty()
        val favoritesIndex = sections.indexOfFirst { it is Favorites }

        return when {
            hasFavorites && favoritesIndex >= 0 ->
                sections.toMutableList().apply {
                    set(favoritesIndex, Favorites(favorites))
                }
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
        emitEffect(WorkBrowseEffect.Navigate(workDetailsRoute.buildWorkDetailsDestination(work)))
    }

    private fun handleLoadMore(section: ContentSection) {
        val sectionKey = section.lazyListKey()
        val currentSection = currentState.findContentSection(sectionKey) ?: return
        if (!currentSection.page.canLoadMore) return

        val nextPage = currentSection.page.currentPage + 1
        updateState { state ->
            state.updateContentSection(sectionKey) { content ->
                content.withPage(content.page.copy(isLoadingMore = true))
            }
        }

        viewModelScope.launch {
            try {
                val page = currentSection.loadPage(nextPage).toViewModel(resource)
                updateState { state ->
                    state.updateContentSection(sectionKey) { content ->
                        if (content.page.currentPage == currentSection.page.currentPage) {
                            content.appendPage(page)
                        } else {
                            content
                        }
                    }
                }
            } catch (cancellation: CancellationException) {
                throw cancellation
            } catch (throwable: Throwable) {
                Timber.e(throwable, "Error while loading more browse works")
                updateState { state ->
                    state.updateContentSection(sectionKey) { content ->
                        content.withPage(content.page.copy(isLoadingMore = false))
                    }
                }
            }
        }
    }

    private suspend fun ContentSection.loadPage(page: Int) =
        when (this) {
            is TopContent -> loadWorkByTypeUseCase(page, type.toBrowseCategoryKey())
            is Genre -> getWorkByGenreUseCase(
                genreId = genreViewModel.id,
                genreSource = when (genreViewModel.source) {
                    Source.MOVIE -> GenreDomainModel.Source.MOVIE
                    Source.TV_SHOW -> GenreDomainModel.Source.TV_SHOW
                },
                page = page,
            )
        }

    private fun TopWorkTypeViewModel.toBrowseCategoryKey() =
        when (this) {
            TopWorkTypeViewModel.FAVORITES_MOVIES -> BrowseCategoryKey.FAVORITES_MOVIES
            TopWorkTypeViewModel.NOW_PLAYING_MOVIES -> BrowseCategoryKey.NOW_PLAYING_MOVIES
            TopWorkTypeViewModel.POPULAR_MOVIES -> BrowseCategoryKey.POPULAR_MOVIES
            TopWorkTypeViewModel.TOP_RATED_MOVIES -> BrowseCategoryKey.TOP_RATED_MOVIES
            TopWorkTypeViewModel.UP_COMING_MOVIES -> BrowseCategoryKey.UP_COMING_MOVIES
            TopWorkTypeViewModel.AIRING_TODAY_TV_SHOWS -> BrowseCategoryKey.AIRING_TODAY_TV_SHOWS
            TopWorkTypeViewModel.ON_THE_AIR_TV_SHOWS -> BrowseCategoryKey.ON_THE_AIR_TV_SHOWS
            TopWorkTypeViewModel.POPULAR_TV_SHOWS -> BrowseCategoryKey.POPULAR_TV_SHOWS
            TopWorkTypeViewModel.TOP_RATED_TV_SHOWS -> BrowseCategoryKey.TOP_RATED_TV_SHOWS
        }

    private fun WorkBrowseState.findContentSection(sectionKey: String): ContentSection? {
        val loadedState = state as? Loaded ?: return null
        return loadedState.sections
            .asSequence()
            .flatMap { section -> section.contentSections().asSequence() }
            .firstOrNull { it.lazyListKey() == sectionKey }
    }

    private fun WorkBrowseState.updateContentSection(
        sectionKey: String,
        transform: (ContentSection) -> ContentSection,
    ): WorkBrowseState {
        val loadedState = state as? Loaded ?: return this
        return copy(
            state = loadedState.copy(
                sections = loadedState.sections.map { section ->
                    when (section) {
                        is Movies -> section.copy(content = section.content.updateContentSection(sectionKey, transform))
                        is TvShows -> section.copy(content = section.content.updateContentSection(sectionKey, transform))
                        is Favorites -> section.copy(content = section.content.updateContentSection(sectionKey, transform))
                        Search -> section
                    }
                },
            ),
        )
    }

    private fun List<ContentSection>.updateContentSection(
        sectionKey: String,
        transform: (ContentSection) -> ContentSection,
    ) = map { content ->
        if (content.lazyListKey() == sectionKey) transform(content) else content
    }

    private fun WorkBrowseState.Section.contentSections(): List<ContentSection> =
        when (this) {
            is Movies -> content
            is TvShows -> content
            is Favorites -> content
            Search -> emptyList()
        }

    private fun ContentSection.withPage(page: PaginationState): ContentSection =
        when (this) {
            is TopContent -> copy(page = page)
            is Genre -> copy(page = page)
        }

    private fun ContentSection.appendPage(page: PageViewModel<WorkViewModel>): ContentSection =
        when (this) {
            is TopContent -> copy(
                works = (works + page.results).distinctBy { it.id },
                page = PaginationState(currentPage = page.page, totalPages = page.totalPages),
            )
            is Genre -> copy(
                works = (works + page.results).distinctBy { it.id },
                page = PaginationState(currentPage = page.page, totalPages = page.totalPages),
            )
        }
}
