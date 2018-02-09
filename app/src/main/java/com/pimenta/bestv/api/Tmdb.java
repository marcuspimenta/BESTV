package com.pimenta.bestv.api;

import com.google.gson.Gson;

import java.util.concurrent.Executor;

import io.reactivex.annotations.NonNull;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by marcus on 08-02-2018.
 */
public class Tmdb {

    private static final String BASE_URL = "https://api.themoviedb.org/3/";

    private GenreApi mGenreApi;

    public Tmdb(Gson gson, @NonNull Executor executor) {
        final Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .callbackExecutor(executor)
                .build();

        mGenreApi = retrofit.create(GenreApi.class);
    }

    public GenreApi getGenreApi() {
        return mGenreApi;
    }
}