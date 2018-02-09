package com.pimenta.bestv.presenters;

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

import javax.inject.Inject;

import io.reactivex.Single;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by marcus on 09-02-2018.
 */
public class MovieGridPresenter extends AbstractPresenter<MovieGridCallback> {

    @Inject
    DisplayMetrics mDisplayMetrics;

    @Inject
    TmdbConnector mTmdbConnector;

    public MovieGridPresenter() {
        BesTV.getApplicationComponent().inject(this);
    }

    public void loadMoviesByGenre(Genre genre) {
        mDisposables.add(Single.create((SingleOnSubscribe<List<Movie>>) e -> e.onSuccess(mTmdbConnector.getMoviesByGenre(genre)))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(movies -> {
                    if (mCallback != null) {
                        mCallback.onMoviesLoaded(movies);
                    }
                }));
    }

    public void loadPosterImage(Movie movie) {
        Glide.with(BesTV.get())
                .load("https://image.tmdb.org/t/p/w1280" + movie.getBackdropPath())
                .centerCrop()
                .error(R.drawable.default_background)
                .into(new SimpleTarget<GlideDrawable>(mDisplayMetrics.widthPixels, mDisplayMetrics.heightPixels) {
                    @Override
                    public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
                        if (mCallback != null) {
                            mCallback.onPosterImageLoaded(resource);
                        }
                    }
                });
    }

}