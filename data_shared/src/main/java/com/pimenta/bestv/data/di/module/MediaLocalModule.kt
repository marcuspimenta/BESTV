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

package com.pimenta.bestv.data.di.module

import android.app.Application
import androidx.room.Room
import com.pimenta.bestv.data.local.database.MediaDb
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/**
 * Created by marcus on 24-06-2019.
 */
@Module
class MediaLocalModule {

    @Provides
    @Singleton
    fun provideLocalDatabase(application: Application) =
            Room.databaseBuilder(application, MediaDb::class.java, "bestv.db")
                    .build()

    @Provides
    @Singleton
    fun provideMovieDao(mediaDb: MediaDb) =
            mediaDb.movieDao()

    @Provides
    @Singleton
    fun provideTvShowDao(mediaDb: MediaDb) =
            mediaDb.tvShowDao()
}