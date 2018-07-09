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
 * Created by marcus on 09-02-2018.
 */
@DatabaseTable(tableName = Movie.TABLE)
public class Movie extends Work {

    public static final String TABLE = "movie";
    private static final String TAG = "Movie";

    @SerializedName("title")
    private String mTitle;
    @SerializedName("original_title")
    private String mOriginalTitle;
    @SerializedName("release_date")
    private String mReleaseDate;
    @SerializedName("adult")
    private boolean mIsAdult;
    private boolean mIsFavorite;

    @Override
    public String getTitle() {
        return mTitle;
    }

    @Override
    public void setTitle(final String title) {
        mTitle = title;
    }

    @Override
    public String getOriginalTitle() {
        return mOriginalTitle;
    }

    @Override
    public void setOriginalTitle(final String originalTitle) {
        mOriginalTitle = originalTitle;
    }

    public Date getReleaseDate() {
        try {
            final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            return dateFormat.parse(mReleaseDate);
        } catch (ParseException e) {
            Log.e(TAG, "Error to get the release data", e);
        }
        return null;
    }

    public void setReleaseDate(final Date releaseDate) {
        mReleaseDate = releaseDate.toString();
    }

    public boolean isAdult() {
        return mIsAdult;
    }

    public void setAdult(final boolean adult) {
        mIsAdult = adult;
    }

    public boolean isFavorite() {
        return mIsFavorite;
    }

    public void setFavorite(final boolean favorite) {
        mIsFavorite = favorite;
    }
}