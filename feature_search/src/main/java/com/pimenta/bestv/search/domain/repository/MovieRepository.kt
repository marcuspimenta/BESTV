package com.pimenta.bestv.search.domain.repository

import com.pimenta.bestv.model.domain.PageDomainModel
import com.pimenta.bestv.model.domain.WorkDomainModel

interface MovieRepository {
    suspend fun searchMoviesByQuery(query: String, page: Int): PageDomainModel<WorkDomainModel>
}
