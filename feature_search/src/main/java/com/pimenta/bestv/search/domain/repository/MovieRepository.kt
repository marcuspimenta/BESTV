package com.pimenta.bestv.search.domain.repository

import com.pimenta.bestv.domain.model.PageDomainModel
import com.pimenta.bestv.domain.model.WorkDomainModel

interface MovieRepository {
    suspend fun searchMoviesByQuery(query: String, page: Int): PageDomainModel<WorkDomainModel>
}
