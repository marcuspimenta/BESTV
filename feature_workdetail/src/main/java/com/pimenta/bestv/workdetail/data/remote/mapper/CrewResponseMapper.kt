package com.pimenta.bestv.workdetail.data.remote.mapper

import com.pimenta.bestv.model.data.remote.CrewResponse
import com.pimenta.bestv.workdetail.domain.model.CrewDomainModel

fun CrewResponse.toDomainModel(): CrewDomainModel? {
    val crewName = name?.takeIf { it.isNotBlank() } ?: return null
    val crewRole = job?.takeIf { it.isNotBlank() } ?: department?.takeIf { it.isNotBlank() } ?: return null
    return CrewDomainModel(id = id, name = crewName, role = crewRole)
}
