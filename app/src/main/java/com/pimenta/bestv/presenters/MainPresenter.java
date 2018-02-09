package com.pimenta.bestv.presenters;

import android.support.v17.leanback.app.BackgroundManager;
import android.util.DisplayMetrics;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.pimenta.bestv.BesTV;
import com.pimenta.bestv.R;
import com.pimenta.bestv.connectors.TmdbConnector;
import com.pimenta.bestv.models.Genre;
import com.pimenta.bestv.models.Movie;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import io.reactivex.Single;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by marcus on 06-02-2018.
 */
public class MainPresenter extends AbstractPresenter<MainCallback> {

    @Inject
    DisplayMetrics mDisplayMetrics;

    @Inject
    TmdbConnector mTmdbConnector;

    public MainPresenter() {
        BesTV.getApplicationComponent().inject(this);
    }

    public DisplayMetrics getDisplayMetrics() {
        return mDisplayMetrics;
    }

    public void loadGenres() {
        mDisposables.add(Single.create((SingleOnSubscribe<List<Genre>>) e -> e.onSuccess(mTmdbConnector.getGenres()))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(genres -> {
                    if (mCallback != null) {
                        mCallback.onGenresLoaded(genres);
                    }
                }));
    }

    public void loadImage(BackgroundManager backgroundManager, String uri) {
        Glide.with(BesTV.get())
                .load(uri)
                .centerCrop()
                .error(R.drawable.default_background)
                .into(new SimpleTarget<GlideDrawable>(mDisplayMetrics.widthPixels, mDisplayMetrics.heightPixels) {
                    @Override
                    public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
                        backgroundManager.setDrawable(resource);
                    }
                });
    }
}