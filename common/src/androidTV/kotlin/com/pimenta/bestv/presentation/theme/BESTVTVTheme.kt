package com.pimenta.bestv.presentation.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontFamily

@Composable
fun BESTVTVTheme(content: @Composable () -> Unit) {
    BESTVTheme {
        val typography = androidx.tv.material3.MaterialTheme.typography
        androidx.tv.material3.MaterialTheme(
            typography = typography.copy(
                displayLarge = typography.displayLarge.copy(fontFamily = FontFamily.SansSerif),
                displayMedium = typography.displayMedium.copy(fontFamily = FontFamily.SansSerif),
                displaySmall = typography.displaySmall.copy(fontFamily = FontFamily.SansSerif),
                headlineLarge = typography.headlineLarge.copy(fontFamily = FontFamily.SansSerif),
                headlineMedium = typography.headlineMedium.copy(fontFamily = FontFamily.SansSerif),
                headlineSmall = typography.headlineSmall.copy(fontFamily = FontFamily.SansSerif),
                titleLarge = typography.titleLarge.copy(fontFamily = FontFamily.SansSerif),
                titleMedium = typography.titleMedium.copy(fontFamily = FontFamily.SansSerif),
                titleSmall = typography.titleSmall.copy(fontFamily = FontFamily.SansSerif),
                bodyLarge = typography.bodyLarge.copy(fontFamily = FontFamily.SansSerif),
                bodyMedium = typography.bodyMedium.copy(fontFamily = FontFamily.SansSerif),
                bodySmall = typography.bodySmall.copy(fontFamily = FontFamily.SansSerif),
                labelLarge = typography.labelLarge.copy(fontFamily = FontFamily.SansSerif),
                labelMedium = typography.labelMedium.copy(fontFamily = FontFamily.SansSerif),
                labelSmall = typography.labelSmall.copy(fontFamily = FontFamily.SansSerif),
            ),
            content = content,
        )
    }
}
