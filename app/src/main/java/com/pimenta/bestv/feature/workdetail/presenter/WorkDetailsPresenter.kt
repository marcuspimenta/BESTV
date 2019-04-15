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

package com.pimenta.bestv.feature.workdetail.presenter

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.pimenta.bestv.BuildConfig
import com.pimenta.bestv.common.presentation.model.VideoViewModel
import com.pimenta.bestv.common.usecase.GetVideosUseCase
import com.pimenta.bestv.feature.base.DisposablePresenter
import com.pimenta.bestv.manager.ImageManager
import com.pimenta.bestv.repository.MediaRepository
import com.pimenta.bestv.repository.entity.*
import io.reactivex.Maybe
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Function4
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import javax.inject.Inject

/**
 * Created by marcus on 07-02-2018.
 */
class WorkDetailsPresenter @Inject constructor(
        private val view: View,
        private val mediaRepository: MediaRepository,
        private val imageManager: ImageManager,
        private val getVideosUseCase: GetVideosUseCase
) : DisposablePresenter() {

    private var recommendedPage = 0
    private var similarPage = 0

    /**
     * Checks if the [Work] is favorite
     *
     * @param work [Work]
     * @return `true` if yes, `false` otherwise
     */
    fun isFavorite(work: Work): Boolean {
        work.isFavorite = mediaRepository.isFavorite(work)
        return work.isFavorite
    }

    /**
     * Sets if a [Work] is or not is favorite
     *
     * @param work [Work]
     */
    fun setFavorite(work: Work) {
        compositeDisposable.add(Maybe.create<Boolean> {
                    val result = if (work.isFavorite) {
                        mediaRepository.deleteFavorite(work)
                    } else {
                        mediaRepository.saveFavorite(work)
                    }

                    if (result) {
                        it.onSuccess(true)
                    } else {
                        it.onComplete()
                    }
                }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    work.isFavorite = !work.isFavorite
                    view.onResultSetFavoriteMovie(true)
                }, { throwable ->
                    Timber.e(throwable, "Error while settings the work as favorite")
                    view.onResultSetFavoriteMovie(false)
                }))
    }

    /**
     * Loads the [<] by the [Work]
     *
     * @param work [Work]
     */
    fun loadDataByWork(work: Work) {
        val recommendedPageSearch = recommendedPage + 1
        val similarPageSearch = similarPage + 1

        compositeDisposable.add(Single.zip(
                getVideosUseCase(work),
                mediaRepository.getCastByWork(work),
                mediaRepository.getRecommendationByWork(work, recommendedPageSearch),
                mediaRepository.getSimilarByWork(work, similarPageSearch),
                Function4<List<VideoViewModel>?, CastList, WorkPage<*>, WorkPage<*>, WorkInfo> { videos, casts, recommendedMovies, similarMovies ->
                    WorkInfo(videos, casts, recommendedMovies, similarMovies)
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ movieInfo ->
                    val recommendedPage = movieInfo.recommendedMovies
                    if (recommendedPage.page <= recommendedPage.totalPages) {
                        this.recommendedPage = recommendedPage.page
                    }
                    val similarPage = movieInfo.similarMovies
                    if (similarPage.page <= similarPage.totalPages) {
                        this.similarPage = similarPage.page
                    }

                    view.onDataLoaded(
                            movieInfo.casts.casts,
                            recommendedPage.works,
                            similarPage.works,
                            movieInfo.videos
                    )
                }, { throwable ->
                    Timber.e(throwable, "Error while loading data by work")
                    view.onDataLoaded(null, null, null, null)
                }))
    }

    /**
     * Loads the [<] recommended by the [Work]
     *
     * @param work [Work]
     */
    fun loadRecommendationByWork(work: Work) {
        val pageSearch = recommendedPage + 1

        compositeDisposable.add(mediaRepository.getRecommendationByWork(work, pageSearch)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ movieList ->
                    if (movieList != null && movieList.page <= movieList.totalPages) {
                        recommendedPage = movieList.page
                        view.onRecommendationLoaded(movieList.works)
                    } else {
                        view.onRecommendationLoaded(null)
                    }
                }, { throwable ->
                    Timber.e(throwable, "Error while loading recommendations by work")
                    view.onRecommendationLoaded(null)
                }))
    }

    /**
     * Loads the [<] similar by the [Work]
     *
     * @param work [Work]
     */
    fun loadSimilarByWork(work: Work) {
        val pageSearch = similarPage + 1

        compositeDisposable.add(mediaRepository.getSimilarByWork(work, pageSearch)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ movieList ->
                    if (movieList != null && movieList.page <= movieList.totalPages) {
                        similarPage = movieList.page
                        view.onSimilarLoaded(movieList.works)
                    } else {
                        view.onSimilarLoaded(null)
                    }
                }, { throwable ->
                    Timber.e(throwable, "Error while loading similar by work")
                    view.onSimilarLoaded(null)
                }))
    }

    /**
     * Loads the [Work] poster
     *
     * @param work [Work]
     */
    fun loadCardImage(work: Work) {
        imageManager.loadImage(String.format(BuildConfig.TMDB_LOAD_IMAGE_BASE_URL, work.posterPath),
                object : SimpleTarget<Drawable>() {
                    override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
                        view.onCardImageLoaded(resource)

                    }

                    override fun onLoadFailed(errorDrawable: Drawable?) {
                        super.onLoadFailed(errorDrawable)
                        Timber.e("Error while loading card image")
                        view.onCardImageLoaded(null)
                    }
                })
    }

    /**
     * Loads the [Work] backdrop image using Glide tool
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
                        view.onBackdropImageLoaded(null)
                    }
                })
    }

    /**
     * Wrapper class to keep the work info
     */
    private inner class WorkInfo(
            val videos: List<VideoViewModel>?,
            val casts: CastList,
            val recommendedMovies: WorkPage<*>,
            val similarMovies: WorkPage<*>
    )

    interface View {

        fun onResultSetFavoriteMovie(success: Boolean)

        fun onDataLoaded(casts: List<Cast>?, recommendedMovies: List<Work>?, similarMovies: List<Work>?, videos: List<VideoViewModel>?)

        fun onRecommendationLoaded(works: List<Work>?)

        fun onSimilarLoaded(works: List<Work>?)

        fun onCardImageLoaded(resource: Drawable?)

        fun onBackdropImageLoaded(bitmap: Bitmap?)

    }
}