package com.pimenta.bestv.data.local.datasource

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.pimenta.bestv.data.local.database.MediaDatabase
import com.pimenta.bestv.data.local.model.TvShowDbModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class TvShowLocalDataSource(
    private val database: MediaDatabase,
    private val ioDispatcher: CoroutineDispatcher,
) {
    private val queries get() = database.tvShowQueries

    fun observeFavorites(): Flow<List<TvShowDbModel>> =
        queries.observeFavorites()
            .asFlow()
            .mapToList(ioDispatcher)
            .map { tvShows ->
                tvShows.map { tvShow ->
                    TvShowDbModel(
                        id = tvShow.id.toInt(),
                        title = tvShow.title,
                        originalTitle = tvShow.originalTitle,
                        releaseDate = tvShow.releaseDate,
                        originalLanguage = tvShow.originalLanguage,
                        overview = tvShow.overview,
                        source = tvShow.source,
                        backdropPath = tvShow.backdropPath,
                        posterPath = tvShow.posterPath,
                        popularity = tvShow.popularity.toFloat(),
                        voteAverage = tvShow.voteAverage.toFloat(),
                        voteCount = tvShow.voteCount.toFloat(),
                        isAdult = tvShow.isAdult == 1L,
                        isFavorite = tvShow.isFavorite == 1L,
                    )
                }
            }

    suspend fun getById(tvShowId: Int): TvShowDbModel? = withContext(ioDispatcher) {
        queries.selectById(id = tvShowId.toLong()).executeAsOneOrNull()?.let { tvShow ->
            TvShowDbModel(
                id = tvShow.id.toInt(),
                title = tvShow.title,
                originalTitle = tvShow.originalTitle,
                releaseDate = tvShow.releaseDate,
                originalLanguage = tvShow.originalLanguage,
                overview = tvShow.overview,
                source = tvShow.source,
                backdropPath = tvShow.backdropPath,
                posterPath = tvShow.posterPath,
                popularity = tvShow.popularity.toFloat(),
                voteAverage = tvShow.voteAverage.toFloat(),
                voteCount = tvShow.voteCount.toFloat(),
                isAdult = tvShow.isAdult == 1L,
                isFavorite = tvShow.isFavorite == 1L,
            )
        }
    }

    suspend fun saveFavoriteTvShow(tvShowId: Int) = withContext(ioDispatcher) {
        val model = TvShowDbModel.favoritePlaceholder(tvShowId)
        queries.transaction {
            insertIfAbsent(model)
            queries.updateFavorite(isFavorite = 1L, id = tvShowId.toLong())
        }
    }

    suspend fun deleteFavoriteTvShow(tvShowId: Int) {
        withContext(ioDispatcher) {
            queries.updateFavorite(isFavorite = 0L, id = tvShowId.toLong())
        }
    }

    suspend fun saveCachedWork(model: TvShowDbModel) = withContext(ioDispatcher) {
        queries.transaction {
            val isFavorite = queries.selectById(id = model.id.toLong())
                .executeAsOneOrNull()
                ?.isFavorite
                ?.let { it == 1L }
                ?: model.isFavorite
            insertOrReplace(model.copy(isFavorite = isFavorite))
        }
    }

    private fun insertIfAbsent(model: TvShowDbModel) {
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

    private fun insertOrReplace(model: TvShowDbModel) {
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
