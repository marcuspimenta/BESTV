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

package com.pimenta.bestv.repository;

import android.support.annotation.NonNull;

import com.pimenta.bestv.repository.database.dao.MovieDao;
import com.pimenta.bestv.repository.database.dao.TvShowDao;
import com.pimenta.bestv.repository.entity.Cast;
import com.pimenta.bestv.repository.entity.CastList;
import com.pimenta.bestv.repository.entity.Genre;
import com.pimenta.bestv.repository.entity.Movie;
import com.pimenta.bestv.repository.entity.MovieGenreList;
import com.pimenta.bestv.repository.entity.MoviePage;
import com.pimenta.bestv.repository.entity.TvShow;
import com.pimenta.bestv.repository.entity.TvShowGenreList;
import com.pimenta.bestv.repository.entity.TvShowPage;
import com.pimenta.bestv.repository.entity.VideoList;
import com.pimenta.bestv.repository.entity.Work;
import com.pimenta.bestv.repository.entity.WorkPage;
import com.pimenta.bestv.repository.remote.MediaRemote;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Single;

/**
 * Created by marcus on 05-03-2018.
 */
public class MediaRepositoryImpl implements MediaRepository {

    private MovieDao mMovieDao;
    private TvShowDao mTvShowDao;
    private MediaRemote mMediaRemote;

    @Inject
    public MediaRepositoryImpl(MovieDao movieDao, TvShowDao tvShowDao, MediaRemote mediaRemote) {
        mMovieDao = movieDao;
        mTvShowDao = tvShowDao;
        mMediaRemote = mediaRemote;
    }

    @Override
    public boolean isFavorite(final Work work) {
        Work workSaved = null;
        if (work instanceof Movie) {
            workSaved = mMovieDao.getById(work.getId());
        } else if (work instanceof TvShow) {
            workSaved = mTvShowDao.getById(work.getId());
        }
        if (workSaved != null) {
            work.setId(workSaved.getId());
            return true;
        }
        return false;
    }

    @Override
    public Single<Boolean> hasFavorite() {
        return Single.create(e -> {
            final List<Movie> favoritesMovies = mMovieDao.queryForAll();
            final List<TvShow> favoritesTvShows = mTvShowDao.queryForAll();
            e.onSuccess((favoritesMovies != null && favoritesMovies.size() > 0) || (favoritesTvShows != null && favoritesTvShows.size() > 0));
        });
    }

    @Override
    public boolean saveFavorite(@NonNull final Work work) {
        if (work instanceof Movie) {
            return mMovieDao.create((Movie) work);
        } else if (work instanceof TvShow) {
            return mTvShowDao.create((TvShow) work);
        }
        return false;
    }

    @Override
    public boolean deleteFavorite(@NonNull final Work work) {
        if (work instanceof Movie) {
            return mMovieDao.delete((Movie) work);
        } else if (work instanceof TvShow) {
            return mTvShowDao.delete((TvShow) work);
        }
        return false;
    }

    @Override
    public Single<List<Work>> getFavorites() {
        return Single.create(e -> {
            final List<Work> works = new ArrayList<>();

            final List<Movie> favoritesMovies = mMovieDao.queryForAll();
            for (final Movie movie : favoritesMovies) {
                final Work detailWork = mMediaRemote.getMovie(movie.getId());
                if (detailWork != null) {
                    detailWork.setFavorite(true);
                    works.add(detailWork);
                }
            }

            final List<TvShow> favoritesTvShows = mTvShowDao.queryForAll();
            for (final TvShow tvShow : favoritesTvShows) {
                final Work detailWork = mMediaRemote.getTvShow(tvShow.getId());
                if (detailWork != null) {
                    detailWork.setFavorite(true);
                    works.add(detailWork);
                }
            }
            e.onSuccess(works);
        });
    }

    @Override
    public Single<? extends WorkPage> loadWorkByType(int page, WorkType movieListType) {
        switch (movieListType) {
            case NOW_PLAYING_MOVIES:
                return mMediaRemote.getNowPlayingMovies(page);
            case POPULAR_MOVIES:
                return mMediaRemote.getPopularMovies(page);
            case TOP_RATED_MOVIES:
                return mMediaRemote.getTopRatedMovies(page);
            case UP_COMING_MOVIES:
                return mMediaRemote.getUpComingMovies(page);
            case AIRING_TODAY_TV_SHOWS:
                return mMediaRemote.getAiringTodayTvShows(page);
            case ON_THE_AIR_TV_SHOWS:
                return mMediaRemote.getOnTheAirTvShows(page);
            case POPULAR_TV_SHOWS:
                return mMediaRemote.getPopularTvShows(page);
            case TOP_RATED_TV_SHOWS:
                return mMediaRemote.getTopRatedTvShows(page);
            default:
                return Single.error(new Throwable());
        }
    }

    @Override
    public Single<MovieGenreList> getMovieGenres() {
        return mMediaRemote.getMovieGenres();
    }

    @Override
    public Single<? extends WorkPage> getWorkByGenre(final Genre genre, final int page) {
        switch (genre.getSource()) {
            case MOVIE:
                return mMediaRemote.getMoviesByGenre(genre, page);
            case TV_SHOW:
                return mMediaRemote.getTvShowByGenre(genre, page);
            default:
                return Single.error(new Throwable());
        }
    }

    @Override
    public Movie getMovie(final int movieId) {
        return mMediaRemote.getMovie(movieId);
    }

    @Override
    public Single<CastList> getCastByWork(final Work work) {
        if (work instanceof Movie) {
            return mMediaRemote.getCastByMovie((Movie) work);
        } else if (work instanceof TvShow) {
            return mMediaRemote.getCastByTvShow((TvShow) work);
        } else {
            return Single.error(new Throwable());
        }
    }

    @Override
    public Single<? extends WorkPage> getRecommendationByWork(final Work work, final int page) {
        if (work instanceof Movie) {
            return mMediaRemote.getRecommendationByMovie((Movie) work, page);
        } else if (work instanceof TvShow) {
            return mMediaRemote.getRecommendationByTvShow((TvShow) work, page);
        } else {
            return Single.error(new Throwable());
        }
    }

    @Override
    public Single<? extends WorkPage> getSimilarByWork(final Work work, final int page) {
        if (work instanceof Movie) {
            return mMediaRemote.getSimilarByMovie((Movie) work, page);
        } else if (work instanceof TvShow) {
            return mMediaRemote.getSimilarByTvShow((TvShow) work, page);
        } else {
            return Single.error(new Throwable());
        }
    }

    @Override
    public Single<VideoList> getVideosByWork(final Work work) {
        if (work instanceof Movie) {
            return mMediaRemote.getVideosByMovie((Movie) work);
        } else if (work instanceof TvShow) {
            return mMediaRemote.getVideosByTvShow((TvShow) work);
        } else {
            return Single.error(new Throwable());
        }
    }

    @Override
    public Single<MoviePage> getNowPlayingMovies(final int page) {
        return mMediaRemote.getNowPlayingMovies(page);
    }

    @Override
    public Single<MoviePage> getPopularMovies(final int page) {
        return mMediaRemote.getPopularMovies(page);
    }

    @Override
    public Single<MoviePage> getTopRatedMovies(final int page) {
        return mMediaRemote.getTopRatedMovies(page);
    }

    @Override
    public Single<MoviePage> getUpComingMovies(final int page) {
        return mMediaRemote.getUpComingMovies(page);
    }

    @Override
    public Single<MoviePage> searchMoviesByQuery(final String query, final int page) {
        return mMediaRemote.searchMoviesByQuery(query, page);
    }

    @Override
    public Single<TvShowPage> searchTvShowsByQuery(final String query, final int page) {
        return mMediaRemote.searchTvShowsByQuery(query, page);
    }

    @Override
    public Single<Cast> getCastDetails(final Cast cast) {
        return mMediaRemote.getCastDetails(cast);
    }

    @Override
    public Single<TvShowGenreList> getTvShowGenres() {
        return mMediaRemote.getTvShowGenres();
    }
}