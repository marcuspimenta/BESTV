/*
 * Copyright (C) 2018 Marcus Pimenta
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 */

package com.pimenta.bestv.route

import android.content.Intent
import android.net.Uri
import com.pimenta.bestv.model.presentation.model.WorkType
import com.pimenta.bestv.model.presentation.model.WorkViewModel
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class BestvDestinationTest {
    @Test
    fun `browse URI maps to browse destination`() {
        val destination = Intent(Intent.ACTION_VIEW, Uri.parse("bestv://workbrowse/")).toBestvDestination()

        assertEquals(WorkBrowseDestination, destination)
    }

    @Test
    fun `search URI maps to a unique search destination`() {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("bestv://search/"))

        val first = intent.toBestvDestination()
        val second = intent.toBestvDestination()

        assertTrue(first is SearchDestination)
        assertTrue(second is SearchDestination)
        assertTrue(first != second)
    }

    @Test
    fun `work details URI maps its Parcelable seed to a saveable destination`() {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("bestv://workdetail/"))
            .putExtra("WORK", work)

        val destination = intent.toBestvDestination() as WorkDetailsDestination
        val json = Json.encodeToString(destination)
        val restored = Json.decodeFromString<WorkDetailsDestination>(json)

        assertEquals(work, restored.work)
        assertEquals(destination.entryId, restored.entryId)
    }

    @Test
    fun `cast details URI maps its Parcelable seed to a saveable destination`() {
        val cast = com.pimenta.bestv.model.presentation.model.CastViewModel(
            id = 42,
            name = "Actor",
            character = "Character",
            birthday = "",
            source = "tmdb",
            deathDay = "",
            biography = "",
            thumbnailUrl = "",
        )
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("bestv://castdetail/"))
            .putExtra("CAST", cast)

        val destination = intent.toBestvDestination() as CastDetailsDestination
        val restored = Json.decodeFromString<CastDetailsDestination>(Json.encodeToString(destination))

        assertEquals(42, restored.castId)
        assertEquals(destination.entryId, restored.entryId)
    }

    @Test
    fun `detail URIs without required Parcelable extras are rejected`() {
        assertNull(Intent(Intent.ACTION_VIEW, Uri.parse("bestv://workdetail/")).toBestvDestination())
        assertNull(Intent(Intent.ACTION_VIEW, Uri.parse("bestv://castdetail/")).toBestvDestination())
    }

    @Test
    fun `unknown host and scheme are rejected`() {
        assertNull(Intent(Intent.ACTION_VIEW, Uri.parse("bestv://unknown/")).toBestvDestination())
        assertNull(Intent(Intent.ACTION_VIEW, Uri.parse("https://workbrowse/")).toBestvDestination())
    }

    private companion object {
        val work = WorkViewModel(
            id = 7,
            originalLanguage = "en",
            overview = "A work overview",
            source = "TMDb",
            backdropUrl = "https://example.com/backdrop.jpg",
            posterUrl = "https://example.com/poster.jpg",
            title = "A Movie",
            originalTitle = "A Movie",
            releaseDate = "2024-01-01",
            type = WorkType.MOVIE,
            voteAverage = 8.1f,
            isFavorite = true,
        )
    }
}
