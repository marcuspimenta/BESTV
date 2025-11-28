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

package com.pimenta.bestv.workbrowse.di

import com.pimenta.bestv.workbrowse.data.remote.api.GenreTmdbApi
import com.pimenta.bestv.workbrowse.data.remote.api.MovieTmdbApi
import com.pimenta.bestv.workbrowse.data.remote.api.TvShowTmdbApi
import com.pimenta.bestv.workbrowse.data.remote.datasource.GenreRemoteDataSource
import com.pimenta.bestv.workbrowse.data.remote.datasource.MovieRemoteDataSource
import com.pimenta.bestv.workbrowse.data.remote.datasource.TvShowRemoteDataSource
import com.pimenta.bestv.workbrowse.data.repository.GenreRepository
import com.pimenta.bestv.workbrowse.data.repository.MovieRepository
import com.pimenta.bestv.workbrowse.data.repository.TvShowRepository
import com.pimenta.bestv.workbrowse.domain.GetAiringTodayTvShowsUseCase
import com.pimenta.bestv.workbrowse.domain.GetFavoriteMoviesUseCase
import com.pimenta.bestv.workbrowse.domain.GetFavoriteTvShowsUseCase
import com.pimenta.bestv.workbrowse.domain.GetFavoritesUseCase
import com.pimenta.bestv.workbrowse.domain.GetMovieByGenreUseCase
import com.pimenta.bestv.workbrowse.domain.GetMovieGenresUseCase
import com.pimenta.bestv.workbrowse.domain.GetNowPlayingMoviesUseCase
import com.pimenta.bestv.workbrowse.domain.GetOnTheAirTvShowsUseCase
import com.pimenta.bestv.workbrowse.domain.GetPopularMoviesUseCase
import com.pimenta.bestv.workbrowse.domain.GetPopularTvShowsUseCase
import com.pimenta.bestv.workbrowse.domain.GetSectionDetailsUseCase
import com.pimenta.bestv.workbrowse.domain.GetTopRatedMoviesUseCase
import com.pimenta.bestv.workbrowse.domain.GetTopRatedTvShowsUseCase
import com.pimenta.bestv.workbrowse.domain.GetTvShowByGenreUseCase
import com.pimenta.bestv.workbrowse.domain.GetTvShowGenresUseCase
import com.pimenta.bestv.workbrowse.domain.GetUpComingMoviesUseCase
import com.pimenta.bestv.workbrowse.domain.GetWorkByGenreUseCase
import com.pimenta.bestv.workbrowse.domain.LoadWorkByTypeUseCase
import com.pimenta.bestv.workbrowse.presentation.viewmodel.WorkBrowseViewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.module.dsl.factoryOf
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit

val workBrowseModule = module {
    // APIs
    single { get<Retrofit>().create(GenreTmdbApi::class.java) }
    single { get<Retrofit>().create(MovieTmdbApi::class.java) }
    single { get<Retrofit>().create(TvShowTmdbApi::class.java) }

    // DataSources
    factory {
        GenreRemoteDataSource(
            tmdbApiKey = get(named("tmdbApiKey")),
            tmdbFilterLanguage = get(named("tmdbFilterLanguage")),
            genreTmdbApi = get()
        )
    }
    factory {
        MovieRemoteDataSource(
            tmdbApiKey = get(named("tmdbApiKey")),
            tmdbFilterLanguage = get(named("tmdbFilterLanguage")),
            movieTmdbApi = get()
        )
    }
    factory {
        TvShowRemoteDataSource(
            tmdbApiKey = get(named("tmdbApiKey")),
            tmdbFilterLanguage = get(named("tmdbFilterLanguage")),
            tvShowTmdbApi = get()
        )
    }

    // Repositories
    factoryOf(::GenreRepository)
    factoryOf(::MovieRepository)
    factoryOf(::TvShowRepository)

    // UseCases
    factoryOf(::GetAiringTodayTvShowsUseCase)
    factoryOf(::GetFavoriteMoviesUseCase)
    factoryOf(::GetFavoriteTvShowsUseCase)
    factoryOf(::GetFavoritesUseCase)
    factoryOf(::GetMovieByGenreUseCase)
    factoryOf(::GetMovieGenresUseCase)
    factoryOf(::GetNowPlayingMoviesUseCase)
    factoryOf(::GetOnTheAirTvShowsUseCase)
    factoryOf(::GetPopularMoviesUseCase)
    factoryOf(::GetPopularTvShowsUseCase)
    factoryOf(::GetSectionDetailsUseCase)
    factoryOf(::GetTopRatedMoviesUseCase)
    factoryOf(::GetTopRatedTvShowsUseCase)
    factoryOf(::GetTvShowByGenreUseCase)
    factoryOf(::GetTvShowGenresUseCase)
    factoryOf(::GetUpComingMoviesUseCase)
    factoryOf(::GetWorkByGenreUseCase)
    factoryOf(::LoadWorkByTypeUseCase)

    // ViewModel
    viewModelOf(::WorkBrowseViewModel)
}