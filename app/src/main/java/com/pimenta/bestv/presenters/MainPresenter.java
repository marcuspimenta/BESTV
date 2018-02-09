package com.pimenta.bestv.presenters;

import android.util.DisplayMetrics;

import com.pimenta.bestv.BesTV;
import com.pimenta.bestv.connectors.TmdbConnector;
import com.pimenta.bestv.models.Genre;

import java.util.List;

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

}