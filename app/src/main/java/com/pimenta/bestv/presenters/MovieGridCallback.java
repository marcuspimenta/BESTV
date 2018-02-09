package com.pimenta.bestv.presenters;

import android.graphics.drawable.Drawable;

import com.pimenta.bestv.models.Movie;

import java.util.List;

/**
 * Created by marcus on 09-02-2018.
 */
public interface MovieGridCallback extends BasePresenter.Callback{

    void onMoviesLoaded(List<Movie> movies);

    void onPosterImageLoaded(Drawable drawable);

}