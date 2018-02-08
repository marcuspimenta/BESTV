package com.pimenta.bestv.presenters;

import android.graphics.Bitmap;

import com.bumptech.glide.load.resource.drawable.GlideDrawable;

/**
 * Created by marcus on 07-02-2018.
 */
public interface MovieDetailsCallback extends BasePresenter.Callback {

    void onCardImageLoaded(GlideDrawable resource);

    void onBackgroundImageLoaded(Bitmap bitmap);

}