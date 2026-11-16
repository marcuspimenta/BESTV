package com.pimenta.bestv.recommendation.domain.repository

import com.pimenta.bestv.domain.model.PageDomainModel
import com.pimenta.bestv.domain.model.WorkDomainModel
import com.pimenta.bestv.domain.repository.BaseRepository

interface MovieRepository : BaseRepository {
    suspend fun getPopularMovies(page: Int): PageDomainModel<WorkDomainModel>
}
