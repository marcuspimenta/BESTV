package com.pimenta.bestv.castdetail.presentation.ui.compose.tv

import androidx.compose.ui.test.assertIsFocused
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.pimenta.bestv.castdetail.presentation.model.CastDetailsState
import com.pimenta.bestv.model.presentation.model.CastViewModel
import com.pimenta.bestv.model.presentation.model.WorkType
import com.pimenta.bestv.model.presentation.model.WorkViewModel
import com.pimenta.bestv.presentation.theme.BESTVTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TVCastDetailsFocusTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun loadedCastScreenPlacesFocusOnFirstWorkCard() {
        val work = WorkViewModel(
            id = 1,
            originalLanguage = "en",
            overview = "A science fiction film.",
            source = "TMDb",
            backdropUrl = "",
            posterUrl = "",
            title = "The Matrix",
            originalTitle = "The Matrix",
            releaseDate = "1999",
            type = WorkType.MOVIE,
            voteAverage = 8.7f,
        )

        composeTestRule.setContent {
            BESTVTheme {
                TVCastDetailsContent(
                    state = CastDetailsState.Loaded(
                        cast = CastViewModel(
                            id = 1,
                            name = "Keanu Reeves",
                            character = "Neo",
                            birthday = "1964-09-02",
                            deathDay = "",
                            biography = "Actor",
                            thumbnailUrl = "",
                            source = "TMDb",
                        ),
                        movies = listOf(work),
                        tvShows = emptyList(),
                    ),
                    onWorkClick = {},
                    onRetryClicked = {},
                )
            }
        }

        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithContentDescription("The Matrix").assertIsFocused()
    }
}
