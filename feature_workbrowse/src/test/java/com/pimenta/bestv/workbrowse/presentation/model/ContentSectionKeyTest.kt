package com.pimenta.bestv.workbrowse.presentation.model

import com.pimenta.bestv.presentation.model.PaginationState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test

class ContentSectionKeyTest {
    @Test
    fun keysStayStableWhenWorksUpdateAndGenresRemainDistinct() {
        val page = PaginationState()
        val firstGenre = ContentSection.Genre(GenreViewModel(id = 1), emptyList(), page)
        val updatedFirstGenre = ContentSection.Genre(GenreViewModel(id = 1), emptyList(), page)
        val tvGenre = ContentSection.Genre(
            GenreViewModel(id = 1, source = Source.TV_SHOW),
            emptyList(),
            page,
        )
        val popular = ContentSection.TopContent(TopWorkTypeViewModel.POPULAR_MOVIES, emptyList(), page)

        assertEquals(firstGenre.lazyListKey(), updatedFirstGenre.lazyListKey())
        assertNotEquals(firstGenre.lazyListKey(), tvGenre.lazyListKey())
        assertNotEquals(firstGenre.lazyListKey(), popular.lazyListKey())
        assertEquals("genre", firstGenre.lazyContentType())
        assertEquals("top-content", popular.lazyContentType())
    }
}
