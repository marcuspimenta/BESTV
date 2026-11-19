package com.pimenta.bestv.workbrowse.domain.repository

import com.pimenta.bestv.domain.repository.BaseRepository
import com.pimenta.bestv.workbrowse.domain.model.GenreDomainModel

interface GenreRepository : BaseRepository {
    suspend fun getMovieGenres(): List<GenreDomainModel>?

    suspend fun getTvShowGenres(): List<GenreDomainModel>?
}
