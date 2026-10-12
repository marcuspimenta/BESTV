package com.pimenta.bestv.workbrowse.domain.model

import com.pimenta.bestv.model.domain.PageDomainModel
import com.pimenta.bestv.model.domain.WorkDomainModel

sealed interface BrowseSectionDomainModel {
    val page: PageDomainModel<WorkDomainModel>

    data class TopContent(
        val category: BrowseCategoryKey,
        override val page: PageDomainModel<WorkDomainModel>,
    ) : BrowseSectionDomainModel

    data class GenreContent(
        val genre: GenreDomainModel,
        override val page: PageDomainModel<WorkDomainModel>,
    ) : BrowseSectionDomainModel
}

data class BrowseSectionDetails(
    val movieSections: List<BrowseSectionDomainModel>,
    val tvShowSections: List<BrowseSectionDomainModel>,
    val favoriteSections: List<BrowseSectionDomainModel>,
)
