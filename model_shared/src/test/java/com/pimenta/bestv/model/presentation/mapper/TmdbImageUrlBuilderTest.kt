/*
 * Copyright (C) 2018 Marcus Pimenta
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 */

package com.pimenta.bestv.model.presentation.mapper

import org.junit.Assert.assertEquals
import org.junit.Test

class TmdbImageUrlBuilderTest {
    @Test
    fun `provider logo uses small image variant`() {
        assertEquals(
            "https://image.tmdb.org/t/p/w92/logo.png",
            TmdbImageUrlBuilder.providerLogo("/logo.png"),
        )
    }
}
