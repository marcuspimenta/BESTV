/*
 * Copyright (C) 2018 Marcus Pimenta
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 */

package com.pimenta.bestv.model.presentation.mapper

import com.pimenta.bestv.model.domain.CastDomainModel
import com.pimenta.bestv.model.domain.WorkDomainModel
import org.junit.Assert.assertEquals
import org.junit.Test

class TmdbImageUrlBuilderTest {
    @Test
    fun `work mapper selects resized poster and backdrop variants`() {
        val work = WorkDomainModel(
            id = 1,
            title = "Movie",
            originalTitle = "Movie",
            releaseDate = "2025-01-01",
            originalLanguage = "en",
            overview = "Overview",
            source = "TMDB",
            backdropPath = "/backdrop.jpg",
            posterPath = "/poster.jpg",
        ).toViewModel()

        assertEquals("https://image.tmdb.org/t/p/w1280/backdrop.jpg", work?.backdropUrl)
        assertEquals("https://image.tmdb.org/t/p/w780/backdrop.jpg", work?.backdropCardUrl)
        assertEquals("https://image.tmdb.org/t/p/w500/poster.jpg", work?.posterUrl)
    }

    @Test
    fun `cast mapper selects resized profile variant`() {
        val cast = CastDomainModel(
            id = 1,
            name = "Actor",
            profilePath = "/profile.jpg",
        ).toViewModel()

        assertEquals("https://image.tmdb.org/t/p/h632/profile.jpg", cast?.thumbnailUrl)
    }

    @Test
    fun `provider logo uses small image variant`() {
        assertEquals(
            "https://image.tmdb.org/t/p/w92/logo.png",
            TmdbImageUrlBuilder.providerLogo("/logo.png"),
        )
    }
}
