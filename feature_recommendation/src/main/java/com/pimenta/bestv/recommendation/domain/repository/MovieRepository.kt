package com.pimenta.bestv.recommendation.domain.repository

import com.pimenta.bestv.domain.model.PageDomainModel
import com.pimenta.bestv.domain.model.WorkDomainModel

interface MovieRepository {
    suspend fun getPopularMovies(page: Int): PageDomainModel<WorkDomainModel>
}
