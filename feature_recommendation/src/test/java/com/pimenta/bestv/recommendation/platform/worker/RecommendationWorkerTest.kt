package com.pimenta.bestv.recommendation.platform.worker

import androidx.work.ListenableWorker
import java.io.IOException
import kotlinx.coroutines.CancellationException
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import retrofit2.HttpException

class RecommendationWorkerTest {
    @Test
    fun `cancellation propagates to WorkManager`() {
        assertThrows(CancellationException::class.java) {
            CancellationException().toRecommendationResult()
        }
    }

    @Test
    fun `recoverable network failures retry`() {
        assertEquals(ListenableWorker.Result.retry()::class, IOException().toRecommendationResult()::class)
        val throttled: HttpException = mock()
        whenever(throttled.code()).thenReturn(429)
        assertEquals(ListenableWorker.Result.retry()::class, throttled.toRecommendationResult()::class)
        val unavailable: HttpException = mock()
        whenever(unavailable.code()).thenReturn(503)
        assertEquals(ListenableWorker.Result.retry()::class, unavailable.toRecommendationResult()::class)
    }

    @Test
    fun `permanent failures do not retry`() {
        val badRequest: HttpException = mock()
        whenever(badRequest.code()).thenReturn(400)
        assertEquals(ListenableWorker.Result.failure()::class, badRequest.toRecommendationResult()::class)
    }
}
