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
import com.pimenta.bestv.repository.entity.Genre;
import com.pimenta.bestv.repository.entity.Movie;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by marcus on 09-02-2018.
 */
public class MovieGridPresenter extends BasePresenter<MovieGridContract> {

    private int mCurrentPage = 0;

    private MediaRepository mMediaRepository;
    private ImageManager mImageManager;

    @Inject
    public MovieGridPresenter(MediaRepository mediaRepository, ImageManager imageManager) {
        super();
        mImageManager = imageManager;
        mMediaRepository = mediaRepository;
    }

    /**
     * Loads the now playing {@link List<Movie>}
     */
    public void loadMoviesByType(MediaRepository.MovieListType movieListType) {
        switch (movieListType) {
            case FAVORITES:
                mCompositeDisposable.add(mMediaRepository.getFavoriteMovies()
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
                break;
            default:
                int page = mCurrentPage + 1;
                mCompositeDisposable.add(mMediaRepository.loadMoviesByType(page, movieListType)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(movieList -> {
                            if (mContract != null) {
                                if (movieList != null && movieList.getPage() <= movieList.getTotalPages()) {
                                    mCurrentPage = movieList.getPage();
                                    mContract.onMoviesLoaded(movieList.getMovies());
                                } else {
                                    mContract.onMoviesLoaded(null);
                                }
                            }
                        }, throwable -> {
                            if (mContract != null) {
                                mContract.onMoviesLoaded(null);
                            }
                        }));
                break;
        }
    }

    /**
     * Loads the {@link List<Movie>} by the {@link Genre}
     *
     * @param genre {@link Genre}
     */
    public void loadMoviesByGenre(Genre genre) {
        int pageSearch = mCurrentPage + 1;
        mCompositeDisposable.add(mMediaRepository.getMoviesByGenre(genre, pageSearch)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(movieList -> {
                    if (mContract != null) {
                        if (movieList != null && movieList.getPage() <= movieList.getTotalPages()) {
                            mCurrentPage = movieList.getPage();
                            mContract.onMoviesLoaded(movieList.getMovies());
                        } else {
                            mContract.onMoviesLoaded(null);
                        }
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
     * Loads the {@link Movie} porter into {@link ImageView}
     *
     * @param movie     {@link Movie}
     * @param imageView {@link ImageView}
     */
    public void loadMoviePosterImage(@NonNull Movie movie, ImageView imageView) {
        mImageManager.loadImageInto(imageView,
                String.format(BuildConfig.TMDB_LOAD_IMAGE_BASE_URL, movie.getPosterPath()));
    }
}