/*
 * Copyright (C) 2018 Marcus Pimenta
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 */

package com.pimenta.bestv.presentation.ui.compose.mobile

import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.pimenta.bestv.presentation.theme.BESTVTheme

@Composable
fun MobileLoading(
    modifier: Modifier = Modifier,
) {
    CircularProgressIndicator(
        modifier = modifier.size(BESTVTheme.scale.s200),
        color = BESTVTheme.colors.white,
        strokeWidth = BESTVTheme.scale.s015,
    )
}
