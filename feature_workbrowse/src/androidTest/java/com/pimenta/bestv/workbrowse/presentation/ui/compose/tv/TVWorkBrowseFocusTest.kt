package com.pimenta.bestv.workbrowse.presentation.ui.compose.tv

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsFocused
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performKeyInput
import androidx.compose.ui.test.pressKey
import androidx.compose.ui.input.key.Key
import androidx.compose.runtime.mutableStateOf
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.pimenta.bestv.model.presentation.model.WorkType
import com.pimenta.bestv.model.presentation.model.WorkViewModel
import com.pimenta.bestv.presentation.model.PaginationState
import com.pimenta.bestv.presentation.theme.BESTVTheme
import com.pimenta.bestv.workbrowse.presentation.model.ContentSection
import com.pimenta.bestv.workbrowse.presentation.model.TopWorkTypeViewModel.POPULAR_MOVIES
import com.pimenta.bestv.workbrowse.presentation.model.WorkBrowseState
import com.pimenta.bestv.workbrowse.presentation.model.WorkBrowseState.Section.Favorites
import com.pimenta.bestv.workbrowse.presentation.model.WorkBrowseState.Section.Movies
import com.pimenta.bestv.workbrowse.presentation.model.WorkBrowseState.Section.Search
import com.pimenta.bestv.workbrowse.presentation.model.WorkBrowseState.State.Loaded
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TVWorkBrowseFocusTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun loadedBrowseScreenPlacesFocusOnFirstWorkCard() {
        val works = testWorks()

        composeTestRule.setContent {
            BESTVTheme {
                TVWorkBrowseContent(
                    state = browseState(works),
                    onBackClicked = {},
                    onSplashAnimationFinished = {},
                    onSectionClicked = {},
                    onWorkSelected = {},
                    onWorkClicked = {},
                    onLoadMore = {},
                    onRetryClicked = {},
                )
            }
        }

        composeTestRule.waitUntil(timeoutMillis = 5_000) {
            composeTestRule.onAllNodesWithContentDescription("The Matrix").fetchSemanticsNodes().isNotEmpty()
        }

        composeTestRule.onNodeWithContentDescription("The Matrix").assertIsFocused()
        composeTestRule.onNodeWithContentDescription("The Matrix").performKeyInput {
            pressKey(Key.DirectionRight)
        }
        composeTestRule.onNodeWithContentDescription("Arrival").assertIsFocused()
    }

    @Test
    fun movingBetweenDrawerAndBrowseContentRestoresFocusedWork() {
        composeTestRule.setContent {
            BESTVTheme {
                TVWorkBrowseContent(
                    state = browseState(testWorks()),
                    onBackClicked = {},
                    onSplashAnimationFinished = {},
                    onSectionClicked = {},
                    onWorkSelected = {},
                    onWorkClicked = {},
                    onLoadMore = {},
                    onRetryClicked = {},
                )
            }
        }

        composeTestRule.waitUntil(timeoutMillis = 5_000) {
            composeTestRule.onAllNodesWithContentDescription("The Matrix").fetchSemanticsNodes().isNotEmpty()
        }
        composeTestRule.onNodeWithContentDescription("The Matrix").assertIsFocused()
        composeTestRule.onNodeWithContentDescription("The Matrix").performKeyInput {
            pressKey(Key.DirectionLeft)
        }
        composeTestRule.onNodeWithText("Movies").assertIsFocused()
        composeTestRule.onNodeWithText("Movies").performKeyInput {
            pressKey(Key.DirectionRight)
        }
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithContentDescription("The Matrix").assertIsFocused()
    }

    @Test
    fun returningToBrowseFocusesPreviouslySelectedWork() {
        val works = testWorks()

        composeTestRule.setContent {
            BESTVTheme {
                TVWorkBrowseContent(
                    state = browseState(works = works, workSelected = works.last()),
                    onBackClicked = {},
                    onSplashAnimationFinished = {},
                    onSectionClicked = {},
                    onWorkSelected = {},
                    onWorkClicked = {},
                    onLoadMore = {},
                    onRetryClicked = {},
                )
            }
        }

        composeTestRule.waitUntil(timeoutMillis = 5_000) {
            composeTestRule.onAllNodesWithContentDescription("Arrival").fetchSemanticsNodes().isNotEmpty()
        }

        composeTestRule.onNodeWithContentDescription("Arrival").assertIsFocused()
    }

    @Test
    fun browseSectionsKeepIndependentHorizontalScrollPositions() {
        val movies = List(12) { index ->
            testWorks().first().copy(
                id = index + 1,
                title = "Movie $index",
                originalTitle = "Movie $index",
            )
        }
        val favorites = movies.mapIndexed { index, work ->
            work.copy(
                id = index + 101,
                title = "Favorite $index",
                originalTitle = "Favorite $index",
            )
        }
        val state = mutableStateOf(browseState(works = movies, favoriteWorks = favorites))

        composeTestRule.setContent {
            BESTVTheme {
                TVWorkBrowseContent(
                    state = state.value,
                    onBackClicked = {},
                    onSplashAnimationFinished = {},
                    onSectionClicked = { index ->
                        val loaded = state.value.state as Loaded
                        state.value = state.value.copy(
                            state = loaded.copy(selectedSectionIndex = index),
                        )
                    },
                    onWorkSelected = { work ->
                        val loaded = state.value.state as Loaded
                        state.value = state.value.copy(
                            state = loaded.copy(workSelected = work),
                        )
                    },
                    onWorkClicked = {},
                    onLoadMore = {},
                    onRetryClicked = {},
                )
            }
        }

        composeTestRule.waitUntil(timeoutMillis = 5_000) {
            composeTestRule.onAllNodesWithContentDescription("Movie 0").fetchSemanticsNodes().isNotEmpty()
        }
        composeTestRule.onNodeWithContentDescription("Movie 0").assertIsFocused()
        for (index in 1..8) {
            composeTestRule.onNodeWithContentDescription("Movie ${index - 1}").performKeyInput {
                pressKey(Key.DirectionRight)
            }
            composeTestRule.onNodeWithContentDescription("Movie $index").assertIsFocused()
        }

        composeTestRule.runOnIdle { selectSection(state, 2) }
        composeTestRule.waitUntil(timeoutMillis = 5_000) {
            composeTestRule.onAllNodesWithContentDescription("Favorite 0").fetchSemanticsNodes().isNotEmpty()
        }
        composeTestRule.runOnIdle { selectSection(state, 1) }
        composeTestRule.waitUntil(timeoutMillis = 5_000) {
            composeTestRule.onAllNodesWithContentDescription("Movie 8").fetchSemanticsNodes().isNotEmpty()
        }

        composeTestRule.onNodeWithContentDescription("Movie 8").assertIsDisplayed()
    }

    private fun testWorks(): List<WorkViewModel> {
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
        return listOf(
            work,
            work.copy(
                id = 2,
                title = "Arrival",
                originalTitle = "Arrival",
            ),
        )
    }

    private fun browseState(
        works: List<WorkViewModel>,
        workSelected: WorkViewModel = works.first(),
        favoriteWorks: List<WorkViewModel> = works,
    ) = WorkBrowseState(
        state = Loaded(
            workSelected = workSelected,
            selectedSectionIndex = 1,
            sections = listOf(
                Search,
                Movies(listOf(testContent(works))),
                Favorites(listOf(testContent(favoriteWorks))),
            ),
        ),
    )

    private fun testContent(works: List<WorkViewModel>) = ContentSection.TopContent(
        type = POPULAR_MOVIES,
        works = works,
        page = PaginationState(currentPage = 1, totalPages = 1),
    )

    private fun selectSection(state: androidx.compose.runtime.MutableState<WorkBrowseState>, index: Int) {
        val loaded = state.value.state as Loaded
        state.value = state.value.copy(state = loaded.copy(selectedSectionIndex = index))
    }
}
