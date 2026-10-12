package com.pimenta.bestv.recommendation.domain.repository

import com.pimenta.bestv.model.domain.PageDomainModel
import com.pimenta.bestv.model.domain.WorkDomainModel

interface MovieRepository {
    suspend fun getPopularMovies(page: Int): PageDomainModel<WorkDomainModel>
}
