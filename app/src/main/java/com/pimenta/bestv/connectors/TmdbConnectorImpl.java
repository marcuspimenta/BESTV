package com.pimenta.bestv.connectors;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.pimenta.bestv.R;
import com.pimenta.bestv.api.Tmdb;
import com.pimenta.bestv.models.Genre;
import com.pimenta.bestv.models.Movie;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

/**
 * Created by marcus on 08-02-2018.
 */
public class TmdbConnectorImpl extends BasePreferences implements TmdbConnector {

    private static final String TAG = "TmdbConnectorImpl";

    private String mApiKey;
    private String mLanguage;

    private Tmdb mTmdb;

    @Inject
    public TmdbConnectorImpl(Gson gson) {
        mApiKey = getString(R.string.tmdb_api_key);
        mLanguage = getString(R.string.tmdb_language);
        mTmdb = new Tmdb(gson, getThreadPool());
    }

    @Override
    public List<Genre> getGenres() {
        try {
            return mTmdb.getGenreApi().getGenres(mApiKey, mLanguage).execute().body().getGenres();
        } catch (IOException e) {
            Log.e(TAG, "Failed to get the genres", e);
            return null;
        }
    }

    @Override
    public Map<String, List<Movie>> getTopMovies() {
        return null;
    }

}