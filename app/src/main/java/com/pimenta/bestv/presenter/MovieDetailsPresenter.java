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
import com.pimenta.bestv.repository.entity.Cast;
import com.pimenta.bestv.repository.entity.CastList;
import com.pimenta.bestv.repository.entity.Movie;
import com.pimenta.bestv.repository.entity.MoviePage;
import com.pimenta.bestv.repository.entity.Video;
import com.pimenta.bestv.repository.entity.VideoList;
import com.pimenta.bestv.repository.entity.Work;

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
public class MovieDetailsPresenter extends BasePresenter<MovieDetailsContract> {

    private int mRecommendedPage = 0;
    private int mSimilarPage = 0;

    private MediaRepository mMediaRepository;
    private ImageManager mImageManager;

    @Inject
    public MovieDetailsPresenter(MediaRepository mediaRepository, ImageManager imageManager) {
        super();
        mImageManager = imageManager;
        mMediaRepository = mediaRepository;
    }

    /**
     * Checks if the {@link Movie} is favorite
     *
     * @param movie {@link Movie}
     *
     * @return {@code true} if yes, {@code false} otherwise
     */
    public boolean isMovieFavorite(@NonNull Movie movie) {
        movie.setFavorite(mMediaRepository.isFavorite(movie));
        return movie.isFavorite();
    }

    /**
     * Sets if a {@link Movie} is or not is favorite
     *
     * @param movie {@link Movie}
     */
    public void setFavoriteMovie(@NonNull Movie movie) {
        mCompositeDisposable.add(Maybe.create((MaybeOnSubscribe<Boolean>) e -> {
            boolean result;
            if (movie.isFavorite()) {
                result = mMediaRepository.deleteFavoriteMovie(movie);
            } else {
                result = mMediaRepository.saveFavoriteMovie(movie);
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
                    movie.setFavorite(!movie.isFavorite());
                    if (mContract != null) {
                        mContract.onResultSetFavoriteMovie(true);
                    }
                }, throwable -> {
                    if (mContract != null) {
                        mContract.onResultSetFavoriteMovie(false);
                    }
                }));
    }

    /**
     * Loads the {@link List<Cast>} by the {@link Movie}
     *
     * @param movie {@link Movie}
     */
    public void loadDataByMovie(@NonNull Movie movie) {
        int recommendedPageSearch = mRecommendedPage + 1;
        int similarPageSearch = mSimilarPage + 1;
        mCompositeDisposable.add(Single.zip(mMediaRepository.getCastByMovie(movie),
                mMediaRepository.getRecommendationByMovie(movie, recommendedPageSearch),
                mMediaRepository.getSimilarByMovie(movie, similarPageSearch),
                mMediaRepository.getVideosByMovie(movie),
                MovieInfo::new)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(movieInfo -> {
                    if (mContract != null) {
                        final MoviePage recommendedMovies = movieInfo.getRecommendedMovies();
                        if (recommendedMovies != null && recommendedMovies.getPage() <= recommendedMovies.getTotalPages()) {
                            mRecommendedPage = recommendedMovies.getPage();
                        }
                        final MoviePage similarMovies = movieInfo.getSimilarMovies();
                        if (similarMovies != null && similarMovies.getPage() <= similarMovies.getTotalPages()) {
                            mSimilarPage = similarMovies.getPage();
                        }

                        mContract.onDataLoaded(movieInfo.getCasts() != null ? movieInfo.getCasts().getCasts() : null,
                                recommendedMovies != null ? recommendedMovies.getWorks() : null,
                                similarMovies != null ? similarMovies.getWorks() : null,
                                movieInfo.getVideos() != null ? movieInfo.getVideos().getVideos() : null);
                    }
                }, throwable -> {
                    if (mContract != null) {
                        mContract.onDataLoaded(null, null, null, null);
                    }
                }));
    }

    /**
     * Loads the {@link List<Movie>} recommended by the {@link Movie}
     *
     * @param movie {@link Movie}
     */
    public void loadRecommendationByMovie(@NonNull Movie movie) {
        int pageSearch = mRecommendedPage + 1;
        mCompositeDisposable.add(mMediaRepository.getRecommendationByMovie(movie, pageSearch)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(movieList -> {
                    if (mContract != null) {
                        if (movieList != null && movieList.getPage() <= movieList.getTotalPages()) {
                            mRecommendedPage = movieList.getPage();
                            mContract.onRecommendationLoaded(movieList.getWorks());
                        } else {
                            mContract.onRecommendationLoaded(null);
                        }
                    }
                }, throwable -> {
                    if (mContract != null) {
                        mContract.onRecommendationLoaded(null);
                    }
                }));
    }

    /**
     * Loads the {@link List<Movie>} similar by the {@link Movie}
     *
     * @param movie {@link Movie}
     */
    public void loadSimilarByMovie(@NonNull Movie movie) {
        int pageSearch = mSimilarPage + 1;
        mCompositeDisposable.add(mMediaRepository.getSimilarByMovie(movie, pageSearch)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(movieList -> {
                    if (mContract != null) {
                        if (movieList != null && movieList.getPage() <= movieList.getTotalPages()) {
                            mSimilarPage = movieList.getPage();
                            mContract.onSimilarLoaded(movieList.getWorks());
                        } else {
                            mContract.onSimilarLoaded(null);
                        }
                    }
                }, throwable -> {
                    if (mContract != null) {
                        mContract.onSimilarLoaded(null);
                    }
                }));
    }

    /**
     * Loads the {@link Movie} poster
     *
     * @param movie {@link Movie}
     */
    public void loadCardImage(@NonNull Movie movie) {
        mImageManager.loadImage(String.format(BuildConfig.TMDB_LOAD_IMAGE_BASE_URL, movie.getPosterPath()),
                new SimpleTarget<Drawable>() {
                    @Override
                    public void onResourceReady(@NonNull final Drawable resource, @Nullable final Transition<? super Drawable> transition) {
                        if (mContract != null) {
                            mContract.onCardImageLoaded(resource);
                        }
                    }

                    @Override
                    public void onLoadFailed(@Nullable final Drawable errorDrawable) {
                        super.onLoadFailed(errorDrawable);
                        if (mContract != null) {
                            mContract.onCardImageLoaded(null);
                        }
                    }
                });
    }

    /**
     * Loads the {@link Movie} backdrop image using Glide tool
     *
     * @param movie {@link Movie}
     */
    public void loadBackdropImage(@NonNull Movie movie) {
        mImageManager.loadBitmapImage(String.format(BuildConfig.TMDB_LOAD_IMAGE_BASE_URL, movie.getBackdropPath()),
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
        mImageManager.loadImageInto(imageView,
                String.format(BuildConfig.TMDB_LOAD_IMAGE_BASE_URL, work.getPosterPath()));
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
     * Wrapper class to keep the movie info
     */
    private class MovieInfo {

        private CastList mCasts;
        private MoviePage mRecommendedMovies;
        private MoviePage mSimilarMovies;
        private VideoList mVideos;

        public MovieInfo(final CastList casts, final MoviePage recommendedMovies, final MoviePage similarMovies, final VideoList videos) {
            mCasts = casts;
            mRecommendedMovies = recommendedMovies;
            mSimilarMovies = similarMovies;
            mVideos = videos;
        }

        public CastList getCasts() {
            return mCasts;
        }

        public MoviePage getRecommendedMovies() {
            return mRecommendedMovies;
        }

        public MoviePage getSimilarMovies() {
            return mSimilarMovies;
        }

        public VideoList getVideos() {
            return mVideos;
        }
    }
}