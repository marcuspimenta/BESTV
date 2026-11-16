package com.pimenta.bestv.castdetail.domain.repository

import com.pimenta.bestv.domain.model.CastDomainModel
import com.pimenta.bestv.domain.model.WorkDomainModel
import com.pimenta.bestv.domain.repository.BaseRepository

interface CastRepository : BaseRepository {
    suspend fun getCastDetails(castId: Int): CastDomainModel

    suspend fun getMovieCreditsByCast(castId: Int): List<WorkDomainModel>?

    suspend fun getTvShowCreditsByCast(castId: Int): List<WorkDomainModel>?
}
