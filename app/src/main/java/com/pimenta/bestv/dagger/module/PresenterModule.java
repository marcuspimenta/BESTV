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

package com.pimenta.bestv.dagger.module;

import android.app.AlarmManager;
import android.app.Application;
import android.util.DisplayMetrics;

import com.pimenta.bestv.connector.TmdbConnector;
import com.pimenta.bestv.manager.ImageManager;
import com.pimenta.bestv.manager.MovieManager;
import com.pimenta.bestv.manager.RecommendationManager;
import com.pimenta.bestv.presenter.BootPresenter;
import com.pimenta.bestv.presenter.DefaultPresenter;
import com.pimenta.bestv.presenter.MovieBrowsePresenter;
import com.pimenta.bestv.presenter.MovieDetailsPresenter;
import com.pimenta.bestv.presenter.MovieGridPresenter;
import com.pimenta.bestv.presenter.RecommendationPresenter;
import com.pimenta.bestv.presenter.SearchPresenter;

import dagger.Module;
import dagger.Provides;

/**
 * Created by marcus on 20-03-2018.
 */
@Module
public class PresenterModule {

    @Provides
    DefaultPresenter provideDefaultPresenter() {
        return new DefaultPresenter();
    }

    @Provides
    BootPresenter provideBootPresenter(Application application, AlarmManager alarmManager) {
        return new BootPresenter(application, alarmManager);
    }

    @Provides
    MovieBrowsePresenter provideMovieBrowsePresenter(DisplayMetrics displayMetrics, RecommendationManager recommendationManager,
            MovieManager movieManager, TmdbConnector tmdbConnector) {
        return new MovieBrowsePresenter(displayMetrics, recommendationManager, movieManager, tmdbConnector);
    }

    @Provides
    MovieGridPresenter provideMovieGridPresenter(MovieManager movieManager, ImageManager imageManager, TmdbConnector tmdbConnector) {
        return new MovieGridPresenter(movieManager, imageManager, tmdbConnector);
    }

    @Provides
    MovieDetailsPresenter provideMovieDetailsPresenter(Application application, MovieManager movieManager, ImageManager imageManager,
            TmdbConnector tmdbConnector) {
        return new MovieDetailsPresenter(application, movieManager, imageManager, tmdbConnector);
    }

    @Provides
    RecommendationPresenter provideRecommendationPresenter(RecommendationManager recommendationManager) {
        return new RecommendationPresenter(recommendationManager);
    }

    @Provides
    SearchPresenter provideSearchPresenter(TmdbConnector tmdbConnector) {
        return new SearchPresenter(tmdbConnector);
    }

}