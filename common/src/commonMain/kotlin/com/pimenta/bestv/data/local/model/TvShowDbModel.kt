package com.pimenta.bestv.data.local.model

data class TvShowDbModel(
    val id: Int,
    val title: String?,
    val originalTitle: String?,
    val releaseDate: String?,
    val originalLanguage: String?,
    val overview: String?,
    val source: String,
    val backdropPath: String?,
    val posterPath: String?,
    val popularity: Float,
    val voteAverage: Float,
    val voteCount: Float,
    val isAdult: Boolean,
    val isFavorite: Boolean,
) {
    companion object {
        fun favoritePlaceholder(id: Int) =
            TvShowDbModel(
                id = id,
                title = null,
                originalTitle = null,
                releaseDate = null,
                originalLanguage = null,
                overview = null,
                source = "TMDB",
                backdropPath = null,
                posterPath = null,
                popularity = 0f,
                voteAverage = 0f,
                voteCount = 0f,
                isAdult = false,
                isFavorite = true,
            )
    }
}
