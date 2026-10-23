package com.pimenta.bestv.recommendation.platform.scheduler

import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

class RecommendationSchedulerTest {
    private val workManager: WorkManager = mock()
    private val scheduler = RecommendationScheduler(workManager)

    @Test
    fun `repeated boots clean legacy work and keep the unique periodic job`() {
        scheduler.schedule()
        scheduler.schedule()

        verify(workManager, org.mockito.kotlin.times(2)).cancelAllWorkByTag("RECOMMENDATION")
        verify(workManager, org.mockito.kotlin.times(2)).enqueueUniquePeriodicWork(
            eq("recommendation_refresh"),
            eq(ExistingPeriodicWorkPolicy.KEEP),
            any<PeriodicWorkRequest>(),
        )
    }
}
