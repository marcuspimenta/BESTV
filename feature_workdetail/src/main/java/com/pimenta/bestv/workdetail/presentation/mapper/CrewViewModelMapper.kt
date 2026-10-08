package com.pimenta.bestv.workdetail.presentation.mapper

import com.pimenta.bestv.workdetail.domain.model.CrewDomainModel
import com.pimenta.bestv.workdetail.presentation.model.CrewViewModel

fun CrewDomainModel.toViewModel() = CrewViewModel(id = id, name = name, role = role)
