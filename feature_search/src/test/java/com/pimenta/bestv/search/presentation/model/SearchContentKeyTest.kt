package com.pimenta.bestv.search.presentation.model

import com.pimenta.bestv.presentation.model.PaginationState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test

class SearchContentKeyTest {
    @Test
    fun keysAreStableAcrossResultUpdatesAndUniqueByQueryAndCategory() {
        val movies = SearchState.Content.Movies("batman", emptyList(), PaginationState())
        val updatedMovies = SearchState.Content.Movies("batman", emptyList(), PaginationState())
        val tvShows = SearchState.Content.TvShows("batman", emptyList(), PaginationState())
        val otherQuery = SearchState.Content.Movies("superman", emptyList(), PaginationState())

        assertEquals(movies.lazyListKey(), updatedMovies.lazyListKey())
        assertNotEquals(movies.lazyListKey(), tvShows.lazyListKey())
        assertNotEquals(movies.lazyListKey(), otherQuery.lazyListKey())
        assertEquals("movies", movies.lazyContentType())
        assertEquals("tv-shows", tvShows.lazyContentType())
    }
}
