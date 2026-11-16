package com.pimenta.bestv.recommendation.domain.repository

import com.pimenta.bestv.domain.model.WorkDomainModel
import com.pimenta.bestv.domain.repository.BaseRepository

interface RecommendationRepository : BaseRepository {
    suspend fun loadRecommendations(works: List<WorkDomainModel>?)
}
