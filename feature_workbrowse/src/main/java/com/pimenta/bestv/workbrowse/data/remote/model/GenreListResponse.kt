package com.pimenta.bestv.workbrowse.data.remote.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GenreListResponse(
    @SerialName("genres") var genres: List<GenreResponse>? = null,
)
