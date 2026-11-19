package com.pimenta.bestv.workbrowse.data.repository

import com.pimenta.bestv.workbrowse.data.remote.datasource.GenreRemoteDataSource
import com.pimenta.bestv.workbrowse.data.remote.model.GenreListResponse
import com.pimenta.bestv.workbrowse.data.remote.model.GenreResponse
import com.pimenta.bestv.workbrowse.domain.model.GenreDomainModel
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

class GenreRepositoryTest {
    private val testDispatcher = StandardTestDispatcher()
    private val remoteDataSource: GenreRemoteDataSource = mock()
    private val repository = GenreRepository(remoteDataSource, testDispatcher)

    @Test
    fun `getMovieGenres maps the remote response`() = runTest(testDispatcher) {
        whenever(remoteDataSource.getMovieGenres()).thenReturn(
            GenreListResponse().apply { genres = listOf(GenreResponse(28, "Action")) },
        )

        assertEquals(
            listOf(GenreDomainModel(28, "Action", GenreDomainModel.Source.MOVIE)),
            repository.getMovieGenres(),
        )
    }

    @Test
    fun `getTvShowGenres maps the remote response`() = runTest(testDispatcher) {
        whenever(remoteDataSource.getTvShowGenres()).thenReturn(
            GenreListResponse().apply { genres = listOf(GenreResponse(10759, "Action & Adventure")) },
        )

        assertEquals(
            listOf(GenreDomainModel(10759, "Action & Adventure", GenreDomainModel.Source.TV_SHOW)),
            repository.getTvShowGenres(),
        )
    }
}
