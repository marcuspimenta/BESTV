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
import com.pimenta.bestv.model.domain.PageDomainModel
import com.pimenta.bestv.model.domain.WorkDomainModel
import com.pimenta.bestv.workbrowse.data.repository.TvShowRepository
import io.reactivex.Single
import org.junit.Test

/**
 * Created by marcus on 2019-10-21.
 */
private const val GENRE_ID = 1
private const val PAGE = 1
private val TV_SHOW_PAGE_DOMAIN_MODEL = PageDomainModel(
        page = 1,
        totalPages = 1,
        results = listOf(
                WorkDomainModel(
                        id = 1,
                        title = "Batman",
                        originalTitle = "Batman",
                        type = WorkDomainModel.Type.TV_SHOW
                )
        )
)

class GetTvShowByGenreUseCaseTest {

    private val tvShowRepository: TvShowRepository = mock()

    private val useCase = GetTvShowByGenreUseCase(
            tvShowRepository
    )

    @Test
    fun `should return the right data when loading the tv shows by genre`() {
        whenever(tvShowRepository.getTvShowByGenre(GENRE_ID, PAGE))
                .thenReturn(Single.just(TV_SHOW_PAGE_DOMAIN_MODEL))

        useCase(GENRE_ID, PAGE)
                .test()
                .assertComplete()
                .assertResult(TV_SHOW_PAGE_DOMAIN_MODEL)
    }

    @Test
    fun `should return an error when some exception happens`() {
        whenever(tvShowRepository.getTvShowByGenre(GENRE_ID, PAGE))
                .thenReturn(Single.error(Throwable()))

        useCase(GENRE_ID, PAGE)
                .test()
                .assertError(Throwable::class.java)
    }
}
