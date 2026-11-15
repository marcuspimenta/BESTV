package com.pimenta.bestv.workbrowse.domain.repository

import com.pimenta.bestv.domain.model.PageDomainModel
import com.pimenta.bestv.domain.model.WorkDomainModel
import kotlinx.coroutines.flow.Flow

interface TvShowRepository {
    fun observeFavoriteTvShows(): Flow<List<WorkDomainModel>>

    suspend fun getTvShowByGenre(genreId: Int, page: Int): PageDomainModel<WorkDomainModel>

    suspend fun getAiringTodayTvShows(page: Int): PageDomainModel<WorkDomainModel>

    suspend fun getOnTheAirTvShows(page: Int): PageDomainModel<WorkDomainModel>

    suspend fun getPopularTvShows(page: Int): PageDomainModel<WorkDomainModel>

    suspend fun getTopRatedTvShows(page: Int): PageDomainModel<WorkDomainModel>
}
