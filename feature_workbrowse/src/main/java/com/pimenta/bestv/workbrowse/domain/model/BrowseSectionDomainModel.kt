package com.pimenta.bestv.workbrowse.domain.model

import com.pimenta.bestv.model.domain.WorkDomainModel

sealed interface BrowseSectionDomainModel {
    val works: List<WorkDomainModel>

    data class TopContent(
        val category: BrowseCategoryKey,
        override val works: List<WorkDomainModel>,
    ) : BrowseSectionDomainModel

    data class GenreContent(
        val genre: GenreDomainModel,
        override val works: List<WorkDomainModel>,
    ) : BrowseSectionDomainModel
}

data class BrowseSectionDetails(
    val movieSections: List<BrowseSectionDomainModel>,
    val tvShowSections: List<BrowseSectionDomainModel>,
) {
    val hasSections: Boolean
        get() = movieSections.isNotEmpty() || tvShowSections.isNotEmpty()
}
