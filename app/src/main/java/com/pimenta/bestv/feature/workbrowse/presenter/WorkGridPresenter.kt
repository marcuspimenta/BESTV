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

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.util.DisplayMetrics
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.pimenta.bestv.BuildConfig
import com.pimenta.bestv.feature.base.DisposablePresenter
import com.pimenta.bestv.manager.ImageManager
import com.pimenta.bestv.repository.MediaRepository
import com.pimenta.bestv.repository.entity.Genre
import com.pimenta.bestv.repository.entity.Work
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import javax.inject.Inject

/**
 * Created by marcus on 09-02-2018.
 */
class WorkGridPresenter @Inject constructor(
        val displayMetrics: DisplayMetrics,
        private val view: View,
        private val mediaRepository: MediaRepository,
        private val imageManager: ImageManager
) : DisposablePresenter() {

    private var currentPage = 0

    /**
     * Loads the [<] by [MediaRepository.WorkType]
     */
    fun loadWorksByType(movieListType: MediaRepository.WorkType) {
        when (movieListType) {
            MediaRepository.WorkType.FAVORITES_MOVIES ->
                compositeDisposable.add(mediaRepository.getFavorites()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({ movies ->
                            view.onWorksLoaded(movies)
                        }, { throwable ->
                            Timber.e(throwable, "Error while loading the favorite works")
                            view.onWorksLoaded(null)
                        }))
            else -> {
                val page = currentPage + 1
                compositeDisposable.add(mediaRepository.loadWorkByType(page, movieListType)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({ workPage ->
                            if (workPage != null && workPage.page <= workPage.totalPages) {
                                currentPage = workPage.page
                                view.onWorksLoaded(workPage.works)
                            } else {
                                view.onWorksLoaded(null)
                            }
                        }, { throwable ->
                            Timber.e(throwable, "Error while loading the works by type")
                            view.onWorksLoaded(null)
                        }))
            }
        }
    }

    /**
     * Loads the [<] by the [Genre]
     *
     * @param genre [Genre]
     */
    fun loadWorkByGenre(genre: Genre) {
        val pageSearch = currentPage + 1
        compositeDisposable.add(mediaRepository.getWorkByGenre(genre, pageSearch)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ workPage ->
                    if (workPage != null && workPage.page <= workPage.totalPages) {
                        currentPage = workPage.page
                        view.onWorksLoaded(workPage.works)
                    } else {
                        view.onWorksLoaded(null)
                    }
                }, { throwable ->
                    Timber.e(throwable, "Error while loading the works by genre")
                    view.onWorksLoaded(null)
                }))
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

    interface View {

        fun onWorksLoaded(works: List<Work>?)

        fun onBackdropImageLoaded(bitmap: Bitmap?)

    }
}