package com.pimenta.bestv.workdetail.presentation.mapper

import com.pimenta.bestv.model.domain.WorkDomainModel
import com.pimenta.bestv.model.presentation.model.WorkType

fun WorkType.toDomainType(): WorkDomainModel.Type =
    when (this) {
        WorkType.MOVIE -> WorkDomainModel.Type.MOVIE
        WorkType.TV_SHOW -> WorkDomainModel.Type.TV_SHOW
    }
