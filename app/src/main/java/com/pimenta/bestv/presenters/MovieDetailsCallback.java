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

package com.pimenta.bestv.presenters;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

import com.pimenta.bestv.models.Cast;
import com.pimenta.bestv.models.Movie;

import java.util.List;

/**
 * Created by marcus on 07-02-2018.
 */
public interface MovieDetailsCallback extends BasePresenter.Callback {

    void onCastLoaded(List<Cast> casts);

    void onRecommendationLoaded(List<Movie> movies);

    void onCardImageLoaded(Drawable resource);

    void onBackdropImageLoaded(Bitmap bitmap);

}