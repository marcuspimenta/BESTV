package com.pimenta.bestv.domain.extension

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async

fun <T> CoroutineScope.asyncCatching(load: suspend () -> T) = async {
    runCatching { load() }
}
