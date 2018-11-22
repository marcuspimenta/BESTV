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

package com.pimenta.bestv.repository

import com.pimenta.bestv.repository.database.dao.MovieDao
import com.pimenta.bestv.repository.database.dao.TvShowDao
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
import com.pimenta.bestv.repository.entity.Work
import com.pimenta.bestv.repository.entity.WorkPage
import com.pimenta.bestv.repository.remote.MediaRemote

import java.util.ArrayList

import javax.inject.Inject

import io.reactivex.Single

/**
 * Created by marcus on 05-03-2018.
 */
class MediaRepositoryImpl @Inject constructor(
        private val movieDao: MovieDao,
        private val tvShowDao: TvShowDao,
        private val mediaRemote: MediaRemote
) : MediaRepository {

    override fun isFavorite(work: Work): Boolean {
        var workSaved: Work? = null
        when (work) {
            is Movie -> workSaved = movieDao.getById(work.id)
            is TvShow -> workSaved = tvShowDao.getById(work.id)
        }
        if (workSaved != null) {
            work.id = workSaved.id
            return true
        }
        return false
    }

    override fun hasFavorite(): Single<Boolean> = Single.create { e ->
        val favoritesMovies = movieDao.queryForAll()
        val favoritesTvShows = tvShowDao.queryForAll()
        e.onSuccess(favoritesMovies.isNotEmpty() || favoritesTvShows.isNotEmpty())
    }

    override fun saveFavorite(work: Work): Boolean =
            when (work) {
                is Movie -> movieDao.create(work)
                is TvShow -> tvShowDao.create(work)
                else -> false
            }

    override fun deleteFavorite(work: Work): Boolean =
            when (work) {
                is Movie -> movieDao.delete(work)
                is TvShow -> tvShowDao.delete(work)
                else -> false
            }

    override fun getFavorites(): Single<List<Work>> = Single.create { e ->
        val works = ArrayList<Work>()

        val favoritesMovies = movieDao.queryForAll()
        for (movie in favoritesMovies) {
            val detailWork = mediaRemote.getMovie(movie.id)
            if (detailWork != null) {
                detailWork.isFavorite = true
                works.add(detailWork)
            }
        }

        val favoritesTvShows = tvShowDao.queryForAll()
        for (tvShow in favoritesTvShows) {
            val detailWork = mediaRemote.getTvShow(tvShow.id)
            if (detailWork != null) {
                detailWork.isFavorite = true
                works.add(detailWork)
            }
        }
        e.onSuccess(works)
    }

    override fun loadWorkByType(page: Int, movieListType: MediaRepository.WorkType): Single<out WorkPage<*>> =
            when (movieListType) {
                MediaRepository.WorkType.NOW_PLAYING_MOVIES -> mediaRemote.getNowPlayingMovies(page)
                MediaRepository.WorkType.POPULAR_MOVIES -> mediaRemote.getPopularMovies(page)
                MediaRepository.WorkType.TOP_RATED_MOVIES -> mediaRemote.getTopRatedMovies(page)
                MediaRepository.WorkType.UP_COMING_MOVIES -> mediaRemote.getUpComingMovies(page)
                MediaRepository.WorkType.AIRING_TODAY_TV_SHOWS -> mediaRemote.getAiringTodayTvShows(page)
                MediaRepository.WorkType.ON_THE_AIR_TV_SHOWS -> mediaRemote.getOnTheAirTvShows(page)
                MediaRepository.WorkType.POPULAR_TV_SHOWS -> mediaRemote.getPopularTvShows(page)
                MediaRepository.WorkType.TOP_RATED_TV_SHOWS -> mediaRemote.getTopRatedTvShows(page)
                else -> Single.error(Throwable())
            }

    override fun getMovieGenres(): Single<MovieGenreList> = mediaRemote.getMovieGenres()

    override fun getWorkByGenre(genre: Genre, page: Int): Single<out WorkPage<*>> =
            when (genre.source) {
                Genre.Source.MOVIE -> mediaRemote.getMoviesByGenre(genre, page)
                Genre.Source.TV_SHOW -> mediaRemote.getTvShowByGenre(genre, page)
            }

    override fun getMovie(movieId: Int): Movie? = mediaRemote.getMovie(movieId)

    override fun getCastByWork(work: Work): Single<CastList> =
            when (work) {
                is Movie -> mediaRemote.getCastByMovie(work)
                is TvShow -> mediaRemote.getCastByTvShow(work)
                else -> Single.error(Throwable())
            }

    override fun getRecommendationByWork(work: Work, page: Int): Single<out WorkPage<*>> =
            when (work) {
                is Movie -> mediaRemote.getRecommendationByMovie(work, page)
                is TvShow -> mediaRemote.getRecommendationByTvShow(work, page)
                else -> Single.error(Throwable())
            }

    override fun getSimilarByWork(work: Work, page: Int): Single<out WorkPage<*>> =
            when (work) {
                is Movie -> mediaRemote.getSimilarByMovie(work, page)
                is TvShow -> mediaRemote.getSimilarByTvShow(work, page)
                else -> Single.error(Throwable())
            }

    override fun getVideosByWork(work: Work): Single<VideoList> =
            when (work) {
                is Movie -> mediaRemote.getVideosByMovie(work)
                is TvShow -> mediaRemote.getVideosByTvShow(work)
                else -> Single.error(Throwable())
            }

    override fun searchMoviesByQuery(query: String, page: Int): Single<MoviePage> =
            mediaRemote.searchMoviesByQuery(query, page)

    override fun searchTvShowsByQuery(query: String, page: Int): Single<TvShowPage> =
            mediaRemote.searchTvShowsByQuery(query, page)

    override fun getCastDetails(cast: Cast): Single<Cast> =
            mediaRemote.getCastDetails(cast)

    override fun getMovieCreditsByCast(cast: Cast): Single<CastMovieList> =
            mediaRemote.getMovieCreditsByCast(cast)

    override fun getTvShowCreditsByCast(cast: Cast): Single<CastTvShowList> =
            mediaRemote.getTvShowCreditsByCast(cast)

    override fun getTvShowGenres(): Single<TvShowGenreList> =
            mediaRemote.getTvShowGenres()
}