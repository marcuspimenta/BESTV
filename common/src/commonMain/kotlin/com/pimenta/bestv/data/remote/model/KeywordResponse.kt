/*
 * Copyright (C) 2018 Marcus Pimenta
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 */

package com.pimenta.bestv.data.remote.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class KeywordResponse(
    @SerialName("id") val id: Int = 0,
    @SerialName("name") val name: String? = null,
)

@Serializable
data class MovieKeywordsResponse(
    @SerialName("keywords") val keywords: List<KeywordResponse>? = null,
)

@Serializable
data class TvShowKeywordsResponse(
    @SerialName("results") val results: List<KeywordResponse>? = null,
)
