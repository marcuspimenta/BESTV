package com.pimenta.bestv.search.domain.repository

import com.pimenta.bestv.domain.model.PageDomainModel
import com.pimenta.bestv.domain.model.WorkDomainModel
import com.pimenta.bestv.domain.repository.BaseRepository

interface TvShowRepository : BaseRepository {
    suspend fun searchTvShowsByQuery(query: String, page: Int): PageDomainModel<WorkDomainModel>
}
