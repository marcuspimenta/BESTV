package com.pimenta.bestv.workdetail.domain.repository

import com.pimenta.bestv.model.data.local.MovieDbModel
import com.pimenta.bestv.model.domain.PageDomainModel
import com.pimenta.bestv.model.domain.WorkDomainModel
import com.pimenta.bestv.workdetail.domain.model.ReviewDomainModel
import com.pimenta.bestv.workdetail.domain.model.VideoDomainModel
import com.pimenta.bestv.workdetail.domain.model.WatchProvidersDomainModel
import com.pimenta.bestv.workdetail.domain.model.WorkCreditsDomainModel
import com.pimenta.bestv.workdetail.domain.model.WorkMetadataDomainModel

interface MovieRepository {
    suspend fun getWorkMetadata(movieId: Int): WorkMetadataDomainModel

    suspend fun saveFavoriteMovie(movieDbModel: MovieDbModel)

    suspend fun deleteFavoriteMovie(movieDbModel: MovieDbModel)

    suspend fun isFavoriteMove(movieDbModel: MovieDbModel): Boolean

    suspend fun getCreditsByMovie(movieId: Int): WorkCreditsDomainModel

    suspend fun getRecommendationByMovie(movieId: Int, page: Int): PageDomainModel<WorkDomainModel>

    suspend fun getSimilarByMovie(movieId: Int, page: Int): PageDomainModel<WorkDomainModel>

    suspend fun getReviewByMovie(movieId: Int, page: Int): PageDomainModel<ReviewDomainModel>

    suspend fun getVideosByMovie(movieId: Int): List<VideoDomainModel>?

    suspend fun getWatchProvidersByMovie(movieId: Int, countryCode: String): WatchProvidersDomainModel?
}
