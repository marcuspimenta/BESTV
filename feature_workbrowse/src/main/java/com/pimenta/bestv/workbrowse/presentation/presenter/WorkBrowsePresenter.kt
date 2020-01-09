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

import androidx.leanback.widget.DividerRow
import androidx.leanback.widget.HeaderItem
import androidx.leanback.widget.PageRow
import androidx.leanback.widget.Row
import androidx.leanback.widget.SectionRow
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
import com.pimenta.bestv.workbrowse.presentation.ui.headeritem.GenreHeaderItem
import com.pimenta.bestv.workbrowse.presentation.ui.headeritem.WorkTypeHeaderItem
import javax.inject.Inject
import timber.log.Timber

/**
 * Created by marcus on 06-02-2018.
 */
const val TOP_WORK_LIST_ID = 1L
const val WORK_GENRE_ID = 2L
const val ABOUT_ID = 3L
private const val FAVORITE_INDEX = 0
private const val INVALID_INDEX = -1

class WorkBrowsePresenter @Inject constructor(
    private val view: View,
    private val hasFavoriteUseCase: HasFavoriteUseCase,
    private val getWorkBrowseDetailsUseCase: GetWorkBrowseDetailsUseCase,
    private val searchRoute: SearchRoute,
    private val resource: Resource,
    private val rxScheduler: RxScheduler
) : AutoDisposablePresenter() {

    private var refreshRows = false
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

                    buildRowList(hasFavoriteMovie, movieGenres, tvShowGenres)

                    view.onDataLoaded(rows)
                }, { throwable ->
                    Timber.e(throwable, "Error while loading data")
                    view.onErrorDataLoaded()
                }).addTo(compositeDisposable)
    }

    fun addFavoriteRow(selectedPosition: Int) {
        if (rows.isEmpty()) {
            return
        }

        hasFavoriteUseCase()
                .subscribeOn(rxScheduler.ioScheduler)
                .observeOn(rxScheduler.mainScheduler)
                .subscribe({ hasFavorite ->
                    if (hasFavorite) {
                        if (rows.indexOf(favoritePageRow) == INVALID_INDEX) {
                            rows.add(FAVORITE_INDEX, favoritePageRow)
                            view.onDataLoaded(rows)
                        }
                    } else {
                        if (rows.indexOf(favoritePageRow) == FAVORITE_INDEX) {
                            rows.remove(favoritePageRow)
                            if (selectedPosition == FAVORITE_INDEX) {
                                refreshRows = true
                                view.onUpdateSelectedPosition(FAVORITE_INDEX + 3)
                            }
                        }
                    }
                }, { throwable ->
                    Timber.e(throwable, "Error while checking if has any work as favorite")
                }).addTo(compositeDisposable)
    }

    fun refreshRows() {
        if (refreshRows) {
            refreshRows = false
            view.onDataLoaded(rows)
        }
    }

    fun searchClicked() {
        val route = searchRoute.buildSearchRoute()
        view.openSearch(route)
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

        fun onUpdateSelectedPosition(selectedPosition: Int)

        fun onErrorDataLoaded()

        fun openSearch(route: Route)
    }
}
