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

package com.pimenta.bestv.feature.castdetail.usecase

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import com.pimenta.bestv.common.presentation.model.CastViewModel
import com.pimenta.bestv.data.entity.Cast
import io.reactivex.Single
import org.junit.Test

/**
 * Created by marcus on 23-05-2018.
 */
class GetCastDetailsUseCaseTest {

    private val getCastPersonalDetails: GetCastPersonalDetails = mock()
    private val getMovieCreditsByCastUseCase: GetMovieCreditsByCastUseCase = mock()
    private val getTvShowCreditsByCastUseCase: GetTvShowCreditsByCastUseCase = mock()

    private val useCase = GetCastDetailsUseCase(
            getCastPersonalDetails,
            getMovieCreditsByCastUseCase,
            getTvShowCreditsByCastUseCase
    )

    @Test
    fun `should return the correct data when load the cast details`() {
        whenever(getCastPersonalDetails(any())).thenReturn(Single.just(aCastDetailedViewModel))
        whenever(getMovieCreditsByCastUseCase(any())).thenReturn(Single.just(emptyList()))
        whenever(getTvShowCreditsByCastUseCase(any())).thenReturn(Single.just(emptyList()))

        useCase(aCast)
                .test()
                .assertNoErrors()
                .assertComplete()
                .assertResult(
                        Triple(aCastDetailedViewModel, emptyList(), emptyList())
                )
    }

    @Test
    fun `should return an error when some exception happens`() {
        whenever(getCastPersonalDetails(any())).thenReturn(Single.just(aCastDetailedViewModel))
        whenever(getMovieCreditsByCastUseCase(any())).thenReturn(Single.error(Throwable()))
        whenever(getTvShowCreditsByCastUseCase(any())).thenReturn(Single.just(emptyList()))

        useCase(aCast)
                .test()
                .assertError(Throwable::class.java)
    }

    companion object {

        private val aCast = Cast(
                id = 1
        )

        private val aCastDetailedViewModel = CastViewModel(
                id = 1,
                name = "Carlos",
                character = "Batman",
                birthday = "1990-07-13"
        )

    }

}