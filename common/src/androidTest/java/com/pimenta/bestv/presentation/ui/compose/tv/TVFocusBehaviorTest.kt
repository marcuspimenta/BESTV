package com.pimenta.bestv.presentation.ui.compose.tv

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.test.assertIsFocused
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performKeyInput
import androidx.compose.ui.test.pressKey
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.tv.material3.Button
import androidx.tv.material3.Text
import com.pimenta.bestv.presentation.theme.BESTVTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TVFocusBehaviorTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun rowRestoresLastFocusedCardWhenDpadReturnsToIt() {
        val secondCardFocusRequester = FocusRequester()
        val outsideFocusRequester = FocusRequester()

        composeTestRule.setContent {
            BESTVTheme {
                Column {
                    TVStartAlignedLazyRow(
                        modifier = Modifier
                            .width(600.dp)
                            .height(160.dp),
                    ) {
                        items(listOf("First", "Second")) { label ->
                            Button(
                                onClick = {},
                                modifier = if (label == "Second") {
                                    Modifier.focusRequester(secondCardFocusRequester)
                                } else {
                                    Modifier
                                },
                            ) {
                                Text(label)
                            }
                        }
                    }

                    Button(
                        onClick = {},
                        modifier = Modifier.focusRequester(outsideFocusRequester),
                    ) {
                        Text("Outside")
                    }
                }
            }
        }

        composeTestRule.runOnIdle { secondCardFocusRequester.requestFocus() }
        composeTestRule.onNodeWithText("Second").assertIsFocused()
        composeTestRule.runOnIdle { outsideFocusRequester.requestFocus() }
        composeTestRule.onNodeWithText("Outside").assertIsFocused()
        composeTestRule.onNodeWithText("Outside").performKeyInput {
            pressKey(Key.DirectionUp)
        }

        composeTestRule.onNodeWithText("Second").assertIsFocused()
    }

    @Test
    fun transientErrorAnnouncementDoesNotStealContentFocus() {
        val contentFocusRequester = FocusRequester()
        val errorMessage = mutableStateOf<String?>(null)

        composeTestRule.setContent {
            BESTVTheme {
                Box(modifier = Modifier.fillMaxSize()) {
                    Button(
                        onClick = {},
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .focusRequester(contentFocusRequester),
                    ) {
                        Text("Current item")
                    }

                    TVErrorBanner(
                        errorMessage = errorMessage.value,
                        onDismiss = {},
                        modifier = Modifier.align(Alignment.BottomCenter),
                    )
                }
            }
        }

        composeTestRule.runOnIdle { contentFocusRequester.requestFocus() }
        composeTestRule.onNodeWithText("Current item").assertIsFocused()
        composeTestRule.runOnIdle { errorMessage.value = "A warning" }
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("A warning").assertIsDisplayed()
        composeTestRule.onNodeWithText("Current item").assertIsFocused()
        composeTestRule.onNodeWithText("Current item").performKeyInput {
            pressKey(Key.DirectionDown)
        }
        composeTestRule.onNodeWithText("Dismiss").assertIsFocused()
    }
}
