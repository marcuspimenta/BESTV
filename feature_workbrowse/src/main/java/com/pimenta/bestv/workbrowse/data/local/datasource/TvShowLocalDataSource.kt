package com.pimenta.bestv.workbrowse.data.local.datasource

import com.pimenta.bestv.data.local.dao.TvShowDao
import com.pimenta.bestv.model.domain.WorkDomainModel
import com.pimenta.bestv.workbrowse.data.local.mapper.toDomainModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TvShowLocalDataSource(
    private val tvShowDao: TvShowDao,
) {
    fun observeFavoriteTvShows(): Flow<List<WorkDomainModel>> =
        tvShowDao.observeFavorites().map { tvShows -> tvShows.map { it.toDomainModel() } }
}
