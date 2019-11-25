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
import com.pimenta.bestv.workdetail.R
import com.pimenta.bestv.model.presentation.model.VideoViewModel
import com.pimenta.bestv.presentation.extension.loadImageInto

/**
 * Created by marcus on 23-02-2018.
 */
class VideoCardRender : Presenter() {

    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
        val cardView = ImageCardView(parent.context)
        cardView.isFocusable = true
        cardView.isFocusableInTouchMode = true
        return ViewHolder(cardView)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, item: Any) {
        val videoViewModel = item as VideoViewModel
        val cardView = viewHolder.view as ImageCardView
        cardView.titleText = videoViewModel.name
        cardView.contentText = videoViewModel.type
        cardView.setMainImageDimensions(viewHolder.view.context.resources.getDimensionPixelSize(R.dimen.video_card_width),
                viewHolder.view.context.resources.getDimensionPixelSize(R.dimen.video_card_height))

        videoViewModel.thumbnailUrl?.let {
            cardView.mainImageView.loadImageInto(it)
        }
    }

    override fun onUnbindViewHolder(viewHolder: ViewHolder) {
        val cardView = viewHolder.view as ImageCardView
        cardView.badgeImage = null
        cardView.mainImage = null
    }
}