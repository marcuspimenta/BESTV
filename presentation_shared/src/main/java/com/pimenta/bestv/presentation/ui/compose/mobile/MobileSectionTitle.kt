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
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.padding
import com.pimenta.bestv.presentation.theme.BESTVTheme

@Composable
fun MobileSectionTitle(
    title: String,
    modifier: Modifier = Modifier,
) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleLarge.copy(fontSize = 20.sp),
        color = BESTVTheme.colors.white,
        modifier = modifier.padding(
            start = BESTVTheme.scale.s080,
            top = BESTVTheme.scale.s120,
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
