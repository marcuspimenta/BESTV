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
import com.pimenta.bestv.workbrowse.data.repository.GenreRepository
import com.pimenta.bestv.workbrowse.domain.model.GenreDomainModel
import io.reactivex.Single
import org.junit.Test

/**
 * Created by marcus on 2019-08-26.
 */
private val TV_SHOW_GENRES_DOMAIN_MODEL = listOf(
        GenreDomainModel(
                id = 2,
                name = "Action",
                source = GenreDomainModel.Source.TV_SHOW
        )
)

class GetTvShowGenresUseCaseTest {

    private val genreRepository: GenreRepository = mock()
    private val useCase = GetTvShowGenresUseCase(
            genreRepository
    )

    @Test
    fun `should return the right data when loading the tv show genres`() {
        whenever(genreRepository.getTvShowGenres())
                .thenReturn(Single.just(TV_SHOW_GENRES_DOMAIN_MODEL))

        useCase()
                .test()
                .assertComplete()
                .assertResult(TV_SHOW_GENRES_DOMAIN_MODEL)
    }

    @Test
    fun `should return an error when some exception happens`() {
        whenever(genreRepository.getTvShowGenres())
                .thenReturn(Single.error(Throwable()))

        useCase()
                .test()
                .assertError(Throwable::class.java)
    }
}