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

package com.pimenta.bestv.workbrowse.presentation.presenter

import androidx.leanback.widget.*
import com.pimenta.bestv.presentation.extension.addTo
import com.pimenta.bestv.presentation.platform.Resource
import com.pimenta.bestv.presentation.presenter.AutoDisposablePresenter
import com.pimenta.bestv.presentation.scheduler.RxScheduler
import com.pimenta.bestv.route.Route
import com.pimenta.bestv.route.search.SearchRoute
import com.pimenta.bestv.workbrowse.R
import com.pimenta.bestv.workbrowse.domain.GetWorkBrowseDetailsUseCase
import com.pimenta.bestv.workbrowse.domain.HasFavoriteUseCase
import com.pimenta.bestv.workbrowse.presentation.mapper.toViewModel
import com.pimenta.bestv.workbrowse.presentation.model.GenreViewModel
import com.pimenta.bestv.workbrowse.presentation.model.TopWorkTypeViewModel
import com.pimenta.bestv.workbrowse.presentation.ui.fragment.ABOUT_ID
import com.pimenta.bestv.workbrowse.presentation.ui.fragment.TOP_WORK_LIST_ID
import com.pimenta.bestv.workbrowse.presentation.ui.fragment.WORK_GENRE_ID
import com.pimenta.bestv.workbrowse.presentation.ui.headeritem.GenreHeaderItem
import com.pimenta.bestv.workbrowse.presentation.ui.headeritem.WorkTypeHeaderItem
import timber.log.Timber
import javax.inject.Inject

/**
 * Created by marcus on 06-02-2018.
 */
public const val TOP_WORK_LIST_ID = 1L
public const val WORK_GENRE_ID = 2L
public const val ABOUT_ID = 3L
private const val FAVORITE_INDEX = 0

class WorkBrowsePresenter @Inject constructor(
        private val view: View,
        private val hasFavoriteUseCase: HasFavoriteUseCase,
        private val getWorkBrowseDetailsUseCase: GetWorkBrowseDetailsUseCase,
        private val searchRoute: SearchRoute,
        private val resource: Resource,
        private val rxScheduler: RxScheduler
) : AutoDisposablePresenter() {

    private val rows = mutableListOf<Row>()
    private val favoritePageRow by lazy {
        PageRow(
                WorkTypeHeaderItem(
                        TOP_WORK_LIST_ID,
                        resource.getStringResource(TopWorkTypeViewModel.FAVORITES_MOVIES.resource),
                        TopWorkTypeViewModel.FAVORITES_MOVIES
                )
        )
    }

    fun hasFavorite() {
        hasFavoriteUseCase()
                .subscribeOn(rxScheduler.ioScheduler)
                .observeOn(rxScheduler.mainScheduler)
                .subscribe({ result ->
                    view.onHasFavorite(result)
                }, { throwable ->
                    Timber.e(throwable, "Error while checking if has any work as favorite")
                }).addTo(compositeDisposable)
    }

    fun loadData() {
        getWorkBrowseDetailsUseCase()
                .subscribeOn(rxScheduler.ioScheduler)
                .observeOn(rxScheduler.mainScheduler)
                .doOnSubscribe { view.onShowProgress() }
                .doFinally { view.onHideProgress() }
                .subscribe({
                    val hasFavoriteMovie = it.first
                    val movieGenres = it.second?.map { genre -> genre.toViewModel() }
                    val tvShowGenres = it.third?.map { genre -> genre.toViewModel() }

                    buildRowList()

                    view.onDataLoaded(rows)
                }, { throwable ->
                    Timber.e(throwable, "Error while loading data")
                    view.onErrorDataLoaded()
                }).addTo(compositeDisposable)
    }

    fun searchClicked() {
        val route = searchRoute.buildSearchRoute()
        view.openSearch(route)
    }

    private fun setFavoriteRow(hasFavorite: Boolean) {
        rows.run {
            if (hasFavorite) {
                if (indexOf(favoritePageRow) == -1) {
                    add(FAVORITE_INDEX, favoritePageRow)
                }
            } else {
                if (indexOf(favoritePageRow) == FAVORITE_INDEX) {
                    if (selectedPosition == FAVORITE_INDEX) {
                        selectedPosition = FAVORITE_INDEX + 3
                    }
                }
            }
        }
    }

    private fun buildRowList(hasFavorite: Boolean, movieGenres: List<GenreViewModel>?, tvShowGenres: List<GenreViewModel>?) {
        rows.run {
            if (hasFavorite) {
                add(favoritePageRow)
            }

            add(DividerRow())
            add(SectionRow(resource.getStringResource(R.string.movies)))
            add(PageRow(WorkTypeHeaderItem(TOP_WORK_LIST_ID, resource.getStringResource(TopWorkTypeViewModel.NOW_PLAYING_MOVIES.resource), TopWorkTypeViewModel.NOW_PLAYING_MOVIES)))
            add(PageRow(WorkTypeHeaderItem(TOP_WORK_LIST_ID, resource.getStringResource(TopWorkTypeViewModel.POPULAR_MOVIES.resource), TopWorkTypeViewModel.POPULAR_MOVIES)))
            add(PageRow(WorkTypeHeaderItem(TOP_WORK_LIST_ID, resource.getStringResource(TopWorkTypeViewModel.TOP_RATED_MOVIES.resource), TopWorkTypeViewModel.TOP_RATED_MOVIES)))
            add(PageRow(WorkTypeHeaderItem(TOP_WORK_LIST_ID, resource.getStringResource(TopWorkTypeViewModel.UP_COMING_MOVIES.resource), TopWorkTypeViewModel.UP_COMING_MOVIES)))

            movieGenres?.forEach {
                add(PageRow(GenreHeaderItem(WORK_GENRE_ID, it)))
            }

            add(DividerRow())
            add(SectionRow(resource.getStringResource(R.string.tv_shows)))
            add(PageRow(WorkTypeHeaderItem(TOP_WORK_LIST_ID, resource.getStringResource(TopWorkTypeViewModel.AIRING_TODAY_TV_SHOWS.resource), TopWorkTypeViewModel.AIRING_TODAY_TV_SHOWS)))
            add(PageRow(WorkTypeHeaderItem(TOP_WORK_LIST_ID, resource.getStringResource(TopWorkTypeViewModel.ON_THE_AIR_TV_SHOWS.resource), TopWorkTypeViewModel.ON_THE_AIR_TV_SHOWS)))
            add(PageRow(WorkTypeHeaderItem(TOP_WORK_LIST_ID, resource.getStringResource(TopWorkTypeViewModel.TOP_RATED_TV_SHOWS.resource), TopWorkTypeViewModel.TOP_RATED_TV_SHOWS)))
            add(PageRow(WorkTypeHeaderItem(TOP_WORK_LIST_ID, resource.getStringResource(TopWorkTypeViewModel.POPULAR_TV_SHOWS.resource), TopWorkTypeViewModel.POPULAR_TV_SHOWS)))

            tvShowGenres?.forEach {
                add(PageRow(GenreHeaderItem(WORK_GENRE_ID, it)))
            }

            add(DividerRow())
            add(PageRow(HeaderItem(ABOUT_ID, resource.getStringResource(R.string.about))))
        }
    }

    interface View {

        fun onShowProgress()

        fun onHideProgress()

        fun onDataLoaded(rows: List<Row>)

        fun onErrorDataLoaded()

        fun openSearch(route: Route)
    }
}