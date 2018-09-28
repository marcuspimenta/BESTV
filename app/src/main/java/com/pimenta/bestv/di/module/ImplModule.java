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

package com.pimenta.bestv.di.module;

import com.pimenta.bestv.manager.DeviceManager;
import com.pimenta.bestv.manager.DeviceManagerImpl;
import com.pimenta.bestv.manager.ImageManager;
import com.pimenta.bestv.manager.ImageManagerImpl;
import com.pimenta.bestv.manager.PermissionManager;
import com.pimenta.bestv.manager.PermissionManagerImpl;
import com.pimenta.bestv.manager.PreferenceManager;
import com.pimenta.bestv.manager.PreferenceManagerImpl;
import com.pimenta.bestv.manager.RecommendationManager;
import com.pimenta.bestv.manager.RecommendationManagerImpl;
import com.pimenta.bestv.repository.MediaRepository;
import com.pimenta.bestv.repository.MediaRepositoryImpl;
import com.pimenta.bestv.repository.remote.MediaRemote;
import com.pimenta.bestv.repository.remote.TmdbMediaRemote;

import javax.inject.Singleton;

import dagger.Binds;
import dagger.Module;

/**
 * Created by marcus on 09-02-2018.
 */
@Module
public interface ImplModule {

    @Binds
    @Singleton
    MediaRemote provideMediaRemote(TmdbMediaRemote connector);

    @Binds
    @Singleton
    DeviceManager provideDeviceManager(DeviceManagerImpl manager);

    @Binds
    @Singleton
    MediaRepository provideMovieRepository(MediaRepositoryImpl repository);

    @Binds
    @Singleton
    ImageManager provideImageManager(ImageManagerImpl manager);

    @Binds
    @Singleton
    RecommendationManager provideRecommendationManager(RecommendationManagerImpl manager);

    @Binds
    @Singleton
    PreferenceManager providePreferenceManager(PreferenceManagerImpl manager);

    @Binds
    @Singleton
    PermissionManager providePermissionManager(PermissionManagerImpl manager);

}