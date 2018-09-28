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

import android.support.v17.leanback.widget.AbstractDetailsDescriptionPresenter;

import com.pimenta.bestv.repository.entity.Work;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class WorkDetailsDescriptionPresenter extends AbstractDetailsDescriptionPresenter {

    private static final DateFormat sDateFormat = new SimpleDateFormat("MMM dd, yyyy");

    @Override
    protected void onBindDescription(ViewHolder viewHolder, Object item) {
        Work work = (Work) item;
        if (work != null) {
            viewHolder.getTitle().setText(work.getTitle());
            viewHolder.getSubtitle().setText(sDateFormat.format(work.getReleaseDate()));
            viewHolder.getBody().setText(work.getOverview());
        }
    }
}