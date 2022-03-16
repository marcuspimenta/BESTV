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

package com.pimenta.bestv.recommendation.domain

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.pimenta.bestv.model.domain.PageDomainModel
import com.pimenta.bestv.model.domain.WorkDomainModel
import com.pimenta.bestv.recommendation.data.repository.MovieRepository
import com.pimenta.bestv.recommendation.data.repository.RecommendationRepository
import io.reactivex.Completable
import io.reactivex.Single
import org.junit.Test

/**
 * Created by marcus on 2019-08-27.
 */
private val MOVIE_PAGE_DOMAIN_MODEL = PageDomainModel(
    page = 1,
    totalPages = 1,
    results = listOf(
        WorkDomainModel(
            id = 1,
            title = "Batman",
            originalTitle = "Batman",
            type = WorkDomainModel.Type.MOVIE
        )
    )
)

class LoadRecommendationUseCaseTest {

    private val movieRepository: MovieRepository = mock()
    private val recommendationRepository: RecommendationRepository = mock()
    private val useCase = LoadRecommendationUseCase(
        movieRepository,
        recommendationRepository
    )

    @Test
    fun `should return the right data when loading the recommendations`() {
        whenever(movieRepository.getPopularMovies(1))
            .thenReturn(Single.just(MOVIE_PAGE_DOMAIN_MODEL))
        whenever(recommendationRepository.loadRecommendations(MOVIE_PAGE_DOMAIN_MODEL.results))
            .thenReturn(Completable.complete())

        useCase()
            .test()
            .assertComplete()

        verify(recommendationRepository).loadRecommendations(MOVIE_PAGE_DOMAIN_MODEL.results)
    }

    @Test
    fun `should return an error when some exception happens`() {
        whenever(movieRepository.getPopularMovies(1))
            .thenReturn(Single.error(Throwable()))

        useCase()
            .test()
            .assertError(Throwable::class.java)
    }
}
