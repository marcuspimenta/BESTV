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

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.widget.ImageView

import com.bumptech.glide.request.target.SimpleTarget

/**
 * Created by marcus on 13-03-2018.
 */
interface ImageManager {

    /**
     * Loads an image URL in [ImageView].
     *
     * @param imageView [ImageView]
     * @param imageUrl  Image URL to be loaded
     */
    fun loadImageInto(imageView: ImageView, imageUrl: String)

    /**
     * Loads an image URL.
     *
     * @param imageUrl Image URL to be loaded
     * @param target   [SimpleTarget]
     */
    fun loadImage(imageUrl: String, target: SimpleTarget<Drawable>)

    /**
     * Loads an bitmap image URL.
     *
     * @param imageUrl Image URL to be loaded
     * @param target   [SimpleTarget]
     */
    fun loadBitmapImage(imageUrl: String, target: SimpleTarget<Bitmap>)

}