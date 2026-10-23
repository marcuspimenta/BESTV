package com.pimenta.bestv.recommendation.di

import android.app.Application
import com.pimenta.bestv.presentation.platform.Resource
import com.pimenta.bestv.recommendation.data.repository.MovieRepository as MovieRepositoryImpl
import com.pimenta.bestv.recommendation.domain.repository.MovieRepository
import com.pimenta.bestv.recommendation.domain.repository.RecommendationRepository
import com.pimenta.bestv.recommendation.platform.provider.channel.RecommendationChannelApi
import com.pimenta.bestv.route.workbrowse.WorkBrowseRoute
import com.pimenta.bestv.route.workdetail.WorkDetailsRoute
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.koinApplication
import org.koin.dsl.module
import org.mockito.kotlin.mock
import retrofit2.Retrofit
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [26])
class RepositoryBindingsTest {
    @Test
    fun `domain repository ports resolve to data implementations`() {
        val infrastructure =
            module {
                single { Retrofit.Builder().baseUrl("https://example.test/").build() }
                single(named("tmdbApiKey")) { "test-key" }
                single(named("tmdbFilterLanguage")) { "en-US" }
                single { Resource(mock<Application>()) }
                single<WorkDetailsRoute> { mock() }
                single<WorkBrowseRoute> { mock() }
            }
        val application = koinApplication {
            androidContext(RuntimeEnvironment.getApplication())
            allowOverride(true)
            modules(recommendationModule, infrastructure)
        }

        assertTrue(application.koin.get<MovieRepository>() is MovieRepositoryImpl)
        assertTrue(application.koin.get<RecommendationRepository>() is RecommendationChannelApi)

        application.close()
    }
}
