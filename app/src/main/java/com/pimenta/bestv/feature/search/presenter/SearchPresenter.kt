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

package com.pimenta.bestv.feature.search.presenter

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.util.DisplayMetrics
import android.util.Pair
import android.widget.ImageView
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.pimenta.bestv.BuildConfig
import com.pimenta.bestv.feature.base.BasePresenter
import com.pimenta.bestv.manager.ImageManager
import com.pimenta.bestv.repository.MediaRepository
import com.pimenta.bestv.repository.entity.*
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import java.io.UnsupportedEncodingException
import java.net.URLEncoder
import javax.inject.Inject

/**
 * Created by marcus on 12-03-2018.
 */
class SearchPresenter @Inject constructor(
        val displayMetrics: DisplayMetrics,
        private val mediaRepository: MediaRepository,
        private val imageManager: ImageManager
) : BasePresenter<SearchPresenter.View>() {

    private var resultMoviePage = 0
    private var resultTvShowPage = 0
    private lateinit var query: String
    private lateinit var disposable: Disposable

    override fun unRegister() {
        disposeSearchMovie()
        super.unRegister()
    }

    /**
     * Searches the movies by a query
     *
     * @param text Query to search the movies
     */
    fun searchWorksByQuery(text: String) {
        disposeSearchMovie()
        try {
            val queryEncode = URLEncoder.encode(text, "UTF-8")
            if (::query.isInitialized && queryEncode != query) {
                resultMoviePage = 0
                resultTvShowPage = 0
            }
            query = queryEncode
            val resultMoviePage = resultMoviePage + 1
            val resultTvShowPage = resultTvShowPage + 1
            disposable = Single.zip<MoviePage, TvShowPage, Pair<MoviePage, TvShowPage>>(
                    mediaRepository.searchMoviesByQuery(query, resultMoviePage),
                    mediaRepository.searchTvShowsByQuery(query, resultTvShowPage),
                    BiFunction<MoviePage, TvShowPage, Pair<MoviePage, TvShowPage>> {
                        first, second -> Pair(first, second)
                    })
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ pair ->
                        var movies: List<Movie>? = null
                        if (pair.first != null && pair.first.page <= pair.first.totalPages) {
                            this.resultMoviePage = pair.first.page
                            movies = pair.first.works
                        }

                        var tvShows: List<TvShow>? = null
                        if (pair.second != null && pair.second.page <= pair.second.totalPages) {
                            this.resultTvShowPage = pair.second.page
                            tvShows = pair.second.works
                        }
                        view.onResultLoaded(movies, tvShows)
                    }, { throwable ->
                        Timber.e(throwable, "Error while searching movies by query")
                        view.onResultLoaded(null, null)
                    })
        } catch (exception: UnsupportedEncodingException) {
            Timber.e(exception, "Error while encoding the query")
        }
    }

    /**
     * Load the movies by a query
     */
    fun loadMovies() {
        val resultMoviePage = resultMoviePage + 1
        compositeDisposable.add(mediaRepository.searchMoviesByQuery(query, resultMoviePage)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ moviePage ->
                    if (moviePage != null && moviePage.page <= moviePage.totalPages) {
                        this.resultMoviePage = moviePage.page
                        view.onMoviesLoaded(moviePage.works)
                    } else {
                        view.onMoviesLoaded(null)
                    }
                }, { throwable ->
                    Timber.e(throwable, "Error while loading movies by query")
                    view.onMoviesLoaded(null)
                }))
    }

    /**
     * Load the tv shows by a query
     */
    fun loadTvShows() {
        val resultTvShowPage = resultTvShowPage + 1
        compositeDisposable.add(mediaRepository.searchTvShowsByQuery(query, resultTvShowPage)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ tvShowPage ->
                    if (tvShowPage != null && tvShowPage.page <= tvShowPage.totalPages) {
                        this.resultTvShowPage = tvShowPage.page
                        view.onTvShowsLoaded(tvShowPage.works)
                    } else {
                        view.onTvShowsLoaded(null)
                    }
                }, { throwable ->
                    Timber.e(throwable, "Error while loading tv shows by query")
                    view.onTvShowsLoaded(null)
                }))
    }

    /**
     * Loads the [Work] porter into [ImageView]
     *
     * @param work      [Work]
     * @param imageView [ImageView]
     */
    fun loadWorkPosterImage(work: Work, imageView: ImageView) {
        imageManager.loadImageInto(imageView, String.format(BuildConfig.TMDB_LOAD_IMAGE_BASE_URL, work.posterPath))
    }

    /**
     * Loads the [android.graphics.drawable.Drawable] from the [Work]
     *
     * @param work [Work]
     */
    fun loadBackdropImage(work: Work) {
        imageManager.loadBitmapImage(String.format(BuildConfig.TMDB_LOAD_IMAGE_BASE_URL, work.backdropPath),
                object : SimpleTarget<Bitmap>() {
                    override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                        view.onBackdropImageLoaded(resource)
                    }

                    override fun onLoadFailed(errorDrawable: Drawable?) {
                        super.onLoadFailed(errorDrawable)
                        Timber.w("Error while loading backdrop image")
                        view.onBackdropImageLoaded(null)
                    }
                })
    }

    /**
     * Disposes the search movies.
     */
    private fun disposeSearchMovie() {
        if (::disposable.isInitialized && !disposable.isDisposed) {
            disposable.dispose()
        }
    }

    interface View : BasePresenter.BaseView {

        fun onResultLoaded(movies: List<Work>?, tvShows: List<Work>?)

        fun onMoviesLoaded(movies: List<Work>?)

        fun onTvShowsLoaded(tvShows: List<Work>?)

        fun onBackdropImageLoaded(bitmap: Bitmap?)

    }
}