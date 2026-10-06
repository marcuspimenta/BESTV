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
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontFamily
import android.graphics.Typeface
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

private val bestvFontFamily: FontFamily = FontFamily(Typeface.create("sans-serif-condensed", Typeface.NORMAL))

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
    val s200: Dp = 40.dp,
    val s240: Dp = 48.dp,
    val s250: Dp = 50.dp,
    val s280: Dp = 56.dp,
    val s300: Dp = 60.dp,
    val s320: Dp = 64.dp,
    val s360: Dp = 72.dp,
    val s400: Dp = 80.dp,
    val s500: Dp = 100.dp,
    val s520: Dp = 104.dp,
    val s600: Dp = 120.dp,
    val s700: Dp = 140.dp,
    val s715: Dp = 143.dp,
    val s750: Dp = 150.dp,
    val s780: Dp = 156.dp,
    val s850: Dp = 170.dp,
    val s900: Dp = 180.dp,
    val s3000: Dp = 600.dp,
    val s1000: Dp = 200.dp,
    val s1250: Dp = 250.dp,
    val s2000: Dp = 400.dp,
    val s2140: Dp = 428.dp,
    val s3790: Dp = 758.dp,
)

class Colors(
    val white: Color = Color.White,
    val black: Color = Color.Black,
    val transparent: Color = Color.Transparent,
    val reviewCardSurface: Color = Color.DarkGray,
    val mobileNavigationSurface: Color = Color(0xE6262626),
    val mobileNavigationSelectedSurface: Color = Color(0xFF3A3A3A),
    val mobileNavigationContent: Color = Color(0xFFE6E6E6),
    val mobileNavigationScrim: Color = black.copy(alpha = 0.7f),
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
    val drawerScrim: Color = black.copy(alpha = 0.8f),
)

object BESTVTheme {
    val scale = Scale()
    val colors = Colors()

    @Composable
    operator fun invoke(content: @Composable () -> Unit) {
        val typography = androidx.compose.material3.MaterialTheme.typography
        androidx.compose.material3.MaterialTheme(
            colorScheme =
                androidx.compose.material3.darkColorScheme(
                    primary = colors.white,
                    onPrimary = colors.black,
                    background = colors.black,
                    onBackground = colors.white,
                    surface = colors.reviewCardSurface,
                    onSurface = colors.white,
                    error = colors.errorBannerSurface,
                    onError = colors.white,
                ),
            typography =
                typography.copy(
                    displayLarge = typography.displayLarge.copy(fontFamily = bestvFontFamily),
                    displayMedium = typography.displayMedium.copy(fontFamily = bestvFontFamily),
                    displaySmall = typography.displaySmall.copy(fontFamily = bestvFontFamily),
                    headlineLarge = typography.headlineLarge.copy(fontFamily = bestvFontFamily),
                    headlineMedium = typography.headlineMedium.copy(fontFamily = bestvFontFamily),
                    headlineSmall = typography.headlineSmall.copy(fontFamily = bestvFontFamily),
                    titleLarge = typography.titleLarge.copy(fontFamily = bestvFontFamily),
                    titleMedium = typography.titleMedium.copy(fontFamily = bestvFontFamily),
                    titleSmall = typography.titleSmall.copy(fontFamily = bestvFontFamily),
                    bodyLarge = typography.bodyLarge.copy(fontFamily = bestvFontFamily),
                    bodyMedium = typography.bodyMedium.copy(fontFamily = bestvFontFamily),
                    bodySmall = typography.bodySmall.copy(fontFamily = bestvFontFamily),
                    labelLarge = typography.labelLarge.copy(fontFamily = bestvFontFamily),
                    labelMedium = typography.labelMedium.copy(fontFamily = bestvFontFamily),
                    labelSmall = typography.labelSmall.copy(fontFamily = bestvFontFamily),
                ),
        ) {
            val tvTypography = androidx.tv.material3.MaterialTheme.typography
            androidx.tv.material3.MaterialTheme(
                typography =
                    tvTypography.copy(
                        displayLarge = tvTypography.displayLarge.copy(fontFamily = bestvFontFamily),
                        displayMedium = tvTypography.displayMedium.copy(fontFamily = bestvFontFamily),
                        displaySmall = tvTypography.displaySmall.copy(fontFamily = bestvFontFamily),
                        headlineLarge = tvTypography.headlineLarge.copy(fontFamily = bestvFontFamily),
                        headlineMedium = tvTypography.headlineMedium.copy(fontFamily = bestvFontFamily),
                        headlineSmall = tvTypography.headlineSmall.copy(fontFamily = bestvFontFamily),
                        titleLarge = tvTypography.titleLarge.copy(fontFamily = bestvFontFamily),
                        titleMedium = tvTypography.titleMedium.copy(fontFamily = bestvFontFamily),
                        titleSmall = tvTypography.titleSmall.copy(fontFamily = bestvFontFamily),
                        bodyLarge = tvTypography.bodyLarge.copy(fontFamily = bestvFontFamily),
                        bodyMedium = tvTypography.bodyMedium.copy(fontFamily = bestvFontFamily),
                        bodySmall = tvTypography.bodySmall.copy(fontFamily = bestvFontFamily),
                        labelLarge = tvTypography.labelLarge.copy(fontFamily = bestvFontFamily),
                        labelMedium = tvTypography.labelMedium.copy(fontFamily = bestvFontFamily),
                        labelSmall = tvTypography.labelSmall.copy(fontFamily = bestvFontFamily),
                    ),
                content = content,
            )
        }
    }
}
