package com.pimenta.bestv.workbrowse.presentation.mapper

import com.pimenta.bestv.model.domain.PageDomainModel
import com.pimenta.bestv.model.domain.WorkDomainModel
import com.pimenta.bestv.model.presentation.mapper.toViewModel
import com.pimenta.bestv.presentation.model.PaginationState
import com.pimenta.bestv.workbrowse.domain.model.BrowseCategoryKey
import com.pimenta.bestv.workbrowse.domain.model.BrowseSectionDomainModel
import com.pimenta.bestv.workbrowse.domain.model.GenreDomainModel
import com.pimenta.bestv.workbrowse.presentation.model.ContentSection
import com.pimenta.bestv.workbrowse.presentation.model.Source
import com.pimenta.bestv.workbrowse.presentation.model.TopWorkTypeViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

private fun browseWork(id: Int = 1) =
    WorkDomainModel(
        id = id,
        title = "Batman",
        originalTitle = "Batman",
        originalLanguage = "en",
        overview = "A superhero movie",
        source = "tmdb",
        backdropPath = "/backdrop.jpg",
        posterPath = "/poster.jpg",
        releaseDate = "2023-01-01",
        type = WorkDomainModel.Type.MOVIE,
    )

private fun browsePage(
    page: Int = 1,
    totalPages: Int = 1,
    works: List<WorkDomainModel>? = listOf(browseWork()),
) = PageDomainModel(page = page, totalPages = totalPages, results = works)

class BrowseSectionViewModelMapperTest {
    @Test
    fun `top section maps category works and pagination metadata`() {
        val domainSection =
            BrowseSectionDomainModel
                .TopContent(
                    category = BrowseCategoryKey.POPULAR_MOVIES,
                    page = browsePage(page = 3, totalPages = 9),
                )
        val section = domainSection.toViewModel()

        assertEquals(
            ContentSection.TopContent(
                type = TopWorkTypeViewModel.POPULAR_MOVIES,
                works = listOf(browseWork().toViewModel()!!),
                page = PaginationState(currentPage = 3, totalPages = 9),
            ),
            section,
        )
    }

    @Test
    fun `genre section maps domain source into the presentation source`() {
        val movieGenre = GenreDomainModel(id = 28, name = "Action", source = GenreDomainModel.Source.MOVIE)
        val tvGenre = GenreDomainModel(id = 10759, name = "Adventure", source = GenreDomainModel.Source.TV_SHOW)

        val movieSection =
            BrowseSectionDomainModel.GenreContent(movieGenre, browsePage()).toViewModel() as ContentSection.Genre
        val tvSection =
            BrowseSectionDomainModel.GenreContent(tvGenre, browsePage()).toViewModel() as ContentSection.Genre

        assertEquals(Source.MOVIE, movieSection.genreViewModel.source)
        assertEquals(Source.TV_SHOW, tvSection.genreViewModel.source)
        assertEquals("Action", movieSection.genreViewModel.name)
        assertEquals("Adventure", tvSection.genreViewModel.name)
    }

    @Test
    fun `list mapping preserves order and filters empty or unmappable sections`() {
        val sections =
            listOf(
                BrowseSectionDomainModel.TopContent(BrowseCategoryKey.NOW_PLAYING_MOVIES, browsePage()),
                BrowseSectionDomainModel.TopContent(BrowseCategoryKey.POPULAR_MOVIES, browsePage(works = emptyList())),
                BrowseSectionDomainModel.TopContent(
                    BrowseCategoryKey.TOP_RATED_MOVIES,
                    browsePage(works = listOf(WorkDomainModel(id = 3))),
                ),
                BrowseSectionDomainModel.TopContent(
                    BrowseCategoryKey.UP_COMING_MOVIES,
                    browsePage(works = listOf(browseWork(id = 4))),
                ),
            )

        val result = sections.toViewModels()

        assertEquals(2, result.size)
        assertEquals(
            listOf(TopWorkTypeViewModel.NOW_PLAYING_MOVIES, TopWorkTypeViewModel.UP_COMING_MOVIES),
            result.filterIsInstance<ContentSection.TopContent>().map { it.type },
        )
        assertTrue(result.all { it.works.isNotEmpty() })
    }
}
