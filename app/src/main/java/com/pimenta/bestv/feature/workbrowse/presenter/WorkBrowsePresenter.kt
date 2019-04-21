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

package com.pimenta.bestv.feature.workbrowse.presenter

import com.pimenta.bestv.common.usecase.WorkUseCase
import com.pimenta.bestv.feature.base.DisposablePresenter
import com.pimenta.bestv.repository.entity.*
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Function3
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import javax.inject.Inject

/**
 * Created by marcus on 06-02-2018.
 */
class WorkBrowsePresenter @Inject constructor(
        private val view: View,
        private val workUseCase: WorkUseCase
) : DisposablePresenter() {

    /**
     * Checks if there is any [Work] saved as favorite
     */
    fun hasFavorite() {
        compositeDisposable.add(workUseCase.hasFavorite()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ result ->
                    view.onHasFavorite(result)
                }, { throwable ->
                    Timber.e(throwable, "Error while checking if has any work as favorite")
                    view.onHasFavorite(false)
                }))
    }

    /**
     * Loads the [<] available at TMDb
     */
    fun loadData() {
        compositeDisposable.add(Single.zip<MovieGenreList, TvShowGenreList, Boolean, BrowserWorkInfo>(
                workUseCase.getMovieGenres(),
                workUseCase.getTvShowGenres(),
                workUseCase.hasFavorite(),
                Function3<MovieGenreList, TvShowGenreList, Boolean, BrowserWorkInfo> { movieGenreList, tvShowGenreList, hasFavoriteMovie ->
                    BrowserWorkInfo(movieGenreList, tvShowGenreList, hasFavoriteMovie)
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ result ->
                    view.onDataLoaded(
                            result.hasFavoriteMovie,
                            result.movieGenreList?.genres,
                            result.tvShowGenreList?.genres
                    )
                }, { throwable ->
                    Timber.e(throwable, "Error while loading data")
                    view.onDataLoaded(false, null, null)
                }))
    }

    /**
     * Wrapper class to keep the movie info
     */
    private inner class BrowserWorkInfo(
            val movieGenreList: MovieGenreList? = null,
            val tvShowGenreList: TvShowGenreList? = null,
            val hasFavoriteMovie: Boolean
    )

    interface View {

        fun onDataLoaded(hasFavoriteMovie: Boolean, movieGenres: List<MovieGenre>?, tvShowGenres: List<TvShowGenre>?)

        fun onHasFavorite(hasFavoriteMovie: Boolean)

    }
}