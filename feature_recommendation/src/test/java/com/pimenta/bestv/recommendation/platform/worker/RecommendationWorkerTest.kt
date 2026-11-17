package com.pimenta.bestv.recommendation.platform.worker

import androidx.work.ListenableWorker
import io.ktor.client.plugins.ResponseException
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpStatusCode
import java.io.IOException
import kotlinx.coroutines.CancellationException
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

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
        val throttled = responseException(HttpStatusCode.TooManyRequests)
        assertEquals(ListenableWorker.Result.retry()::class, throttled.toRecommendationResult()::class)
        val unavailable = responseException(HttpStatusCode.ServiceUnavailable)
        assertEquals(ListenableWorker.Result.retry()::class, unavailable.toRecommendationResult()::class)
    }

    @Test
    fun `permanent failures do not retry`() {
        val badRequest = responseException(HttpStatusCode.BadRequest)
        assertEquals(ListenableWorker.Result.failure()::class, badRequest.toRecommendationResult()::class)
    }

    private fun responseException(statusCode: HttpStatusCode): ResponseException {
        val response = mock<HttpResponse>()
        whenever(response.status).thenReturn(statusCode)
        return mock<ResponseException>().also { whenever(it.response).thenReturn(response) }
    }
}
