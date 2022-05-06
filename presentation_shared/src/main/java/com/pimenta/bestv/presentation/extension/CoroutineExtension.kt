package com.pimenta.bestv.presentation.extension

import kotlinx.coroutines.Job

fun Job.cancelIfActive() {
    if (isActive) {
        cancel()
    }
}
