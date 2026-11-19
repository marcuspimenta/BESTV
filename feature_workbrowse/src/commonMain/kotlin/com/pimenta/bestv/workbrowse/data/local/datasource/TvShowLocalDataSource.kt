package com.pimenta.bestv.workbrowse.data.local.datasource

import com.pimenta.bestv.data.local.datasource.TvShowLocalDataSource as SqlDelightTvShowLocalDataSource
import com.pimenta.bestv.domain.model.WorkDomainModel
import com.pimenta.bestv.workbrowse.data.local.mapper.toDomainModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TvShowLocalDataSource(
    private val localDataSource: SqlDelightTvShowLocalDataSource,
) {
    fun observeFavoriteTvShows(): Flow<List<WorkDomainModel>> =
        localDataSource.observeFavorites().map { tvShows -> tvShows.map { it.toDomainModel() } }
}
