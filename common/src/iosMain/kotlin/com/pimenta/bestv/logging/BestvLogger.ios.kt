package com.pimenta.bestv.logging

actual fun logError(throwable: Throwable, message: String) {
    println("$message: ${throwable.message}")
}
