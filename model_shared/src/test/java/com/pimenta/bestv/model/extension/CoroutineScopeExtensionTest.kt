package com.pimenta.bestv.model.extension

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test

class CoroutineScopeExtensionTest {
    @Test
    fun `should preserve a successful value`() = runTest {
        val result = asyncCatching { "Loaded" }.await()

        assertEquals("Loaded", result.getOrThrow())
    }

    @Test
    fun `should treat null as a successful result`() = runTest {
        val result = asyncCatching<String?> { null }.await()

        assertTrue(result.isSuccess)
        assertNull(result.getOrThrow())
    }

    @Test
    fun `should preserve the original failure`() = runTest {
        val exception = IllegalStateException("Request failed")

        val result = asyncCatching<String> { throw exception }.await()

        assertTrue(result.isFailure)
        assertSame(exception, result.exceptionOrNull())
    }

    @Test
    fun `should capture a thrown cancellation exception as a failure`() = runTest {
        val exception = CancellationException("Request cancelled")
        val request = asyncCatching<String> { throw exception }

        val result = request.await()

        assertTrue(result.isFailure)
        assertSame(exception, result.exceptionOrNull())
        assertFalse(request.isCancelled)
    }

    @Test
    fun `should cancel a suspended request`() = runTest {
        val started = CompletableDeferred<Unit>()
        val exception = CancellationException("Request cancelled")
        val request = asyncCatching {
            started.complete(Unit)
            awaitCancellation()
        }
        started.await()

        request.cancel(exception)
        val thrown = runCatching { request.await() }.exceptionOrNull()
        request.join()

        assertTrue(thrown is CancellationException)
        assertEquals(exception.message, thrown?.message)
        assertTrue(request.isCancelled)
    }

    @Test
    fun `should allow a sibling request to finish after an ordinary failure`() = runTest {
        val releaseSibling = CompletableDeferred<Unit>()
        val failedRequest = asyncCatching<String> { throw IllegalStateException("Request failed") }
        val successfulRequest = asyncCatching {
            releaseSibling.await()
            "Loaded"
        }

        assertTrue(failedRequest.await().isFailure)
        releaseSibling.complete(Unit)

        assertEquals("Loaded", successfulRequest.await().getOrThrow())
    }
}
