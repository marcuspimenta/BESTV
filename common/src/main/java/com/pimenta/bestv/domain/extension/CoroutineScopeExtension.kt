package com.pimenta.bestv.domain.extension

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async

/** Captures ordinary exceptions from [load] while allowing coroutine cancellation to propagate. */
fun <T> CoroutineScope.asyncCatching(load: suspend () -> T) = async {
    try {
        Result.success(load())
    } catch (exception: CancellationException) {
        throw exception
    } catch (exception: Exception) {
        Result.failure(exception)
    }
}
