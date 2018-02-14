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

package com.pimenta.bestv.presenters;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.pimenta.bestv.BesTV;
import com.pimenta.bestv.R;
import com.pimenta.bestv.models.Movie;

/**
 * Created by marcus on 07-02-2018.
 */
public class MovieDetailsPresenter extends AbstractPresenter<MovieDetailsCallback> {

    public MovieDetailsPresenter() {
        super();
    }

    public void loadCardImage(Movie movie) {
        Glide.with(BesTV.get())
            .load(String.format(BesTV.get().getString(R.string.tmdb_load_image_url_api_w780), movie.getPosterPath()))
            .centerCrop()
            .error(R.drawable.lb_ic_sad_cloud)
            .into(new SimpleTarget<GlideDrawable>(convertDpToPixel(BesTV.get().getResources().getDimension(R.dimen.movie_card_width)),
                    convertDpToPixel(BesTV.get().getResources().getDimension(R.dimen.movie_card_height))) {
                @Override
                public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
                    if (mCallback != null) {
                        mCallback.onCardImageLoaded(resource);
                    }
                }

                @Override
                public void onLoadFailed(final Exception e, final Drawable errorDrawable) {
                    if (mCallback != null) {
                        mCallback.onCardImageLoaded(null);
                    }
                }
            });
    }

    public void loadBackdropImage(Movie movie) {
        Glide.with(BesTV.get())
            .load(String.format(BesTV.get().getString(R.string.tmdb_load_image_url_api_w1280), movie.getBackdropPath()))
            .asBitmap()
            .centerCrop()
            .error(R.drawable.lb_ic_sad_cloud)
            .into(new SimpleTarget<Bitmap>() {
                @Override
                public void onResourceReady(Bitmap bitmap, GlideAnimation<? super Bitmap> glideAnimation) {
                    if (mCallback != null) {
                        mCallback.onBackdropImageLoaded(bitmap);
                    }
                }

                @Override
                public void onLoadFailed(final Exception e, final Drawable errorDrawable) {
                    if (mCallback != null) {
                        mCallback.onBackdropImageLoaded(null);
                    }
                }
            });
    }

    private int convertDpToPixel(float dp) {
        float density = BesTV.get().getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }

}