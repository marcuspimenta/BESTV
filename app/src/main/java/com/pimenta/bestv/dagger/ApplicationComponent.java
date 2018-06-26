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

import com.pimenta.bestv.broadcastreceiver.BootBroadcastReceiver;
import com.pimenta.bestv.dagger.module.ApplicationModule;
import com.pimenta.bestv.service.RecommendationService;
import com.pimenta.bestv.view.activity.CastDetailsActivity;
import com.pimenta.bestv.view.activity.MainActivity;
import com.pimenta.bestv.view.activity.MovieDetailsActivity;
import com.pimenta.bestv.view.activity.SplashActivity;
import com.pimenta.bestv.view.fragment.CastDetailsFragment;
import com.pimenta.bestv.view.fragment.ErrorFragment;
import com.pimenta.bestv.view.fragment.GenreMovieGridFragment;
import com.pimenta.bestv.view.fragment.MovieBrowseFragment;
import com.pimenta.bestv.view.fragment.MovieDetailsFragment;
import com.pimenta.bestv.view.fragment.SearchFragment;
import com.pimenta.bestv.view.fragment.SplashFragment;
import com.pimenta.bestv.view.fragment.TopMovieGridFragment;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by marcus on 07-02-2018.
 */
@Singleton
@Component(modules = {
        ApplicationModule.class
})
public interface ApplicationComponent {

    Application getApplication();

    void inject(BootBroadcastReceiver receiver);

    void inject(RecommendationService service);

    void inject(SplashActivity activity);

    void inject(MainActivity activity);

    void inject(MovieDetailsActivity activity);

    void inject(CastDetailsActivity activity);

    void inject(SplashFragment fragment);

    void inject(MovieBrowseFragment fragment);

    void inject(GenreMovieGridFragment fragment);

    void inject(TopMovieGridFragment fragment);

    void inject(SearchFragment fragment);

    void inject(MovieDetailsFragment fragment);

    void inject(CastDetailsFragment fragment);

    void inject(ErrorFragment fragment);
}