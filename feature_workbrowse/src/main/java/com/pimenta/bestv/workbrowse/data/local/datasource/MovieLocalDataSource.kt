package com.pimenta.bestv.workbrowse.data.local.datasource

import com.pimenta.bestv.data.local.datasource.MovieLocalDataSource as SqlDelightMovieLocalDataSource
import com.pimenta.bestv.domain.model.WorkDomainModel
import com.pimenta.bestv.workbrowse.data.local.mapper.toDomainModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class MovieLocalDataSource(
    private val localDataSource: SqlDelightMovieLocalDataSource,
) {
    fun observeFavoriteMovies(): Flow<List<WorkDomainModel>> =
        localDataSource.observeFavorites().map { movies -> movies.map { it.toDomainModel() } }
}
