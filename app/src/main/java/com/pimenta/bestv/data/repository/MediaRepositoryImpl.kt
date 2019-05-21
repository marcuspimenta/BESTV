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

package com.pimenta.bestv.data.repository

import com.pimenta.bestv.data.entity.*
import com.pimenta.bestv.data.local.MediaLocalRepository
import com.pimenta.bestv.data.remote.MediaRemoteRepository
import io.reactivex.Single
import java.util.*
import javax.inject.Inject

/**
 * Created by marcus on 05-03-2018.
 */
class MediaRepositoryImpl @Inject constructor(
        private val mediaLocalRepository: MediaLocalRepository,
        private val mediaRemoteRepository: MediaRemoteRepository
) : MediaRepository {

    override fun isFavorite(work: Work): Boolean =
            mediaLocalRepository.isFavorite(work)

    override fun hasFavorite(): Single<Boolean> =
            mediaLocalRepository.hasFavorite()

    override fun saveFavorite(work: Work): Boolean =
            mediaLocalRepository.saveFavorite(work)

    override fun deleteFavorite(work: Work): Boolean =
            mediaLocalRepository.deleteFavorite(work)

    override fun getFavorites(): Single<List<Work>> =
            Single.fromCallable {
                val works = ArrayList<Work>()

                val favoritesMovies = mediaLocalRepository.getMovies()
                for (movie in favoritesMovies) {
                    val detailWork = mediaRemoteRepository.getMovie(movie.id)
                    if (detailWork != null) {
                        detailWork.isFavorite = true
                        works.add(detailWork)
                    }
                }

                val favoritesTvShows = mediaLocalRepository.getTvShows()
                for (tvShow in favoritesTvShows) {
                    val detailWork = mediaRemoteRepository.getTvShow(tvShow.id)
                    if (detailWork != null) {
                        detailWork.isFavorite = true
                        works.add(detailWork)
                    }
                }
                works
            }

    override fun loadWorkByType(page: Int, movieListType: MediaRepository.WorkType): Single<out WorkPage<*>> =
            when (movieListType) {
                MediaRepository.WorkType.NOW_PLAYING_MOVIES -> mediaRemoteRepository.getNowPlayingMovies(page)
                MediaRepository.WorkType.POPULAR_MOVIES -> mediaRemoteRepository.getPopularMovies(page)
                MediaRepository.WorkType.TOP_RATED_MOVIES -> mediaRemoteRepository.getTopRatedMovies(page)
                MediaRepository.WorkType.UP_COMING_MOVIES -> mediaRemoteRepository.getUpComingMovies(page)
                MediaRepository.WorkType.AIRING_TODAY_TV_SHOWS -> mediaRemoteRepository.getAiringTodayTvShows(page)
                MediaRepository.WorkType.ON_THE_AIR_TV_SHOWS -> mediaRemoteRepository.getOnTheAirTvShows(page)
                MediaRepository.WorkType.POPULAR_TV_SHOWS -> mediaRemoteRepository.getPopularTvShows(page)
                MediaRepository.WorkType.TOP_RATED_TV_SHOWS -> mediaRemoteRepository.getTopRatedTvShows(page)
                else -> Single.error(Throwable())
            }

    override fun getMovieGenres(): Single<MovieGenreList> =
            mediaRemoteRepository.getMovieGenres()

    override fun getWorkByGenre(genre: Genre, page: Int): Single<out WorkPage<*>> =
            when (genre.source) {
                Genre.Source.MOVIE -> mediaRemoteRepository.getMoviesByGenre(genre, page)
                Genre.Source.TV_SHOW -> mediaRemoteRepository.getTvShowByGenre(genre, page)
            }

    override fun getMovie(movieId: Int): Movie? =
            mediaRemoteRepository.getMovie(movieId)

    override fun getCastByWork(work: Work): Single<CastList> =
            when (work) {
                is Movie -> mediaRemoteRepository.getCastByMovie(work)
                is TvShow -> mediaRemoteRepository.getCastByTvShow(work)
                else -> Single.error(Throwable())
            }

    override fun getRecommendationByWork(work: Work, page: Int): Single<out WorkPage<*>> =
            when (work) {
                is Movie -> mediaRemoteRepository.getRecommendationByMovie(work, page)
                is TvShow -> mediaRemoteRepository.getRecommendationByTvShow(work, page)
                else -> Single.error(Throwable())
            }

    override fun getSimilarByWork(work: Work, page: Int): Single<out WorkPage<*>> =
            when (work) {
                is Movie -> mediaRemoteRepository.getSimilarByMovie(work, page)
                is TvShow -> mediaRemoteRepository.getSimilarByTvShow(work, page)
                else -> Single.error(Throwable())
            }

    override fun getVideosByWork(work: Work): Single<VideoList> =
            when (work) {
                is Movie -> mediaRemoteRepository.getVideosByMovie(work)
                is TvShow -> mediaRemoteRepository.getVideosByTvShow(work)
                else -> Single.error(Throwable())
            }

    override fun searchMoviesByQuery(query: String, page: Int): Single<MoviePage> =
            mediaRemoteRepository.searchMoviesByQuery(query, page)

    override fun searchTvShowsByQuery(query: String, page: Int): Single<TvShowPage> =
            mediaRemoteRepository.searchTvShowsByQuery(query, page)

    override fun getCastDetails(cast: Cast): Single<Cast> =
            mediaRemoteRepository.getCastDetails(cast)

    override fun getMovieCreditsByCast(cast: Cast): Single<CastMovieList> =
            mediaRemoteRepository.getMovieCreditsByCast(cast)

    override fun getTvShowCreditsByCast(cast: Cast): Single<CastTvShowList> =
            mediaRemoteRepository.getTvShowCreditsByCast(cast)

    override fun getTvShowGenres(): Single<TvShowGenreList> =
            mediaRemoteRepository.getTvShowGenres()
}