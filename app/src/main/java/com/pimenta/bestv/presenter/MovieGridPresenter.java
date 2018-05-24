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
import com.pimenta.bestv.repository.TmdbRepository;
import com.pimenta.bestv.manager.ImageManager;
import com.pimenta.bestv.manager.MovieManager;
import com.pimenta.bestv.domain.Genre;
import com.pimenta.bestv.domain.Movie;
import com.pimenta.bestv.domain.MovieList;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Maybe;
import io.reactivex.MaybeOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by marcus on 09-02-2018.
 */
public class MovieGridPresenter extends BasePresenter<MovieGridContract> {

    private int mCurrentPage = 0;

    private Application mApplication;
    private MovieManager mMovieManager;
    private ImageManager mImageManager;
    private MediaRepository mMediaRepository;

    @Inject
    public MovieGridPresenter(Application application, MovieManager movieManager, ImageManager imageManager, MediaRepository mediaRepository) {
        super();
        mApplication = application;
        mMovieManager = movieManager;
        mImageManager = imageManager;
        mMediaRepository = mediaRepository;
    }

    /**
     * Loads the now playing {@link List<Movie>}
     */
    public void loadMoviesByType(TmdbRepository.MovieListType movieListType) {
        mCompositeDisposable.add(Maybe.create((MaybeOnSubscribe<List<Movie>>) e -> {
                    if (movieListType.equals(TmdbRepository.MovieListType.FAVORITES)) {
                        final List<Movie> movies = mMovieManager.getFavoriteMovies();
                        if (movies != null) {
                            e.onSuccess(movies);
                        } else {
                            e.onError(new AssertionError());
                        }
                    } else {
                        int pageSearch = mCurrentPage + 1;
                        MovieList movieList = null;
                        switch (movieListType) {
                            case NOW_PLAYING:
                                movieList = mMediaRepository.getNowPlayingMovies(pageSearch);
                                break;
                            case POPULAR:
                                movieList = mMediaRepository.getPopularMovies(pageSearch);
                                break;
                            case TOP_RATED:
                                movieList = mMediaRepository.getTopRatedMovies(pageSearch);
                                break;
                            case UP_COMING:
                                movieList = mMediaRepository.getUpComingMovies(pageSearch);
                                break;
                        }

                        if (movieList != null && movieList.getPage() <= movieList.getTotalPages()) {
                            mCurrentPage = movieList.getPage();
                            e.onSuccess(movieList.getMovies());
                        } else {
                            e.onComplete();
                        }
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(movies -> {
                    if (mContract != null) {
                        mContract.onMoviesLoaded(movies);
                    }
                }, throwable -> {
                    if (mContract != null) {
                        mContract.onMoviesLoaded(null);
                    }
                }));
    }

    /**
     * Loads the {@link List<Movie>} by the {@link Genre}
     *
     * @param genre {@link Genre}
     */
    public void loadMoviesByGenre(Genre genre) {
        mCompositeDisposable.add(Maybe.create((MaybeOnSubscribe<List<Movie>>) e -> {
                    int pageSearch = mCurrentPage + 1;
                    final MovieList movieList = mMediaRepository.getMoviesByGenre(genre, pageSearch);

                    if (movieList != null && movieList.getPage() <= movieList.getTotalPages()) {
                        mCurrentPage = movieList.getPage();
                        e.onSuccess(movieList.getMovies());
                    } else {
                        e.onComplete();
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(movies -> {
                    if (mContract != null) {
                        mContract.onMoviesLoaded(movies);
                    }
                }, throwable -> {
                    if (mContract != null) {
                        mContract.onMoviesLoaded(null);
                    }
                }));
    }

    /**
     * Loads the {@link android.graphics.drawable.Drawable} from the {@link Movie}
     *
     * @param movie {@link Movie}
     */
    public void loadBackdropImage(@NonNull Movie movie) {
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
}