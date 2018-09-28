/*
 * Copyright (C) 2018 Marcus Pimenta
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package com.pimenta.bestv.repository.remote

import android.util.Log

import com.pimenta.bestv.BuildConfig
import com.pimenta.bestv.repository.entity.Cast
import com.pimenta.bestv.repository.entity.CastList
import com.pimenta.bestv.repository.entity.CastMovieList
import com.pimenta.bestv.repository.entity.CastTvShowList
import com.pimenta.bestv.repository.entity.Genre
import com.pimenta.bestv.repository.entity.Movie
import com.pimenta.bestv.repository.entity.MovieGenreList
import com.pimenta.bestv.repository.entity.MoviePage
import com.pimenta.bestv.repository.entity.TvShow
import com.pimenta.bestv.repository.entity.TvShowGenreList
import com.pimenta.bestv.repository.entity.TvShowPage
import com.pimenta.bestv.repository.entity.VideoList
import com.pimenta.bestv.repository.remote.api.tmdb.CastApi
import com.pimenta.bestv.repository.remote.api.tmdb.GenreApi
import com.pimenta.bestv.repository.remote.api.tmdb.MovieApi
import com.pimenta.bestv.repository.remote.api.tmdb.TvShowApi

import java.io.IOException

import javax.inject.Inject

import io.reactivex.Single

/**
 * Created by marcus on 08-02-2018.
 */
class TmdbMediaRemote @Inject constructor(
        private val genreApi: GenreApi,
        private val movieApi: MovieApi,
        private val personApi: CastApi,
        private val tvShowApi: TvShowApi
) : MediaRemote {

    override fun getMovieGenres(): Single<MovieGenreList> =
            genreApi.getMovieGenres(BuildConfig.TMDB_API_KEY, BuildConfig.TMDB_FILTER_LANGUAGE)

    override fun getMoviesByGenre(genre: Genre, page: Int): Single<MoviePage> =
            movieApi.getMoviesByGenre(genre.id, BuildConfig.TMDB_API_KEY, BuildConfig.TMDB_FILTER_LANGUAGE, false, page)

    override fun getMovie(movieId: Int): Movie? =
            try {
                movieApi.getMovie(movieId, BuildConfig.TMDB_API_KEY, BuildConfig.TMDB_FILTER_LANGUAGE).execute().body()
            } catch (e: IOException) {
                Log.e(TAG, "Error while getting a movie", e)
                null
            }

    override fun getCastByMovie(movie: Movie): Single<CastList> =
            movieApi.getCastByMovie(movie.id, BuildConfig.TMDB_API_KEY, BuildConfig.TMDB_FILTER_LANGUAGE)

    override fun getRecommendationByMovie(movie: Movie, page: Int): Single<MoviePage> =
            movieApi.getRecommendationByMovie(movie.id, BuildConfig.TMDB_API_KEY, BuildConfig.TMDB_FILTER_LANGUAGE, page)

    override fun getSimilarByMovie(movie: Movie, page: Int): Single<MoviePage> =
            movieApi.getSimilarByMovie(movie.id, BuildConfig.TMDB_API_KEY, BuildConfig.TMDB_FILTER_LANGUAGE, page)

    override fun getVideosByMovie(movie: Movie): Single<VideoList> =
            movieApi.getVideosByMovie(movie.id, BuildConfig.TMDB_API_KEY, BuildConfig.TMDB_FILTER_LANGUAGE)

    override fun getNowPlayingMovies(page: Int): Single<MoviePage> =
            movieApi.getNowPlayingMovies(BuildConfig.TMDB_API_KEY, BuildConfig.TMDB_FILTER_LANGUAGE, page)

    override fun getPopularMovies(page: Int): Single<MoviePage> =
            movieApi.getPopularMovies(BuildConfig.TMDB_API_KEY, BuildConfig.TMDB_FILTER_LANGUAGE, page)

    override fun getTopRatedMovies(page: Int): Single<MoviePage> =
            movieApi.getTopRatedMovies(BuildConfig.TMDB_API_KEY, BuildConfig.TMDB_FILTER_LANGUAGE, page)

    override fun getUpComingMovies(page: Int): Single<MoviePage> =
            movieApi.getUpComingMovies(BuildConfig.TMDB_API_KEY, BuildConfig.TMDB_FILTER_LANGUAGE, page)

    override fun searchMoviesByQuery(query: String, page: Int): Single<MoviePage> =
            movieApi.searchMoviesByQuery(BuildConfig.TMDB_API_KEY, query, BuildConfig.TMDB_FILTER_LANGUAGE, page)

    override fun getCastDetails(cast: Cast): Single<Cast> =
            personApi.getCastDetails(cast.id, BuildConfig.TMDB_API_KEY, BuildConfig.TMDB_FILTER_LANGUAGE)

    override fun getMovieCreditsByCast(cast: Cast): Single<CastMovieList> =
            personApi.getMovieCredits(cast.id, BuildConfig.TMDB_API_KEY, BuildConfig.TMDB_FILTER_LANGUAGE)

    override fun getTvShowCreditsByCast(cast: Cast): Single<CastTvShowList> =
            personApi.getTvShowCredits(cast.id, BuildConfig.TMDB_API_KEY, BuildConfig.TMDB_FILTER_LANGUAGE)

    override fun getTvShowGenres(): Single<TvShowGenreList> =
            genreApi.getTvShowGenres(BuildConfig.TMDB_API_KEY, BuildConfig.TMDB_FILTER_LANGUAGE)

    override fun getTvShowByGenre(genre: Genre, page: Int): Single<TvShowPage> =
            tvShowApi.getTvShowByGenre(genre.id, BuildConfig.TMDB_API_KEY, BuildConfig.TMDB_FILTER_LANGUAGE, false, page)

    override fun getAiringTodayTvShows(page: Int): Single<TvShowPage> =
            tvShowApi.getAiringTodayTvShows(BuildConfig.TMDB_API_KEY, BuildConfig.TMDB_FILTER_LANGUAGE, page)

    override fun getOnTheAirTvShows(page: Int): Single<TvShowPage> =
            tvShowApi.getOnTheAirTvShows(BuildConfig.TMDB_API_KEY, BuildConfig.TMDB_FILTER_LANGUAGE, page)

    override fun getPopularTvShows(page: Int): Single<TvShowPage> =
            tvShowApi.getPopularTvShows(BuildConfig.TMDB_API_KEY, BuildConfig.TMDB_FILTER_LANGUAGE, page)

    override fun getTopRatedTvShows(page: Int): Single<TvShowPage> =
            tvShowApi.getTopRatedTvShows(BuildConfig.TMDB_API_KEY, BuildConfig.TMDB_FILTER_LANGUAGE, page)

    override fun getTvShow(tvId: Int): TvShow? =
            try {
                tvShowApi.getTvShow(tvId, BuildConfig.TMDB_API_KEY, BuildConfig.TMDB_FILTER_LANGUAGE).execute().body()
            } catch (e: IOException) {
                Log.e(TAG, "Error while getting a tv show", e)
                null
            }

    override fun getCastByTvShow(tvShow: TvShow): Single<CastList> =
            tvShowApi.getCastByTvShow(tvShow.id, BuildConfig.TMDB_API_KEY, BuildConfig.TMDB_FILTER_LANGUAGE)

    override fun getRecommendationByTvShow(tvShow: TvShow, page: Int): Single<TvShowPage> =
            tvShowApi.getRecommendationByTvShow(tvShow.id, BuildConfig.TMDB_API_KEY, BuildConfig.TMDB_FILTER_LANGUAGE, page)

    override fun getSimilarByTvShow(tvShow: TvShow, page: Int): Single<TvShowPage> =
            tvShowApi.getSimilarByTvShow(tvShow.id, BuildConfig.TMDB_API_KEY, BuildConfig.TMDB_FILTER_LANGUAGE, page)

    override fun getVideosByTvShow(tvShow: TvShow): Single<VideoList> =
            tvShowApi.getVideosByTvShow(tvShow.id, BuildConfig.TMDB_API_KEY, BuildConfig.TMDB_FILTER_LANGUAGE)

    override fun searchTvShowsByQuery(query: String, page: Int): Single<TvShowPage> =
            tvShowApi.searchTvShowsByQuery(BuildConfig.TMDB_API_KEY, query, BuildConfig.TMDB_FILTER_LANGUAGE, page)

    companion object {

        private val TAG = TmdbMediaRemote::class.java.simpleName
    }
}