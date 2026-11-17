package com.pimenta.bestv.data.local.datasource

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.pimenta.bestv.data.local.database.MediaDatabase
import com.pimenta.bestv.data.local.model.MovieDbModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class MovieLocalDataSource(
    private val database: MediaDatabase,
) {
    private val queries get() = database.movieQueries

    fun observeFavorites(): Flow<List<MovieDbModel>> =
        queries.observeFavorites()
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { movies ->
                movies.map { movie ->
                    MovieDbModel(
                        id = movie.id.toInt(),
                        title = movie.title,
                        originalTitle = movie.originalTitle,
                        releaseDate = movie.releaseDate,
                        originalLanguage = movie.originalLanguage,
                        overview = movie.overview,
                        source = movie.source,
                        backdropPath = movie.backdropPath,
                        posterPath = movie.posterPath,
                        popularity = movie.popularity.toFloat(),
                        voteAverage = movie.voteAverage.toFloat(),
                        voteCount = movie.voteCount.toFloat(),
                        isAdult = movie.isAdult == 1L,
                        isFavorite = movie.isFavorite == 1L,
                    )
                }
            }

    suspend fun getById(movieId: Int): MovieDbModel? = withContext(Dispatchers.IO) {
        queries.selectById(id = movieId.toLong()).executeAsOneOrNull()?.let { movie ->
            MovieDbModel(
                id = movie.id.toInt(),
                title = movie.title,
                originalTitle = movie.originalTitle,
                releaseDate = movie.releaseDate,
                originalLanguage = movie.originalLanguage,
                overview = movie.overview,
                source = movie.source,
                backdropPath = movie.backdropPath,
                posterPath = movie.posterPath,
                popularity = movie.popularity.toFloat(),
                voteAverage = movie.voteAverage.toFloat(),
                voteCount = movie.voteCount.toFloat(),
                isAdult = movie.isAdult == 1L,
                isFavorite = movie.isFavorite == 1L,
            )
        }
    }

    suspend fun saveFavoriteMovie(movieId: Int) = withContext(Dispatchers.IO) {
        val model = MovieDbModel.favoritePlaceholder(movieId)
        queries.transaction {
            insertIfAbsent(model)
            queries.updateFavorite(isFavorite = 1L, id = movieId.toLong())
        }
    }

    suspend fun deleteFavoriteMovie(movieId: Int) {
        withContext(Dispatchers.IO) {
            queries.updateFavorite(isFavorite = 0L, id = movieId.toLong())
        }
    }

    suspend fun saveCachedWork(model: MovieDbModel) = withContext(Dispatchers.IO) {
        queries.transaction {
            val isFavorite = queries.selectById(id = model.id.toLong())
                .executeAsOneOrNull()
                ?.isFavorite
                ?.let { it == 1L }
                ?: model.isFavorite
            insertOrReplace(model.copy(isFavorite = isFavorite))
        }
    }

    private fun insertIfAbsent(model: MovieDbModel) {
        queries.insertIfAbsent(
            id = model.id.toLong(),
            title = model.title,
            originalTitle = model.originalTitle,
            releaseDate = model.releaseDate,
            originalLanguage = model.originalLanguage,
            overview = model.overview,
            source = model.source,
            backdropPath = model.backdropPath,
            posterPath = model.posterPath,
            popularity = model.popularity.toDouble(),
            voteAverage = model.voteAverage.toDouble(),
            voteCount = model.voteCount.toDouble(),
            isAdult = model.isAdult.asLong(),
            isFavorite = model.isFavorite.asLong(),
        )
    }

    private fun insertOrReplace(model: MovieDbModel) {
        queries.insertOrReplace(
            id = model.id.toLong(),
            title = model.title,
            originalTitle = model.originalTitle,
            releaseDate = model.releaseDate,
            originalLanguage = model.originalLanguage,
            overview = model.overview,
            source = model.source,
            backdropPath = model.backdropPath,
            posterPath = model.posterPath,
            popularity = model.popularity.toDouble(),
            voteAverage = model.voteAverage.toDouble(),
            voteCount = model.voteCount.toDouble(),
            isAdult = model.isAdult.asLong(),
            isFavorite = model.isFavorite.asLong(),
        )
    }
}

private fun Boolean.asLong() = if (this) 1L else 0L
