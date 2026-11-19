package com.pimenta.bestv.data.remote.api

import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import java.io.File
import java.util.concurrent.TimeUnit
import okhttp3.Cache

private const val HTTP_CACHE_SIZE_BYTES = 20L * 1024 * 1024

fun createAndroidTmdbHttpClient(
    config: TmdbHttpClientConfig,
    cacheDirectory: File,
): HttpClient = HttpClient(OkHttp) {
    configureTmdbClient(config)
    engine {
        config {
            cache(Cache(cacheDirectory, HTTP_CACHE_SIZE_BYTES))
            readTimeout(60, TimeUnit.SECONDS)
            writeTimeout(60, TimeUnit.SECONDS)
            connectTimeout(60, TimeUnit.SECONDS)
        }
    }
}
