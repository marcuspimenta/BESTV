package com.pimenta.bestv.workdetail.domain.model

import com.pimenta.bestv.domain.model.CastDomainModel

data class WorkCreditsDomainModel(
    val casts: List<CastDomainModel>?,
    val crew: List<CrewDomainModel>,
)
