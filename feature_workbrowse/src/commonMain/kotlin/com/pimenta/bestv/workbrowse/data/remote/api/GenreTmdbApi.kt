package com.pimenta.bestv.workbrowse.data.remote.api

import com.pimenta.bestv.data.remote.api.getTmdb
import com.pimenta.bestv.workbrowse.data.remote.model.GenreListResponse
import io.ktor.client.HttpClient
import io.ktor.client.request.parameter

class GenreTmdbApi(private val client: HttpClient) {
    suspend fun getMovieGenres(language: String): GenreListResponse =
        client.getTmdb("genre/movie/list") {
            parameter("language", language)
        }

    suspend fun getTvShowGenres(language: String): GenreListResponse =
        client.getTmdb("genre/tv/list") {
            parameter("language", language)
        }
}
