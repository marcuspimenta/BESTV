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

package com.pimenta.bestv.castdetail.presentation.viewmodel

import androidx.lifecycle.viewModelScope
import com.pimenta.bestv.castdetail.domain.GetCastDetailsUseCase
import com.pimenta.bestv.castdetail.presentation.model.CastDetailsEffect
import com.pimenta.bestv.castdetail.presentation.model.CastDetailsEvent
import com.pimenta.bestv.castdetail.presentation.model.CastDetailsState
import com.pimenta.bestv.model.presentation.mapper.toViewModel
import com.pimenta.bestv.model.presentation.model.CastViewModel
import com.pimenta.bestv.model.presentation.model.WorkViewModel
import com.pimenta.bestv.presentation.di.annotation.FragmentScope
import com.pimenta.bestv.presentation.presenter.BaseViewModel
import com.pimenta.bestv.route.workdetail.WorkDetailsRoute
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

/**
 * ViewModel for the Cast Details screen following MVI architecture
 *
 * Created by marcus on 21-10-2025.
 */
@FragmentScope
class CastDetailsViewModel @Inject constructor(
    private val cast: CastViewModel,
    private val getCastDetailsUseCase: GetCastDetailsUseCase,
    private val workDetailsRoute: WorkDetailsRoute
) : BaseViewModel<CastDetailsState, CastDetailsEffect>(CastDetailsState.Loading) {

    fun handleEvent(event: CastDetailsEvent) {
        when (event) {
            is CastDetailsEvent.LoadData -> loadData()
            is CastDetailsEvent.WorkClicked -> handleWorkClicked(event.work)
        }
    }

    private fun loadData() {
        viewModelScope.launch {
            try {
                updateState { CastDetailsState.Loading }

                val (castDetails, movies, tvShows) = getCastDetailsUseCase(cast.id)

                updateState {
                    val cast = castDetails.toViewModel()
                    if (cast != null) {
                        CastDetailsState.Loaded(
                            cast = cast,
                            movies = movies?.mapNotNull { movie -> movie.toViewModel() }.orEmpty(),
                            tvShows = tvShows?.mapNotNull { tvShow -> tvShow.toViewModel() }.orEmpty()
                        )
                    } else {
                        CastDetailsState.Error
                    }
                }
            } catch (throwable: Throwable) {
                Timber.e(throwable, "Error while getting the cast details")
                updateState { CastDetailsState.Error }
            }
        }
    }

    private fun handleWorkClicked(work: WorkViewModel) {
        val intent = workDetailsRoute.buildWorkDetailIntent(work)
        emitEvent(CastDetailsEffect.OpenIntent(intent, true))
    }
}
