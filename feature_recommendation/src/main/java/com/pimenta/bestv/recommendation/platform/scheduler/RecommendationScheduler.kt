package com.pimenta.bestv.recommendation.platform.scheduler

import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import com.pimenta.bestv.recommendation.platform.worker.RecommendationWorker
import java.util.concurrent.TimeUnit

private const val LEGACY_WORK_TAG = "RECOMMENDATION"
private const val UNIQUE_WORK_NAME = "recommendation_refresh"

class RecommendationScheduler(private val workManager: WorkManager) {
    fun schedule() {
        // The old request was tagged but not uniquely named. The new request omits that tag.
        workManager.cancelAllWorkByTag(LEGACY_WORK_TAG)
        val request = PeriodicWorkRequest.Builder(RecommendationWorker::class.java, 1, TimeUnit.HOURS).build()
        workManager.enqueueUniquePeriodicWork(UNIQUE_WORK_NAME, ExistingPeriodicWorkPolicy.KEEP, request)
    }
}
