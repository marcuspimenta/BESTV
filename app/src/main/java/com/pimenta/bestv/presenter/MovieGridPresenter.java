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
import android.support.annotation.NonNull;

import com.pimenta.bestv.connector.TmdbConnector;
import com.pimenta.bestv.connector.TmdbConnectorImpl;
import com.pimenta.bestv.manager.ImageManager;
import com.pimenta.bestv.manager.MovieManager;
import com.pimenta.bestv.model.Genre;
import com.pimenta.bestv.model.Movie;
import com.pimenta.bestv.model.MovieList;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Single;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by marcus on 09-02-2018.
 */
public class MovieGridPresenter extends AbstractPresenter<MovieGridCallback> {

    private int mCurrentPage = 0;

    private MovieManager mMovieManager;
    private ImageManager mImageManager;
    private TmdbConnector mTmdbConnector;

    @Inject
    public MovieGridPresenter(MovieManager movieManager, ImageManager imageManager, TmdbConnector tmdbConnector) {
        super();
        mMovieManager = movieManager;
        mImageManager = imageManager;
        mTmdbConnector = tmdbConnector;
    }

    /**
     * Loads the now playing {@link List<Movie>}
     */
    public void loadMoviesByType(TmdbConnectorImpl.MovieListType movieListType) {
        mCompositeDisposable.add(Single.create((SingleOnSubscribe<List<Movie>>) e -> {
                    if (movieListType.equals(TmdbConnectorImpl.MovieListType.FAVORITE)) {
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
                                movieList = mTmdbConnector.getNowPlayingMovies(pageSearch);
                                break;
                            case POPULAR:
                                movieList = mTmdbConnector.getPopularMovies(pageSearch);
                                break;
                            case TOP_RATED:
                                movieList = mTmdbConnector.getTopRatedMovies(pageSearch);
                                break;
                            case UP_COMING:
                                movieList = mTmdbConnector.getUpComingMovies(pageSearch);
                                break;
                        }

                        if (movieList != null && movieList.getPage() <= movieList.getTotalPages()) {
                            mCurrentPage = movieList.getPage();
                            e.onSuccess(movieList.getMovies());
                        } else {
                            e.onError(new AssertionError());
                        }
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(movies -> {
                    if (mCallback != null) {
                        mCallback.onMoviesLoaded(movies);
                    }
                }, throwable -> {
                    if (mCallback != null) {
                        mCallback.onMoviesLoaded(null);
                    }
                }));
    }

    /**
     * Loads the {@link List<Movie>} by the {@link Genre}
     *
     * @param genre {@link Genre}
     */
    public void loadMoviesByGenre(Genre genre) {
        mCompositeDisposable.add(Single.create((SingleOnSubscribe<List<Movie>>) e -> {
                    int pageSearch = mCurrentPage + 1;
                    final MovieList movieList = mTmdbConnector.getMoviesByGenre(genre, pageSearch);

                    if (movieList != null && movieList.getPage() <= movieList.getTotalPages()) {
                        mCurrentPage = movieList.getPage();
                        e.onSuccess(movieList.getMovies());
                    } else {
                        e.onError(new AssertionError());
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(movies -> {
                    if (mCallback != null) {
                        mCallback.onMoviesLoaded(movies);
                    }
                }, throwable -> {
                    if (mCallback != null) {
                        mCallback.onMoviesLoaded(null);
                    }
                }));
    }

    /**
     * Loads the {@link android.graphics.drawable.Drawable} from the {@link Movie}
     *
     * @param movie {@link Movie}
     */
    public void loadBackdropImage(@NonNull Movie movie) {
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
}