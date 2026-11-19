/*
 * Copyright (C) 2018 Marcus Pimenta
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 */

package com.pimenta.bestv.presentation.ui.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.pimenta.bestv.presentation.ui.compose.BestvApp
import com.pimenta.bestv.presentation.platform.DeviceCapabilities
import com.pimenta.bestv.presentation.route.WorkBrowseDestination
import com.pimenta.bestv.presentation.route.navigation.NavigationEntryProvider
import com.pimenta.bestv.presentation.route.toBestvDestination
import org.koin.android.ext.android.inject

class MainActivity : ComponentActivity() {

    private val navigationEntryProviders: List<NavigationEntryProvider> by inject()
    private val deviceCapabilities: DeviceCapabilities by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val initialDestination = intent.toBestvDestination() ?: WorkBrowseDestination

        setContent {
            BestvApp(
                initialDestination = initialDestination,
                navigationEntryProviders = navigationEntryProviders,
                isMobileDevice = deviceCapabilities.isMobileDevice,
                onExit = { finish() },
            )
        }
    }
}
