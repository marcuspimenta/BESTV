package com.pimenta.bestv.workbrowse.data.local.datasource

import com.pimenta.bestv.data.local.dao.MovieDao
import com.pimenta.bestv.model.domain.WorkDomainModel
import com.pimenta.bestv.workbrowse.data.local.mapper.toDomainModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class MovieLocalDataSource(
    private val movieDao: MovieDao,
) {
    fun observeFavoriteMovies(): Flow<List<WorkDomainModel>> =
        movieDao.observeFavorites().map { movies -> movies.map { it.toDomainModel() } }
}
