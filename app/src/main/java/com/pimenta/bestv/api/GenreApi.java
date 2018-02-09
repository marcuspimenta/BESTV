package com.pimenta.bestv.api;

import com.pimenta.bestv.models.GenreList;
import com.pimenta.bestv.models.MovieList;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by marcus on 08-02-2018.
 */
public interface GenreApi {

    @GET("genre/movie/list")
    Call<GenreList> getGenres(@Query("api_key") String apiKey, @Query("language") String language);

    @GET("genre/{genre_id}/movies")
    Call<MovieList> getMovies(@Path("genre_id") int genreId, @Query("api_key") String apiKey, @Query("language") String language,
            @Query("include_adult") boolean includeAdult, @Query("sort_by") String sortBy);

}