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

import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.pimenta.bestv.R;
import com.pimenta.bestv.repository.remote.MediaRepository;
import com.pimenta.bestv.manager.ImageManager;
import com.pimenta.bestv.repository.entity.Cast;

import javax.inject.Inject;

import io.reactivex.Maybe;
import io.reactivex.MaybeOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by marcus on 05-04-2018.
 */
public class CastDetailsPresenter extends BasePresenter<CastDetailsContract> {

    private Application mApplication;
    private MediaRepository mMediaRepository;
    private ImageManager mImageManager;

    @Inject
    public CastDetailsPresenter(Application application, MediaRepository mediaRepository, ImageManager imageManager) {
        super();
        mApplication = application;
        mMediaRepository = mediaRepository;
        mImageManager = imageManager;
    }

    /**
     * Load the {@link Cast} details
     *
     * @param cast {@link Cast}
     */
    public void loadCastDetails(Cast cast) {
        mCompositeDisposable.add(Maybe.create((MaybeOnSubscribe<Cast>) e -> {
                    final Cast castResult = mMediaRepository.getCastDetails(cast);
                    if (castResult != null) {
                        e.onSuccess(castResult);
                    } else {
                        e.onComplete();
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(castResult -> {
                    if (mContract != null) {
                        mContract.onCastLoaded(castResult);
                    }
                }, throwable -> {
                    if (mContract != null) {
                        mContract.onCastLoaded(null);
                    }
                }));
    }

    /**
     * Loads the {@link Cast} image
     *
     * @param cast {@link Cast}
     */
    public void loadCastImage(Cast cast) {
        mImageManager.loadImage(String.format(mApplication.getString(R.string.tmdb_load_image_url_api), cast.getProfilePath()),
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
}