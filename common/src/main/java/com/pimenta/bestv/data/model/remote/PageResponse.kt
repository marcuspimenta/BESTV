package com.pimenta.bestv.data.model.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Created by marcus on 20-04-2020.
 */
@Serializable
open class PageResponse<T>(
    @SerialName("page") var page: Int = 0,
    @SerialName("total_pages") var totalPages: Int = 0,
    @SerialName("total_results") var totalResults: Int = 0,
    @SerialName("results") var results: List<T>? = null
)
