package com.pimenta.bestv.workdetail.presentation.mapper

import com.pimenta.bestv.domain.model.WorkDomainModel
import com.pimenta.bestv.presentation.model.WorkType

fun WorkType.toDomainType(): WorkDomainModel.Type =
    when (this) {
        WorkType.MOVIE -> WorkDomainModel.Type.MOVIE
        WorkType.TV_SHOW -> WorkDomainModel.Type.TV_SHOW
    }
