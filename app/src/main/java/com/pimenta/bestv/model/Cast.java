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

package com.pimenta.bestv.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by marcus on 15-02-2018.
 */
public class Cast {

    @SerializedName("cast_id")
    private int mCastId;
    @SerializedName("credit_id")
    private String mCreditId;
    @SerializedName("gender")
    private int mGender;
    @SerializedName("id")
    private int mId;
    @SerializedName("order")
    private int mOrder;
    @SerializedName("name")
    private String mName;
    @SerializedName("character")
    private String mCharacter;
    @SerializedName("profile_path")
    private String mProfilePath;
    @SerializedName("birthday")
    private String mBirthday;
    @SerializedName("deathday")
    private String mDeathDay;
    @SerializedName("biography")
    private String mBiography;
    @SerializedName("popularity")
    private Double mPopularity;
    @SerializedName("place_of_birth")
    private String mPlaceOfBirth;

    public int getCastId() {
        return mCastId;
    }

    public void setCastId(final int castId) {
        mCastId = castId;
    }

    public String getCreditId() {
        return mCreditId;
    }

    public void setCreditId(final String creditId) {
        mCreditId = creditId;
    }

    public int getGender() {
        return mGender;
    }

    public void setGender(final int gender) {
        mGender = gender;
    }

    public int getId() {
        return mId;
    }

    public void setId(final int id) {
        mId = id;
    }

    public int getOrder() {
        return mOrder;
    }

    public void setOrder(final int order) {
        mOrder = order;
    }

    public String getName() {
        return mName;
    }

    public void setName(final String name) {
        mName = name;
    }

    public String getCharacter() {
        return mCharacter;
    }

    public void setCharacter(final String character) {
        mCharacter = character;
    }

    public String getProfilePath() {
        return mProfilePath;
    }

    public void setProfilePath(final String profilePath) {
        mProfilePath = profilePath;
    }

    public String getBirthday() {
        return mBirthday;
    }

    public void setBirthday(final String birthday) {
        mBirthday = birthday;
    }

    public String getDeathDay() {
        return mDeathDay;
    }

    public void setDeathDay(final String deathDay) {
        mDeathDay = deathDay;
    }

    public String getBiography() {
        return mBiography;
    }

    public void setBiography(final String biography) {
        mBiography = biography;
    }

    public Double getPopularity() {
        return mPopularity;
    }

    public void setPopularity(final Double popularity) {
        mPopularity = popularity;
    }

    public String getPlaceOfBirth() {
        return mPlaceOfBirth;
    }

    public void setPlaceOfBirth(final String placeOfBirth) {
        mPlaceOfBirth = placeOfBirth;
    }
}