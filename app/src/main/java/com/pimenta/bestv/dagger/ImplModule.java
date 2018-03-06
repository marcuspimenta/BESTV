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

package com.pimenta.bestv.dagger;

import com.pimenta.bestv.connectors.TmdbConnector;
import com.pimenta.bestv.connectors.TmdbConnectorImpl;
import com.pimenta.bestv.managers.DeviceManager;
import com.pimenta.bestv.managers.DeviceManagerImpl;
import com.pimenta.bestv.managers.MovieManager;
import com.pimenta.bestv.managers.MovieManagerImpl;
import com.pimenta.bestv.managers.RecommendationManager;
import com.pimenta.bestv.managers.RecommendationManagerImpl;

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
    TmdbConnector provideTmdbConnector(TmdbConnectorImpl connector);

    @Binds
    @Singleton
    DeviceManager provideDeviceManager(DeviceManagerImpl manager);

    @Binds
    @Singleton
    MovieManager provideMovieManager(MovieManagerImpl manager);

    @Binds
    @Singleton
    RecommendationManager provideRecommendationManager(RecommendationManagerImpl manager);

}