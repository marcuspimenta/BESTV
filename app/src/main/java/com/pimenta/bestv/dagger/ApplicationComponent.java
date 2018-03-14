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

import android.app.Application;

import com.pimenta.bestv.database.DatabaseHelper;
import com.pimenta.bestv.model.Movie;
import com.pimenta.bestv.presenter.BootPresenter;
import com.pimenta.bestv.presenter.MovieBrowsePresenter;
import com.pimenta.bestv.presenter.MovieDetailsPresenter;
import com.pimenta.bestv.presenter.MovieGridPresenter;
import com.pimenta.bestv.presenter.RecommendationPresenter;
import com.pimenta.bestv.presenter.SearchPresenter;
import com.pimenta.bestv.widget.MovieCardPresenter;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by marcus on 07-02-2018.
 */
@Singleton
@Component(modules = {ApplicationModule.class})
public interface ApplicationComponent {

    Application getApplication();

    DatabaseHelper getDatabaseHelper();

    void inject(Movie movie);

    void inject(MovieBrowsePresenter presenter);

    void inject(MovieGridPresenter presenter);

    void inject(MovieDetailsPresenter presenter);

    void inject(MovieCardPresenter presenter);

    void inject(BootPresenter presenter);

    void inject(RecommendationPresenter presenter);

    void inject(SearchPresenter presenter);

}