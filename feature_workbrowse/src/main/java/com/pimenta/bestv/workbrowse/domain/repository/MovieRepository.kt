package com.pimenta.bestv.workbrowse.domain.repository

import com.pimenta.bestv.domain.model.PageDomainModel
import com.pimenta.bestv.domain.model.WorkDomainModel
import kotlinx.coroutines.flow.Flow

interface MovieRepository {
    fun observeFavoriteMovies(): Flow<List<WorkDomainModel>>

    suspend fun getMoviesByGenre(genreId: Int, page: Int): PageDomainModel<WorkDomainModel>

    suspend fun getNowPlayingMovies(page: Int): PageDomainModel<WorkDomainModel>

    suspend fun getPopularMovies(page: Int): PageDomainModel<WorkDomainModel>

    suspend fun getTopRatedMovies(page: Int): PageDomainModel<WorkDomainModel>

    suspend fun getUpComingMovies(page: Int): PageDomainModel<WorkDomainModel>
}
