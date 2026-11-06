package com.pimenta.bestv.workbrowse.data.repository

import com.pimenta.bestv.model.data.remote.PageResponse
import com.pimenta.bestv.model.data.remote.TvShowResponse
import com.pimenta.bestv.model.domain.WorkDomainModel
import com.pimenta.bestv.workbrowse.data.local.datasource.TvShowLocalDataSource
import com.pimenta.bestv.workbrowse.data.remote.datasource.TvShowRemoteDataSource
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class TvShowRepositoryTest {
    private val tvShowLocalDataSource: TvShowLocalDataSource = mock()
    private val remoteDataSource: TvShowRemoteDataSource = mock()
    private val repository = TvShowRepository(tvShowLocalDataSource, remoteDataSource)

    @Test
    fun `category result is mapped from the remote source without catalog persistence`() = runTest {
        whenever(remoteDataSource.getPopularTvShows(1)).thenReturn(
            PageResponse(page = 1, totalPages = 4, results = emptyList()),
        )

        val page = repository.getPopularTvShows(1)

        assertEquals(1, page.page)
        assertEquals(emptyList<WorkDomainModel>(), page.results)
        verify(remoteDataSource).getPopularTvShows(1)
    }

    @Test
    fun `genre result is mapped and returned`() = runTest {
        whenever(remoteDataSource.getTvShowByGenre(10759, 1)).thenReturn(
            PageResponse(page = 1, totalPages = 1, results = listOf(TvShowResponse(title = "Show"))),
        )

        val page = repository.getTvShowByGenre(10759, 1)

        assertEquals("Show", page.results?.single()?.title)
        assertEquals(WorkDomainModel.Type.TV_SHOW, page.results?.single()?.type)
    }

    @Test
    fun `favorite changes are observed from the local data source`() = runTest {
        val favorites = listOf(WorkDomainModel(id = 5, type = WorkDomainModel.Type.TV_SHOW, isFavorite = true))
        whenever(tvShowLocalDataSource.observeFavoriteTvShows()).thenReturn(flowOf(favorites))

        assertEquals(favorites, repository.observeFavoriteTvShows().first())
    }
}
