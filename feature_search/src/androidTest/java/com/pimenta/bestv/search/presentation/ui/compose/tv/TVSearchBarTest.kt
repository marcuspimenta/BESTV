package com.pimenta.bestv.search.presentation.ui.compose.tv

import androidx.compose.ui.test.assertIsFocused
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.performImeAction
import androidx.compose.ui.test.performKeyInput
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.pressKey
import androidx.compose.ui.input.key.Key
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.pimenta.bestv.presentation.theme.BESTVTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TVSearchBarTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun searchFieldReceivesInitialFocus() {
        composeTestRule.setContent {
            BESTVTheme {
                TVSearchBar(
                    query = "",
                    onQueryChange = {},
                    onQuerySubmit = {},
                    onClear = {},
                    placeholder = "Search",
                )
            }
        }

        composeTestRule.waitForIdle()

        composeTestRule.onNode(hasSetTextAction()).assertIsFocused()
    }

    @Test
    fun searchActionSubmitsCurrentQuery() {
        var submittedQuery = ""

        composeTestRule.setContent {
            BESTVTheme {
                TVSearchBar(
                    query = "The Matrix",
                    onQueryChange = {},
                    onQuerySubmit = { submittedQuery = it },
                    onClear = {},
                    placeholder = "Search",
                )
            }
        }

        composeTestRule.onNode(hasSetTextAction()).performImeAction()

        assertEquals("The Matrix", submittedQuery)
    }

    @Test
    fun searchFieldAcceptsTextEntry() {
        var changedQuery = ""

        composeTestRule.setContent {
            BESTVTheme {
                TVSearchBar(
                    query = "",
                    onQueryChange = { changedQuery = it },
                    onQuerySubmit = {},
                    onClear = {},
                    placeholder = "Search",
                )
            }
        }

        composeTestRule.onNode(hasSetTextAction()).performTextInput("Batman")

        assertEquals("Batman", changedQuery)
    }

    @Test
    fun clearActionCanBeSelected() {
        var cleared = false

        composeTestRule.setContent {
            BESTVTheme {
                TVSearchBar(
                    query = "The Matrix",
                    onQueryChange = {},
                    onQuerySubmit = {},
                    onClear = { cleared = true },
                    placeholder = "Search",
                )
            }
        }

        composeTestRule.waitForIdle()
        composeTestRule.onNode(hasSetTextAction()).assertIsFocused()
        composeTestRule.onNode(hasSetTextAction()).performKeyInput {
            pressKey(Key.DirectionRight)
        }
        composeTestRule.onNodeWithContentDescription("Clear search").assertIsFocused()
        composeTestRule.onNodeWithContentDescription("Clear search").performKeyInput {
            pressKey(Key.DirectionLeft)
        }
        composeTestRule.onNode(hasSetTextAction()).assertIsFocused()
        composeTestRule.onNode(hasSetTextAction()).performKeyInput {
            pressKey(Key.DirectionRight)
        }
        composeTestRule.onNodeWithContentDescription("Clear search").assertIsFocused()
        composeTestRule.onNodeWithContentDescription("Clear search").performKeyInput {
            pressKey(Key.DirectionCenter)
        }

        assertTrue(cleared)
        composeTestRule.onNode(hasSetTextAction()).assertIsFocused()
    }
}
