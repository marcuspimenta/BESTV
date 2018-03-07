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
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.pimenta.bestv.BesTV;
import com.pimenta.bestv.R;
import com.pimenta.bestv.connector.TmdbConnector;
import com.pimenta.bestv.connector.TmdbConnectorImpl;
import com.pimenta.bestv.models.Genre;
import com.pimenta.bestv.models.Movie;
import com.pimenta.bestv.models.MovieList;

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

    @Inject
    Application mApplication;

    @Inject
    DisplayMetrics mDisplayMetrics;

    @Inject
    TmdbConnector mTmdbConnector;

    private int mCurrentPage = 0;

    public MovieGridPresenter() {
        super();
        BesTV.getApplicationComponent().inject(this);
    }

    /**
     * Loads the now playing {@link List<Movie>}
     */
    public void loadToMoviesByType(TmdbConnectorImpl.MovieListType movieListType) {
        mCompositeDisposable.add(Single.create((SingleOnSubscribe<List<Movie>>) e -> {
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
        Glide.with(mApplication)
            .load(String.format(mApplication.getResources().getString(R.string.tmdb_load_image_url_api_w1280), movie.getBackdropPath()))
            .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL))
            .into(new SimpleTarget<Drawable>() {
                @Override
                public void onResourceReady(@NonNull final Drawable resource, @Nullable final Transition<? super Drawable> transition) {
                    if (mCallback != null) {
                        mCallback.onBackdropImageLoaded(resource);
                    }
                }

                @Override
                public void onLoadFailed(@Nullable final Drawable errorDrawable) {
                    if (mCallback != null) {
                        mCallback.onBackdropImageLoaded(null);
                    }
                }
            });
    }
}