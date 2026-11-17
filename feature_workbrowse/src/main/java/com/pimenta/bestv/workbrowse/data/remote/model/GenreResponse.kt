package com.pimenta.bestv.workbrowse.data.remote.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GenreResponse(
    @SerialName("id") val id: Int = 0,
    @SerialName("name") val name: String? = null,
)
