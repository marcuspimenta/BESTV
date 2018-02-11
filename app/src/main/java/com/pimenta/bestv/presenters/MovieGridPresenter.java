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

package com.pimenta.bestv.presenters;

import android.support.annotation.NonNull;
import android.util.DisplayMetrics;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.pimenta.bestv.BesTV;
import com.pimenta.bestv.R;
import com.pimenta.bestv.connectors.TmdbConnector;
import com.pimenta.bestv.connectors.TmdbConnectorImpl;
import com.pimenta.bestv.models.Genre;
import com.pimenta.bestv.models.Movie;

import java.util.Collections;
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
    DisplayMetrics mDisplayMetrics;

    @Inject
    TmdbConnector mTmdbConnector;

    public MovieGridPresenter() {
        super();
        BesTV.getApplicationComponent().inject(this);
    }

    /**
     * Loads the now playing {@link List<Movie>}
     */
    public void loadToMoviesByType(TmdbConnectorImpl.MovieListType movieListType) {
        mCompositeDisposable.add(Single.create((SingleOnSubscribe<List<Movie>>) e -> {
                List<Movie> movies = null;
                switch (movieListType) {
                    case NOW_PLAYING:
                        movies = mTmdbConnector.getNowPlayingMovies();
                        break;
                    case POPULAR:
                        movies = mTmdbConnector.getPopularMovies();
                        break;
                    case TOP_RATED:
                        movies = mTmdbConnector.getTopRatedMovies();
                        break;
                    case UP_COMING:
                        movies = mTmdbConnector.getUpComingMovies();
                        break;
                }

                if (movies != null) {
                    Collections.sort(movies, (a, b) -> a.getReleaseDate().getTime() > b.getReleaseDate().getTime() ? -1 : 1);
                }
                e.onSuccess(movies);
            })
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(movies -> {
                if (mCallback != null) {
                    mCallback.onMoviesLoaded(movies);
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
                final List<Movie> movies = mTmdbConnector.getMoviesByGenre(genre);
                if (movies != null) {
                    Collections.sort(movies, (a, b) -> a.getReleaseDate().getTime() > b.getReleaseDate().getTime() ? -1 : 1);
                }
                e.onSuccess(movies);
            })
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(movies -> {
                if (mCallback != null) {
                    mCallback.onMoviesLoaded(movies);
                }
            }));
    }

    /**
     * Loads the {@link android.graphics.drawable.Drawable} from the {@link Movie}
     *
     * @param movie {@link Movie}
     */
    public void loadBackdropImage(@NonNull Movie movie) {
        Glide.with(BesTV.get())
            .load(String.format(BesTV.get().getString(R.string.tmdb_load_image_url_api_w1280), movie.getBackdropPath()))
            .centerCrop()
            .error(R.drawable.lb_ic_sad_cloud)
            .into(new SimpleTarget<GlideDrawable>(mDisplayMetrics.widthPixels, mDisplayMetrics.heightPixels) {
                @Override
                public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
                    if (mCallback != null) {
                        mCallback.onBackdropImageLoaded(resource);
                    }
                }
            });
    }

}