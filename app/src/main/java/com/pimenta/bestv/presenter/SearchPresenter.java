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
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Pair;
import android.widget.ImageView;

import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.pimenta.bestv.BuildConfig;
import com.pimenta.bestv.manager.ImageManager;
import com.pimenta.bestv.repository.MediaRepository;
import com.pimenta.bestv.repository.entity.Movie;
import com.pimenta.bestv.repository.entity.TvShow;
import com.pimenta.bestv.repository.entity.Work;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by marcus on 12-03-2018.
 */
public class SearchPresenter extends BasePresenter<SearchContract> {

    private static final String TAG = SearchPresenter.class.getSimpleName();

    private DisplayMetrics mDisplayMetrics;
    private MediaRepository mMediaRepository;
    private ImageManager mImageManager;

    private String mQuery;
    private int mResultMoviePage = 0;
    private int mResultTvShowPage = 0;
    private Disposable mDisposable;

    @Inject
    public SearchPresenter(DisplayMetrics displayMetrics, MediaRepository mediaRepository, ImageManager imageManager) {
        super();
        mDisplayMetrics = displayMetrics;
        mMediaRepository = mediaRepository;
        mImageManager = imageManager;
    }

    @Override
    public void unRegister() {
        disposeSearchMovie();
        super.unRegister();
    }

    /**
     * Gets the {@link DisplayMetrics} instance
     *
     * @return {@link DisplayMetrics}
     */
    public DisplayMetrics getDisplayMetrics() {
        return mDisplayMetrics;
    }

    /**
     * Searches the movies by a query
     *
     * @param query Query to search the movies
     */
    public void searchWorksByQuery(String query) {
        disposeSearchMovie();
        try {
            String queryEncode = URLEncoder.encode(query, "UTF-8");
            if (!queryEncode.equals(mQuery)) {
                mResultMoviePage = 0;
                mResultTvShowPage = 0;
            }
            mQuery = queryEncode;
            int resultMoviePage = mResultMoviePage + 1;
            int resultTvShowPage = mResultTvShowPage + 1;
            mDisposable = Single.zip(mMediaRepository.searchMoviesByQuery(mQuery, resultMoviePage),
                    mMediaRepository.searchTvShowsByQuery(mQuery, resultTvShowPage),
                    Pair::new)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(pair -> {
                        if (mContract != null) {
                            List<Movie> movies = null;
                            if (pair.first != null && pair.first.getPage() <= pair.first.getTotalPages()) {
                                mResultMoviePage = pair.first.getPage();
                                movies = pair.first.getWorks();
                            }
                            List<TvShow> tvShows = null;
                            if (pair.second != null && pair.second.getPage() <= pair.second.getTotalPages()) {
                                mResultTvShowPage = pair.second.getPage();
                                tvShows = pair.second.getWorks();
                            }
                            mContract.onResultLoaded(movies, tvShows);
                        }
                    }, throwable -> {
                        Log.e(TAG, "Error while searching movies by query", throwable);
                        if (mContract != null) {
                            mContract.onResultLoaded(null, null);
                        }
                    });
        } catch (UnsupportedEncodingException e) {
            Log.e(TAG, "Error while encoding the query", e);
        }
    }

    /**
     * Load the movies by a query
     */
    public void loadMovies() {
        int resultMoviePage = mResultMoviePage + 1;
        mCompositeDisposable.add(mMediaRepository.searchMoviesByQuery(mQuery, resultMoviePage)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(moviePage -> {
                    if (mContract != null) {
                        if (moviePage != null && moviePage.getPage() <= moviePage.getTotalPages()) {
                            mResultMoviePage = moviePage.getPage();
                            mContract.onMoviesLoaded(moviePage.getWorks());
                        } else {
                            mContract.onMoviesLoaded(null);
                        }
                    }
                }, throwable -> {
                    Log.e(TAG, "Error while loading movies by query", throwable);
                    if (mContract != null) {
                        mContract.onMoviesLoaded(null);
                    }
                }));
    }

    /**
     * Load the tv shows by a query
     */
    public void loadTvShows() {
        int resultTvShowPage = mResultTvShowPage + 1;
        mCompositeDisposable.add(mMediaRepository.searchTvShowsByQuery(mQuery, resultTvShowPage)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(tvShowPage -> {
                    if (mContract != null) {
                        if (tvShowPage != null && tvShowPage.getPage() <= tvShowPage.getTotalPages()) {
                            mResultTvShowPage = tvShowPage.getPage();
                            mContract.onTvShowsLoaded(tvShowPage.getWorks());
                        } else {
                            mContract.onTvShowsLoaded(null);
                        }
                    }
                }, throwable -> {
                    Log.e(TAG, "Error while loading tv shows by query", throwable);
                    if (mContract != null) {
                        mContract.onTvShowsLoaded(null);
                    }
                }));
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
                        Log.w(TAG, "Error while loading backdrop image");
                        if (mContract != null) {
                            mContract.onBackdropImageLoaded(null);
                        }
                    }
                });
    }

    /**
     * Disposes the search movies.
     */
    private void disposeSearchMovie() {
        if (mDisposable != null && !mDisposable.isDisposed()) {
            mDisposable.dispose();
            mDisposable = null;
        }
    }
}