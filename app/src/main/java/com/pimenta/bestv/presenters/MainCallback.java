package com.pimenta.bestv.presenters;

import com.pimenta.bestv.models.Genre;

import java.util.List;

/**
 * Created by marcus on 06-02-2018.
 */
public interface MainCallback extends BasePresenter.Callback {

    void onGenresLoaded(List<Genre> genres);

}