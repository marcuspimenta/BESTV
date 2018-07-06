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

import java.util.List;

/**
 * Created by marcus on 06/07/18.
 */
public abstract class WorkPage<T extends Work> {

    @SerializedName("page")
    private int mPage;
    @SerializedName("total_pages")
    private int mTotalPages;
    @SerializedName("total_results")
    private int mTotalResults;
    @SerializedName("results")
    protected List<T> mWorks;

    public int getPage() {
        return mPage;
    }

    public void setPage(final int page) {
        mPage = page;
    }

    public int getTotalPages() {
        return mTotalPages;
    }

    public void setTotalPages(final int totalPages) {
        mTotalPages = totalPages;
    }

    public int getTotalResults() {
        return mTotalResults;
    }

    public void setTotalResults(final int totalResults) {
        mTotalResults = totalResults;
    }

}