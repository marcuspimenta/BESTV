/*
 * Copyright (C) 2018 Marcus Pimenta
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package com.pimenta.bestv.presentation.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

class Scale(
    val s000: Dp = 0.dp,
    val s005: Dp = 1.dp,
    val s010: Dp = 2.dp,
    val s015: Dp = 3.dp,
    val s020: Dp = 4.dp,
    val s030: Dp = 6.dp,
    val s040: Dp = 8.dp,
    val s060: Dp = 12.dp,
    val s080: Dp = 16.dp,
    val s090: Dp = 18.dp,
    val s100: Dp = 20.dp,
    val s120: Dp = 24.dp,
    val s160: Dp = 32.dp,
    val s180: Dp = 36.dp,
    val s240: Dp = 48.dp,
    val s250: Dp = 50.dp,
    val s320: Dp = 64.dp,
    val s400: Dp = 80.dp,
    val s500: Dp = 100.dp,
    val s600: Dp = 120.dp,
    val s700: Dp = 140.dp,
    val s715: Dp = 143.dp,
    val s850: Dp = 170.dp,
    val s1000: Dp = 200.dp,
    val s1250: Dp = 250.dp,
    val s2000: Dp = 400.dp,
    val s2140: Dp = 428.dp,
    val s3790: Dp = 758.dp
)

class Colors(
    val white: Color = Color.White,
    val black: Color = Color.Black,
    val transparent: Color = Color.Transparent,
    val reviewCardSurface: Color = Color.DarkGray,
    val errorBannerSurface: Color = Color(0xFFD32F2F).copy(alpha = 0.95f),
    val secondaryText: Color = white.copy(alpha = 0.7f),
    val secondaryInfoText: Color = white.copy(alpha = 0.8f),
    val reviewText: Color = white.copy(alpha = 0.8f),
    val biographyText: Color = white.copy(alpha = 0.9f),
    val searchPlaceholder: Color = white.copy(alpha = 0.5f),
    val searchFocusedBorder: Color = white.copy(alpha = 0.5f),
    val searchUnfocusedBorder: Color = white.copy(alpha = 0.3f),
    val searchFieldSurface: Color = white.copy(alpha = 0.15f),
    val focusSurface: Color = white.copy(alpha = 0.4f),
    val imageScrim: Color = black.copy(alpha = 0.4f),
    val drawerScrim: Color = black.copy(alpha = 0.8f)
)

object BESTVTheme {
    val scale = Scale()
    val colors = Colors()
}
