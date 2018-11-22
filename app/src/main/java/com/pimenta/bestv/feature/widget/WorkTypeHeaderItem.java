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

package com.pimenta.bestv.feature.widget;

import android.support.v17.leanback.widget.HeaderItem;

import com.pimenta.bestv.repository.MediaRepository;

/**
 * Created by marcus on 11-02-2018.
 */
public class WorkTypeHeaderItem extends HeaderItem {

    private final MediaRepository.WorkType mMovieListType;

    public WorkTypeHeaderItem(int id, String name, MediaRepository.WorkType movieListType) {
        super(id, name);
        mMovieListType = movieListType;
    }

    public MediaRepository.WorkType getMovieListType() {
        return mMovieListType;
    }
}
