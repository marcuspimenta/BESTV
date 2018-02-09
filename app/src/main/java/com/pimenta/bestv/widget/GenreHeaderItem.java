package com.pimenta.bestv.widget;

import android.support.v17.leanback.widget.HeaderItem;

import com.pimenta.bestv.models.Genre;

/**
 * Created by marcus on 09-02-2018.
 */
public class GenreHeaderItem extends HeaderItem {

    private Genre mGenre;

    public GenreHeaderItem(Genre genre) {
        super(genre.getId(), genre.getName());
        mGenre = genre;
    }

    public Genre getGenre() {
        return mGenre;
    }

    public void setGenre(final Genre genre) {
        mGenre = genre;
    }
}