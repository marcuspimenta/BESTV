package com.pimenta.bestv.castdetail.di

import android.app.Application
import com.pimenta.bestv.castdetail.data.repository.CastRepository as CastRepositoryImpl
import com.pimenta.bestv.castdetail.domain.repository.CastRepository
import com.pimenta.bestv.presentation.platform.Resource
import org.junit.Assert.assertTrue
import org.junit.Test
import org.koin.core.qualifier.named
import org.koin.dsl.koinApplication
import org.koin.dsl.module
import org.mockito.kotlin.mock
import retrofit2.Retrofit

class RepositoryBindingsTest {
    @Test
    fun `domain repository port resolves to data implementation`() {
        val infrastructure =
            module {
                single { Retrofit.Builder().baseUrl("https://example.test/").build() }
                single(named("tmdbApiKey")) { "test-key" }
                single(named("tmdbFilterLanguage")) { "en-US" }
                single { Resource(mock<Application>()) }
            }
        val application = koinApplication { modules(castDetailModule, infrastructure) }

        assertTrue(application.koin.get<CastRepository>() is CastRepositoryImpl)

        application.close()
    }
}
