/*
 * Copyright (C) 2018 Marcus Pimenta
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 */

package com.pimenta.bestv.model.data.remote

import com.google.gson.annotations.SerializedName

data class KeywordResponse(
    @SerializedName("id") val id: Int = 0,
    @SerializedName("name") val name: String? = null,
)

data class MovieKeywordsResponse(
    @SerializedName("keywords") val keywords: List<KeywordResponse>? = null,
)

data class TvShowKeywordsResponse(
    @SerializedName("results") val results: List<KeywordResponse>? = null,
)
