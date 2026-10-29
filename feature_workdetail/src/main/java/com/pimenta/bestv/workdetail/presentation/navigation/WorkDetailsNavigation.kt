/*
 * Copyright (C) 2018 Marcus Pimenta
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 */

package com.pimenta.bestv.workdetail.presentation.navigation

import android.content.ActivityNotFoundException
import androidx.compose.ui.platform.LocalContext
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.pimenta.bestv.route.navigation.NavigationEntryProvider
import com.pimenta.bestv.route.WorkDetailsDestination
import com.pimenta.bestv.workdetail.presentation.model.ErrorType.FailedToOpenYouTubeVideo
import com.pimenta.bestv.workdetail.presentation.model.WorkDetailsEvent.ShowError
import com.pimenta.bestv.workdetail.presentation.ui.compose.WorkDetailsWrapperScreen
import com.pimenta.bestv.workdetail.presentation.viewmodel.WorkDetailsViewModel
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

class WorkDetailsNavigationEntryProvider : NavigationEntryProvider {
    override fun install(
        scope: EntryProviderScope<NavKey>,
        navigate: (NavKey) -> Unit,
        onBack: () -> Unit,
    ) {
        scope.workDetailsEntryBuilder(navigate, onBack)
    }
}

fun EntryProviderScope<NavKey>.workDetailsEntryBuilder(
    navigate: (NavKey) -> Unit,
    onBack: () -> Unit,
) {
    entry<WorkDetailsDestination> { destination ->
        val viewModel = koinViewModel<WorkDetailsViewModel>(parameters = { parametersOf(destination.work) })
        val context = LocalContext.current
        WorkDetailsWrapperScreen(
            viewModel = viewModel,
            navigate = navigate,
            openExternalIntent = { intent ->
                try {
                    context.startActivity(intent)
                } catch (_: ActivityNotFoundException) {
                    viewModel.handleEvent(ShowError(FailedToOpenYouTubeVideo))
                }
            },
            onBack = onBack,
        )
    }
}
