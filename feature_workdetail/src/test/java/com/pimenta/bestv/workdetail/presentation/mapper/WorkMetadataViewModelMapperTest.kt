/*
 * Copyright (C) 2018 Marcus Pimenta
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 */

package com.pimenta.bestv.workdetail.presentation.mapper

import com.pimenta.bestv.workdetail.domain.model.WorkMetadataDomainModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import java.util.Locale

class WorkMetadataViewModelMapperTest {
    @Test
    fun `metadata mapper localizes language and formats USD amounts`() {
        val metadata = WorkMetadataDomainModel(
            status = "Released",
            originalLanguage = "en",
            budget = 225000000L,
            revenue = 2505477000L,
            keywords = listOf(" hero ", "hero", "sequel"),
        ).toViewModel(Locale.US)

        assertEquals("English", metadata.originalLanguage)
        assertEquals("$225,000,000", metadata.budget)
        assertEquals("$2,505,477,000", metadata.revenue)
        assertEquals(listOf("hero", "sequel"), metadata.keywords)
    }

    @Test
    fun `metadata mapper omits zero financial values and uses fallback language`() {
        val metadata = WorkMetadataDomainModel(budget = 0L, revenue = 0L)
            .toViewModel(Locale.US, fallbackOriginalLanguage = "en")

        assertEquals("English", metadata.originalLanguage)
        assertNull(metadata.budget)
        assertNull(metadata.revenue)
    }
}
