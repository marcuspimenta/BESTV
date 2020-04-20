package com.pimenta.bestv.model.data.remote

import com.google.gson.annotations.SerializedName

/**
 * Created by marcus on 20-04-2020.
 */
open class PageResponse<T>(
    @SerializedName("page") var page: Int = 0,
    @SerializedName("total_pages") var totalPages: Int = 0,
    @SerializedName("total_results") var totalResults: Int = 0,
    @SerializedName("results") var works: List<T>? = null
)
