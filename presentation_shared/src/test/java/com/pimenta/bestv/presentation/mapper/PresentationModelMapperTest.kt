package com.pimenta.bestv.presentation.mapper

import android.app.Application
import com.pimenta.bestv.model.domain.CastDomainModel
import com.pimenta.bestv.model.domain.WorkDomainModel
import com.pimenta.bestv.presentation.R
import com.pimenta.bestv.presentation.platform.Resource
import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

class PresentationModelMapperTest {
    private val application: Application = mock()
    private val resource = Resource(application)

    @Test
    fun `work mapper resolves source label and image sizes`() {
        whenever(application.getString(R.string.source_tmdb)).thenReturn("TMDb")
        val work = WorkDomainModel(
            id = 1,
            title = "Movie",
            originalTitle = "Movie",
            releaseDate = "2025-01-01",
            originalLanguage = "en",
            overview = "Overview",
            backdropPath = "/backdrop.jpg",
            posterPath = "/poster.jpg",
        ).toViewModel(resource)

        assertEquals("TMDb", work?.source)
        assertEquals("https://image.tmdb.org/t/p/w1280/backdrop.jpg", work?.backdropUrl)
        assertEquals("https://image.tmdb.org/t/p/w780/backdrop.jpg", work?.backdropCardUrl)
        assertEquals("https://image.tmdb.org/t/p/w500/poster.jpg", work?.posterUrl)
    }

    @Test
    fun `cast mapper resolves source label and profile image`() {
        whenever(application.getString(R.string.source_tmdb)).thenReturn("TMDb")
        val cast = CastDomainModel(id = 1, name = "Actor", profilePath = "/profile.jpg").toViewModel(resource)

        assertEquals("TMDb", cast?.source)
        assertEquals("https://image.tmdb.org/t/p/h632/profile.jpg", cast?.thumbnailUrl)
    }
}
