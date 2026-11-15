package com.pimenta.bestv.recommendation.domain.repository

import com.pimenta.bestv.domain.model.WorkDomainModel

interface RecommendationRepository {
    suspend fun loadRecommendations(works: List<WorkDomainModel>?)
}
