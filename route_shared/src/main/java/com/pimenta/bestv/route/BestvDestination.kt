/*
 * Copyright (C) 2018 Marcus Pimenta
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 */

package com.pimenta.bestv.route

import android.content.Intent
import androidx.navigation3.runtime.NavKey
import com.pimenta.bestv.model.presentation.model.WorkViewModel
import com.pimenta.bestv.route.castdetail.getCastDeepLink
import com.pimenta.bestv.route.workdetail.getWorkDetail
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data object WorkBrowseDestination : NavKey

@Serializable
data class SearchDestination(
    val entryId: String,
) : NavKey {
    companion object {
        fun create() = SearchDestination(UUID.randomUUID().toString())
    }
}

@Serializable
data class WorkDetailsDestination(
    val work: WorkViewModel,
    val entryId: String,
) : NavKey {
    companion object {
        fun create(work: WorkViewModel) = WorkDetailsDestination(work, UUID.randomUUID().toString())
    }
}

@Serializable
data class CastDetailsDestination(
    val castId: Int,
    val entryId: String,
) : NavKey {
    companion object {
        fun create(castId: Int) = CastDetailsDestination(castId, UUID.randomUUID().toString())
    }
}

/** Maps the existing custom-URI Activity contract to the initial Navigation 3 destination. */
fun Intent.toBestvDestination(): NavKey? {
    val uri = data ?: return null
    if (uri.scheme != BESTV_SCHEME) return null

    return when (uri.host) {
        WORK_BROWSE_HOST -> WorkBrowseDestination
        SEARCH_HOST -> SearchDestination.create()
        WORK_DETAILS_HOST -> getWorkDetail()?.let(WorkDetailsDestination::create)
        CAST_DETAILS_HOST -> getCastDeepLink()?.let { CastDetailsDestination.create(it.id) }
        else -> null
    }
}

const val BESTV_SCHEME = "bestv"
const val WORK_BROWSE_HOST = "workbrowse"
const val SEARCH_HOST = "search"
const val WORK_DETAILS_HOST = "workdetail"
const val CAST_DETAILS_HOST = "castdetail"
