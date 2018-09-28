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
import com.pimenta.bestv.repository.entity.Video;

/**
 * Created by marcus on 23-02-2018.
 */
public class VideoCardPresenter extends Presenter {

    private LoadVideoThumbnailListener mLoadVideoThumbnailListener;

    public void setLoadVideoThumbnailListener(LoadVideoThumbnailListener loadVideoThumbnailListener) {
        mLoadVideoThumbnailListener = loadVideoThumbnailListener;
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
        Video video = (Video) item;
        ImageCardView cardView = (ImageCardView) viewHolder.view;
        cardView.setTitleText(video.getName());
        cardView.setContentText(video.getType());
        cardView.setMainImageDimensions(viewHolder.view.getContext().getResources().getDimensionPixelSize(R.dimen.video_card_width),
                viewHolder.view.getContext().getResources().getDimensionPixelSize(R.dimen.video_card_height));

        if (mLoadVideoThumbnailListener != null) {
            mLoadVideoThumbnailListener.onLoadVideoThumbnail(video, cardView.getMainImageView());
        }
    }

    @Override
    public void onUnbindViewHolder(Presenter.ViewHolder viewHolder) {
        ImageCardView cardView = (ImageCardView) viewHolder.view;
        cardView.setBadgeImage(null);
        cardView.setMainImage(null);
    }

    public interface LoadVideoThumbnailListener {

        void onLoadVideoThumbnail(Video video, ImageView imageView);

    }
}