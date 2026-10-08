/*
 * Copyright (C) 2018 Marcus Pimenta
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 */

package com.pimenta.bestv.model.data.remote

import com.google.gson.Gson
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class WorkDetailsResponseTest {
    @Test
    fun `missing detail values remain absent`() {
        val response = Gson().fromJson("{}", MovieResponse::class.java)

        assertNull(response.status)
        assertNull(response.originalLanguage)
        assertNull(response.budget)
        assertNull(response.revenue)
        assertNull(response.keywords)
    }

    @Test
    fun `movie details map financial fields and keywords`() {
        val response = Gson().fromJson(
            """{"status":"Released","original_language":"en","budget":225000000,"revenue":2505477000,"keywords":{"keywords":[{"id":1,"name":"hero"}]}}""",
            MovieResponse::class.java,
        )

        assertEquals("Released", response.status)
        assertEquals("en", response.originalLanguage)
        assertEquals(225000000L, response.budget)
        assertEquals(2505477000L, response.revenue)
        assertEquals(
            "hero",
            response.keywords
                ?.keywords
                ?.single()
                ?.name
        )
    }

    @Test
    fun `tv details map keywords from results`() {
        val response = Gson().fromJson(
            """{"status":"Returning Series","original_language":"en","keywords":{"results":[{"id":2,"name":"drama"}]}}""",
            TvShowResponse::class.java,
        )

        assertEquals("Returning Series", response.status)
        assertEquals("en", response.originalLanguage)
        assertEquals(
            "drama",
            response.keywords
                ?.results
                ?.single()
                ?.name
        )
    }
}
