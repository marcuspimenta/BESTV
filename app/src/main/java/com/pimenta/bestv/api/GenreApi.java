package com.pimenta.bestv.api;

import com.pimenta.bestv.models.GenreList;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by marcus on 08-02-2018.
 */
public interface GenreApi {

    @GET("genre/movie/list")
    Call<GenreList> getGenres(@Query("api_key") String apiKey, @Query("language") String language);

}