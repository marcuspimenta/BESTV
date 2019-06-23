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

package com.pimenta.bestv.feature.workdetail.usecase

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import com.pimenta.bestv.common.presentation.model.CastViewModel
import com.pimenta.bestv.data.entity.Cast
import com.pimenta.bestv.data.entity.CastList
import com.pimenta.bestv.data.entity.Movie
import com.pimenta.bestv.data.repository.MediaRepository
import io.reactivex.Single
import org.junit.Test

/**
 * Created by marcus on 23-06-2018.
 */
class GetCastsUseCaseTest {

    private val mediaRepository: MediaRepository = mock()
    private val useCase = GetCastsUseCase(mediaRepository)

    @Test
    fun `should return the right data when loading the casts`() {
        whenever(mediaRepository.getCastByWork(any())).thenReturn(Single.just(castList))

        useCase(movie)
                .test()
                .assertComplete()
                .assertResult(castViewModels)
    }

    @Test
    fun `should return an error when some exception happens`() {
        whenever(mediaRepository.getCastByWork(any())).thenReturn(Single.error(Throwable()))

        useCase(movie)
                .test()
                .assertError(Throwable::class.java)
    }

    companion object {

        private val movie = Movie(
                id = 1
        )

        private val castList = CastList(
                id = 1,
                casts = listOf(
                        Cast(
                                id = 1,
                                name = "Name",
                                character = "Character",
                                birthday = "Birthday",
                                deathDay = null,
                                biography = null
                        )
                )
        )

        private val castViewModels = listOf(
                CastViewModel(
                        id = 1,
                        name = "Name",
                        character = "Character",
                        birthday = "Birthday",
                        deathDay = null,
                        biography = null
                )
        )
    }
}