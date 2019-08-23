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

package com.pimenta.bestv.data.remote

import com.pimenta.bestv.BuildConfig
import com.pimenta.bestv.data.remote.api.CastApi
import com.pimenta.bestv.data.remote.api.GenreApi
import com.pimenta.bestv.data.remote.api.MovieApi
import com.pimenta.bestv.data.remote.api.TvShowApi
import com.pimenta.bestv.data.remote.entity.*
import io.reactivex.Single
import timber.log.Timber
import java.io.IOException
import javax.inject.Inject

/**
 * Created by marcus on 08-02-2018.
 */
class TmdbMediaRemoteRepository @Inject constructor(
        private val genreApi: GenreApi,
        private val movieApi: MovieApi,
        private val personApi: CastApi,
        private val tvShowApi: TvShowApi
) : MediaRemoteRepository {

    override fun getMovieGenres(): Single<MovieGenreListResponse> =
            genreApi.getMovieGenres(BuildConfig.TMDB_API_KEY, BuildConfig.TMDB_FILTER_LANGUAGE)

    override fun getMoviesByGenre(genreId: Int, page: Int): Single<MoviePageResponse> =
            movieApi.getMoviesByGenre(genreId, BuildConfig.TMDB_API_KEY, BuildConfig.TMDB_FILTER_LANGUAGE, false, page)

    override fun getMovie(movieId: Int): MovieResponse? =
            try {
                movieApi.getMovie(movieId, BuildConfig.TMDB_API_KEY, BuildConfig.TMDB_FILTER_LANGUAGE).execute().body()
            } catch (e: IOException) {
                Timber.e(e, "Error while getting a movie")
                null
            }

    override fun getCastByMovie(movieId: Int): Single<CastListResponse> =
            movieApi.getCastByMovie(movieId, BuildConfig.TMDB_API_KEY, BuildConfig.TMDB_FILTER_LANGUAGE)

    override fun getRecommendationByMovie(movieId: Int, page: Int): Single<MoviePageResponse> =
            movieApi.getRecommendationByMovie(movieId, BuildConfig.TMDB_API_KEY, BuildConfig.TMDB_FILTER_LANGUAGE, page)

    override fun getSimilarByMovie(movieId: Int, page: Int): Single<MoviePageResponse> =
            movieApi.getSimilarByMovie(movieId, BuildConfig.TMDB_API_KEY, BuildConfig.TMDB_FILTER_LANGUAGE, page)

    override fun getVideosByMovie(movieId: Int): Single<VideoListResponse> =
            movieApi.getVideosByMovie(movieId, BuildConfig.TMDB_API_KEY, BuildConfig.TMDB_FILTER_LANGUAGE)

    override fun getNowPlayingMovies(page: Int): Single<MoviePageResponse> =
            movieApi.getNowPlayingMovies(BuildConfig.TMDB_API_KEY, BuildConfig.TMDB_FILTER_LANGUAGE, page)

    override fun getPopularMovies(page: Int): Single<MoviePageResponse> =
            movieApi.getPopularMovies(BuildConfig.TMDB_API_KEY, BuildConfig.TMDB_FILTER_LANGUAGE, page)

    override fun getTopRatedMovies(page: Int): Single<MoviePageResponse> =
            movieApi.getTopRatedMovies(BuildConfig.TMDB_API_KEY, BuildConfig.TMDB_FILTER_LANGUAGE, page)

    override fun getUpComingMovies(page: Int): Single<MoviePageResponse> =
            movieApi.getUpComingMovies(BuildConfig.TMDB_API_KEY, BuildConfig.TMDB_FILTER_LANGUAGE, page)

    override fun searchMoviesByQuery(query: String, page: Int): Single<MoviePageResponse> =
            movieApi.searchMoviesByQuery(BuildConfig.TMDB_API_KEY, query, BuildConfig.TMDB_FILTER_LANGUAGE, page)

    override fun getCastDetails(castId: Int): Single<CastResponse> =
            personApi.getCastDetails(castId, BuildConfig.TMDB_API_KEY, BuildConfig.TMDB_FILTER_LANGUAGE)

    override fun getMovieCreditsByCast(castId: Int): Single<CastMovieListResponse> =
            personApi.getMovieCredits(castId, BuildConfig.TMDB_API_KEY, BuildConfig.TMDB_FILTER_LANGUAGE)

    override fun getTvShowCreditsByCast(castId: Int): Single<CastTvShowListResponse> =
            personApi.getTvShowCredits(castId, BuildConfig.TMDB_API_KEY, BuildConfig.TMDB_FILTER_LANGUAGE)

    override fun getTvShowGenres(): Single<TvShowGenreListResponse> =
            genreApi.getTvShowGenres(BuildConfig.TMDB_API_KEY, BuildConfig.TMDB_FILTER_LANGUAGE)

    override fun getTvShowByGenre(genreId: Int, page: Int): Single<TvShowPageResponse> =
            tvShowApi.getTvShowByGenre(genreId, BuildConfig.TMDB_API_KEY, BuildConfig.TMDB_FILTER_LANGUAGE, false, page)

    override fun getAiringTodayTvShows(page: Int): Single<TvShowPageResponse> =
            tvShowApi.getAiringTodayTvShows(BuildConfig.TMDB_API_KEY, BuildConfig.TMDB_FILTER_LANGUAGE, page)

    override fun getOnTheAirTvShows(page: Int): Single<TvShowPageResponse> =
            tvShowApi.getOnTheAirTvShows(BuildConfig.TMDB_API_KEY, BuildConfig.TMDB_FILTER_LANGUAGE, page)

    override fun getPopularTvShows(page: Int): Single<TvShowPageResponse> =
            tvShowApi.getPopularTvShows(BuildConfig.TMDB_API_KEY, BuildConfig.TMDB_FILTER_LANGUAGE, page)

    override fun getTopRatedTvShows(page: Int): Single<TvShowPageResponse> =
            tvShowApi.getTopRatedTvShows(BuildConfig.TMDB_API_KEY, BuildConfig.TMDB_FILTER_LANGUAGE, page)

    override fun getTvShow(tvId: Int): TvShowResponse? =
            try {
                tvShowApi.getTvShow(tvId, BuildConfig.TMDB_API_KEY, BuildConfig.TMDB_FILTER_LANGUAGE).execute().body()
            } catch (e: IOException) {
                Timber.e(e, "Error while getting a tv show")
                null
            }

    override fun getCastByTvShow(tvShowId: Int): Single<CastListResponse> =
            tvShowApi.getCastByTvShow(tvShowId, BuildConfig.TMDB_API_KEY, BuildConfig.TMDB_FILTER_LANGUAGE)

    override fun getRecommendationByTvShow(tvShowId: Int, page: Int): Single<TvShowPageResponse> =
            tvShowApi.getRecommendationByTvShow(tvShowId, BuildConfig.TMDB_API_KEY, BuildConfig.TMDB_FILTER_LANGUAGE, page)

    override fun getSimilarByTvShow(tvShowId: Int, page: Int): Single<TvShowPageResponse> =
            tvShowApi.getSimilarByTvShow(tvShowId, BuildConfig.TMDB_API_KEY, BuildConfig.TMDB_FILTER_LANGUAGE, page)

    override fun getVideosByTvShow(tvShowId: Int): Single<VideoListResponse> =
            tvShowApi.getVideosByTvShow(tvShowId, BuildConfig.TMDB_API_KEY, BuildConfig.TMDB_FILTER_LANGUAGE)

    override fun searchTvShowsByQuery(query: String, page: Int): Single<TvShowPageResponse> =
            tvShowApi.searchTvShowsByQuery(BuildConfig.TMDB_API_KEY, query, BuildConfig.TMDB_FILTER_LANGUAGE, page)
}