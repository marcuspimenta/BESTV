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

package com.pimenta.bestv.widget.render

import androidx.leanback.widget.ImageCardView
import androidx.leanback.widget.Presenter
import android.view.ViewGroup
import com.pimenta.bestv.BesTV

import com.pimenta.bestv.R
import com.pimenta.bestv.widget.presenter.CastCardPresenter
import com.pimenta.bestv.repository.entity.Cast
import javax.inject.Inject

/**
 * Created by marcus on 16-02-2018.
 */
class CastCardRender : Presenter() {

    @Inject
    lateinit var castCardPresenter: CastCardPresenter

    override fun onCreateViewHolder(parent: ViewGroup): Presenter.ViewHolder {
        BesTV.applicationComponent.inject(this)

        val cardView = ImageCardView(parent.context)
        cardView.isFocusable = true
        cardView.isFocusableInTouchMode = true
        return Presenter.ViewHolder(cardView)
    }

    override fun onBindViewHolder(viewHolder: Presenter.ViewHolder, item: Any) {
        val cast = item as Cast
        val cardView = viewHolder.view as ImageCardView
        cardView.titleText = cast.name
        cardView.contentText = cast.character
        cardView.setMainImageDimensions(viewHolder.view.context.resources.getDimensionPixelSize(R.dimen.character_image_card_width),
                viewHolder.view.context.resources.getDimensionPixelSize(R.dimen.character_image_card_height))

        castCardPresenter.loadCastProfileImage(cast, cardView.mainImageView)
    }

    override fun onUnbindViewHolder(viewHolder: Presenter.ViewHolder) {
        val cardView = viewHolder.view as ImageCardView
        cardView.badgeImage = null
        cardView.mainImage = null
    }
}