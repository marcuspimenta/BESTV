/*
 * Copyright (C) 2018 Marcus Pimenta
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 */

package com.pimenta.bestv.workdetail.data.remote.mapper

import com.pimenta.bestv.model.data.remote.KeywordResponse
import com.pimenta.bestv.model.data.remote.MovieKeywordsResponse
import com.pimenta.bestv.model.data.remote.MovieResponse
import com.pimenta.bestv.model.data.remote.TvShowKeywordsResponse
import com.pimenta.bestv.model.data.remote.TvShowResponse
import org.junit.Assert.assertEquals
import org.junit.Test

class WorkMetadataResponseMapperTest {
    @Test
    fun `movie response maps details and filters blank keywords`() {
        val metadata = MovieResponse(
            budget = 225000000L,
            revenue = 2505477000L,
            keywords = MovieKeywordsResponse(
                keywords = listOf(
                    KeywordResponse(id = 1, name = "hero"),
                    KeywordResponse(id = 2, name = " "),
                ),
            ),
        ).toWorkMetadataDomainModel()

        assertEquals(225000000L, metadata.budget)
        assertEquals(2505477000L, metadata.revenue)
        assertEquals(listOf("hero"), metadata.keywords)
    }

    @Test
    fun `tv response maps result keywords and has no financial values`() {
        val metadata = TvShowResponse(
            keywords = TvShowKeywordsResponse(results = listOf(KeywordResponse(id = 1, name = "drama"))),
        ).toWorkMetadataDomainModel()

        assertEquals(listOf("drama"), metadata.keywords)
        assertEquals(null, metadata.budget)
        assertEquals(null, metadata.revenue)
    }
}
