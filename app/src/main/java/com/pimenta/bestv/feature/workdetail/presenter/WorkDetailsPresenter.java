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

package com.pimenta.bestv.feature.workdetail.presenter;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.pimenta.bestv.BuildConfig;
import com.pimenta.bestv.feature.base.BasePresenter;
import com.pimenta.bestv.manager.ImageManager;
import com.pimenta.bestv.repository.MediaRepository;
import com.pimenta.bestv.repository.entity.Cast;
import com.pimenta.bestv.repository.entity.CastList;
import com.pimenta.bestv.repository.entity.Video;
import com.pimenta.bestv.repository.entity.VideoList;
import com.pimenta.bestv.repository.entity.Work;
import com.pimenta.bestv.repository.entity.WorkPage;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Maybe;
import io.reactivex.MaybeOnSubscribe;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by marcus on 07-02-2018.
 */
public class WorkDetailsPresenter extends BasePresenter<WorkDetailsPresenter.WorkDetailsView> {

    private static final String TAG = WorkDetailsPresenter.class.getSimpleName();

    private int mRecommendedPage = 0;
    private int mSimilarPage = 0;

    private final MediaRepository mMediaRepository;
    private final ImageManager mImageManager;

    @Inject
    public WorkDetailsPresenter(MediaRepository mediaRepository, ImageManager imageManager) {
        mImageManager = imageManager;
        mMediaRepository = mediaRepository;
    }

    /**
     * Checks if the {@link Work} is favorite
     *
     * @param work {@link Work}
     *
     * @return {@code true} if yes, {@code false} otherwise
     */
    public boolean isFavorite(@NonNull Work work) {
        work.setFavorite(mMediaRepository.isFavorite(work));
        return work.isFavorite();
    }

    /**
     * Sets if a {@link Work} is or not is favorite
     *
     * @param work {@link Work}
     */
    public void setFavorite(@NonNull Work work) {
        getCompositeDisposable().add(Maybe.create((MaybeOnSubscribe<Boolean>) e -> {
                    boolean result;
                    if (work.isFavorite()) {
                        result = mMediaRepository.deleteFavorite(work);
                    } else {
                        result = mMediaRepository.saveFavorite(work);
                    }
                    if (result) {
                        e.onSuccess(true);
                    } else {
                        e.onComplete();
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result -> {
                    work.setFavorite(!work.isFavorite());
                    if (getView() != null) {
                        getView().onResultSetFavoriteMovie(true);
                    }
                }, throwable -> {
                    Log.e(TAG, "Error while settings the work as favorite", throwable);
                    if (getView() != null) {
                        getView().onResultSetFavoriteMovie(false);
                    }
                }));
    }

    /**
     * Loads the {@link List<Cast>} by the {@link Work}
     *
     * @param work {@link Work}
     */
    public void loadDataByWork(@NonNull Work work) {
        int recommendedPageSearch = mRecommendedPage + 1;
        int similarPageSearch = mSimilarPage + 1;
        getCompositeDisposable().add(Single.zip(mMediaRepository.getVideosByWork(work),
                mMediaRepository.getCastByWork(work),
                mMediaRepository.getRecommendationByWork(work, recommendedPageSearch),
                mMediaRepository.getSimilarByWork(work, similarPageSearch),
                WorkInfo::new)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(movieInfo -> {
                    if (getView() != null) {
                        final WorkPage recommendedPage = movieInfo.getRecommendedMovies();
                        if (recommendedPage != null && recommendedPage.getPage() <= recommendedPage.getTotalPages()) {
                            mRecommendedPage = recommendedPage.getPage();
                        }
                        final WorkPage similarPage = movieInfo.getSimilarMovies();
                        if (similarPage != null && similarPage.getPage() <= similarPage.getTotalPages()) {
                            mSimilarPage = similarPage.getPage();
                        }

                        getView().onDataLoaded(movieInfo.getCasts() != null ? movieInfo.getCasts().getCasts() : null,
                                recommendedPage != null ? recommendedPage.getWorks() : null,
                                similarPage != null ? similarPage.getWorks() : null,
                                movieInfo.getVideos() != null ? movieInfo.getVideos().getVideos() : null);
                    }
                }, throwable -> {
                    Log.e(TAG, "Error while loading data by work", throwable);
                    if (getView() != null) {
                        getView().onDataLoaded(null, null, null, null);
                    }
                }));
    }

    /**
     * Loads the {@link List<Work>} recommended by the {@link Work}
     *
     * @param work {@link Work}
     */
    public void loadRecommendationByWork(@NonNull Work work) {
        int pageSearch = mRecommendedPage + 1;
        getCompositeDisposable().add(mMediaRepository.getRecommendationByWork(work, pageSearch)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(movieList -> {
                    if (getView() != null) {
                        if (movieList != null && movieList.getPage() <= movieList.getTotalPages()) {
                            mRecommendedPage = movieList.getPage();
                            getView().onRecommendationLoaded(movieList.getWorks());
                        } else {
                            getView().onRecommendationLoaded(null);
                        }
                    }
                }, throwable -> {
                    Log.e(TAG, "Error while loading recommendations by work", throwable);
                    if (getView() != null) {
                        getView().onRecommendationLoaded(null);
                    }
                }));
    }

    /**
     * Loads the {@link List<Work>} similar by the {@link Work}
     *
     * @param work {@link Work}
     */
    public void loadSimilarByWork(@NonNull Work work) {
        int pageSearch = mSimilarPage + 1;
        getCompositeDisposable().add(mMediaRepository.getSimilarByWork(work, pageSearch)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(movieList -> {
                    if (getView() != null) {
                        if (movieList != null && movieList.getPage() <= movieList.getTotalPages()) {
                            mSimilarPage = movieList.getPage();
                            getView().onSimilarLoaded(movieList.getWorks());
                        } else {
                            getView().onSimilarLoaded(null);
                        }
                    }
                }, throwable -> {
                    Log.e(TAG, "Error while loading similar by work", throwable);
                    if (getView() != null) {
                        getView().onSimilarLoaded(null);
                    }
                }));
    }

    /**
     * Loads the {@link Work} poster
     *
     * @param work {@link Work}
     */
    public void loadCardImage(@NonNull Work work) {
        mImageManager.loadImage(String.format(BuildConfig.TMDB_LOAD_IMAGE_BASE_URL, work.getPosterPath()),
                new SimpleTarget<Drawable>() {
                    @Override
                    public void onResourceReady(@NonNull final Drawable resource, @Nullable final Transition<? super Drawable> transition) {
                        if (getView() != null) {
                            getView().onCardImageLoaded(resource);
                        }
                    }

                    @Override
                    public void onLoadFailed(@Nullable final Drawable errorDrawable) {
                        super.onLoadFailed(errorDrawable);
                        Log.w(TAG, "Error while loading card image");
                        if (getView() != null) {
                            getView().onCardImageLoaded(null);
                        }
                    }
                });
    }

    /**
     * Loads the {@link Work} backdrop image using Glide tool
     *
     * @param work {@link Work}
     */
    public void loadBackdropImage(@NonNull Work work) {
        mImageManager.loadBitmapImage(String.format(BuildConfig.TMDB_LOAD_IMAGE_BASE_URL, work.getBackdropPath()),
                new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull final Bitmap resource, @Nullable final Transition<? super Bitmap> transition) {
                        if (getView() != null) {
                            getView().onBackdropImageLoaded(resource);
                        }
                    }

                    @Override
                    public void onLoadFailed(@Nullable final Drawable errorDrawable) {
                        super.onLoadFailed(errorDrawable);
                        if (getView() != null) {
                            getView().onBackdropImageLoaded(null);
                        }
                    }
                });
    }

    /**
     * Loads the {@link Cast} profile into {@link ImageView}
     *
     * @param cast      {@link Cast}
     * @param imageView {@link ImageView}
     */
    public void loadCastProfileImage(@NonNull Cast cast, ImageView imageView) {
        mImageManager.loadImageInto(imageView, String.format(BuildConfig.TMDB_LOAD_IMAGE_BASE_URL, cast.getProfilePath()));
    }

    /**
     * Loads the {@link Work} porter into {@link ImageView}
     *
     * @param work      {@link Work}
     * @param imageView {@link ImageView}
     */
    public void loadWorkPosterImage(@NonNull Work work, ImageView imageView) {
        mImageManager.loadImageInto(imageView, String.format(BuildConfig.TMDB_LOAD_IMAGE_BASE_URL, work.getPosterPath()));
    }

    /**
     * Loads the {@link Video} thumbnail into {@link ImageView}
     *
     * @param video     {@link Video}
     * @param imageView {@link ImageView}
     */
    public void loadVideoThumbnailImage(@NonNull Video video, ImageView imageView) {
        mImageManager.loadImageInto(imageView, String.format(BuildConfig.YOUTUBE_THUMBNAIL_BASE_URL, video.getKey()));
    }

    /**
     * Wrapper class to keep the work info
     */
    private class WorkInfo {

        private final VideoList mVideos;
        private final CastList mCasts;
        private final WorkPage mRecommendedMovies;
        private final WorkPage mSimilarMovies;

        public WorkInfo(VideoList videos, CastList casts, WorkPage recommendedMovies, WorkPage similarMovies) {
            mVideos = videos;
            mCasts = casts;
            mRecommendedMovies = recommendedMovies;
            mSimilarMovies = similarMovies;
        }

        public VideoList getVideos() {
            return mVideos;
        }

        public CastList getCasts() {
            return mCasts;
        }

        public WorkPage getRecommendedMovies() {
            return mRecommendedMovies;
        }

        public WorkPage getSimilarMovies() {
            return mSimilarMovies;
        }
    }

    public interface WorkDetailsView extends BasePresenter.BaseView {

        void onResultSetFavoriteMovie(boolean success);

        void onDataLoaded(List<Cast> casts, List<? extends Work> recommendedMovies, List<? extends Work> similarMovies, List<? extends Video> videos);

        void onRecommendationLoaded(List<? extends Work> works);

        void onSimilarLoaded(List<? extends Work> works);

        void onCardImageLoaded(Drawable resource);

        void onBackdropImageLoaded(Bitmap bitmap);

    }
}