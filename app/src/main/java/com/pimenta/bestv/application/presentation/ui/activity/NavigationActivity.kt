/*
 * Copyright (C) 2018 Marcus Pimenta
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 */

package com.pimenta.bestv.application.presentation.ui.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.pimenta.bestv.application.presentation.ui.compose.NavigationHost
import com.pimenta.bestv.route.WorkBrowseDestination
import com.pimenta.bestv.route.navigation.NavigationEntryProvider
import com.pimenta.bestv.route.toBestvDestination
import org.koin.android.ext.android.inject

class NavigationActivity : ComponentActivity() {

    private val navigationEntryProviders: List<NavigationEntryProvider> by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val initialDestination = intent.toBestvDestination() ?: WorkBrowseDestination

        setContent {
            NavigationHost(
                initialDestination = initialDestination,
                navigationEntryProviders = navigationEntryProviders,
                onExit = { finish() },
            )
        }
    }
}
