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

package com.pimenta.bestv.model.presentation.model

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import java.io.Serializable

/**
 * Created by marcus on 18-04-2019.
 */
data class WorkViewModel(
    var id: Int,
    var originalLanguage: String? = null,
    var overview: String? = null,
    val source: String? = null,
    var backdropUrl: String? = null,
    var posterUrl: String? = null,
    var title: String? = null,
    var originalTitle: String? = null,
    var releaseDate: String? = null,
    var isFavorite: Boolean = false,
    var type: WorkType
) : Serializable

inline fun WorkViewModel.loadPoster(context: Context, crossinline result: (resource: Drawable) -> Unit) {
    Glide.with(context)
        .load(posterUrl)
        .apply(RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL))
        .into(object : CustomTarget<Drawable>() {
            override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
                result(resource)
            }

            override fun onLoadCleared(placeholder: Drawable?) {
                // DO ANYTHING
            }
        })
}

inline fun WorkViewModel.loadBackdrop(context: Context, crossinline result: (resource: Bitmap) -> Unit) {
    Glide.with(context)
        .asBitmap()
        .load(backdropUrl)
        .apply(RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL))
        .into(object : CustomTarget<Bitmap>() {
            override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                result(resource)
            }

            override fun onLoadCleared(placeholder: Drawable?) {
                // DO ANYTHING
            }
        })
}

enum class WorkType {
    TV_SHOW,
    MOVIE
}
