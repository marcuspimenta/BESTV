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

package com.pimenta.bestv.presenter;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.pimenta.bestv.BuildConfig;
import com.pimenta.bestv.manager.ImageManager;
import com.pimenta.bestv.repository.MediaRepository;
import com.pimenta.bestv.repository.entity.Genre;
import com.pimenta.bestv.repository.entity.Movie;
import com.pimenta.bestv.repository.entity.Work;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by marcus on 09-02-2018.
 */
public class WorkGridPresenter extends BasePresenter<WorkGridContract> {

    private int mCurrentPage = 0;

    private MediaRepository mMediaRepository;
    private ImageManager mImageManager;

    @Inject
    public WorkGridPresenter(MediaRepository mediaRepository, ImageManager imageManager) {
        super();
        mImageManager = imageManager;
        mMediaRepository = mediaRepository;
    }

    /**
     * Loads the now playing {@link List<Movie>}
     */
    public void loadMoviesByType(MediaRepository.WorkType movieListType) {
        switch (movieListType) {
            case FAVORITES_MOVIES:
                mCompositeDisposable.add(mMediaRepository.getFavorites()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(movies -> {
                            if (mContract != null) {
                                mContract.onWorksLoaded(movies);
                            }
                        }, throwable -> {
                            if (mContract != null) {
                                mContract.onWorksLoaded(null);
                            }
                        }));
                break;
            default:
                int page = mCurrentPage + 1;
                mCompositeDisposable.add(mMediaRepository.loadWorkByType(page, movieListType)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(workPage -> {
                            if (mContract != null) {
                                if (workPage != null && workPage.getPage() <= workPage.getTotalPages()) {
                                    mCurrentPage = workPage.getPage();
                                    mContract.onWorksLoaded(workPage.getWorks());
                                } else {
                                    mContract.onWorksLoaded(null);
                                }
                            }
                        }, throwable -> {
                            if (mContract != null) {
                                mContract.onWorksLoaded(null);
                            }
                        }));
                break;
        }
    }

    /**
     * Loads the {@link List<Work>} by the {@link Genre}
     *
     * @param genre {@link Genre}
     */
    public void loadWorkByGenre(Genre genre) {
        int pageSearch = mCurrentPage + 1;
        mCompositeDisposable.add(mMediaRepository.getWorkByGenre(genre, pageSearch)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(workPage -> {
                    if (mContract != null) {
                        if (workPage != null && workPage.getPage() <= workPage.getTotalPages()) {
                            mCurrentPage = workPage.getPage();
                            mContract.onWorksLoaded(workPage.getWorks());
                        } else {
                            mContract.onWorksLoaded(null);
                        }
                    }
                }, throwable -> {
                    if (mContract != null) {
                        mContract.onWorksLoaded(null);
                    }
                }));
    }

    /**
     * Loads the {@link android.graphics.drawable.Drawable} from the {@link Work}
     *
     * @param work {@link Work}
     */
    public void loadBackdropImage(@NonNull Work work) {
        mImageManager.loadBitmapImage(String.format(BuildConfig.TMDB_LOAD_IMAGE_BASE_URL, work.getBackdropPath()),
                new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull final Bitmap resource, @Nullable final Transition<? super Bitmap> transition) {
                        if (mContract != null) {
                            mContract.onBackdropImageLoaded(resource);
                        }
                    }

                    @Override
                    public void onLoadFailed(@Nullable final Drawable errorDrawable) {
                        super.onLoadFailed(errorDrawable);
                        if (mContract != null) {
                            mContract.onBackdropImageLoaded(null);
                        }
                    }
                });
    }

    /**
     * Loads the {@link Work} porter into {@link ImageView}
     *
     * @param work     {@link Work}
     * @param imageView {@link ImageView}
     */
    public void loadWorkPosterImage(@NonNull Work work, ImageView imageView) {
        mImageManager.loadImageInto(imageView, String.format(BuildConfig.TMDB_LOAD_IMAGE_BASE_URL, work.getPosterPath()));
    }
}