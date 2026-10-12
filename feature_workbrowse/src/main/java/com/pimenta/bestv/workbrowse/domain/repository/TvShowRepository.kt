package com.pimenta.bestv.workbrowse.domain.repository

import com.pimenta.bestv.model.domain.PageDomainModel
import com.pimenta.bestv.model.domain.WorkDomainModel

interface TvShowRepository {
    suspend fun getFavoriteTvShows(): List<WorkDomainModel>

    suspend fun getTvShowByGenre(genreId: Int, page: Int): PageDomainModel<WorkDomainModel>

    suspend fun getAiringTodayTvShows(page: Int): PageDomainModel<WorkDomainModel>

    suspend fun getOnTheAirTvShows(page: Int): PageDomainModel<WorkDomainModel>

    suspend fun getPopularTvShows(page: Int): PageDomainModel<WorkDomainModel>

    suspend fun getTopRatedTvShows(page: Int): PageDomainModel<WorkDomainModel>
}
