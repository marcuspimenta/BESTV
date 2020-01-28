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

package com.pimenta.bestv.recommendation.domain

import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import com.pimenta.bestv.recommendation.presentation.worker.RecommendationWorker
import java.util.concurrent.TimeUnit
import javax.inject.Inject

/**
 * Created by marcus on 23-08-2019.
 */
private const val REPEAT_INTERVAL_MINUTES = 1L
private const val WORK_TAG = "RECOMMENDATION"

class ScheduleRecommendationUseCase @Inject constructor(
    private val workerManager: WorkManager
) {

    operator fun invoke() {
        with(workerManager) {
            val workerInstance = PeriodicWorkRequest.Builder(RecommendationWorker::class.java, REPEAT_INTERVAL_MINUTES, TimeUnit.HOURS)
                    .addTag(WORK_TAG)
                    .build()

            cancelAllWorkByTag(WORK_TAG)
            enqueue(workerInstance)
        }
    }
}
