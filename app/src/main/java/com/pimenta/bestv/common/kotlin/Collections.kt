package com.pimenta.bestv.common.kotlin

/**
 * Returns `true` if this nullable collection is not either null or empty.
 */
fun <T> Collection<T>?.isNotNullOrEmpty(): Boolean =
        !this.isNullOrEmpty()