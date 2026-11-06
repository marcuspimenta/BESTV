package com.pimenta.bestv.workbrowse.domain

import com.pimenta.bestv.model.domain.WorkDomainModel
import com.pimenta.bestv.workbrowse.domain.repository.MovieRepository
import com.pimenta.bestv.workbrowse.domain.repository.TvShowRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

class ObserveFavoritesUseCase(
    private val movieRepository: MovieRepository,
    private val tvShowRepository: TvShowRepository,
) {
    operator fun invoke(): Flow<List<WorkDomainModel>> =
        combine(
            movieRepository.observeFavoriteMovies(),
            tvShowRepository.observeFavoriteTvShows(),
        ) { movies, tvShows -> movies + tvShows }
}
