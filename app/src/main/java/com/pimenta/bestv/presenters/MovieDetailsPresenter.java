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

import android.app.Application;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.pimenta.bestv.BesTV;
import com.pimenta.bestv.R;
import com.pimenta.bestv.connectors.TmdbConnector;
import com.pimenta.bestv.models.Cast;
import com.pimenta.bestv.models.CastList;
import com.pimenta.bestv.models.Movie;
import com.pimenta.bestv.models.MovieList;

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

    @Inject
    Application mApplication;

    @Inject
    TmdbConnector mTmdbConnector;

    private int mCurrentPage = 0;

    public MovieDetailsPresenter() {
        super();
        BesTV.getApplicationComponent().inject(this);
    }

    /**
     * Loads the {@link List<Cast>} by the {@link Movie}
     *
     * @param movie {@link Movie}
     */
    public void loadCastByMovie(Movie movie) {
        mCompositeDisposable.add(Single.create((SingleOnSubscribe<List<Cast>>) e -> {
                final CastList castList = mTmdbConnector.getCastByMovie(movie);
                if (castList != null) {
                    e.onSuccess(castList.getCasts());
                } else {
                    e.onError(new AssertionError());
                }
            })
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(casts -> {
                if (mCallback != null) {
                    mCallback.onCastLoaded(casts);
                }
            }, throwable -> {
                if (mCallback != null) {
                    mCallback.onCastLoaded(null);
                }
            }));
    }

    /**
     * Loads the {@link List<Cast>} by the {@link Movie}
     *
     * @param movie {@link Movie}
     */
    public void loadRecommendationByMovie(Movie movie) {
        mCompositeDisposable.add(Single.create((SingleOnSubscribe<List<Movie>>) e -> {
                int pageSearch = mCurrentPage + 1;
                MovieList movieList = mTmdbConnector.getRecommendationByMovie(movie, pageSearch);

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
                    mCallback.onRecommendationLoaded(movies);
                }
            }, throwable -> {
                if (mCallback != null) {
                    mCallback.onRecommendationLoaded(null);
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
            .load(String.format(mApplication.getString(R.string.tmdb_load_image_url_api_w780), movie.getPosterPath()))
            .centerCrop()
            .error(R.drawable.lb_ic_sad_cloud)
            .into(new SimpleTarget<GlideDrawable>(convertDpToPixel(mApplication.getResources().getDimension(R.dimen.movie_card_width)),
                    convertDpToPixel(mApplication.getResources().getDimension(R.dimen.movie_card_height))) {
                @Override
                public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
                    if (mCallback != null) {
                        mCallback.onCardImageLoaded(resource);
                    }
                }

                @Override
                public void onLoadFailed(final Exception e, final Drawable errorDrawable) {
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
        Glide.with(mApplication)
            .load(String.format(mApplication.getString(R.string.tmdb_load_image_url_api_w1280), movie.getBackdropPath()))
            .asBitmap()
            .centerCrop()
            .error(R.drawable.lb_ic_sad_cloud)
            .into(new SimpleTarget<Bitmap>() {
                @Override
                public void onResourceReady(Bitmap bitmap, GlideAnimation<? super Bitmap> glideAnimation) {
                    if (mCallback != null) {
                        mCallback.onBackdropImageLoaded(bitmap);
                    }
                }

                @Override
                public void onLoadFailed(final Exception e, final Drawable errorDrawable) {
                    if (mCallback != null) {
                        mCallback.onBackdropImageLoaded(null);
                    }
                }
            });
    }

    private int convertDpToPixel(float dp) {
        float density = mApplication.getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }

}