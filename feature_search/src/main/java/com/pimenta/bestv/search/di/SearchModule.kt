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

package com.pimenta.bestv.search.di

import com.pimenta.bestv.search.data.remote.api.SearchMovieTmdbApi
import com.pimenta.bestv.search.data.remote.api.SearchTvShowTmdbApi
import com.pimenta.bestv.search.data.remote.datasource.MovieRemoteDataSource
import com.pimenta.bestv.search.data.remote.datasource.TvShowRemoteDataSource
import com.pimenta.bestv.search.data.repository.MovieRepository
import com.pimenta.bestv.search.data.repository.TvShowRepository
import com.pimenta.bestv.search.domain.SearchMoviesByQueryUseCase
import com.pimenta.bestv.search.domain.SearchTvShowsByQueryUseCase
import com.pimenta.bestv.search.domain.SearchWorksByQueryUseCase
import com.pimenta.bestv.search.domain.UrlEncoderTextUseCase
import com.pimenta.bestv.search.presentation.viewmodel.SearchViewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.module.dsl.factoryOf
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit

val searchModule = module {
    // APIs
    single { get<Retrofit>().create(SearchMovieTmdbApi::class.java) }
    single { get<Retrofit>().create(SearchTvShowTmdbApi::class.java) }

    // DataSources
    factory {
        MovieRemoteDataSource(
            tmdbApiKey = get(named("tmdbApiKey")),
            tmdbFilterLanguage = get(named("tmdbFilterLanguage")),
            searchMovieTmdbApi = get()
        )
    }
    factory {
        TvShowRemoteDataSource(
            tmdbApiKey = get(named("tmdbApiKey")),
            tmdbFilterLanguage = get(named("tmdbFilterLanguage")),
            searchTvShowTmdbApi = get()
        )
    }

    // Repositories
    factoryOf(::MovieRepository)
    factoryOf(::TvShowRepository)

    // UseCases
    factoryOf(::UrlEncoderTextUseCase)
    factoryOf(::SearchMoviesByQueryUseCase)
    factoryOf(::SearchTvShowsByQueryUseCase)
    factoryOf(::SearchWorksByQueryUseCase)

    // ViewModel
    viewModelOf(::SearchViewModel)
}