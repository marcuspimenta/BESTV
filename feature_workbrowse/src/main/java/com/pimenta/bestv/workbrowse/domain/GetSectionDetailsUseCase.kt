package com.pimenta.bestv.workbrowse.domain

import com.pimenta.bestv.model.presentation.mapper.toViewModel
import com.pimenta.bestv.workbrowse.presentation.model.ContentSection
import com.pimenta.bestv.workbrowse.presentation.model.PaginationState
import com.pimenta.bestv.workbrowse.presentation.model.TopWorkTypeViewModel
import com.pimenta.bestv.workbrowse.presentation.model.topMoviesTypes
import com.pimenta.bestv.workbrowse.presentation.model.topTvShowTypes
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject

class GetSectionDetailsUseCase @Inject constructor(
    private val loadWorkByTypeUseCase: LoadWorkByTypeUseCase
) {

    suspend operator fun invoke(): SectionDetails = coroutineScope {
        SectionDetails(
            movieSectionDetails = topMoviesTypes.getWorks().mapNotNull { it.await() },
            tvSectionDetails = topTvShowTypes.getWorks().mapNotNull { it.await() }
        )
    }

    private suspend fun List<TopWorkTypeViewModel>.getWorks() = coroutineScope {
        map { type ->
            async {
                val result = loadWorkByTypeUseCase(1, type).toViewModel()
                result.results?.let {
                    ContentSection.TopContent(
                        type = type,
                        works = it,
                        page = PaginationState(
                            currentPage = result.page,
                            totalPages = result.totalPages
                        )
                    )
                }
            }
        }
    }
}

data class SectionDetails(
    val movieSectionDetails: List<ContentSection>,
    val tvSectionDetails: List<ContentSection>
)