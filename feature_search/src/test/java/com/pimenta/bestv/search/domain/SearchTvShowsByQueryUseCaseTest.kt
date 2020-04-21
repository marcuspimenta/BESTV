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

package com.pimenta.bestv.search.domain

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import com.pimenta.bestv.model.domain.PageDomainModel
import com.pimenta.bestv.model.domain.WorkDomainModel
import com.pimenta.bestv.search.data.repository.TvShowRepository
import io.reactivex.Single
import org.junit.Test

/**
 * Created by marcus on 2019-08-28.
 */
private const val QUERY = "Batman"
private val WORK_PAGE = PageDomainModel(
        page = 1,
        totalPages = 1,
        results = listOf(
                WorkDomainModel(
                        id = 1,
                        title = "Batman",
                        originalTitle = "Batman"
                )
        )
)

class SearchTvShowsByQueryUseCaseTest {

    private val tvShowRepository: TvShowRepository = mock()
    private val useCase = SearchTvShowsByQueryUseCase(
            tvShowRepository
    )

    @Test
    fun `should return the right data when searching tv show by query`() {
        whenever(tvShowRepository.searchTvShowsByQuery(QUERY, 1))
                .thenReturn(Single.just(WORK_PAGE))

        useCase(QUERY, 1)
                .test()
                .assertComplete()
                .assertResult(WORK_PAGE)
    }

    @Test
    fun `should return an error when some exception happens`() {
        whenever(tvShowRepository.searchTvShowsByQuery(QUERY, 1))
                .thenReturn(Single.error(Throwable()))

        useCase(QUERY, 1)
                .test()
                .assertError(Throwable::class.java)
    }
}
