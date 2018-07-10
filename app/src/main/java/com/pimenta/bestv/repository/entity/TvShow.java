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

package com.pimenta.bestv.repository.entity;

import android.util.Log;

import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.table.DatabaseTable;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by marcus on 06/07/18.
 */
@DatabaseTable(tableName = TvShow.TABLE)
public class TvShow extends Work {

    private static final String TAG = TvShow.class.getSimpleName();
    public static final String TABLE = "tv_show";

    @SerializedName("name")
    private String mName;
    @SerializedName("original_name")
    private String mOriginalName;
    @SerializedName("first_air_date")
    private String mFirstAirDate;

    @Override
    public String getTitle() {
        return mName;
    }

    @Override
    public void setTitle(final String title) {
        mName = title;
    }

    @Override
    public String getOriginalTitle() {
        return mOriginalName;
    }

    @Override
    public void setOriginalTitle(final String originalTitle) {
        mOriginalName = originalTitle;
    }

    @Override
    public Date getReleaseDate() {
        try {
            final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            return dateFormat.parse(mFirstAirDate);
        } catch (ParseException e) {
            Log.e(TAG, "Error to get the release data", e);
        }
        return null;
    }

    @Override
    public void setReleaseDate(final Date releaseDate) {
        mFirstAirDate = releaseDate.toString();
    }
}