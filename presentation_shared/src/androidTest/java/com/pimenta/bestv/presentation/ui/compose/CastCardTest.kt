/*
 * Copyright (C) 2018 Marcus Pimenta
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package com.pimenta.bestv.presentation.ui.compose

import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performSemanticsAction
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.tv.material3.MaterialTheme
import com.pimenta.bestv.model.presentation.model.CastViewModel
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented UI tests for [CastCard] composable.
 */
@RunWith(AndroidJUnit4::class)
class CastCardTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val testCast = CastViewModel(
        id = 1,
        name = "Christian Bale",
        character = "Bruce Wayne",
        birthday = "January 30, 1974",
        deathDay = "",
        biography = "Christian Charles Philip Bale is an English actor.",
        thumbnailUrl = "https://example.com/image.jpg",
        source = "TMDB"
    )

    @Test
    fun castCard_displaysName_whenIncludeCastNameIsTrue() {
        composeTestRule.setContent {
            MaterialTheme {
                CastCard(
                    cast = testCast,
                    includeCastName = true
                )
            }
        }

        composeTestRule
            .onNodeWithText("Christian Bale")
            .assertIsDisplayed()
    }

    @Test
    fun castCard_doesNotDisplayName_whenIncludeCastNameIsFalse() {
        composeTestRule.setContent {
            MaterialTheme {
                CastCard(
                    cast = testCast,
                    includeCastName = false
                )
            }
        }

        composeTestRule
            .onNodeWithText("Christian Bale")
            .assertDoesNotExist()
    }

    @Test
    fun castCard_displaysImageWithCorrectContentDescription() {
        composeTestRule.setContent {
            MaterialTheme {
                CastCard(
                    cast = testCast,
                    includeCastName = true
                )
            }
        }

        composeTestRule
            .onNodeWithContentDescription("Christian Bale")
            .assertIsDisplayed()
    }

    @Test
    fun castCard_triggersOnClick_whenClicked() {
        var clicked = false

        composeTestRule.setContent {
            MaterialTheme {
                CastCard(
                    cast = testCast,
                    onClick = { clicked = true }
                )
            }
        }

        // Find the clickable Card node and invoke the onClick semantic action directly
        composeTestRule
            .onNode(hasClickAction())
            .performSemanticsAction(SemanticsActions.OnClick)

        assertTrue("onClick should have been triggered", clicked)
    }

    @Test
    fun castCard_displaysLongName() {
        val castWithLongName = testCast.copy(
            name = "A Very Long Actor Name That Should Be Truncated With Ellipsis"
        )

        composeTestRule.setContent {
            MaterialTheme {
                CastCard(
                    cast = castWithLongName,
                    includeCastName = true
                )
            }
        }

        composeTestRule
            .onNodeWithText(castWithLongName.name, substring = true)
            .assertIsDisplayed()
    }
}
