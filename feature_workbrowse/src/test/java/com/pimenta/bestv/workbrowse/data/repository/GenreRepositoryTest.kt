package com.pimenta.bestv.workbrowse.data.repository

import com.pimenta.bestv.workbrowse.data.remote.datasource.GenreRemoteDataSource
import com.pimenta.bestv.workbrowse.data.remote.model.MovieGenreListResponse
import com.pimenta.bestv.workbrowse.data.remote.model.MovieGenreResponse
import com.pimenta.bestv.workbrowse.data.remote.model.TvShowGenreListResponse
import com.pimenta.bestv.workbrowse.data.remote.model.TvShowGenreResponse
import com.pimenta.bestv.workbrowse.domain.model.GenreDomainModel
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

class GenreRepositoryTest {
    private val remoteDataSource: GenreRemoteDataSource = mock()
    private val repository = GenreRepository(remoteDataSource)

    @Test
    fun `getMovieGenres maps the remote response`() = runTest {
        whenever(remoteDataSource.getMovieGenres()).thenReturn(
            MovieGenreListResponse().apply { genres = listOf(MovieGenreResponse(28, "Action")) },
        )

        assertEquals(
            listOf(GenreDomainModel(28, "Action", GenreDomainModel.Source.MOVIE)),
            repository.getMovieGenres(),
        )
    }

    @Test
    fun `getTvShowGenres maps the remote response`() = runTest {
        whenever(remoteDataSource.getTvShowGenres()).thenReturn(
            TvShowGenreListResponse().apply { genres = listOf(TvShowGenreResponse(10759, "Action & Adventure")) },
        )

        assertEquals(
            listOf(GenreDomainModel(10759, "Action & Adventure", GenreDomainModel.Source.TV_SHOW)),
            repository.getTvShowGenres(),
        )
    }
}
