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
import com.pimenta.bestv.common.presentation.model.WorkType
import com.pimenta.bestv.common.presentation.model.WorkViewModel
import com.pimenta.bestv.data.entity.Cast
import com.pimenta.bestv.data.entity.CastMovieList
import com.pimenta.bestv.data.entity.Movie
import com.pimenta.bestv.data.repository.MediaRepository
import io.reactivex.Single
import org.junit.Test

/**
 * Created by marcus on 24-05-2018.
 */
class GetMovieCreditsByCastUseCaseTest {

    private val mediaRepository: MediaRepository = mock()

    private val useCase = GetMovieCreditsByCastUseCase(
            mediaRepository
    )

    @Test
    fun `should return the right data when loading the movies by cast`() {
        val castMovieList = CastMovieList()
        castMovieList.works = listOf(aMovie)

        val workViewModels = listOf(aWorkViewModel)

        whenever(mediaRepository.getMovieCreditsByCast(any())).thenReturn(Single.just(castMovieList))

        useCase(aCast)
                .test()
                .assertComplete()
                .assertResult(workViewModels)
    }

    @Test
    fun `should return an error when some exception happens`() {
        whenever(mediaRepository.getMovieCreditsByCast(any())).thenReturn(Single.error(Throwable()))

        useCase(aCast)
                .test()
                .assertError(Throwable::class.java)
    }

    companion object {

        private val aCast = Cast(
                id = 1
        )

        private val aMovie = Movie(
                id = 1,
                title = "Batman",
                originalTitle = "Batman"
        )

        private val aWorkViewModel = WorkViewModel(
                id = 1,
                title = "Batman",
                originalTitle = "Batman",
                type = WorkType.MOVIE
        )

    }

}