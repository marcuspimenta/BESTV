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
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;

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
    public void loadImage(final String imageUrl, final SimpleTarget target) {
        Glide.with(mApplication)
                .load(imageUrl)
                .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL))
                .into(target);
    }

    @Override
    public void loadBitmapImage(final String imageUrl, final SimpleTarget target) {
        Glide.with(mApplication)
                .asBitmap()
                .load(imageUrl)
                .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL))
                .into(target);
    }
}