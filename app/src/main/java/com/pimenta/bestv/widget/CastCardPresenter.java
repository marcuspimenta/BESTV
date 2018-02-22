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

package com.pimenta.bestv.widget;

import android.support.v17.leanback.widget.ImageCardView;
import android.support.v17.leanback.widget.Presenter;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.pimenta.bestv.R;
import com.pimenta.bestv.models.Cast;

/**
 * Created by marcus on 16-02-2018.
 */
public class CastCardPresenter extends Presenter {

    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup parent) {
        ImageCardView cardView = new ImageCardView(parent.getContext());
        cardView.setFocusable(true);
        cardView.setFocusableInTouchMode(true);
        return new ViewHolder(cardView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final Object item) {
        final Cast cast = (Cast) item;
        final ImageCardView cardView = (ImageCardView) viewHolder.view;

        cardView.setTitleText(cast.getName());
        cardView.setContentText(cast.getCharacter());
        cardView.setMainImageDimensions(viewHolder.view.getContext().getResources().getDimensionPixelSize(R.dimen.character_image_card_width),
                viewHolder.view.getContext().getResources().getDimensionPixelSize(R.dimen.character_image_card_height));
        Glide.with(viewHolder.view.getContext())
                .load(String.format(viewHolder.view.getContext().getString(R.string.tmdb_load_image_url_api_h632), cast.getProfilePath()))
                .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL))
                .into(cardView.getMainImageView());
    }

    @Override
    public void onUnbindViewHolder(final ViewHolder viewHolder) {
        final ImageCardView cardView = (ImageCardView) viewHolder.view;
        cardView.setBadgeImage(null);
        cardView.setMainImage(null);
    }
}