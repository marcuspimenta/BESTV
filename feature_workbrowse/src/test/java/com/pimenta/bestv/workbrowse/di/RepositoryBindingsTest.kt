package com.pimenta.bestv.workbrowse.di

import android.app.Application
import com.pimenta.bestv.data.local.dao.MovieDao
import com.pimenta.bestv.data.local.dao.TvShowDao
import com.pimenta.bestv.presentation.platform.Resource
import com.pimenta.bestv.workbrowse.data.repository.GenreRepository as GenreRepositoryImpl
import com.pimenta.bestv.workbrowse.data.repository.MovieRepository as MovieRepositoryImpl
import com.pimenta.bestv.workbrowse.data.repository.TvShowRepository as TvShowRepositoryImpl
import com.pimenta.bestv.workbrowse.domain.GetBrowseDataUseCase
import com.pimenta.bestv.workbrowse.domain.ObserveFavoritesUseCase
import com.pimenta.bestv.workbrowse.domain.repository.GenreRepository
import com.pimenta.bestv.workbrowse.domain.repository.MovieRepository
import com.pimenta.bestv.workbrowse.domain.repository.TvShowRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
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
                single<CoroutineDispatcher> { Dispatchers.IO }
                single { Retrofit.Builder().baseUrl("https://example.test/").build() }
                single(named("tmdbApiKey")) { "test-key" }
                single(named("tmdbFilterLanguage")) { "en-US" }
                single { Resource(mock<Application>()) }
                single { mock<MovieDao>() }
                single { mock<TvShowDao>() }
            }
        val application = koinApplication { modules(workBrowseModule, infrastructure) }

        assertTrue(application.koin.get<MovieRepository>() is MovieRepositoryImpl)
        assertTrue(application.koin.get<TvShowRepository>() is TvShowRepositoryImpl)
        assertTrue(application.koin.get<GenreRepository>() is GenreRepositoryImpl)
        application.koin.get<GetBrowseDataUseCase>()
        application.koin.get<ObserveFavoritesUseCase>()

        application.close()
    }
}
