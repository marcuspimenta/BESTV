package com.pimenta.bestv.recommendation.domain.repository

import com.pimenta.bestv.model.domain.WorkDomainModel

interface RecommendationRepository {
    suspend fun loadRecommendations(works: List<WorkDomainModel>?)
}
