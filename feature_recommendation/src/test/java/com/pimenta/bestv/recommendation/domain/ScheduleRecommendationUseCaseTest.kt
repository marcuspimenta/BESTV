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

import android.content.Intent
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.only
import com.nhaarman.mockitokotlin2.verify
import com.pimenta.bestv.recommendation.data.local.alarm.LocalAlarm
import org.junit.Test

/**
 * Created by marcus on 2019-08-27.
 */
private const val INITIAL_DELAY = 5000L

class ScheduleRecommendationUseCaseTest {

    private val localAlarm: LocalAlarm = mock()
    private val useCase = ScheduleRecommendationUseCase(
            localAlarm
    )

    @Test
    fun `should load the schedule to update the recommendations`() {
        val intent: Intent = mock()

        useCase(intent)

        verify(localAlarm, only()).scheduleRecommendationUpdate(intent, INITIAL_DELAY)
    }
}
