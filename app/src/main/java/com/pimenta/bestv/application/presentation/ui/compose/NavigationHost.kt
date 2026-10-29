/*
 * Copyright (C) 2018 Marcus Pimenta
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */

package com.pimenta.bestv.application.presentation.ui.compose

import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.pimenta.bestv.presentation.theme.BESTVTheme
import com.pimenta.bestv.route.navigation.NavigationEntryProvider

private const val SCREEN_TRANSITION_DURATION_MILLIS = 300

@Composable
fun NavigationHost(
    initialDestination: NavKey,
    navigationEntryProviders: List<NavigationEntryProvider>,
    onExit: () -> Unit,
) {
    BESTVTheme {
        val backStack = rememberNavBackStack(initialDestination)
        val navigate: (NavKey) -> Unit = { destination -> backStack.add(destination) }
        val onBack: () -> Unit = {
            if (backStack.size > 1) {
                backStack.removeLastOrNull()
            } else {
                onExit()
            }
        }

        NavDisplay(
            backStack = backStack,
            onBack = onBack,
            entryDecorators = listOf(
                rememberSaveableStateHolderNavEntryDecorator(),
                rememberViewModelStoreNavEntryDecorator(),
            ),
            transitionSpec = { pushScreenTransition() },
            popTransitionSpec = { popScreenTransition() },
            predictivePopTransitionSpec = { popScreenTransition() },
            entryProvider = entryProvider {
                navigationEntryProviders.forEach { provider ->
                    provider.install(this, navigate, onBack)
                }
            },
        )
    }
}

private fun pushScreenTransition() = slideInHorizontally(
    initialOffsetX = { it },
    animationSpec = tween(SCREEN_TRANSITION_DURATION_MILLIS),
) togetherWith slideOutHorizontally(
    targetOffsetX = { -it },
    animationSpec = tween(SCREEN_TRANSITION_DURATION_MILLIS),
)

private fun popScreenTransition() = slideInHorizontally(
    initialOffsetX = { -it },
    animationSpec = tween(SCREEN_TRANSITION_DURATION_MILLIS),
) togetherWith slideOutHorizontally(
    targetOffsetX = { it },
    animationSpec = tween(SCREEN_TRANSITION_DURATION_MILLIS),
)
