/*
 * Copyright (C) 2018 Marcus Pimenta
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 */

package com.pimenta.bestv.presentation.ui.compose.mobile

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import com.pimenta.bestv.presentation.theme.BESTVTheme

@Composable
fun MobileSectionTitle(
    title: String,
    modifier: Modifier = Modifier,
    style: TextStyle = MaterialTheme.typography.titleLarge,
    startPadding: Dp = BESTVTheme.scale.s080,
    topPadding: Dp = BESTVTheme.scale.s120,
) {
    Text(
        text = title,
        style = style,
        color = BESTVTheme.colors.white,
        modifier = modifier.padding(
            start = startPadding,
            top = topPadding,
            bottom = BESTVTheme.scale.s060,
        ),
    )
}

@Preview(showBackground = true)
@Composable
private fun MobileSectionTitlePreview() {
    BESTVTheme {
        MobileSectionTitle("Popular")
    }
}
