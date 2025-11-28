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

package com.pimenta.bestv.castdetail.di

import com.pimenta.bestv.castdetail.data.remote.api.CastTmdbApi
import com.pimenta.bestv.castdetail.data.remote.datasource.CastRemoteDataSource
import com.pimenta.bestv.castdetail.data.repository.CastRepository
import com.pimenta.bestv.castdetail.domain.GetCastDetailsUseCase
import com.pimenta.bestv.castdetail.domain.GetCastPersonalDetails
import com.pimenta.bestv.castdetail.domain.GetMovieCreditsByCastUseCase
import com.pimenta.bestv.castdetail.domain.GetTvShowCreditsByCastUseCase
import com.pimenta.bestv.castdetail.presentation.viewmodel.CastDetailsViewModel
import com.pimenta.bestv.model.presentation.model.CastViewModel
import org.koin.core.module.dsl.factoryOf
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit

val castDetailModule = module {
    // API
    single { get<Retrofit>().create(CastTmdbApi::class.java) }

    // DataSource
    factory {
        CastRemoteDataSource(
            tmdbApiKey = get(named("tmdbApiKey")),
            tmdbFilterLanguage = get(named("tmdbFilterLanguage")),
            castTmdbApi = get()
        )
    }

    // Repository
    factoryOf(::CastRepository)

    // UseCases
    factoryOf(::GetCastPersonalDetails)
    factoryOf(::GetMovieCreditsByCastUseCase)
    factoryOf(::GetTvShowCreditsByCastUseCase)
    factoryOf(::GetCastDetailsUseCase)

    // ViewModel with intent parameter
    factory { (cast: CastViewModel) ->
        CastDetailsViewModel(
            cast = cast,
            getCastDetailsUseCase = get(),
            workDetailsRoute = get()
        )
    }
}