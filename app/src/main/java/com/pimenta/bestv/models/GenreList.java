package com.pimenta.bestv.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by marcus on 09-02-2018.
 */
public class GenreList {

    @SerializedName("genres")
    private List<Genre> mGenres;

    public List<Genre> getGenres() {
        return mGenres;
    }

    public void setGenres(final List<Genre> genres) {
        mGenres = genres;
    }
}