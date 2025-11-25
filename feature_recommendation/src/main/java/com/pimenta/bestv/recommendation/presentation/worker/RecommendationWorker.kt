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

package com.pimenta.bestv.recommendation.presentation.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.pimenta.bestv.recommendation.domain.LoadRecommendationUseCase
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent

/**
 * Created by marcus on 28-01-2020.
 */
class RecommendationWorker(
    context: Context,
    workerParameters: WorkerParameters,
) : CoroutineWorker(context, workerParameters) {
    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface RecommendationWorkerEntryPoint {
        fun loadRecommendationUseCase(): LoadRecommendationUseCase
    }

    private val loadRecommendationUseCase: LoadRecommendationUseCase by lazy {
        val appContext = applicationContext
        val entryPoint =
            EntryPointAccessors.fromApplication(
                appContext,
                RecommendationWorkerEntryPoint::class.java,
            )
        entryPoint.loadRecommendationUseCase()
    }

    override suspend fun doWork(): Result =
        try {
            loadRecommendationUseCase()
            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
}
