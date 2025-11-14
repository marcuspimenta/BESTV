package com.pimenta.bestv.workbrowse.domain

import com.pimenta.bestv.model.presentation.mapper.toViewModel
import com.pimenta.bestv.workbrowse.presentation.model.ContentSection
import com.pimenta.bestv.workbrowse.presentation.model.PaginationState
import com.pimenta.bestv.workbrowse.presentation.model.TopWorkTypeViewModel
import com.pimenta.bestv.workbrowse.presentation.model.topMoviesTypes
import com.pimenta.bestv.workbrowse.presentation.model.topTvShowTypes
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject

class GetSectionDetailsUseCase @Inject constructor(
    private val loadWorkByTypeUseCase: LoadWorkByTypeUseCase
) {

    suspend operator fun invoke(): SectionDetails = coroutineScope {
        SectionDetails(
            movieSectionDetails = topMoviesTypes.getWorks().awaitAll(),
            tvSectionDetails = topTvShowTypes.getWorks().awaitAll(),
        )
    }

    private suspend fun List<TopWorkTypeViewModel>.getWorks() = coroutineScope {
        map { type ->
            async {
                val resultPage = loadWorkByTypeUseCase(1, type).toViewModel()
                ContentSection.TopContent(
                    type = type,
                    works = resultPage.results,
                    page = PaginationState(
                        currentPage = resultPage.page,
                        totalPages = resultPage.totalPages
                    )
                )
            }
        }
    }
}

data class SectionDetails(
    val movieSectionDetails: List<ContentSection>,
    val tvSectionDetails: List<ContentSection>
)