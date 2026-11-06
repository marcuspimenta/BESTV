package com.pimenta.bestv.workbrowse.data.local.datasource

import com.pimenta.bestv.data.local.dao.MovieDao
import com.pimenta.bestv.data.local.model.MovieDbModel
import com.pimenta.bestv.model.domain.WorkDomainModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

class MovieLocalDataSourceTest {
    private val movieDao: MovieDao = mock()
    private val dataSource = MovieLocalDataSource(movieDao)

    @Test
    fun `observeFavoriteMovies maps only favorite MovieDao rows`() = runTest {
        whenever(movieDao.observeFavorites()).thenReturn(
            flowOf(listOf(movie(id = 10, title = "Favorite movie", isFavorite = true))),
        )

        val work = dataSource.observeFavoriteMovies().first().single()

        assertEquals(10, work.id)
        assertEquals("Favorite movie", work.title)
        assertEquals(WorkDomainModel.Type.MOVIE, work.type)
        assertEquals(true, work.isFavorite)
    }

    private fun movie(
        id: Int,
        title: String?,
        isFavorite: Boolean = false,
    ) = MovieDbModel(
        id = id,
        title = title,
        originalTitle = null,
        releaseDate = null,
        originalLanguage = null,
        overview = null,
        source = "TMDB",
        backdropPath = null,
        posterPath = null,
        popularity = 0f,
        voteAverage = 0f,
        voteCount = 0f,
        isAdult = false,
        isFavorite = isFavorite,
    )
}
