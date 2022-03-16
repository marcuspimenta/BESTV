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

package com.pimenta.bestv.workdetail.presentation.ui.render

import android.view.ViewGroup
import androidx.leanback.widget.ImageCardView
import androidx.leanback.widget.Presenter
import com.pimenta.bestv.model.presentation.model.CastViewModel
import com.pimenta.bestv.presentation.extension.loadImageInto
import com.pimenta.bestv.workdetail.R

/**
 * Created by marcus on 16-02-2018.
 */
class CastCardRender : Presenter() {

    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder =
        ViewHolder(
            ImageCardView(parent.context).apply {
                isFocusable = true
                isFocusableInTouchMode = true
            }
        )

    override fun onBindViewHolder(viewHolder: ViewHolder, item: Any) {
        val castViewModel = item as CastViewModel
        val cardView = viewHolder.view as ImageCardView
        cardView.titleText = castViewModel.name
        cardView.contentText = castViewModel.character
        cardView.setMainImageDimensions(
            viewHolder.view.context.resources.getDimensionPixelSize(R.dimen.character_image_card_width),
            viewHolder.view.context.resources.getDimensionPixelSize(R.dimen.character_image_card_height)
        )

        castViewModel.thumbnailUrl?.let {
            cardView.mainImageView.loadImageInto(it)
        }
    }

    override fun onUnbindViewHolder(viewHolder: ViewHolder) {
        val cardView = viewHolder.view as ImageCardView
        cardView.badgeImage = null
        cardView.mainImage = null
    }
}
