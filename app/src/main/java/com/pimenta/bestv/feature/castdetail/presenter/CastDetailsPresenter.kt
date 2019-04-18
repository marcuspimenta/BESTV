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

package com.pimenta.bestv.feature.castdetail.presenter

import com.pimenta.bestv.common.presentation.mapper.toCast
import com.pimenta.bestv.common.presentation.model.CastViewModel
import com.pimenta.bestv.common.usecase.GetCastDetailsUseCase
import com.pimenta.bestv.feature.base.DisposablePresenter
import com.pimenta.bestv.repository.MediaRepository
import com.pimenta.bestv.repository.entity.Cast
import com.pimenta.bestv.repository.entity.CastMovieList
import com.pimenta.bestv.repository.entity.CastTvShowList
import com.pimenta.bestv.repository.entity.Work
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Function3
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import javax.inject.Inject

/**
 * Created by marcus on 05-04-2018.
 */
class CastDetailsPresenter @Inject constructor(
        private val view: View,
        private val mediaRepository: MediaRepository,
        private val getCastDetailsUseCase: GetCastDetailsUseCase
) : DisposablePresenter() {

    /**
     * Load the [Cast] details
     *
     * @param cast [Cast]
     */
    fun loadCastDetails(castViewModel: CastViewModel) {
        compositeDisposable.add(
                Single.fromCallable { castViewModel.toCast() }
                        .flatMap {
                            Single.zip<CastViewModel, CastMovieList, CastTvShowList, Triple<CastViewModel, CastMovieList, CastTvShowList>>(
                                    getCastDetailsUseCase(it),
                                    mediaRepository.getMovieCreditsByCast(it),
                                    mediaRepository.getTvShowCreditsByCast(it),
                                    Function3<CastViewModel, CastMovieList, CastTvShowList, Triple<CastViewModel, CastMovieList, CastTvShowList>> { castViewModel, castMovieList, castTvShowList ->
                                        Triple(castViewModel, castMovieList, castTvShowList)
                                    }
                            )
                        }
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({ triple ->
                            view.onCastLoaded(
                                    triple.first,
                                    triple.second.works,
                                    triple.third.works
                            )
                        }, { throwable ->
                            Timber.e(throwable, "Error while getting the cast details")
                            view.onCastLoaded(null, null, null)
                        }))
    }

    interface View {

        fun onCastLoaded(castViewModel: CastViewModel?, movies: List<Work>?, tvShow: List<Work>?)

    }
}