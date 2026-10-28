package com.pimenta.bestv.presentation.ui.compose

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class LazyRowPaginationTest {
    @Test
    fun loadingStateSuppressesPaginationAndReloadsWhenCleared() {
        assertFalse(
            isNearEnd(
                lastVisibleItemIndex = 7,
                totalItems = 10,
                isLoadingMore = true,
                threshold = 3,
            ),
        )
        assertTrue(
            isNearEnd(
                lastVisibleItemIndex = 7,
                totalItems = 10,
                isLoadingMore = false,
                threshold = 3,
            ),
        )
    }

    @Test
    fun thresholdControlsPaginationBoundary() {
        assertFalse(isNearEnd(6, 10, false, threshold = 3))
        assertTrue(isNearEnd(6, 10, false, threshold = 4))
    }
}
