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

import androidx.work.WorkManager
import androidx.work.WorkRequest
import org.mockito.kotlin.any
import org.mockito.kotlin.inOrder
import org.mockito.kotlin.mock
import org.mockito.kotlin.verifyNoMoreInteractions
import org.junit.Test

/**
 * Created by marcus on 2019-08-27.
 */
private const val WORK_TAG = "RECOMMENDATION"

class ScheduleRecommendationUseCaseTest {

    private val workerManager: WorkManager = mock()
    private val useCase = ScheduleRecommendationUseCase(
        workerManager
    )

    @Test
    fun `should load the schedule to update the recommendations`() {
        useCase.invoke()

        inOrder(workerManager) {
            verify(workerManager).cancelAllWorkByTag(WORK_TAG)
            verify(workerManager).enqueue(any<WorkRequest>())
            verifyNoMoreInteractions(workerManager)
        }
    }
}
