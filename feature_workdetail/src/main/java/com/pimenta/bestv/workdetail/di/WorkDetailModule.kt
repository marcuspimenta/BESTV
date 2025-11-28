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

package com.pimenta.bestv.workdetail.di

import com.pimenta.bestv.model.presentation.model.WorkViewModel
import com.pimenta.bestv.workdetail.data.remote.api.MovieDetailTmdbApi
import com.pimenta.bestv.workdetail.data.remote.api.TvShowDetailTmdbApi
import com.pimenta.bestv.workdetail.data.remote.datasource.MovieRemoteDataSource
import com.pimenta.bestv.workdetail.data.remote.datasource.TvShowRemoteDataSource
import com.pimenta.bestv.workdetail.data.repository.MovieRepository
import com.pimenta.bestv.workdetail.data.repository.TvShowRepository
import com.pimenta.bestv.workdetail.domain.CheckFavoriteMovieUseCase
import com.pimenta.bestv.workdetail.domain.CheckFavoriteTvShowUseCase
import com.pimenta.bestv.workdetail.domain.CheckFavoriteWorkUseCase
import com.pimenta.bestv.workdetail.domain.GetCastByMovieUseCase
import com.pimenta.bestv.workdetail.domain.GetCastByTvShowUseCase
import com.pimenta.bestv.workdetail.domain.GetCastsUseCase
import com.pimenta.bestv.workdetail.domain.GetRecommendationByMovieUseCase
import com.pimenta.bestv.workdetail.domain.GetRecommendationByTvShowUseCase
import com.pimenta.bestv.workdetail.domain.GetRecommendationByWorkUseCase
import com.pimenta.bestv.workdetail.domain.GetReviewByMovieUseCase
import com.pimenta.bestv.workdetail.domain.GetReviewByTvShowUseCase
import com.pimenta.bestv.workdetail.domain.GetReviewByWorkUseCase
import com.pimenta.bestv.workdetail.domain.GetSimilarByMovieUseCase
import com.pimenta.bestv.workdetail.domain.GetSimilarByTvShowUseCase
import com.pimenta.bestv.workdetail.domain.GetSimilarByWorkUseCase
import com.pimenta.bestv.workdetail.domain.GetVideosByMovieUseCase
import com.pimenta.bestv.workdetail.domain.GetVideosByTvShowUseCase
import com.pimenta.bestv.workdetail.domain.GetVideosUseCase
import com.pimenta.bestv.workdetail.domain.GetWorkDetailsUseCase
import com.pimenta.bestv.workdetail.domain.SetFavoriteUseCase
import com.pimenta.bestv.workdetail.presentation.viewmodel.WorkDetailsViewModel
import org.koin.core.module.dsl.factoryOf
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit

val workDetailModule = module {
    // APIs
    single { get<Retrofit>().create(MovieDetailTmdbApi::class.java) }
    single { get<Retrofit>().create(TvShowDetailTmdbApi::class.java) }

    // DataSources
    factory {
        MovieRemoteDataSource(
            tmdbApiKey = get(named("tmdbApiKey")),
            tmdbFilterLanguage = get(named("tmdbFilterLanguage")),
            movieDetailTmdbApi = get()
        )
    }
    factory {
        TvShowRemoteDataSource(
            tmdbApiKey = get(named("tmdbApiKey")),
            tmdbFilterLanguage = get(named("tmdbFilterLanguage")),
            tvShowDetailTmdbApi = get()
        )
    }

    // Repositories
    factoryOf(::MovieRepository)
    factoryOf(::TvShowRepository)

    // UseCases
    factoryOf(::CheckFavoriteMovieUseCase)
    factoryOf(::CheckFavoriteTvShowUseCase)
    factoryOf(::CheckFavoriteWorkUseCase)
    factoryOf(::GetCastByMovieUseCase)
    factoryOf(::GetCastByTvShowUseCase)
    factoryOf(::GetCastsUseCase)
    factoryOf(::GetRecommendationByMovieUseCase)
    factoryOf(::GetRecommendationByTvShowUseCase)
    factoryOf(::GetRecommendationByWorkUseCase)
    factoryOf(::GetReviewByMovieUseCase)
    factoryOf(::GetReviewByTvShowUseCase)
    factoryOf(::GetReviewByWorkUseCase)
    factoryOf(::GetSimilarByMovieUseCase)
    factoryOf(::GetSimilarByTvShowUseCase)
    factoryOf(::GetSimilarByWorkUseCase)
    factoryOf(::GetVideosByMovieUseCase)
    factoryOf(::GetVideosByTvShowUseCase)
    factoryOf(::GetVideosUseCase)
    factoryOf(::GetWorkDetailsUseCase)
    factoryOf(::SetFavoriteUseCase)

    // ViewModel with intent parameter
    factory { (work: WorkViewModel) ->
        WorkDetailsViewModel(
            work = work,
            setFavoriteUseCase = get(),
            getWorkDetailsUseCase = get(),
            getReviewByWorkUseCase = get(),
            getRecommendationByWorkUseCase = get(),
            getSimilarByWorkUseCase = get(),
            workDetailsRoute = get(),
            castDetailsRoute = get()
        )
    }
}