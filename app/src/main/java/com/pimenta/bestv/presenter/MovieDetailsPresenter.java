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

import android.app.Application;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.pimenta.bestv.R;
import com.pimenta.bestv.repository.MediaRepository;
import com.pimenta.bestv.manager.ImageManager;
import com.pimenta.bestv.manager.MovieManager;
import com.pimenta.bestv.domain.Cast;
import com.pimenta.bestv.domain.CastList;
import com.pimenta.bestv.domain.Movie;
import com.pimenta.bestv.domain.MovieList;
import com.pimenta.bestv.domain.Video;
import com.pimenta.bestv.domain.VideoList;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Maybe;
import io.reactivex.MaybeOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by marcus on 07-02-2018.
 */
public class MovieDetailsPresenter extends BasePresenter<MovieDetailsContract> {

    private int mRecommendedPage = 0;
    private int mSimilarPage = 0;

    private Application mApplication;
    private MovieManager mMovieManager;
    private ImageManager mImageManager;
    private MediaRepository mMediaRepository;

    @Inject
    public MovieDetailsPresenter(Application application, MovieManager movieManager, ImageManager imageManager, MediaRepository mediaRepository) {
        super();
        mApplication = application;
        mMovieManager = movieManager;
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
    public boolean isMovieFavorite(Movie movie) {
        movie.setFavorite(mMovieManager.isFavorite(movie));
        return movie.isFavorite();
    }

    /**
     * Sets if a {@link Movie} is or not is favorite
     *
     * @param movie {@link Movie}
     */
    public void setFavoriteMovie(Movie movie) {
        mCompositeDisposable.add(Maybe.create((MaybeOnSubscribe<Boolean>) e -> {
                    boolean result;
                    if (movie.isFavorite()) {
                        result = mMovieManager.deleteFavoriteMovie(movie);
                    } else {
                        result = mMovieManager.saveFavoriteMovie(movie);
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
    public void loadDataByMovie(Movie movie) {
        mCompositeDisposable.add(Maybe.create((MaybeOnSubscribe<MovieInfo>) e -> {
                    final MovieInfo movieInfo = new MovieInfo();

                    final CastList castList = mMediaRepository.getCastByMovie(movie);
                    if (castList != null) {
                        movieInfo.setCasts(castList.getCasts());
                    }

                    int recommendedPageSearch = mRecommendedPage + 1;
                    final MovieList recommendedMovieList = mMediaRepository.getRecommendationByMovie(movie, recommendedPageSearch);
                    if (recommendedMovieList != null && recommendedMovieList.getPage() <= recommendedMovieList.getTotalPages()) {
                        mRecommendedPage = recommendedMovieList.getPage();
                        movieInfo.setRecommendedMovies(recommendedMovieList.getMovies());
                    }

                    int similarPageSearch = mSimilarPage + 1;
                    final MovieList similarMovieList = mMediaRepository.getSimilarByMovie(movie, similarPageSearch);
                    if (similarMovieList != null && similarMovieList.getPage() <= similarMovieList.getTotalPages()) {
                        mSimilarPage = similarMovieList.getPage();
                        movieInfo.setSimilarMovies(similarMovieList.getMovies());
                    }

                    final VideoList videoList = mMediaRepository.getVideosByMovie(movie);
                    if (videoList != null) {
                        movieInfo.setVideos(videoList.getVideos());
                    }

                    e.onSuccess(movieInfo);
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(movieInfo -> {
                    if (mContract != null) {
                        mContract.onDataLoaded(movieInfo.getCasts(), movieInfo.getRecommendedMovies(), movieInfo.getSimilarMovies(),
                                movieInfo.getVideos());
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
    public void loadRecommendationByMovie(Movie movie) {
        mCompositeDisposable.add(Maybe.create((MaybeOnSubscribe<List<Movie>>) e -> {
                    int pageSearch = mRecommendedPage + 1;
                    final MovieList movieList = mMediaRepository.getRecommendationByMovie(movie, pageSearch);

                    if (movieList != null && movieList.getPage() <= movieList.getTotalPages()) {
                        mRecommendedPage = movieList.getPage();
                        e.onSuccess(movieList.getMovies());
                    } else {
                        e.onComplete();
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(movies -> {
                    if (mContract != null) {
                        mContract.onRecommendationLoaded(movies);
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
    public void loadSimilarByMovie(Movie movie) {
        mCompositeDisposable.add(Maybe.create((MaybeOnSubscribe<List<Movie>>) e -> {
                    int pageSearch = mSimilarPage + 1;
                    final MovieList movieList = mMediaRepository.getSimilarByMovie(movie, pageSearch);

                    if (movieList != null && movieList.getPage() <= movieList.getTotalPages()) {
                        mSimilarPage = movieList.getPage();
                        e.onSuccess(movieList.getMovies());
                    } else {
                        e.onComplete();
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(movies -> {
                    if (mContract != null) {
                        mContract.onSimilarLoaded(movies);
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
    public void loadCardImage(Movie movie) {
        mImageManager.loadImage(String.format(mApplication.getString(R.string.tmdb_load_image_url_api), movie.getPosterPath()),
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
    public void loadBackdropImage(Movie movie) {
        mImageManager.loadBitmapImage(String.format(mApplication.getString(R.string.tmdb_load_image_url_api), movie.getBackdropPath()),
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
     * Wrapper class to keep the movie info
     */
    private class MovieInfo {

        private List<Cast> mCasts;
        private List<Movie> mRecommendedMovies;
        private List<Movie> mSimilarMovies;
        private List<Video> mVideos;

        public List<Cast> getCasts() {
            return mCasts;
        }

        public void setCasts(final List<Cast> casts) {
            mCasts = casts;
        }

        public List<Movie> getRecommendedMovies() {
            return mRecommendedMovies;
        }

        public void setRecommendedMovies(final List<Movie> recommendedMovies) {
            mRecommendedMovies = recommendedMovies;
        }

        public List<Movie> getSimilarMovies() {
            return mSimilarMovies;
        }

        public void setSimilarMovies(final List<Movie> similarMovies) {
            mSimilarMovies = similarMovies;
        }

        public List<Video> getVideos() {
            return mVideos;
        }

        public void setVideos(final List<Video> videos) {
            mVideos = videos;
        }
    }

}