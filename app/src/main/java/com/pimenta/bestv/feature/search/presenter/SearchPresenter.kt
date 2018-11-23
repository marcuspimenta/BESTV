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
import android.util.Log
import android.util.Pair
import android.widget.ImageView

import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.pimenta.bestv.BuildConfig
import com.pimenta.bestv.feature.base.BasePresenter
import com.pimenta.bestv.manager.ImageManager
import com.pimenta.bestv.repository.MediaRepository
import com.pimenta.bestv.repository.entity.*

import java.io.UnsupportedEncodingException
import java.net.URLEncoder

import javax.inject.Inject

import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers

/**
 * Created by marcus on 12-03-2018.
 */
class SearchPresenter @Inject constructor(
        val displayMetrics: DisplayMetrics,
        private val mMediaRepository: MediaRepository,
        private val mImageManager: ImageManager
) : BasePresenter<SearchPresenter.View>() {

    private var mResultMoviePage = 0
    private var mResultTvShowPage = 0
    private lateinit var mQuery: String
    private lateinit var mDisposable: Disposable

    override fun unRegister() {
        disposeSearchMovie()
        super.unRegister()
    }

    /**
     * Searches the movies by a query
     *
     * @param query Query to search the movies
     */
    fun searchWorksByQuery(query: String) {
        disposeSearchMovie()
        try {
            val queryEncode = URLEncoder.encode(query, "UTF-8")
            if (::mQuery.isInitialized && queryEncode != mQuery) {
                mResultMoviePage = 0
                mResultTvShowPage = 0
            }
            mQuery = queryEncode
            val resultMoviePage = mResultMoviePage + 1
            val resultTvShowPage = mResultTvShowPage + 1
            mDisposable = Single.zip<MoviePage, TvShowPage, Pair<MoviePage, TvShowPage>>(mMediaRepository.searchMoviesByQuery(mQuery, resultMoviePage),
                    mMediaRepository.searchTvShowsByQuery(mQuery, resultTvShowPage),
                    BiFunction<MoviePage, TvShowPage, Pair<MoviePage, TvShowPage>> { first, second -> Pair(first, second) })
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ pair ->
                        var movies: List<Movie>? = null
                        if (pair.first != null && pair.first.page <= pair.first.totalPages) {
                            mResultMoviePage = pair.first.page
                            movies = pair.first.works
                        }
                        var tvShows: List<TvShow>? = null
                        if (pair.second != null && pair.second.page <= pair.second.totalPages) {
                            mResultTvShowPage = pair.second.page
                            tvShows = pair.second.works
                        }
                        view.onResultLoaded(movies, tvShows)
                    }, { throwable ->
                        Log.e(TAG, "Error while searching movies by query", throwable)
                        view.onResultLoaded(null, null)
                    })
        } catch (e: UnsupportedEncodingException) {
            Log.e(TAG, "Error while encoding the query", e)
        }
    }

    /**
     * Load the movies by a query
     */
    fun loadMovies() {
        val resultMoviePage = mResultMoviePage + 1
        compositeDisposable.add(mMediaRepository.searchMoviesByQuery(mQuery, resultMoviePage)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ moviePage ->
                    if (moviePage != null && moviePage.page <= moviePage.totalPages) {
                        mResultMoviePage = moviePage.page
                        view.onMoviesLoaded(moviePage.works)
                    } else {
                        view.onMoviesLoaded(null)
                    }
                }, { throwable ->
                    Log.e(TAG, "Error while loading movies by query", throwable)
                    view.onMoviesLoaded(null)
                }))
    }

    /**
     * Load the tv shows by a query
     */
    fun loadTvShows() {
        val resultTvShowPage = mResultTvShowPage + 1
        compositeDisposable.add(mMediaRepository.searchTvShowsByQuery(mQuery, resultTvShowPage)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ tvShowPage ->
                    if (tvShowPage != null && tvShowPage.page <= tvShowPage.totalPages) {
                        mResultTvShowPage = tvShowPage.page
                        view.onTvShowsLoaded(tvShowPage.works)
                    } else {
                        view.onTvShowsLoaded(null)
                    }
                }, { throwable ->
                    Log.e(TAG, "Error while loading tv shows by query", throwable)
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
        mImageManager.loadImageInto(imageView, String.format(BuildConfig.TMDB_LOAD_IMAGE_BASE_URL, work.posterPath))
    }

    /**
     * Loads the [android.graphics.drawable.Drawable] from the [Work]
     *
     * @param work [Work]
     */
    fun loadBackdropImage(work: Work) {
        mImageManager.loadBitmapImage(String.format(BuildConfig.TMDB_LOAD_IMAGE_BASE_URL, work.backdropPath),
                object : SimpleTarget<Bitmap>() {
                    override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                        view.onBackdropImageLoaded(resource)
                    }

                    override fun onLoadFailed(errorDrawable: Drawable?) {
                        super.onLoadFailed(errorDrawable)
                        Log.w(TAG, "Error while loading backdrop image")
                        view.onBackdropImageLoaded(null)
                    }
                })
    }

    /**
     * Disposes the search movies.
     */
    private fun disposeSearchMovie() {
        if (::mDisposable.isInitialized && !mDisposable.isDisposed) {
            mDisposable.dispose()
        }
    }

    interface View : BasePresenter.BaseView {

        fun onResultLoaded(movies: List<Work>?, tvShows: List<Work>?)

        fun onMoviesLoaded(movies: List<Work>?)

        fun onTvShowsLoaded(tvShows: List<Work>?)

        fun onBackdropImageLoaded(bitmap: Bitmap?)

    }

    companion object {

        private val TAG = SearchPresenter::class.java.simpleName
    }
}