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

package com.pimenta.bestv.view.widget;

import android.support.v17.leanback.widget.AbstractDetailsDescriptionPresenter;

import com.pimenta.bestv.repository.entity.Cast;

/**
 * Created by marcus on 07-04-2018.
 */
public class CastDetailsDescriptionPresenter extends AbstractDetailsDescriptionPresenter {

    @Override
    protected void onBindDescription(ViewHolder viewHolder, Object item) {
        Cast cast = (Cast) item;
        if (cast != null) {
            viewHolder.getTitle().setText(cast.getName());
            //viewHolder.getSubtitle().setText(dateFormat.format(movie.getReleaseDate()));
            viewHolder.getBody().setText(cast.getBiography());
        }
    }
}