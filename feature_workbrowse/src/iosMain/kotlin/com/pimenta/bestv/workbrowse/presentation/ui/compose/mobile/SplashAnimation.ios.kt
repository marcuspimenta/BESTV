/*
 * Copyright (C) 2018 Marcus Pimenta
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 */

package com.pimenta.bestv.workbrowse.presentation.ui.compose.mobile

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.UIKitViewController
import kotlinx.cinterop.ExperimentalForeignApi
import com.pimenta.bestv.workbrowse.resources.Res as WorkBrowseRes
import platform.AVFoundation.AVPlayer
import platform.AVFoundation.AVPlayerItem
import platform.AVFoundation.AVPlayerItemDidPlayToEndTimeNotification
import platform.AVKit.AVPlayerViewController
import platform.Foundation.NSNotificationCenter
import platform.Foundation.NSOperationQueue
import platform.Foundation.NSSelectorFromString
import platform.Foundation.NSTimer
import platform.Foundation.NSURL

@OptIn(ExperimentalForeignApi::class)
@Composable
internal fun SplashAnimation(
    modifier: Modifier = Modifier,
    onCompleted: () -> Unit,
) {
    val currentOnCompleted = rememberUpdatedState(onCompleted)
    val videoUrl = remember {
        requireNotNull(NSURL.URLWithString(WorkBrowseRes.getUri(SPLASH_ANIMATION_RESOURCE)))
    }
    val playerItem = remember(videoUrl) { AVPlayerItem(uRL = videoUrl) }
    val player = remember(playerItem) { AVPlayer(playerItem = playerItem) }

    DisposableEffect(playerItem) {
        val notificationCenter = NSNotificationCenter.defaultCenter
        val completionObserver = notificationCenter.addObserverForName(
            name = AVPlayerItemDidPlayToEndTimeNotification,
            `object` = playerItem,
            queue = NSOperationQueue.mainQueue,
        ) {
            currentOnCompleted.value.invoke()
        }
        onDispose {
            notificationCenter.removeObserver(completionObserver)
        }
    }

    UIKitViewController(
        factory = {
            AVPlayerViewController().apply {
                this.player = player
                showsPlaybackControls = false
                NSTimer.scheduledTimerWithTimeInterval(
                    ti = 0.0,
                    target = player,
                    selector = NSSelectorFromString("play"),
                    userInfo = null,
                    repeats = false,
                )
            }
        },
        modifier = modifier,
        update = { controller ->
            controller.player = player
            controller.showsPlaybackControls = false
        },
        onRelease = { controller ->
            controller.player?.let { player ->
                NSTimer.scheduledTimerWithTimeInterval(
                    ti = 0.0,
                    target = player,
                    selector = NSSelectorFromString("pause"),
                    userInfo = null,
                    repeats = false,
                )
            }
            controller.player = null
        },
    )
}

private const val SPLASH_ANIMATION_RESOURCE = "files/splash_animation.mp4"
