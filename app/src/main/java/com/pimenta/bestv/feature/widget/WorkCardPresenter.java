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

package com.pimenta.bestv.feature.widget;

import android.support.v17.leanback.widget.ImageCardView;
import android.support.v17.leanback.widget.Presenter;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.pimenta.bestv.R;
import com.pimenta.bestv.repository.entity.Work;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * Created by marcus on 10-02-2018.
 */
public class WorkCardPresenter extends Presenter {

    private static final DateFormat sDateFormat = new SimpleDateFormat("MMM dd, yyyy");

    private LoadWorkPosterListener mLoadWorkPosterListener;

    public void setLoadWorkPosterListener(LoadWorkPosterListener loadMoviePosterListener) {
        mLoadWorkPosterListener = loadMoviePosterListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent) {
        ImageCardView cardView = new ImageCardView(parent.getContext());
        cardView.setFocusable(true);
        cardView.setFocusableInTouchMode(true);
        return new ViewHolder(cardView);
    }

    @Override
    public void onBindViewHolder(Presenter.ViewHolder viewHolder, Object item) {
        Work work = (Work) item;
        ImageCardView cardView = (ImageCardView) viewHolder.view;
        cardView.setTitleText(work.getTitle());
        if (work.getReleaseDate() != null) {
            cardView.setContentText(sDateFormat.format(work.getReleaseDate()));
        }
        cardView.setMainImageDimensions(viewHolder.view.getContext().getResources().getDimensionPixelSize(R.dimen.movie_card_width),
                viewHolder.view.getContext().getResources().getDimensionPixelSize(R.dimen.movie_card_height));

        if (mLoadWorkPosterListener != null) {
            mLoadWorkPosterListener.onLoadWorkPoster(work, cardView.getMainImageView());
        }
    }

    @Override
    public void onUnbindViewHolder(Presenter.ViewHolder viewHolder) {
        ImageCardView cardView = (ImageCardView) viewHolder.view;
        cardView.setBadgeImage(null);
        cardView.setMainImage(null);
    }

    public interface LoadWorkPosterListener {

        void onLoadWorkPoster(Work work, ImageView imageView);

    }
}