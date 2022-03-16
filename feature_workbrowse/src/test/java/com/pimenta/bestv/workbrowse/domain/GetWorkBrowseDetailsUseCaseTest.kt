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

package com.pimenta.bestv.workbrowse.domain

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import com.pimenta.bestv.workbrowse.domain.model.GenreDomainModel
import io.reactivex.Single
import org.junit.Test

/**
 * Created by marcus on 28-05-2019.
 */
private val MOVIE_GENRES = listOf(
    GenreDomainModel(
        id = 1,
        name = "Action",
        source = GenreDomainModel.Source.MOVIE
    )
)

private val TV_SHOW_GENRES = listOf(
    GenreDomainModel(
        id = 2,
        name = "Action",
        source = GenreDomainModel.Source.TV_SHOW
    )
)

class GetWorkBrowseDetailsUseCaseTest {

    private val hasFavoriteUseCase: HasFavoriteUseCase = mock()
    private val getMovieGenresUseCase: GetMovieGenresUseCase = mock()
    private val getTvShowGenresUseCase: GetTvShowGenresUseCase = mock()
    private val useCase = GetWorkBrowseDetailsUseCase(
        hasFavoriteUseCase,
        getMovieGenresUseCase,
        getTvShowGenresUseCase
    )

    @Test
    fun `should return the right data when loading the browse details`() {
        whenever(hasFavoriteUseCase()).thenReturn(Single.just(true))
        whenever(getMovieGenresUseCase()).thenReturn(Single.just(MOVIE_GENRES))
        whenever(getTvShowGenresUseCase()).thenReturn(Single.just(TV_SHOW_GENRES))

        useCase()
            .test()
            .assertComplete()
            .assertResult(Triple(true, MOVIE_GENRES, TV_SHOW_GENRES))
    }

    @Test
    fun `should return an error when some exception happens`() {
        whenever(hasFavoriteUseCase()).thenReturn(Single.just(true))
        whenever(getMovieGenresUseCase()).thenReturn(Single.error(Throwable()))
        whenever(getTvShowGenresUseCase()).thenReturn(Single.just(TV_SHOW_GENRES))

        useCase()
            .test()
            .assertError(Throwable::class.java)
    }
}
