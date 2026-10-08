/*
 * Copyright (C) 2018 Marcus Pimenta
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 */

package com.pimenta.bestv.workdetail.domain

import com.pimenta.bestv.model.presentation.model.WorkType
import com.pimenta.bestv.workdetail.data.repository.MovieRepository
import com.pimenta.bestv.workdetail.data.repository.TvShowRepository

class GetWorkMetadataUseCase(
    private val movieRepository: MovieRepository,
    private val tvShowRepository: TvShowRepository,
) {
    suspend operator fun invoke(workType: WorkType, workId: Int) =
        when (workType) {
            WorkType.MOVIE -> movieRepository.getWorkMetadata(workId)
            WorkType.TV_SHOW -> tvShowRepository.getWorkMetadata(workId)
        }
}
