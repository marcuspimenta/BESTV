/*
 * Copyright (C) 2018 Marcus Pimenta
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 */

package com.pimenta.bestv.workbrowse.presentation.ui.compose.mobile

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
internal expect fun SplashAnimation(
    modifier: Modifier = Modifier,
    onCompleted: () -> Unit,
)
