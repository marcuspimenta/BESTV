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

package com.pimenta.bestv.view.widget;

import android.support.v17.leanback.widget.ImageCardView;
import android.support.v17.leanback.widget.Presenter;
import android.view.ViewGroup;

import com.pimenta.bestv.BesTV;
import com.pimenta.bestv.R;
import com.pimenta.bestv.manager.ImageManager;
import com.pimenta.bestv.domain.Movie;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import javax.inject.Inject;

/**
 * Created by marcus on 10-02-2018.
 */
public class MovieCardPresenter extends Presenter {

    private static final DateFormat sDateFormat = new SimpleDateFormat("MMM dd, yyyy");

    @Inject
    ImageManager mImageManager;

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent) {
        BesTV.getApplicationComponent().inject(this);

        final ImageCardView cardView = new ImageCardView(parent.getContext());
        cardView.setFocusable(true);
        cardView.setFocusableInTouchMode(true);
        return new ViewHolder(cardView);
    }

    @Override
    public void onBindViewHolder(Presenter.ViewHolder viewHolder, Object item) {
        final Movie movie = (Movie) item;
        final ImageCardView cardView = (ImageCardView) viewHolder.view;
        cardView.setTitleText(movie.getTitle());
        if (movie.getReleaseDate() != null) {
            cardView.setContentText(sDateFormat.format(movie.getReleaseDate()));
        }
        cardView.setMainImageDimensions(viewHolder.view.getContext().getResources().getDimensionPixelSize(R.dimen.movie_card_width),
                viewHolder.view.getContext().getResources().getDimensionPixelSize(R.dimen.movie_card_height));

        mImageManager.loadImageInto(cardView.getMainImageView(),
                String.format(viewHolder.view.getContext().getResources().getString(R.string.tmdb_load_image_url_api), movie.getPosterPath()));
    }

    @Override
    public void onUnbindViewHolder(Presenter.ViewHolder viewHolder) {
        final ImageCardView cardView = (ImageCardView) viewHolder.view;
        cardView.setBadgeImage(null);
        cardView.setMainImage(null);
    }
}