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

package com.pimenta.bestv.manager

import android.app.Application
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.widget.ImageView

import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.SimpleTarget

import javax.inject.Inject

/**
 * Created by marcus on 13-03-2018.
 */
class ImageManagerImpl @Inject constructor(
        private val application: Application
) : ImageManager {

    override fun loadImageInto(imageView: ImageView, imageUrl: String) {
        Glide.with(application)
                .load(imageUrl)
                .apply(RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL))
                .into(imageView)
    }

    override fun loadImage(imageUrl: String, target: SimpleTarget<Drawable>) {
        Glide.with(application)
                .load(imageUrl)
                .apply(RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL))
                .into(target)
    }

    override fun loadBitmapImage(imageUrl: String, target: SimpleTarget<Bitmap>) {
        Glide.with(application)
                .asBitmap()
                .load(imageUrl)
                .apply(RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL))
                .into(target)
    }
}