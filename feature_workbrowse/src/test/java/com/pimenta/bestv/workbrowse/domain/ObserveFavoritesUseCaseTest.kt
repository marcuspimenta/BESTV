package com.pimenta.bestv.workbrowse.domain

import com.pimenta.bestv.model.domain.WorkDomainModel
import com.pimenta.bestv.workbrowse.domain.repository.MovieRepository
import com.pimenta.bestv.workbrowse.domain.repository.TvShowRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

class ObserveFavoritesUseCaseTest {
    private val movies: MovieRepository = mock()
    private val tvShows: TvShowRepository = mock()
    private val movieFavorites = MutableStateFlow(listOf(WorkDomainModel(id = 1, type = WorkDomainModel.Type.MOVIE)))
    private val tvShowFavorites = MutableStateFlow(listOf(WorkDomainModel(id = 2, type = WorkDomainModel.Type.TV_SHOW)))
    private val useCase = ObserveFavoritesUseCase(movies, tvShows)

    @Test
    fun `combines movie and TV favorites and emits local updates`() = runTest {
        whenever(movies.observeFavoriteMovies()).thenReturn(movieFavorites)
        whenever(tvShows.observeFavoriteTvShows()).thenReturn(tvShowFavorites)

        assertEquals(listOf(1, 2), useCase().first().map { it.id })

        movieFavorites.value = listOf(WorkDomainModel(id = 3, type = WorkDomainModel.Type.MOVIE))
        assertEquals(listOf(3, 2), useCase().first().map { it.id })
    }
}
