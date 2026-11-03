package com.pimenta.bestv.workdetail.presentation.ui.compose.tv

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsFocused
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performKeyInput
import androidx.compose.ui.test.performSemanticsAction
import androidx.compose.ui.test.pressKey
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.input.key.Key
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.pimenta.bestv.model.presentation.model.WorkType
import com.pimenta.bestv.model.presentation.model.WorkViewModel
import com.pimenta.bestv.presentation.theme.BESTVTheme
import com.pimenta.bestv.workdetail.presentation.model.ReviewViewModel
import com.pimenta.bestv.workdetail.presentation.model.WorkDetailsState
import com.pimenta.bestv.workdetail.presentation.model.WorkDetailsState.ActionButton.SaveWork
import com.pimenta.bestv.workdetail.presentation.model.WorkDetailsState.Content.Header
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TVWorkDetailsFocusAndReviewTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun workDetailsInitiallyFocusesPrimaryAction() {
        val work = testWork()

        composeTestRule.setContent {
            BESTVTheme {
                TVLoadedWorkDetails(
                    work = work,
                    loadedState = WorkDetailsState.State.Loaded(
                        indexOfContentToScroll = null,
                        contents = listOf(
                            Header(
                                actions = listOf(SaveWork(isFavorite = false)),
                                watchProviders = null,
                                metadata = null,
                                crew = emptyList(),
                            ),
                        ),
                        error = null,
                    ),
                    actionClicked = {},
                    onVideoClick = {},
                    onCastClick = {},
                    onWorkClick = {},
                    onLoadMoreReviews = {},
                    onLoadMoreRecommendations = {},
                    onLoadMoreSimilar = {},
                    onDismissError = {},
                    onClearScrollIndex = {},
                )
            }
        }

        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Save").assertIsFocused()
    }

    @Test
    fun selectingReviewCardExpandsItsText() {
        val review = ReviewViewModel(
            id = "review-1",
            author = "A reviewer",
            content = "A thoughtful review of the film. ".repeat(40),
        )

        composeTestRule.setContent {
            BESTVTheme {
                TVReviewRow(
                    reviews = listOf(review),
                    onLoadMore = {},
                )
            }
        }

        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Read more").assertIsDisplayed()
        val reviewCard = composeTestRule.onNode(hasClickAction())
        reviewCard.performSemanticsAction(SemanticsActions.RequestFocus)
        reviewCard.assertIsFocused()
        reviewCard.performKeyInput { pressKey(Key.DirectionCenter) }
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Show less").assertIsDisplayed()
    }

    private fun testWork() = WorkViewModel(
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
}
