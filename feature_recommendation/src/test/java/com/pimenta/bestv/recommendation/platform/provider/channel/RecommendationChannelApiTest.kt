package com.pimenta.bestv.recommendation.platform.provider.channel

import android.app.Application
import android.content.ContentResolver
import android.content.ContentUris
import android.content.ContentValues
import android.content.Intent
import androidx.tvprovider.media.tv.TvContractCompat
import com.pimenta.bestv.model.domain.WorkDomainModel
import com.pimenta.bestv.presentation.R
import com.pimenta.bestv.presentation.platform.Resource
import com.pimenta.bestv.recommendation.data.local.sharedpreferences.LocalSettings
import com.pimenta.bestv.route.workbrowse.WorkBrowseRoute
import com.pimenta.bestv.route.workdetail.WorkDetailsRoute
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.any
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [26])
class RecommendationChannelApiTest {
    private val application: Application = mock()
    private val resolver: ContentResolver = mock()
    private val settings: LocalSettings = mock()
    private val workDetailsRoute: WorkDetailsRoute = mock()
    private val workBrowseRoute: WorkBrowseRoute = mock()
    private val resource = Resource(application)
    private val provider = RecommendationChannelApi(application, settings, workDetailsRoute, workBrowseRoute, resource)
    private val work = WorkDomainModel(
        id = 42,
        title = "Movie",
        originalTitle = "Movie",
        releaseDate = "2025-01-01",
        originalLanguage = "en",
        overview = "Overview",
        backdropPath = "/backdrop.jpg",
        posterPath = "/poster.jpg",
    )

    private fun stubWork() {
        whenever(application.contentResolver).thenReturn(resolver)
        whenever(application.getString(R.string.source_tmdb)).thenReturn("TMDb")
        whenever(settings.getLongFromPersistence("CHANNEL_ID_KEY", 0L)).thenReturn(12L)
        whenever(workDetailsRoute.buildWorkDetailIntent(any())).thenReturn(Intent())
    }

    @Test
    fun `repeated writes update the saved program id`() = runTest {
        stubWork()
        whenever(settings.getLongFromPersistence("42", 0L)).thenReturn(0L, 77L)
        whenever(resolver.insert(eq(TvContractCompat.PreviewPrograms.CONTENT_URI), any()))
            .thenReturn(ContentUris.withAppendedId(TvContractCompat.PreviewPrograms.CONTENT_URI, 77L))
        whenever(resolver.update(any(), any(), anyOrNull(), anyOrNull())).thenReturn(1)

        provider.loadRecommendations(listOf(work))
        provider.loadRecommendations(listOf(work))

        verify(settings).applyLongToPersistence("42", 77L)
        verify(resolver).update(
            eq(ContentUris.withAppendedId(TvContractCompat.PreviewPrograms.CONTENT_URI, 77L)),
            any<ContentValues>(),
            anyOrNull(),
            anyOrNull(),
        )
    }

    @Test
    fun `stale program id is reinserted and replaced`() = runTest {
        stubWork()
        whenever(settings.getLongFromPersistence("42", 0L)).thenReturn(77L)
        whenever(resolver.update(any(), any(), anyOrNull(), anyOrNull())).thenReturn(0)
        whenever(resolver.insert(eq(TvContractCompat.PreviewPrograms.CONTENT_URI), any()))
            .thenReturn(ContentUris.withAppendedId(TvContractCompat.PreviewPrograms.CONTENT_URI, 88L))

        provider.loadRecommendations(listOf(work))

        verify(settings).applyLongToPersistence("42", 88L)
    }
}
