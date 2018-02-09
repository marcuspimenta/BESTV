package com.pimenta.bestv.presenters;

import android.graphics.Bitmap;

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

    public void loadCardImage(Movie movie) {
        /*Glide.with(BesTV.get())
                .load(movie.getCardImageUrl())
                .centerCrop()
                .error(R.drawable.default_background)
                .into(new SimpleTarget<GlideDrawable>(BesTV.get().getResources().getDimensionPixelSize(R.dimen.movie_details_fragment_thumbnail_width),
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