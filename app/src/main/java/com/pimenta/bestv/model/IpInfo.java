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
 * Created by marcus on 11-02-2018.
 */
public class IpInfo {

    @SerializedName("as")
    private String mAs;
    @SerializedName("city")
    private String mCity;
    @SerializedName("country")
    private String mCountry;
    @SerializedName("countryCode")
    private String mCountryCode;
    @SerializedName("isp")
    private String mIsp;
    @SerializedName("lat")
    private float mLat;
    @SerializedName("lon")
    private float mLon;
    @SerializedName("org")
    private String mOrg;
    @SerializedName("query")
    private String mQuery;
    @SerializedName("region")
    private String mRegion;
    @SerializedName("regionName")
    private String mRegionName;
    @SerializedName("status")
    private String mStatus;
    @SerializedName("timezone")
    private String mTimeZone;
    @SerializedName("zip")
    private String mZip;

    public String getAs() {
        return mAs;
    }

    public void setAs(final String as) {
        mAs = as;
    }

    public String getCity() {
        return mCity;
    }

    public void setCity(final String city) {
        mCity = city;
    }

    public String getCountry() {
        return mCountry;
    }

    public void setCountry(final String country) {
        mCountry = country;
    }

    public String getCountryCode() {
        return mCountryCode;
    }

    public void setCountryCode(final String countryCode) {
        mCountryCode = countryCode;
    }

    public String getIsp() {
        return mIsp;
    }

    public void setIsp(final String isp) {
        mIsp = isp;
    }

    public float getLat() {
        return mLat;
    }

    public void setLat(final float lat) {
        mLat = lat;
    }

    public float getLon() {
        return mLon;
    }

    public void setLon(final float lon) {
        mLon = lon;
    }

    public String getOrg() {
        return mOrg;
    }

    public void setOrg(final String org) {
        mOrg = org;
    }

    public String getQuery() {
        return mQuery;
    }

    public void setQuery(final String query) {
        mQuery = query;
    }

    public String getRegion() {
        return mRegion;
    }

    public void setRegion(final String region) {
        mRegion = region;
    }

    public String getRegionName() {
        return mRegionName;
    }

    public void setRegionName(final String regionName) {
        mRegionName = regionName;
    }

    public String getStatus() {
        return mStatus;
    }

    public void setStatus(final String status) {
        mStatus = status;
    }

    public String getTimeZone() {
        return mTimeZone;
    }

    public void setTimeZone(final String timeZone) {
        mTimeZone = timeZone;
    }

    public String getZip() {
        return mZip;
    }

    public void setZip(final String zip) {
        mZip = zip;
    }
}