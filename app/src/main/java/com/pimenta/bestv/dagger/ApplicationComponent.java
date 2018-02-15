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

import com.pimenta.bestv.presenters.MainPresenter;
import com.pimenta.bestv.presenters.MovieDetailsPresenter;
import com.pimenta.bestv.presenters.MovieGridPresenter;
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

    void inject(MainPresenter presenter);

    void inject(MovieGridPresenter presenter);

    void inject(MovieDetailsPresenter presenter);

    void inject(MovieCardPresenter presenter);

}