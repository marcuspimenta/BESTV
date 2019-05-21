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

package com.pimenta.bestv.common.usecase

import com.pimenta.bestv.common.presentation.mapper.toViewModel
import com.pimenta.bestv.data.repository.MediaRepository
import com.pimenta.bestv.manager.RecommendationManager
import io.reactivex.Completable
import javax.inject.Inject

/**
 * Created by marcus on 23-04-2019.
 */
class LoadRecommendationUseCase @Inject constructor(
        private val mediaRepository: MediaRepository,
        private val recommendationManager: RecommendationManager
) {

    operator fun invoke(): Completable =
            mediaRepository.loadWorkByType(1, MediaRepository.WorkType.POPULAR_MOVIES)
                    .map { it.works?.map { work -> work.toViewModel() } }
                    .map { it.take(RECOMMENDATION_NUMBER) }
                    .flatMap { recommendationManager.loadRecommendations(it) }
                    .ignoreElement()

    companion object {

        private const val RECOMMENDATION_NUMBER = 5

    }
}