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

package com.pimenta.bestv.manager;

import android.app.Application;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.pimenta.bestv.R;
import com.pimenta.bestv.model.Movie;

import javax.inject.Inject;

/**
 * Created by marcus on 13-03-2018.
 */
public class ImageManagerImpl implements ImageManager {

    @Inject
    Application mApplication;

    @Inject
    public ImageManagerImpl() {
    }

    @Override
    public void loadImageInto(final ImageView imageView, final String imageUrl) {
        Glide.with(mApplication)
                .load(imageUrl)
                .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL))
                .into(imageView);
    }

    @Override
    public void loadBackdropImage(final Movie movie, final Callback callback) {
        Glide.with(mApplication)
                .asBitmap()
                .load(String.format(mApplication.getString(R.string.tmdb_load_image_url_api_w1280), movie.getBackdropPath()))
                .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL))
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull final Bitmap resource, @Nullable final Transition<? super Bitmap> transition) {
                        if (callback != null) {
                            callback.onSuccess(resource);
                        }
                    }

                    @Override
                    public void onLoadFailed(@Nullable final Drawable errorDrawable) {
                        if (callback != null) {
                            callback.onError();
                        }
                    }
                });
    }
}