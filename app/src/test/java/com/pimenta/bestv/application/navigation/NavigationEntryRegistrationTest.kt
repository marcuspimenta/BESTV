/*
 * Copyright (C) 2018 Marcus Pimenta
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 */

package com.pimenta.bestv.application.navigation

import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import com.pimenta.bestv.application.di.appModule
import com.pimenta.bestv.model.presentation.model.WorkType
import com.pimenta.bestv.model.presentation.model.WorkViewModel
import com.pimenta.bestv.route.CastDetailsDestination
import com.pimenta.bestv.route.SearchDestination
import com.pimenta.bestv.route.WorkBrowseDestination
import com.pimenta.bestv.route.WorkDetailsDestination
import com.pimenta.bestv.route.navigation.NavigationEntryProvider
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertThrows
import org.junit.Test
import org.koin.dsl.koinApplication

class NavigationEntryRegistrationTest {
    @Test
    fun `all destinations have registered navigation entries`() {
        withNavigationEntryProviders { navigationEntryProviders ->
            assertEquals(4, navigationEntryProviders.size)

            val entryProvider = createEntryProvider(navigationEntryProviders)

            destinations.forEach { destination ->
                assertNotNull(entryProvider(destination))
            }
        }
    }

    @Test
    fun `missing provider leaves a destination unresolved`() {
        withNavigationEntryProviders { navigationEntryProviders ->
            val entryProvider = createEntryProvider(navigationEntryProviders.drop(1))

            assertThrows(IllegalStateException::class.java) {
                destinations.forEach { destination -> entryProvider(destination) }
            }
        }
    }

    @Test
    fun `duplicate provider registration is rejected`() {
        withNavigationEntryProviders { navigationEntryProviders ->
            assertThrows(IllegalArgumentException::class.java) {
                createEntryProvider(navigationEntryProviders + navigationEntryProviders.first())
            }
        }
    }

    private fun withNavigationEntryProviders(block: (List<NavigationEntryProvider>) -> Unit) {
        val application = koinApplication { modules(appModule) }
        try {
            block(application.koin.get())
        } finally {
            application.close()
        }
    }

    private fun createEntryProvider(
        navigationEntryProviders: List<NavigationEntryProvider>,
    ) = entryProvider<NavKey> {
        navigationEntryProviders.forEach { provider ->
            provider.install(this, navigate = {}, onBack = {})
        }
    }

    private companion object {
        val destinations = listOf(
            WorkBrowseDestination,
            SearchDestination.create(),
            WorkDetailsDestination.create(
                WorkViewModel(
                    id = 1,
                    originalLanguage = "en",
                    overview = "",
                    source = "",
                    backdropUrl = "",
                    posterUrl = "",
                    title = "",
                    originalTitle = "",
                    releaseDate = "",
                    type = WorkType.MOVIE,
                    voteAverage = 0f,
                ),
            ),
            CastDetailsDestination.create(castId = 1),
        )
    }
}
