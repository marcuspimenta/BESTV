/*
 * Copyright (C) 2018 Marcus Pimenta
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 */

package com.pimenta.bestv.model.presentation.mapper

object TmdbImageUrlBuilder {
    private const val BASE_URL = "https://image.tmdb.org/t/p"

    fun poster(path: String): String = buildUrl("w500", path)

    fun backdropCard(path: String): String = buildUrl("w780", path)

    fun backdrop(path: String): String = buildUrl("w1280", path)

    fun profile(path: String): String = buildUrl("h632", path)

    fun providerLogo(path: String): String = buildUrl("w92", path)

    private fun buildUrl(size: String, path: String): String =
        "$BASE_URL/$size/${path.removePrefix("/")}"
}
