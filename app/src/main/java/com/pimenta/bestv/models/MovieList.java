package com.pimenta.bestv.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by marcus on 09-02-2018.
 */
public class MovieList {

    @SerializedName("results")
    private List<Movie> mMovies;

    public List<Movie> getMovies() {
        return mMovies;
    }

    public void setMovies(final List<Movie> movies) {
        mMovies = movies;
    }
}