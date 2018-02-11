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

package com.pimenta.bestv.api.tmdb;

import com.google.gson.Gson;

import java.util.concurrent.Executor;

import io.reactivex.annotations.NonNull;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by marcus on 08-02-2018.
 */
public class Tmdb {

    private GenreApi mGenreApi;

    public Tmdb(String baseUrl, Gson gson, @NonNull Executor executor) {
        final Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .callbackExecutor(executor)
                .build();

        mGenreApi = retrofit.create(GenreApi.class);
    }

    public GenreApi getGenreApi() {
        return mGenreApi;
    }
}