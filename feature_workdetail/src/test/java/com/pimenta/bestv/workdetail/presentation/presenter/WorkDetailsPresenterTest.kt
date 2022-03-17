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

package com.pimenta.bestv.workdetail.presentation.presenter

import android.content.Intent
import androidx.leanback.widget.Presenter
import com.nhaarman.mockitokotlin2.inOrder
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.only
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyNoMoreInteractions
import com.nhaarman.mockitokotlin2.whenever
import com.pimenta.bestv.model.domain.PageDomainModel
import com.pimenta.bestv.model.domain.WorkDomainModel
import com.pimenta.bestv.model.presentation.mapper.toViewModel
import com.pimenta.bestv.model.presentation.model.CastViewModel
import com.pimenta.bestv.model.presentation.model.WorkType
import com.pimenta.bestv.model.presentation.model.WorkViewModel
import com.pimenta.bestv.presentation.scheduler.RxScheduler
import com.pimenta.bestv.route.Route
import com.pimenta.bestv.route.castdetail.CastDetailsRoute
import com.pimenta.bestv.route.workdetail.WorkDetailsRoute
import com.pimenta.bestv.workdetail.domain.GetRecommendationByWorkUseCase
import com.pimenta.bestv.workdetail.domain.GetReviewByWorkUseCase
import com.pimenta.bestv.workdetail.domain.GetSimilarByWorkUseCase
import com.pimenta.bestv.workdetail.domain.GetWorkDetailsUseCase
import com.pimenta.bestv.workdetail.domain.SetFavoriteUseCase
import com.pimenta.bestv.workdetail.domain.model.ReviewDomainModel
import com.pimenta.bestv.workdetail.presentation.mapper.toViewModel
import com.pimenta.bestv.workdetail.presentation.model.VideoViewModel
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import org.junit.Test

/**
 * Created by marcus on 24-06-2019.
 */
private val RECOMMENDED_PAGE_1 = PageDomainModel(
    page = 1,
    totalPages = 2,
    results = listOf(
        WorkDomainModel(
            id = 1,
            title = "Recommended movie",
            type = WorkDomainModel.Type.MOVIE
        ),
        WorkDomainModel(
            id = 2,
            title = "Recommended movie",
            type = WorkDomainModel.Type.MOVIE
        )
    )
)
private val RECOMMENDED_PAGE_2 = PageDomainModel(
    page = 2,
    totalPages = 2,
    results = listOf(
        WorkDomainModel(
            id = 3,
            title = "Recommended movie",
            type = WorkDomainModel.Type.MOVIE
        )
    )
)
private val SIMILAR_PAGE_1 = PageDomainModel(
    page = 1,
    totalPages = 2,
    results = listOf(
        WorkDomainModel(
            id = 1,
            title = "Similar movie",
            type = WorkDomainModel.Type.MOVIE
        ),
        WorkDomainModel(
            id = 2,
            title = "Similar movie",
            type = WorkDomainModel.Type.MOVIE
        )
    )
)
private val SIMILAR_PAGE_2 = PageDomainModel(
    page = 2,
    totalPages = 2,
    results = listOf(
        WorkDomainModel(
            id = 3,
            title = "Similar movie",
            type = WorkDomainModel.Type.MOVIE
        )
    )
)
private val REVIEW_PAGE_1 = PageDomainModel(
    page = 1,
    totalPages = 2,
    results = listOf(
        ReviewDomainModel(
            id = "1"
        ),
        ReviewDomainModel(
            id = "2"
        )
    )
)
private val REVIEW_PAGE_2 = PageDomainModel(
    page = 2,
    totalPages = 2,
    results = listOf(
        ReviewDomainModel(
            id = "3"
        )
    )
)
private val CAST_DETAILED_VIEW_MODEL = CastViewModel(
    id = 1,
    name = "Carlos",
    character = "Batman",
    birthday = "1990-07-13"
)
private val MOVIE_VIEW_MODEL = WorkViewModel(
    id = 1,
    title = "Batman",
    originalTitle = "Batman",
    type = WorkType.MOVIE
)

private fun aWorkViewModel(favorite: Boolean = false) = WorkViewModel(
    id = 1,
    title = "Title",
    type = WorkType.MOVIE,
    isFavorite = favorite
)

class WorkDetailsPresenterTest {

    private val view: WorkDetailsPresenter.View = mock()
    private val setFavoriteUseCase: SetFavoriteUseCase = mock()
    private val getRecommendationByWorkUseCase: GetRecommendationByWorkUseCase = mock()
    private val getSimilarByWorkUseCase: GetSimilarByWorkUseCase = mock()
    private val getReviewByWorkUseCase: GetReviewByWorkUseCase = mock()
    private val getWorkDetailsUseCase: GetWorkDetailsUseCase = mock()
    private val workDetailsRoute: WorkDetailsRoute = mock()
    private val castDetailsRoute: CastDetailsRoute = mock()
    private val rxSchedulerTest = RxScheduler(
        Schedulers.trampoline(),
        Schedulers.trampoline(),
        Schedulers.trampoline()
    )

    @Test
    fun `should set a work as favorite`() {
        val workViewModel = aWorkViewModel()
        val presenter = aPresenter(workViewModel)

        whenever(setFavoriteUseCase(workViewModel))
            .thenReturn(Completable.complete())

        presenter.setFavorite()

        verify(view, only()).resultSetFavoriteMovie(true)
    }

    @Test
    fun `should remove a work from favorite`() {
        val workViewModel = aWorkViewModel(favorite = true)
        val presenter = aPresenter(workViewModel)

        whenever(setFavoriteUseCase(workViewModel))
            .thenReturn(Completable.complete())

        presenter.setFavorite()

        verify(view, only()).resultSetFavoriteMovie(false)
    }

    @Test
    fun `should return false if a error happens while setting a work as favorite`() {
        val workViewModel = aWorkViewModel(favorite = true)
        val presenter = aPresenter(workViewModel)

        whenever(setFavoriteUseCase(workViewModel))
            .thenReturn(Completable.error(Throwable()))

        presenter.setFavorite()

        verify(view, only()).resultSetFavoriteMovie(false)
    }

    @Test
    fun `should return the right data when loading the work details`() {
        val workViewModel = aWorkViewModel()
        val presenter = aPresenter(workViewModel)

        val recommendedWorks = RECOMMENDED_PAGE_1.results
            ?.map { it.toViewModel() }
            ?: emptyList()
        val similarWorks = SIMILAR_PAGE_1.results
            ?.map { it.toViewModel() }
            ?: emptyList()
        val reviews = REVIEW_PAGE_1.results
            ?.map { it.toViewModel() }
            ?: emptyList()

        whenever(getWorkDetailsUseCase(workViewModel))
            .thenReturn(Single.just(GetWorkDetailsUseCase.WorkDetailsDomainWrapper(false, null, null, RECOMMENDED_PAGE_1, SIMILAR_PAGE_1, REVIEW_PAGE_1)))

        presenter.loadData()

        inOrder(view) {
            verify(view).showProgress()
            verify(view).dataLoaded(false, reviews, null, null, recommendedWorks, similarWorks)
            verify(view).hideProgress()
            verifyNoMoreInteractions()
        }
    }

    @Test
    fun `should show an error message if a error happens while loading the work details`() {
        val workViewModel = aWorkViewModel()
        val presenter = aPresenter(workViewModel)

        whenever(getWorkDetailsUseCase(workViewModel))
            .thenReturn(Single.error(Throwable()))

        presenter.loadData()

        inOrder(view) {
            verify(view).showProgress()
            verify(view).errorWorkDetailsLoaded()
            verify(view).hideProgress()
            verifyNoMoreInteractions()
        }
    }

    @Test
    fun `should load the recommended works and show them when select the last item and there are more pages`() {
        val workViewModel = aWorkViewModel()
        val presenter = aPresenter(workViewModel)

        val recommendedWorksPage1 = RECOMMENDED_PAGE_1.results
            ?.map { it.toViewModel() }
            ?: emptyList()
        val recommendedWorksPage2 = RECOMMENDED_PAGE_2.results
            ?.map { it.toViewModel() }
            ?: emptyList()
        val similarWorks = SIMILAR_PAGE_1.results
            ?.map { it.toViewModel() }
            ?: emptyList()
        val reviews = REVIEW_PAGE_1.results
            ?.map { it.toViewModel() }
            ?: emptyList()

        whenever(getWorkDetailsUseCase(workViewModel))
            .thenReturn(Single.just(GetWorkDetailsUseCase.WorkDetailsDomainWrapper(false, null, null, RECOMMENDED_PAGE_1, SIMILAR_PAGE_1, REVIEW_PAGE_1)))
        whenever(getRecommendationByWorkUseCase(workViewModel.type, workViewModel.id, 2))
            .thenReturn(Single.just(RECOMMENDED_PAGE_2))

        presenter.loadData()

        inOrder(view) {
            verify(view).showProgress()
            verify(view).dataLoaded(false, reviews, null, null, recommendedWorksPage1, similarWorks)
            verify(view).hideProgress()
        }

        presenter.recommendationItemSelected(recommendedWorksPage1[1])

        verify(view).recommendationLoaded(recommendedWorksPage1 + recommendedWorksPage2)
        verifyNoMoreInteractions(view)
    }

    @Test
    fun `should not load the recommended works when not select the last item`() {
        val workViewModel = aWorkViewModel()
        val presenter = aPresenter(workViewModel)

        val recommendedWorksPage1 = RECOMMENDED_PAGE_1.results
            ?.map { it.toViewModel() }
            ?: emptyList()
        val similarWorks = SIMILAR_PAGE_1.results
            ?.map { it.toViewModel() }
            ?: emptyList()
        val reviews = REVIEW_PAGE_1.results
            ?.map { it.toViewModel() }
            ?: emptyList()

        whenever(getWorkDetailsUseCase(workViewModel))
            .thenReturn(Single.just(GetWorkDetailsUseCase.WorkDetailsDomainWrapper(false, null, null, RECOMMENDED_PAGE_1, SIMILAR_PAGE_1, REVIEW_PAGE_1)))

        presenter.loadData()

        inOrder(view) {
            verify(view).showProgress()
            verify(view).dataLoaded(false, reviews, null, null, recommendedWorksPage1, similarWorks)
            verify(view).hideProgress()
        }

        presenter.recommendationItemSelected(recommendedWorksPage1[0])

        verifyNoMoreInteractions(view)
    }

    @Test
    fun `should not load the recommended works when select the last item and there is no more pages`() {
        val workViewModel = aWorkViewModel()
        val presenter = aPresenter(workViewModel)

        val recommendedWorksPage2 = RECOMMENDED_PAGE_2.results
            ?.map { it.toViewModel() }
            ?: emptyList()
        val similarWorks = SIMILAR_PAGE_1.results
            ?.map { it.toViewModel() }
            ?: emptyList()
        val reviews = REVIEW_PAGE_1.results
            ?.map { it.toViewModel() }
            ?: emptyList()

        whenever(getWorkDetailsUseCase(workViewModel))
            .thenReturn(Single.just(GetWorkDetailsUseCase.WorkDetailsDomainWrapper(false, null, null, RECOMMENDED_PAGE_2, SIMILAR_PAGE_1, REVIEW_PAGE_1)))

        presenter.loadData()

        inOrder(view) {
            verify(view).showProgress()
            verify(view).dataLoaded(false, reviews, null, null, recommendedWorksPage2, similarWorks)
            verify(view).hideProgress()
        }

        presenter.recommendationItemSelected(recommendedWorksPage2[0])

        verifyNoMoreInteractions(view)
    }

    @Test
    fun `should not show any data when a error happens while loading the recommended works`() {
        val workViewModel = aWorkViewModel()
        val presenter = aPresenter(workViewModel)

        val recommendedWorksPage1 = RECOMMENDED_PAGE_1.results
            ?.map { it.toViewModel() }
            ?: emptyList()
        val similarWorks = SIMILAR_PAGE_1.results
            ?.map { it.toViewModel() }
            ?: emptyList()
        val reviews = REVIEW_PAGE_1.results
            ?.map { it.toViewModel() }
            ?: emptyList()

        whenever(getWorkDetailsUseCase(workViewModel))
            .thenReturn(Single.just(GetWorkDetailsUseCase.WorkDetailsDomainWrapper(false, null, null, RECOMMENDED_PAGE_1, SIMILAR_PAGE_1, REVIEW_PAGE_1)))
        whenever(getRecommendationByWorkUseCase(workViewModel.type, workViewModel.id, 2))
            .thenReturn(Single.error(Throwable()))

        presenter.loadData()

        inOrder(view) {
            verify(view).showProgress()
            verify(view).dataLoaded(false, reviews, null, null, recommendedWorksPage1, similarWorks)
            verify(view).hideProgress()
        }

        presenter.recommendationItemSelected(recommendedWorksPage1[1])

        verifyNoMoreInteractions(view)
    }

    @Test
    fun `should load the similar works and show them when select the last item and there are more pages`() {
        val workViewModel = aWorkViewModel()
        val presenter = aPresenter(workViewModel)

        val recommendedWorksPage1 = RECOMMENDED_PAGE_1.results
            ?.map { it.toViewModel() }
            ?: emptyList()
        val similarWorksPage1 = SIMILAR_PAGE_1.results
            ?.map { it.toViewModel() }
            ?: emptyList()
        val similarWorksPage2 = SIMILAR_PAGE_2.results
            ?.map { it.toViewModel() }
            ?: emptyList()
        val reviews = REVIEW_PAGE_1.results
            ?.map { it.toViewModel() }
            ?: emptyList()

        whenever(getWorkDetailsUseCase(workViewModel))
            .thenReturn(Single.just(GetWorkDetailsUseCase.WorkDetailsDomainWrapper(false, null, null, RECOMMENDED_PAGE_1, SIMILAR_PAGE_1, REVIEW_PAGE_1)))
        whenever(getSimilarByWorkUseCase(workViewModel.type, workViewModel.id, 2))
            .thenReturn(Single.just(SIMILAR_PAGE_2))

        presenter.loadData()

        inOrder(view) {
            verify(view).showProgress()
            verify(view).dataLoaded(false, reviews, null, null, recommendedWorksPage1, similarWorksPage1)
            verify(view).hideProgress()
        }

        presenter.similarItemSelected(similarWorksPage1[1])

        verify(view).similarLoaded(similarWorksPage1 + similarWorksPage2)
        verifyNoMoreInteractions(view)
    }

    @Test
    fun `should not load the similar works when not select the last item`() {
        val workViewModel = aWorkViewModel()
        val presenter = aPresenter(workViewModel)

        val recommendedWorksPage1 = RECOMMENDED_PAGE_1.results
            ?.map { it.toViewModel() }
            ?: emptyList()
        val similarWorksPage1 = SIMILAR_PAGE_1.results
            ?.map { it.toViewModel() }
            ?: emptyList()
        val reviews = REVIEW_PAGE_1.results
            ?.map { it.toViewModel() }
            ?: emptyList()

        whenever(getWorkDetailsUseCase(workViewModel))
            .thenReturn(Single.just(GetWorkDetailsUseCase.WorkDetailsDomainWrapper(false, null, null, RECOMMENDED_PAGE_1, SIMILAR_PAGE_1, REVIEW_PAGE_1)))

        presenter.loadData()

        inOrder(view) {
            verify(view).showProgress()
            verify(view).dataLoaded(false, reviews, null, null, recommendedWorksPage1, similarWorksPage1)
            verify(view).hideProgress()
        }

        presenter.similarItemSelected(similarWorksPage1[0])

        verifyNoMoreInteractions(view)
    }

    @Test
    fun `should not load the similar works when select the last item and there is no more pages`() {
        val workViewModel = aWorkViewModel()
        val presenter = aPresenter(workViewModel)

        val recommendedWorksPage1 = RECOMMENDED_PAGE_1.results
            ?.map { it.toViewModel() }
            ?: emptyList()
        val similarWorksPage2 = SIMILAR_PAGE_2.results
            ?.map { it.toViewModel() }
            ?: emptyList()
        val reviews = REVIEW_PAGE_1.results
            ?.map { it.toViewModel() }
            ?: emptyList()

        whenever(getWorkDetailsUseCase(workViewModel))
            .thenReturn(Single.just(GetWorkDetailsUseCase.WorkDetailsDomainWrapper(false, null, null, RECOMMENDED_PAGE_1, SIMILAR_PAGE_2, REVIEW_PAGE_1)))

        presenter.loadData()

        inOrder(view) {
            verify(view).showProgress()
            verify(view).dataLoaded(false, reviews, null, null, recommendedWorksPage1, similarWorksPage2)
            verify(view).hideProgress()
        }

        presenter.similarItemSelected(similarWorksPage2[0])

        verifyNoMoreInteractions(view)
    }

    @Test
    fun `should not show any data when a error happens while loading the similar works`() {
        val workViewModel = aWorkViewModel()
        val presenter = aPresenter(workViewModel)

        val recommendedWorksPage1 = RECOMMENDED_PAGE_1.results
            ?.map { it.toViewModel() }
            ?: emptyList()
        val similarWorksPage1 = SIMILAR_PAGE_1.results
            ?.map { it.toViewModel() }
            ?: emptyList()
        val reviews = REVIEW_PAGE_1.results
            ?.map { it.toViewModel() }
            ?: emptyList()

        whenever(getWorkDetailsUseCase(workViewModel))
            .thenReturn(Single.just(GetWorkDetailsUseCase.WorkDetailsDomainWrapper(false, null, null, RECOMMENDED_PAGE_1, SIMILAR_PAGE_1, REVIEW_PAGE_1)))
        whenever(getSimilarByWorkUseCase(workViewModel.type, workViewModel.id, 2))
            .thenReturn(Single.error(Throwable()))

        presenter.loadData()

        inOrder(view) {
            verify(view).showProgress()
            verify(view).dataLoaded(false, reviews, null, null, recommendedWorksPage1, similarWorksPage1)
            verify(view).hideProgress()
        }

        presenter.similarItemSelected(similarWorksPage1[1])

        verifyNoMoreInteractions(view)
    }

    @Test
    fun `should load the reviews and show them when select the last item and there are more pages`() {
        val workViewModel = aWorkViewModel()
        val presenter = aPresenter(workViewModel)

        val recommendedWorksPage1 = RECOMMENDED_PAGE_1.results
            ?.map { it.toViewModel() }
            ?: emptyList()
        val similarWorksPage1 = SIMILAR_PAGE_1.results
            ?.map { it.toViewModel() }
            ?: emptyList()
        val reviewsPage1 = REVIEW_PAGE_1.results
            ?.map { it.toViewModel() }
            ?: emptyList()
        val reviewsPage2 = REVIEW_PAGE_2.results
            ?.map { it.toViewModel() }
            ?: emptyList()

        whenever(getWorkDetailsUseCase(workViewModel))
            .thenReturn(Single.just(GetWorkDetailsUseCase.WorkDetailsDomainWrapper(false, null, null, RECOMMENDED_PAGE_1, SIMILAR_PAGE_1, REVIEW_PAGE_1)))
        whenever(getReviewByWorkUseCase(workViewModel.type, workViewModel.id, 2))
            .thenReturn(Single.just(REVIEW_PAGE_2))

        presenter.loadData()

        inOrder(view) {
            verify(view).showProgress()
            verify(view).dataLoaded(false, reviewsPage1, null, null, recommendedWorksPage1, similarWorksPage1)
            verify(view).hideProgress()
        }

        presenter.reviewItemSelected(reviewsPage1[1])

        verify(view).reviewLoaded(reviewsPage1 + reviewsPage2)
        verifyNoMoreInteractions(view)
    }

    @Test
    fun `should not load the reviews when not select the last item`() {
        val workViewModel = aWorkViewModel()
        val presenter = aPresenter(workViewModel)

        val recommendedWorksPage1 = RECOMMENDED_PAGE_1.results
            ?.map { it.toViewModel() }
            ?: emptyList()
        val similarWorksPage1 = SIMILAR_PAGE_1.results
            ?.map { it.toViewModel() }
            ?: emptyList()
        val reviewsPage1 = REVIEW_PAGE_1.results
            ?.map { it.toViewModel() }
            ?: emptyList()

        whenever(getWorkDetailsUseCase(workViewModel))
            .thenReturn(Single.just(GetWorkDetailsUseCase.WorkDetailsDomainWrapper(false, null, null, RECOMMENDED_PAGE_1, SIMILAR_PAGE_1, REVIEW_PAGE_1)))

        presenter.loadData()

        inOrder(view) {
            verify(view).showProgress()
            verify(view).dataLoaded(false, reviewsPage1, null, null, recommendedWorksPage1, similarWorksPage1)
            verify(view).hideProgress()
        }

        presenter.reviewItemSelected(reviewsPage1[0])

        verifyNoMoreInteractions(view)
    }

    @Test
    fun `should not load the reviews when select the last item and there is no more pages`() {
        val workViewModel = aWorkViewModel()
        val presenter = aPresenter(workViewModel)

        val recommendedWorksPage1 = RECOMMENDED_PAGE_1.results
            ?.map { it.toViewModel() }
            ?: emptyList()
        val similarWorksPage1 = SIMILAR_PAGE_1.results
            ?.map { it.toViewModel() }
            ?: emptyList()
        val reviewsPage2 = REVIEW_PAGE_2.results
            ?.map { it.toViewModel() }
            ?: emptyList()

        whenever(getWorkDetailsUseCase(workViewModel))
            .thenReturn(Single.just(GetWorkDetailsUseCase.WorkDetailsDomainWrapper(false, null, null, RECOMMENDED_PAGE_1, SIMILAR_PAGE_1, REVIEW_PAGE_2)))

        presenter.loadData()

        inOrder(view) {
            verify(view).showProgress()
            verify(view).dataLoaded(false, reviewsPage2, null, null, recommendedWorksPage1, similarWorksPage1)
            verify(view).hideProgress()
        }

        presenter.reviewItemSelected(reviewsPage2[0])

        verifyNoMoreInteractions(view)
    }

    @Test
    fun `should not show any data when a error happens while loading the reviews`() {
        val workViewModel = aWorkViewModel()
        val presenter = aPresenter(workViewModel)

        val recommendedWorksPage1 = RECOMMENDED_PAGE_1.results
            ?.map { it.toViewModel() }
            ?: emptyList()
        val similarWorksPage1 = SIMILAR_PAGE_1.results
            ?.map { it.toViewModel() }
            ?: emptyList()
        val reviewsPage1 = REVIEW_PAGE_1.results
            ?.map { it.toViewModel() }
            ?: emptyList()

        whenever(getWorkDetailsUseCase(workViewModel))
            .thenReturn(Single.just(GetWorkDetailsUseCase.WorkDetailsDomainWrapper(false, null, null, RECOMMENDED_PAGE_1, SIMILAR_PAGE_1, REVIEW_PAGE_1)))
        whenever(getReviewByWorkUseCase(workViewModel.type, workViewModel.id, 2))
            .thenReturn(Single.error(Throwable()))

        presenter.loadData()

        inOrder(view) {
            verify(view).showProgress()
            verify(view).dataLoaded(false, reviewsPage1, null, null, recommendedWorksPage1, similarWorksPage1)
            verify(view).hideProgress()
        }

        presenter.reviewItemSelected(reviewsPage1[1])

        verifyNoMoreInteractions(view)
    }

    @Test
    fun `should open work details when a work is clicked`() {
        val workViewModel = aWorkViewModel()
        val presenter = aPresenter(workViewModel)
        val itemViewHolder: Presenter.ViewHolder = mock()
        val route: Route = mock()

        whenever(workDetailsRoute.buildWorkDetailRoute(MOVIE_VIEW_MODEL))
            .thenReturn(route)

        presenter.workClicked(itemViewHolder, MOVIE_VIEW_MODEL)

        verify(view, only()).openWorkDetails(itemViewHolder, route)
    }

    @Test
    fun `should open cast details when a cast is clicked`() {
        val workViewModel = aWorkViewModel()
        val presenter = aPresenter(workViewModel)
        val itemViewHolder: Presenter.ViewHolder = mock()
        val intent: Intent = mock()

        whenever(castDetailsRoute.buildCastDetailIntent(CAST_DETAILED_VIEW_MODEL))
            .thenReturn(intent)

        presenter.castClicked(itemViewHolder, CAST_DETAILED_VIEW_MODEL)

        verify(view, only()).openCastDetails(itemViewHolder, intent)
    }

    @Test
    fun `should open the video when a video is clicked`() {
        val workViewModel = aWorkViewModel()
        val presenter = aPresenter(workViewModel)
        val videoViewModel = VideoViewModel()

        presenter.videoClicked(videoViewModel)

        verify(view, only()).openVideo(videoViewModel)
    }

    private fun aPresenter(workViewModel: WorkViewModel) = WorkDetailsPresenter(
        view,
        workViewModel,
        setFavoriteUseCase,
        getRecommendationByWorkUseCase,
        getSimilarByWorkUseCase,
        getReviewByWorkUseCase,
        getWorkDetailsUseCase,
        workDetailsRoute,
        castDetailsRoute,
        rxSchedulerTest
    )
}
