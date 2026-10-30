/*
 * Copyright (C) 2018 Marcus Pimenta
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 */

package com.pimenta.bestv.presentation.ui.compose.mobile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.res.stringResource
import com.pimenta.bestv.presentation.R
import com.pimenta.bestv.presentation.theme.BESTVTheme

@Composable
fun MobileError(
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.padding(BESTVTheme.scale.s160),
        shape = RoundedCornerShape(BESTVTheme.scale.mobileErrorCornerRadius),
        color = BESTVTheme.colors.mobileCardSurface,
        border = BorderStroke(BESTVTheme.scale.s005, BESTVTheme.colors.mobileCardOutline),
    ) {
        Column(
            modifier = Modifier.padding(BESTVTheme.scale.s160),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(BESTVTheme.scale.s080),
        ) {
            Icon(
                imageVector = Icons.Filled.Info,
                contentDescription = null,
                tint = BESTVTheme.colors.secondaryInfoText,
                modifier = Modifier
                    .padding(bottom = BESTVTheme.scale.s040)
                    .size(BESTVTheme.scale.s200),
            )
            Text(
                text = stringResource(R.string.error_fragment_message),
                color = BESTVTheme.colors.white,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyLarge,
            )
            Button(onClick = onRetry, modifier = Modifier.heightIn(min = BESTVTheme.scale.s240)) {
                Text(stringResource(R.string.error_fragment_button))
            }
        }
    }
}
