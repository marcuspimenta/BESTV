package com.pimenta.bestv.workbrowse.domain.model

/** Stable, resource-free identifiers for the browse categories displayed by the feature. */
enum class BrowseCategoryKey {
    FAVORITES_MOVIES,
    NOW_PLAYING_MOVIES,
    POPULAR_MOVIES,
    TOP_RATED_MOVIES,
    UP_COMING_MOVIES,
    AIRING_TODAY_TV_SHOWS,
    ON_THE_AIR_TV_SHOWS,
    POPULAR_TV_SHOWS,
    TOP_RATED_TV_SHOWS,
}

val movieBrowseCategories =
    listOf(
        BrowseCategoryKey.NOW_PLAYING_MOVIES,
        BrowseCategoryKey.POPULAR_MOVIES,
        BrowseCategoryKey.TOP_RATED_MOVIES,
        BrowseCategoryKey.UP_COMING_MOVIES,
    )

val tvShowBrowseCategories =
    listOf(
        BrowseCategoryKey.AIRING_TODAY_TV_SHOWS,
        BrowseCategoryKey.ON_THE_AIR_TV_SHOWS,
        BrowseCategoryKey.TOP_RATED_TV_SHOWS,
        BrowseCategoryKey.POPULAR_TV_SHOWS,
    )
