package com.pimenta.bestv.presenters;

import com.pimenta.bestv.models.Movie;

import java.util.List;
import java.util.Map;

/**
 * Created by marcus on 06-02-2018.
 */
public interface MainCallback extends BasePresenter.Callback {

    void onDataLoaded(Map<String, List<Movie>> movies);

}