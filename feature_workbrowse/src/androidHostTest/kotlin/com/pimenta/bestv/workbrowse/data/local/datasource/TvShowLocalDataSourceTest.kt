package com.pimenta.bestv.workbrowse.data.local.datasource

import com.pimenta.bestv.data.local.datasource.TvShowLocalDataSource as SqlDelightTvShowLocalDataSource
import com.pimenta.bestv.data.local.model.TvShowDbModel
import com.pimenta.bestv.domain.model.WorkDomainModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

class TvShowLocalDataSourceTest {
    private val sqlDelightDataSource: SqlDelightTvShowLocalDataSource = mock()
    private val dataSource = TvShowLocalDataSource(sqlDelightDataSource)

    @Test
    fun `observeFavoriteTvShows maps only favorite database rows`() = runTest {
        whenever(sqlDelightDataSource.observeFavorites()).thenReturn(
            flowOf(listOf(tvShow(id = 22, title = "Favorite show", isFavorite = true))),
        )

        val work = dataSource.observeFavoriteTvShows().first().single()

        assertEquals(22, work.id)
        assertEquals("Favorite show", work.title)
        assertEquals(WorkDomainModel.Type.TV_SHOW, work.type)
        assertEquals(true, work.isFavorite)
    }

    private fun tvShow(
        id: Int,
        title: String?,
        isFavorite: Boolean = false,
    ) = TvShowDbModel(
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
