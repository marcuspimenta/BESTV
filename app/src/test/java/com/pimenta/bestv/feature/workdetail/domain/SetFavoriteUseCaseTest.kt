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

package com.pimenta.bestv.feature.workdetail.domain

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.only
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.pimenta.bestv.common.presentation.mapper.toMovieDbModel
import com.pimenta.bestv.common.presentation.mapper.toTvShowDbModel
import com.pimenta.bestv.common.presentation.model.WorkType
import com.pimenta.bestv.common.presentation.model.WorkViewModel
import com.pimenta.bestv.data.MediaRepository
import io.reactivex.Completable
import org.junit.Test

/**
 * Created by marcus on 2019-08-28.
 */
private val MOVIE_VIEW_MODEL = WorkViewModel(
        id = 1,
        title = "Batman",
        originalTitle = "Batman",
        type = WorkType.MOVIE
)
private val TV_SHOW_VIEW_MODEL = WorkViewModel(
        id = 1,
        title = "Batman",
        originalTitle = "Batman",
        type = WorkType.TV_SHOW
)

class SetFavoriteUseCaseTest {

    private val mediaRepository: MediaRepository = mock()
    private val useCase = SetFavoriteUseCase(
            mediaRepository
    )

    @Test
    fun `should save a movie as favorite is it is not favorite`() {
        val dbModel = MOVIE_VIEW_MODEL.toMovieDbModel()

        whenever(mediaRepository.saveFavoriteMovie(dbModel))
                .thenReturn(Completable.complete())

        useCase(MOVIE_VIEW_MODEL)
                .test()
                .assertComplete()

        verify(mediaRepository, only()).saveFavoriteMovie(dbModel)
    }

    @Test
    fun `should delete a movie as favorite is it is favorite`() {
        val movieViewModel = MOVIE_VIEW_MODEL.copy(isFavorite = true)
        val dbModel = movieViewModel.toMovieDbModel()

        whenever(mediaRepository.deleteFavoriteMovie(dbModel))
                .thenReturn(Completable.complete())

        useCase(movieViewModel)
                .test()
                .assertComplete()

        verify(mediaRepository, only()).deleteFavoriteMovie(dbModel)
    }

    @Test
    fun `should save a tv show as favorite is it is not favorite`() {
        val dbModel = TV_SHOW_VIEW_MODEL.toTvShowDbModel()

        whenever(mediaRepository.saveFavoriteTvShow(dbModel))
                .thenReturn(Completable.complete())

        useCase(TV_SHOW_VIEW_MODEL)
                .test()
                .assertComplete()

        verify(mediaRepository, only()).saveFavoriteTvShow(dbModel)
    }

    @Test
    fun `should delete a tv show as favorite is it is favorite`() {
        val tvShowViewModel = TV_SHOW_VIEW_MODEL.copy(isFavorite = true)
        val dbModel = tvShowViewModel.toTvShowDbModel()

        whenever(mediaRepository.deleteFavoriteTvShow(dbModel))
                .thenReturn(Completable.complete())

        useCase(tvShowViewModel)
                .test()
                .assertComplete()

        verify(mediaRepository, only()).deleteFavoriteTvShow(dbModel)
    }
}