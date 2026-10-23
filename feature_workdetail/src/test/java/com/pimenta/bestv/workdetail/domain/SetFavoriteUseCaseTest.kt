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

package com.pimenta.bestv.workdetail.domain

import com.pimenta.bestv.model.domain.WorkDomainModel
import com.pimenta.bestv.workdetail.domain.repository.MovieRepository
import com.pimenta.bestv.workdetail.domain.repository.TvShowRepository
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.only
import org.mockito.kotlin.verify

class SetFavoriteUseCaseTest {
    private val movieRepository: MovieRepository = mock()
    private val tvShowRepository: TvShowRepository = mock()
    private val useCase = SetFavoriteUseCase(movieRepository, tvShowRepository)

    @Test
    fun `sets movie favorite to requested state`() = runTest {
        useCase(1, WorkDomainModel.Type.MOVIE, true)
        verify(movieRepository, only()).saveFavoriteMovie(1)
    }

    @Test
    fun `clears movie favorite when requested`() = runTest {
        useCase(1, WorkDomainModel.Type.MOVIE, false)
        verify(movieRepository, only()).deleteFavoriteMovie(1)
    }

    @Test
    fun `sets tv show favorite to requested state`() = runTest {
        useCase(1, WorkDomainModel.Type.TV_SHOW, true)
        verify(tvShowRepository, only()).saveFavoriteTvShow(1)
    }

    @Test
    fun `clears tv show favorite when requested`() = runTest {
        useCase(1, WorkDomainModel.Type.TV_SHOW, false)
        verify(tvShowRepository, only()).deleteFavoriteTvShow(1)
    }
}
