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

package com.pimenta.bestv.feature

import com.pimenta.bestv.common.usecase.LoadRecommendationUseCase
import com.pimenta.bestv.feature.base.DisposablePresenter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

/**
 * Created by marcus on 04-05-2018.
 */
class MainPresenter @Inject constructor(
        private val loadRecommendationUseCase: LoadRecommendationUseCase
) : DisposablePresenter() {

    /**
     * Loads the recommendations
     */
    fun loadRecommendations() {
        compositeDisposable.add(loadRecommendationUseCase()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe())
    }
}