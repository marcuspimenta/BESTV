package com.pimenta.bestv.workdetail.presentation.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test

class LazyListKeyTest {
    @Test
    fun videoKeysPreferUpstreamIdentityAndUseMapperFallbackOtherwise() {
        assertEquals("id:video-id", VideoViewModel(id = "video-id").lazyListKey())
        assertEquals("url:youtube-url", VideoViewModel(youtubeUrl = "youtube-url").lazyListKey())

        val first = VideoViewModel(fallbackKey = "index:0").lazyListKey()
        val second = VideoViewModel(fallbackKey = "index:1").lazyListKey()
        assertNotEquals(first, second)
        assertEquals(first, VideoViewModel(fallbackKey = "index:0").lazyListKey())
    }

    @Test
    fun reviewKeysPreferUpstreamIdentityAndUseMapperFallbackOtherwise() {
        assertEquals("id:review-id", ReviewViewModel(id = "review-id").lazyListKey())
        assertEquals("url:review-url", ReviewViewModel(url = "review-url").lazyListKey())

        val first = ReviewViewModel(fallbackKey = "page:1:index:0").lazyListKey()
        val second = ReviewViewModel(fallbackKey = "page:2:index:0").lazyListKey()
        assertNotEquals(first, second)
        assertEquals(first, ReviewViewModel(fallbackKey = "page:1:index:0").lazyListKey())
    }
}
