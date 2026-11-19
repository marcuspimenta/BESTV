package com.pimenta.bestv.logging

import timber.log.Timber

actual fun logError(throwable: Throwable, message: String) {
    Timber.e(throwable, message)
}
