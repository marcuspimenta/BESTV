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

package com.pimenta.bestv.models;

import android.util.Log;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.pimenta.bestv.database.AbstractDatabaseModel;

import java.util.Date;
import java.util.List;

/**
 * Created by marcus on 09-02-2018.
 */
@DatabaseTable(tableName = Movie.TABLE)
public class Movie extends AbstractDatabaseModel {

    private static final String TAG = "Movie";
    public static final String TABLE = "movie";
    public static final String FIELD_ID = "id";

    @DatabaseField(columnName = FIELD_ID, generatedId = true)
    @SerializedName("id")
    @Expose
    private int mId;
    @SerializedName("title")
    @Expose
    private String mTitle;
    @SerializedName("original_title")
    @Expose
    private String mOriginalTitle;
    @SerializedName("original_language")
    @Expose
    private String mOriginalLanguage;
    @SerializedName("overview")
    @Expose
    private String mOverview;
    @SerializedName("release_date")
    @Expose
    private Date mReleaseDate;
    @SerializedName("backdrop_path")
    @Expose
    private String mBackdropPath;
    @SerializedName("poster_path")
    @Expose
    private String mPosterPath;
    @SerializedName("popularity")
    @Expose
    private float mPopularity;
    @SerializedName("vote_average")
    @Expose
    private float mVoteAverage;
    @SerializedName("vote_count")
    @Expose
    private float mVoteCount;
    @SerializedName("adult")
    @Expose
    private boolean mIsAdult;
    @SerializedName("genres")
    @Expose
    private List<Genre> mGenres;

    public static List<Movie> getAll() {
        return getDao(Movie.class).queryForAll();
    }

    public int create() {
        int res = 0;
        try {
            res = getDao(Movie.class).create(this);
        } catch (RuntimeException e) {
            Log.w(TAG, "Failed to create a movie.", e);
        }
        return res;
    }

    public int delete() {
        int res = 0;
        try {
            res = getDao(Movie.class).delete(this);
        } catch (RuntimeException e) {
            Log.w(TAG, "Failed to delete a movie.", e);
        }
        return res;
    }

    public int update() {
        int res = 0;
        try {
            res = getDao(Movie.class).update(this);
        } catch (RuntimeException e) {
            Log.w(TAG, "Failed to update a movie.", e);
        }
        return res;
    }

    public int getId() {
        return mId;
    }

    public void setId(final int id) {
        mId = id;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(final String title) {
        mTitle = title;
    }

    public String getOriginalTitle() {
        return mOriginalTitle;
    }

    public void setOriginalTitle(final String originalTitle) {
        mOriginalTitle = originalTitle;
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

    public Date getReleaseDate() {
        return mReleaseDate;
    }

    public void setReleaseDate(final Date releaseDate) {
        mReleaseDate = releaseDate;
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

    public boolean isAdult() {
        return mIsAdult;
    }

    public void setAdult(final boolean adult) {
        mIsAdult = adult;
    }

    public List<Genre> getGenres() {
        return mGenres;
    }

    public void setGenres(final List<Genre> genres) {
        mGenres = genres;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        Movie that = (Movie) obj;

        return mId == that.getId();
    }
}