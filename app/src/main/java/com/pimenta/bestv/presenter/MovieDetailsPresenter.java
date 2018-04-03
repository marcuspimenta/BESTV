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
import android.app.NotificationManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.pimenta.bestv.BesTV;
import com.pimenta.bestv.R;
import com.pimenta.bestv.connector.TmdbConnector;
import com.pimenta.bestv.manager.ImageManager;
import com.pimenta.bestv.manager.MovieManager;
import com.pimenta.bestv.model.Cast;
import com.pimenta.bestv.model.CastList;
import com.pimenta.bestv.model.Movie;
import com.pimenta.bestv.model.MovieList;
import com.pimenta.bestv.model.Video;
import com.pimenta.bestv.model.VideoList;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Single;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by marcus on 07-02-2018.
 */
public class MovieDetailsPresenter extends AbstractPresenter<MovieDetailsCallback> {

    private int mRecommendedPage = 0;
    private int mSimilarPage = 0;

    private Application mApplication;
    private MovieManager mMovieManager;
    private ImageManager mImageManager;
    private TmdbConnector mTmdbConnector;

    @Inject
    public MovieDetailsPresenter(Application application, MovieManager movieManager, ImageManager imageManager, TmdbConnector tmdbConnector) {
        super();
        mApplication = application;
        mMovieManager= movieManager;
        mImageManager = imageManager;
        mTmdbConnector = tmdbConnector;
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
        mCompositeDisposable.add(Single.create((SingleOnSubscribe<Boolean>) e -> {
                    boolean result;
                    if (movie.isFavorite()) {
                        result = mMovieManager.deleteFavoriteMovie(movie);
                    } else {
                        result = mMovieManager.saveFavoriteMovie(movie);
                    }
                    if (result) {
                        e.onSuccess(true);
                    } else {
                        e.onError(new AssertionError());
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result -> {
                    movie.setFavorite(!movie.isFavorite());
                    if (mCallback != null) {
                        mCallback.onResultSetFavoriteMovie(true);
                    }
                }, throwable -> {
                    if (mCallback != null) {
                        mCallback.onResultSetFavoriteMovie(false);
                    }
                }));
    }

    /**
     * Loads the {@link List<Cast>} by the {@link Movie}
     *
     * @param movie {@link Movie}
     */
    public void loadDataByMovie(Movie movie) {
        mCompositeDisposable.add(Single.create((SingleOnSubscribe<MovieInfo>) e -> {
                    final MovieInfo movieInfo = new MovieInfo();

                    final CastList castList = mTmdbConnector.getCastByMovie(movie);
                    if (castList != null) {
                        movieInfo.setCasts(castList.getCasts());
                    }

                    int recommendedPageSearch = mRecommendedPage + 1;
                    final MovieList recommendedMovieList = mTmdbConnector.getRecommendationByMovie(movie, recommendedPageSearch);
                    if (recommendedMovieList != null && recommendedMovieList.getPage() <= recommendedMovieList.getTotalPages()) {
                        mRecommendedPage = recommendedMovieList.getPage();
                        movieInfo.setRecommendedMovies(recommendedMovieList.getMovies());
                    }

                    int similarPageSearch = mSimilarPage + 1;
                    final MovieList similarMovieList = mTmdbConnector.getSimilarByMovie(movie, similarPageSearch);
                    if (similarMovieList != null && similarMovieList.getPage() <= similarMovieList.getTotalPages()) {
                        mSimilarPage = similarMovieList.getPage();
                        movieInfo.setSimilarMovies(similarMovieList.getMovies());
                    }

                    final VideoList videoList = mTmdbConnector.getVideosByMovie(movie);
                    if (videoList != null) {
                        movieInfo.setVideos(videoList.getVideos());
                    }

                    e.onSuccess(movieInfo);
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(movieInfo -> {
                    if (mCallback != null) {
                        mCallback.onDataLoaded(movieInfo.getCasts(), movieInfo.getRecommendedMovies(), movieInfo.getSimilarMovies(),
                                movieInfo.getVideos());
                    }
                }, throwable -> {
                    if (mCallback != null) {
                        mCallback.onDataLoaded(null, null, null, null);
                    }
                }));
    }

    /**
     * Loads the {@link List<Movie>} recommended by the {@link Movie}
     *
     * @param movie {@link Movie}
     */
    public void loadRecommendationByMovie(Movie movie) {
        mCompositeDisposable.add(Single.create((SingleOnSubscribe<List<Movie>>) e -> {
                    int pageSearch = mRecommendedPage + 1;
                    final MovieList movieList = mTmdbConnector.getRecommendationByMovie(movie, pageSearch);

                    if (movieList != null && movieList.getPage() <= movieList.getTotalPages()) {
                        mRecommendedPage = movieList.getPage();
                        e.onSuccess(movieList.getMovies());
                    } else {
                        e.onError(new AssertionError());
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(movies -> {
                    if (mCallback != null) {
                        mCallback.onRecommendationLoaded(movies);
                    }
                }, throwable -> {
                    if (mCallback != null) {
                        mCallback.onRecommendationLoaded(null);
                    }
                }));
    }

    /**
     * Loads the {@link List<Movie>} similar by the {@link Movie}
     *
     * @param movie {@link Movie}
     */
    public void loadSimilarByMovie(Movie movie) {
        mCompositeDisposable.add(Single.create((SingleOnSubscribe<List<Movie>>) e -> {
                    int pageSearch = mSimilarPage + 1;
                    final MovieList movieList = mTmdbConnector.getSimilarByMovie(movie, pageSearch);

                    if (movieList != null && movieList.getPage() <= movieList.getTotalPages()) {
                        mSimilarPage = movieList.getPage();
                        e.onSuccess(movieList.getMovies());
                    } else {
                        e.onError(new AssertionError());
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(movies -> {
                    if (mCallback != null) {
                        mCallback.onSimilarLoaded(movies);
                    }
                }, throwable -> {
                    if (mCallback != null) {
                        mCallback.onSimilarLoaded(null);
                    }
                }));
    }

    /**
     * Loads the {@link Movie} card image using Glide tool
     *
     * @param movie {@link Movie}
     */
    public void loadCardImage(Movie movie) {
        Glide.with(mApplication)
                .load(String.format(mApplication.getString(R.string.tmdb_load_image_url_api), movie.getPosterPath()))
                .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL))
                .into(new SimpleTarget<Drawable>() {
                    @Override
                    public void onResourceReady(@NonNull final Drawable resource, @Nullable final Transition<? super Drawable> transition) {
                        if (mCallback != null) {
                            mCallback.onCardImageLoaded(resource);
                        }
                    }

                    @Override
                    public void onLoadFailed(@Nullable final Drawable errorDrawable) {
                        if (mCallback != null) {
                            mCallback.onCardImageLoaded(null);
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
        mImageManager.loadBackdropImage(movie, new ImageManager.Callback<Bitmap>() {
            @Override
            public void onSuccess(final Bitmap resource) {
                if (mCallback != null) {
                    mCallback.onBackdropImageLoaded(resource);
                }
            }

            @Override
            public void onError() {
                if (mCallback != null) {
                    mCallback.onBackdropImageLoaded(null);
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