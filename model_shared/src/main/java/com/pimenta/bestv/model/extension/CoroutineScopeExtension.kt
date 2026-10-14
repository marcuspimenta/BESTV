package com.pimenta.bestv.model.extension

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async

fun <T> CoroutineScope.loadOrNull(load: suspend () -> T): Deferred<T?> =
    async { runCatching { load() }.getOrNull() }
