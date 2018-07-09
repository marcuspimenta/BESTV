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

import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.field.DatabaseField;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by marcus on 06/07/18.
 */
public abstract class Work implements Serializable {

    @DatabaseField(id = true, columnName = "id")
    @SerializedName("id")
    private int mId;
    @SerializedName("original_language")
    private String mOriginalLanguage;
    @SerializedName("overview")
    private String mOverview;
    @SerializedName("backdrop_path")
    private String mBackdropPath;
    @SerializedName("poster_path")
    private String mPosterPath;
    @SerializedName("popularity")
    private float mPopularity;
    @SerializedName("vote_average")
    private float mVoteAverage;
    @SerializedName("vote_count")
    private float mVoteCount;

    public abstract String getTitle();

    public abstract void setTitle(String title);

    public abstract String getOriginalTitle();

    public abstract void setOriginalTitle(String title);

    public abstract Date getReleaseDate();

    public abstract void setReleaseDate(Date releaseDate);

    public int getId() {
        return mId;
    }

    public void setId(final int id) {
        mId = id;
    }

    public String getOriginalLanguage() {
        return mOriginalLanguage;
    }

    public void setOriginalLanguage(final String originalLanguage) {
        mOriginalLanguage = originalLanguage;
    }

    public String getOverview() {
        return mOverview;
    }

    public void setOverview(final String overview) {
        mOverview = overview;
    }

    public String getBackdropPath() {
        return mBackdropPath;
    }

    public void setBackdropPath(final String backdropPath) {
        mBackdropPath = backdropPath;
    }

    public String getPosterPath() {
        return mPosterPath;
    }

    public void setPosterPath(final String posterPath) {
        mPosterPath = posterPath;
    }

    public float getPopularity() {
        return mPopularity;
    }

    public void setPopularity(final float popularity) {
        mPopularity = popularity;
    }

    public float getVoteAverage() {
        return mVoteAverage;
    }

    public void setVoteAverage(final float voteAverage) {
        mVoteAverage = voteAverage;
    }

    public float getVoteCount() {
        return mVoteCount;
    }

    public void setVoteCount(final float voteCount) {
        mVoteCount = voteCount;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        Work that = (Work) obj;

        return mId == that.getId();
    }
}