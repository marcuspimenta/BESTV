/*
 * Copyright (C) 2017 The Android Open Source Project
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

package com.pimenta.bestv.presenters;

import com.pimenta.bestv.models.Movie;

/**
 * Created by marcus on 07-02-2018.
 */
public class MovieDetailsPresenter extends AbstractPresenter<MovieDetailsCallback> {

    public void loadCardImage(Movie movie) {
        /*Glide.with(BesTV.get())
                .load(movie.getCardImageUrl())
                .centerCrop()
                .error(R.drawable.default_background)
                .into(new SimpleTarget<GlideDrawable>(BesTV.get().getResources().getDimensionPixelSize(R.dimen
                .movie_details_fragment_thumbnail_width),
                        BesTV.get().getResources().getDimensionPixelSize(R.dimen.movie_details_fragment_thumbnail_height)) {
                    @Override
                    public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
                        if (mCallback != null) {
                            mCallback.onCardImageLoaded(resource);
                        }
                    }
                });*/
    }

    public void loadBackgroundImage(Movie movie) {
        /*Glide.with(BesTV.get())
                .load(movie.getBackgroundImageUrl())
                .asBitmap()
                .centerCrop()
                .error(R.drawable.default_background)
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap bitmap, GlideAnimation<? super Bitmap> glideAnimation) {
                        if (mCallback != null) {
                            mCallback.onBackgroundImageLoaded(bitmap);
                        }
                    }
                });*/
    }

}