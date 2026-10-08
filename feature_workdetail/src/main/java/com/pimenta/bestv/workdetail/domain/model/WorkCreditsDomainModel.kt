package com.pimenta.bestv.workdetail.domain.model

import com.pimenta.bestv.model.domain.CastDomainModel

data class WorkCreditsDomainModel(
    val casts: List<CastDomainModel>?,
    val crew: List<CrewDomainModel>,
)
