package com.pimenta.bestv.repository.remote.api.tmdb;

import com.pimenta.bestv.repository.entity.TvShowPage;

import io.reactivex.Single;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by marcus on 06/07/18.
 */
public interface TvShowApi {

    @GET("discover/tv")
    Single<TvShowPage> getTvShowByGenre(@Query("with_genres") int genreId, @Query("api_key") String apiKey, @Query("language") String language,
            @Query("include_adult") boolean includeAdult, @Query("page") int page);

    @GET("tv/airing_today")
    Single<TvShowPage> getAiringTodayTvShows(@Query("api_key") String apiKey, @Query("language") String language, @Query("page") int page);

    @GET("tv/on_the_air")
    Single<TvShowPage> getOnTheAirTvShows(@Query("api_key") String apiKey, @Query("language") String language, @Query("page") int page);

    @GET("tv/popular")
    Single<TvShowPage> getPopularTvShows(@Query("api_key") String apiKey, @Query("language") String language, @Query("page") int page);

    @GET("tv/top_rated")
    Single<TvShowPage> getTopRatedTvShows(@Query("api_key") String apiKey, @Query("language") String language, @Query("page") int page);

}