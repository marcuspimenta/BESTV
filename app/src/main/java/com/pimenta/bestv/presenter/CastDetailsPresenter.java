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

import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.pimenta.bestv.BuildConfig;
import com.pimenta.bestv.manager.ImageManager;
import com.pimenta.bestv.repository.MediaRepository;
import com.pimenta.bestv.repository.entity.Cast;
import com.pimenta.bestv.repository.entity.CastMovieList;
import com.pimenta.bestv.repository.entity.CastTvShowList;
import com.pimenta.bestv.repository.entity.Work;

import javax.inject.Inject;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by marcus on 05-04-2018.
 */
public class CastDetailsPresenter extends BasePresenter<CastDetailsContract> {

    private static final String TAG = CastDetailsPresenter.class.getSimpleName();

    private MediaRepository mMediaRepository;
    private ImageManager mImageManager;

    @Inject
    public CastDetailsPresenter(MediaRepository mediaRepository, ImageManager imageManager) {
        super();
        mMediaRepository = mediaRepository;
        mImageManager = imageManager;
    }

    /**
     * Load the {@link Cast} details
     *
     * @param cast {@link Cast}
     */
    public void loadCastDetails(Cast cast) {
        mCompositeDisposable.add(Single.zip(mMediaRepository.getCastDetails(cast),
                mMediaRepository.getMovieCreditsByCast(cast),
                mMediaRepository.getTvShowCreditsByCast(cast),
                CastInfo::new)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(castInfo -> {
                    if (mContract != null) {
                        mContract.onCastLoaded(castInfo.getCast(),
                                castInfo.getCastMovieList() != null ? castInfo.getCastMovieList().getWorks() : null,
                                castInfo.getCastTvShowList() != null ? castInfo.getCastTvShowList().getWorks() : null);
                    }
                }, throwable -> {
                    Log.e(TAG, "Error while getting the cast details", throwable);
                    if (mContract != null) {
                        mContract.onCastLoaded(null, null, null);
                    }
                }));
    }

    /**
     * Loads the {@link Cast} image
     *
     * @param cast {@link Cast}
     */
    public void loadCastImage(Cast cast) {
        mImageManager.loadImage(String.format(BuildConfig.TMDB_LOAD_IMAGE_BASE_URL, cast.getProfilePath()),
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
     * Wrapper class that keeps the cast info
     */
    private class CastInfo {

        private Cast mCast;
        private CastMovieList mCastMovieList;
        private CastTvShowList mCastTvShowList;

        public CastInfo(final Cast cast, final CastMovieList castMovieList, final CastTvShowList castTvShowList) {
            mCast = cast;
            mCastMovieList = castMovieList;
            mCastTvShowList = castTvShowList;
        }

        public Cast getCast() {
            return mCast;
        }

        public CastMovieList getCastMovieList() {
            return mCastMovieList;
        }

        public CastTvShowList getCastTvShowList() {
            return mCastTvShowList;
        }
    }
}