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

package com.pimenta.bestv.castdetail.di.module

import com.pimenta.bestv.castdetail.data.remote.api.CastTmdbApi
import com.pimenta.bestv.presentation.di.annotation.FragmentScope
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit

/**
 * Created by marcus on 29-10-2019.
 */
@Module
class CastRemoteDataSourceModule {

    @Provides
    @FragmentScope
    fun provideCastApi(retrofit: Retrofit) =
        retrofit.create(CastTmdbApi::class.java)
}
