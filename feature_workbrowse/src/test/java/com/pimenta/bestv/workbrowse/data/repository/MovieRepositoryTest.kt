package com.pimenta.bestv.workbrowse.data.repository

import com.pimenta.bestv.model.data.remote.MovieResponse
import com.pimenta.bestv.model.data.remote.PageResponse
import com.pimenta.bestv.model.domain.WorkDomainModel
import com.pimenta.bestv.workbrowse.data.local.datasource.MovieLocalDataSource
import com.pimenta.bestv.workbrowse.data.remote.datasource.MovieRemoteDataSource
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class MovieRepositoryTest {
    private val testDispatcher = StandardTestDispatcher()
    private val movieLocalDataSource: MovieLocalDataSource = mock()
    private val remoteDataSource: MovieRemoteDataSource = mock()
    private val repository = MovieRepository(movieLocalDataSource, remoteDataSource, testDispatcher)

    @Test
    fun `category result is mapped from the remote source without catalog persistence`() = runTest(testDispatcher) {
        whenever(remoteDataSource.getPopularMovies(1)).thenReturn(
            PageResponse(page = 1, totalPages = 4, results = emptyList()),
        )

        val page = repository.getPopularMovies(1)

        assertEquals(1, page.page)
        assertEquals(emptyList<WorkDomainModel>(), page.results)
        verify(remoteDataSource).getPopularMovies(1)
    }

    @Test
    fun `genre result is mapped and returned`() = runTest(testDispatcher) {
        whenever(remoteDataSource.getMoviesByGenre(28, 1)).thenReturn(
            PageResponse(page = 1, totalPages = 1, results = listOf(MovieResponse(title = "Movie"))),
        )

        val page = repository.getMoviesByGenre(28, 1)

        assertEquals("Movie", page.results?.single()?.title)
        assertEquals(WorkDomainModel.Type.MOVIE, page.results?.single()?.type)
    }

    @Test
    fun `favorite changes are observed from the local data source`() = runTest(testDispatcher) {
        val favorites = listOf(WorkDomainModel(id = 5, type = WorkDomainModel.Type.MOVIE, isFavorite = true))
        whenever(movieLocalDataSource.observeFavoriteMovies()).thenReturn(flowOf(favorites))

        assertEquals(favorites, repository.observeFavoriteMovies().first())
    }
}
