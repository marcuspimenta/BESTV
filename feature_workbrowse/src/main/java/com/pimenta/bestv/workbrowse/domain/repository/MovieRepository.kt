package com.pimenta.bestv.workbrowse.domain.repository

import com.pimenta.bestv.model.domain.PageDomainModel
import com.pimenta.bestv.model.domain.WorkDomainModel

interface MovieRepository {
    suspend fun getFavoriteMovies(): List<WorkDomainModel>

    suspend fun getMoviesByGenre(genreId: Int, page: Int): PageDomainModel<WorkDomainModel>

    suspend fun getNowPlayingMovies(page: Int): PageDomainModel<WorkDomainModel>

    suspend fun getPopularMovies(page: Int): PageDomainModel<WorkDomainModel>

    suspend fun getTopRatedMovies(page: Int): PageDomainModel<WorkDomainModel>

    suspend fun getUpComingMovies(page: Int): PageDomainModel<WorkDomainModel>
}
