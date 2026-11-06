package com.pimenta.bestv.workbrowse.presentation.mapper

import com.pimenta.bestv.model.domain.ContentSource
import com.pimenta.bestv.model.domain.WorkDomainModel
import com.pimenta.bestv.presentation.mapper.toViewModel
import com.pimenta.bestv.presentation.platform.Resource
import com.pimenta.bestv.workbrowse.domain.model.BrowseCategoryKey
import com.pimenta.bestv.workbrowse.domain.model.BrowseSectionDomainModel
import com.pimenta.bestv.workbrowse.domain.model.GenreDomainModel
import com.pimenta.bestv.workbrowse.presentation.model.ContentSection
import com.pimenta.bestv.workbrowse.presentation.model.Source
import com.pimenta.bestv.workbrowse.presentation.model.TopWorkTypeViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

private fun browseWork(id: Int = 1) =
    WorkDomainModel(
        id = id,
        title = "Batman",
        originalTitle = "Batman",
        originalLanguage = "en",
        overview = "A superhero movie",
        source = ContentSource.TMDB,
        backdropPath = "/backdrop.jpg",
        posterPath = "/poster.jpg",
        releaseDate = "2023-01-01",
        type = WorkDomainModel.Type.MOVIE,
    )

class BrowseSectionViewModelMapperTest {
    private val resource: Resource = mock()

    init {
        whenever(resource.getStringResource(any())).thenReturn("tmdb")
    }

    @Test
    fun `top section maps category and first page works`() {
        val domainSection = BrowseSectionDomainModel.TopContent(
            category = BrowseCategoryKey.POPULAR_MOVIES,
            works = listOf(browseWork()),
        )

        val section = domainSection.toViewModel(resource)

        assertEquals(
            ContentSection.TopContent(
                type = TopWorkTypeViewModel.POPULAR_MOVIES,
                works = listOf(browseWork().toViewModel(resource)!!),
            ),
            section,
        )
    }

    @Test
    fun `genre section maps domain source into the presentation source`() {
        val movieGenre = GenreDomainModel(id = 28, name = "Action", source = GenreDomainModel.Source.MOVIE)
        val tvGenre = GenreDomainModel(id = 10759, name = "Adventure", source = GenreDomainModel.Source.TV_SHOW)

        val movieSection = BrowseSectionDomainModel
            .GenreContent(movieGenre, listOf(browseWork()))
            .toViewModel(resource) as ContentSection.Genre
        val tvSection = BrowseSectionDomainModel
            .GenreContent(tvGenre, listOf(browseWork()))
            .toViewModel(resource) as ContentSection.Genre

        assertEquals(Source.MOVIE, movieSection.genreViewModel.source)
        assertEquals(Source.TV_SHOW, tvSection.genreViewModel.source)
        assertEquals("Action", movieSection.genreViewModel.name)
        assertEquals("Adventure", tvSection.genreViewModel.name)
    }

    @Test
    fun `list mapping preserves order and filters empty or unmappable sections`() {
        val sections = listOf(
            BrowseSectionDomainModel.TopContent(BrowseCategoryKey.NOW_PLAYING_MOVIES, listOf(browseWork())),
            BrowseSectionDomainModel.TopContent(BrowseCategoryKey.POPULAR_MOVIES, emptyList()),
            BrowseSectionDomainModel.TopContent(BrowseCategoryKey.TOP_RATED_MOVIES, listOf(WorkDomainModel(id = 3))),
            BrowseSectionDomainModel.TopContent(BrowseCategoryKey.UP_COMING_MOVIES, listOf(browseWork(id = 4))),
        )

        val result = sections.toViewModels(resource)

        assertEquals(2, result.size)
        assertEquals(
            listOf(TopWorkTypeViewModel.NOW_PLAYING_MOVIES, TopWorkTypeViewModel.UP_COMING_MOVIES),
            result.filterIsInstance<ContentSection.TopContent>().map { it.type },
        )
        assertTrue(result.all { it.works.isNotEmpty() })
    }

    @Test
    fun `section mapping removes duplicate work IDs`() {
        val section = BrowseSectionDomainModel.TopContent(
            category = BrowseCategoryKey.POPULAR_MOVIES,
            works = listOf(browseWork(id = 7), browseWork(id = 7), browseWork(id = 8)),
        )

        val result = section.toViewModel(resource) as ContentSection.TopContent

        assertEquals(listOf(7, 8), result.works.map { it.id })
    }
}
