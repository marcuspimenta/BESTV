package com.pimenta.bestv.presentation.extension

@Suppress("UNCHECKED_CAST")
inline fun <reified T> List<*>.replaceFirst(transform: (T) -> T) =
    toMutableList().apply {
        val current = firstOf<T>()
        val indexOfCurrent = indexOf(current)
        removeAt(indexOfCurrent)
        add(indexOfCurrent, transform(current))
    } as List<T>

inline fun <reified T> Iterable<*>.firstOf(): T = first { it is T } as T
