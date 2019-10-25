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

package com.pimenta.bestv.di.module

import com.pimenta.bestv.data.MediaRepository
import com.pimenta.bestv.data.MediaRepositoryImpl
import com.pimenta.bestv.data.local.MediaLocalRepository
import com.pimenta.bestv.data.local.MediaLocalRepositoryImpl
import com.pimenta.bestv.data.remote.MediaRemoteRepository
import com.pimenta.bestv.data.remote.MediaRemoteRepositoryImpl
import dagger.Binds
import dagger.Module
import javax.inject.Singleton

/**
 * Created by marcus on 09-02-2018.
 */
@Module(
        includes = [
            MediaLocalModule::class,
            MediaRemoteModule::class,
            RecommendationModule::class
        ]
)
interface MediaModule {

    @Binds
    @Singleton
    fun provideMediaRemote(mediaRemoteRepository: MediaRemoteRepositoryImpl): MediaRemoteRepository

    @Binds
    @Singleton
    fun provideMediaLocal(mediaLocalRepository: MediaLocalRepositoryImpl): MediaLocalRepository

    @Binds
    @Singleton
    fun provideMovieRepository(repository: MediaRepositoryImpl): MediaRepository
}