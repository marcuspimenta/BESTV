package com.pimenta.bestv.workdetail.domain.repository

import com.pimenta.bestv.model.domain.PageDomainModel
import com.pimenta.bestv.model.domain.WorkDomainModel
import com.pimenta.bestv.workdetail.domain.model.ReviewDomainModel
import com.pimenta.bestv.workdetail.domain.model.VideoDomainModel
import com.pimenta.bestv.workdetail.domain.model.WatchProvidersDomainModel
import com.pimenta.bestv.workdetail.domain.model.WorkCreditsDomainModel
import com.pimenta.bestv.workdetail.domain.model.WorkMetadataDomainModel

interface TvShowRepository {
    suspend fun getWorkMetadata(tvShowId: Int): WorkMetadataDomainModel

    suspend fun saveFavoriteTvShow(tvShowId: Int)

    suspend fun deleteFavoriteTvShow(tvShowId: Int)

    suspend fun isFavoriteTvShow(tvShowId: Int): Boolean

    suspend fun getCreditsByTvShow(tvShowId: Int): WorkCreditsDomainModel

    suspend fun getRecommendationByTvShow(tvShowId: Int, page: Int): PageDomainModel<WorkDomainModel>

    suspend fun getSimilarByTvShow(tvShowId: Int, page: Int): PageDomainModel<WorkDomainModel>

    suspend fun getReviewByTvShow(tvShowId: Int, page: Int): PageDomainModel<ReviewDomainModel>

    suspend fun getVideosByTvShow(tvShowId: Int): List<VideoDomainModel>?

    suspend fun getWatchProvidersByTvShow(tvShowId: Int, countryCode: String): WatchProvidersDomainModel?
}
