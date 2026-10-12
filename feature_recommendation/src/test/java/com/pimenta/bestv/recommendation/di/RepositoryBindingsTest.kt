package com.pimenta.bestv.recommendation.di

import android.app.Application
import com.pimenta.bestv.presentation.platform.Resource
import com.pimenta.bestv.recommendation.data.local.provider.RecommendationProvider
import com.pimenta.bestv.recommendation.data.repository.MovieRepository as MovieRepositoryImpl
import com.pimenta.bestv.recommendation.data.repository.RecommendationRepository as RecommendationRepositoryImpl
import com.pimenta.bestv.recommendation.domain.repository.MovieRepository
import com.pimenta.bestv.recommendation.domain.repository.RecommendationRepository
import org.junit.Assert.assertTrue
import org.junit.Test
import org.koin.core.qualifier.named
import org.koin.dsl.koinApplication
import org.koin.dsl.module
import org.mockito.kotlin.mock
import retrofit2.Retrofit

class RepositoryBindingsTest {
    @Test
    fun `domain repository ports resolve to data implementations`() {
        val infrastructure =
            module {
                single { Retrofit.Builder().baseUrl("https://example.test/").build() }
                single(named("tmdbApiKey")) { "test-key" }
                single(named("tmdbFilterLanguage")) { "en-US" }
                single { Resource(mock<Application>()) }
                single<RecommendationProvider> { mock() }
            }
        val application = koinApplication {
            allowOverride(true)
            modules(recommendationModule, infrastructure)
        }

        assertTrue(application.koin.get<MovieRepository>() is MovieRepositoryImpl)
        assertTrue(application.koin.get<RecommendationRepository>() is RecommendationRepositoryImpl)

        application.close()
    }
}
