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

package com.pimenta.bestv.feature.search.usecase

import com.pimenta.bestv.common.presentation.model.WorkPageViewModel
import com.pimenta.bestv.common.usecase.WorkUseCase
import io.reactivex.Single
import io.reactivex.functions.BiFunction
import javax.inject.Inject

/**
 * Created by marcus on 20-05-2019.
 */
class SearchWorksByQueryUseCase @Inject constructor(
        private val urlEncoderTextUseCase: UrlEncoderTextUseCase,
        private val workUseCase: WorkUseCase
) {

    operator fun invoke(query: String): Single<Pair<WorkPageViewModel, WorkPageViewModel>> =
            urlEncoderTextUseCase(query)
                    .flatMap {
                        Single.zip<WorkPageViewModel, WorkPageViewModel, Pair<WorkPageViewModel, WorkPageViewModel>>(
                                workUseCase.searchMoviesByQuery(it, 1),
                                workUseCase.searchTvShowsByQuery(it, 1),
                                BiFunction { first, second -> Pair(first, second) }
                        )
                    }

}