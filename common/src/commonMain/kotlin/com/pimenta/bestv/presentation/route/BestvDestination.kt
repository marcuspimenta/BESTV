/*
 * Copyright (C) 2018 Marcus Pimenta
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 */

package com.pimenta.bestv.presentation.route

import androidx.navigation3.runtime.NavKey
import com.pimenta.bestv.presentation.model.WorkViewModel
import kotlinx.serialization.Serializable
import kotlin.random.Random

const val BESTV_SCHEME = "bestv"
const val WORK_BROWSE_HOST = "workbrowse"
const val SEARCH_HOST = "search"
const val WORK_DETAILS_HOST = "workdetail"
const val CAST_DETAILS_HOST = "castdetail"

@Serializable
data object WorkBrowseDestination : NavKey

@Serializable
data class SearchDestination(
    val entryId: String,
) : NavKey {
    companion object {
        fun create() = SearchDestination(newEntryId())
    }
}

@Serializable
data class WorkDetailsDestination(
    val work: WorkViewModel,
    val entryId: String,
) : NavKey {
    companion object {
        fun create(work: WorkViewModel) = WorkDetailsDestination(work, newEntryId())
    }
}

@Serializable
data class CastDetailsDestination(
    val castId: Int,
    val entryId: String,
) : NavKey {
    companion object {
        fun create(castId: Int) = CastDetailsDestination(castId, newEntryId())
    }
}

/** Maps a platform-independent BESTV URI to a destination when its arguments are in the URI. */
fun bestvDestinationFromUri(uri: String): NavKey? {
    val scheme = uri.substringBefore("://", missingDelimiterValue = "")
    if (scheme != BESTV_SCHEME) return null
    val route = uri.substringAfter("://", missingDelimiterValue = "")
    val host = route.substringBefore('/').substringBefore('?')
    val query = route
        .substringAfter('?', "")
        .split('&')
        .mapNotNull { entry ->
            val key = entry.substringBefore('=', missingDelimiterValue = "")
            val value = entry.substringAfter('=', missingDelimiterValue = "")
            key.takeIf(String::isNotBlank)?.let { it to value }
        }.toMap()

    return when (host) {
        WORK_BROWSE_HOST -> WorkBrowseDestination
        SEARCH_HOST -> SearchDestination.create()
        CAST_DETAILS_HOST -> query["id"]?.toIntOrNull()?.let(CastDetailsDestination::create)
        else -> null
    }
}

private fun newEntryId(): String = Random.nextLong().toString(16) + Random.nextLong().toString(16)
