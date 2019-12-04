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

package com.pimenta.bestv.feature.main.domain

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import com.pimenta.bestv.model.presentation.model.WorkType
import com.pimenta.bestv.model.presentation.model.WorkViewModel
import com.pimenta.bestv.model.data.local.MovieDbModel
import io.reactivex.Single
import org.junit.Test

/**
 * Created by marcus on 15-10-2019.
 */
private val MOVIE_DB = MovieDbModel(
        id = 1
)
private val MOVIE_VIEW_MODEL = WorkViewModel(
        id = 1,
        title = "Batman",
        originalTitle = "Batman",
        isFavorite = true,
        type = WorkType.MOVIE
)

class GetFavoriteMoviesUseCaseTest {

    private val getFavoriteMovieIdsUseCase: GetFavoriteMovieIdsUseCase = mock()
    private val getMovieUseCase: GetMovieUseCase = mock()

    private val useCase = com.pimenta.bestv.workbrowse.domain.GetFavoriteMoviesUseCase(
            getFavoriteMovieIdsUseCase,
            getMovieUseCase
    )

    @Test
    fun `should return the right data when loading the favorites movies`() {
        whenever(getFavoriteMovieIdsUseCase()).thenReturn(Single.just(listOf(MOVIE_DB)))
        whenever(getMovieUseCase(MOVIE_DB.id)).thenReturn(MOVIE_VIEW_MODEL)

        useCase()
                .test()
                .assertComplete()
                .assertResult(listOf(MOVIE_VIEW_MODEL))
    }

    @Test
    fun `should return an error when some exception happens`() {
        whenever(getFavoriteMovieIdsUseCase()).thenReturn(Single.error(Throwable()))

        useCase()
                .test()
                .assertError(Throwable::class.java)
    }
}