/*
 * Copyright (C) 2018 Marcus Pimenta
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 */

package com.pimenta.bestv.workbrowse.presentation.ui.compose.mobile

import android.widget.VideoView
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.net.toUri

private const val SPLASH_ANIMATION_URI =
    "android.resource://com.pimenta.bestv/raw/splash_animation"

@Composable
internal fun SplashAnimation(
    modifier: Modifier = Modifier,
    onCompleted: () -> Unit,
) {
    val currentOnCompleted = rememberUpdatedState(onCompleted)
    AndroidView(
        modifier = modifier,
        factory = { context ->
            VideoView(context).apply {
                setOnCompletionListener { currentOnCompleted.value() }
                setVideoURI(SPLASH_ANIMATION_URI.toUri())
                start()
            }
        },
        onRelease = { videoView ->
            videoView.setOnCompletionListener(null)
            videoView.stopPlayback()
        },
    )
}
