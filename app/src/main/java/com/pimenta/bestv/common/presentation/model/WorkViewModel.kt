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

package com.pimenta.bestv.common.presentation.model

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import java.io.Serializable
import java.util.*

/**
 * Created by marcus on 18-04-2019.
 */
class WorkViewModel(
        var id: Int,
        var originalLanguage: String?,
        var overview: String?,
        var backdropUrl: String?,
        var posterUrl: String?,
        var title: String?,
        var originalTitle: String?,
        var releaseDate: Date?,
        var isFavorite: Boolean,
        var type: WorkType
) : Serializable

fun WorkViewModel.loadPoster(context: Context, target: CustomTarget<Drawable>) {
    Glide.with(context)
            .load(posterUrl)
            .apply(RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL))
            .into(target)
}

fun WorkViewModel.loadBackdrop(context: Context, target: CustomTarget<Bitmap>) {
    Glide.with(context)
            .asBitmap()
            .load(backdropUrl)
            .apply(RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL))
            .into(target)
}

enum class WorkType {
    TV_SHOW,
    MOVIE
}

