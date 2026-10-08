/*
 * Copyright (C) 2018 Marcus Pimenta
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 */

package com.pimenta.bestv.workdetail.data.remote.mapper

import com.pimenta.bestv.model.data.remote.MovieResponse
import com.pimenta.bestv.model.data.remote.TvShowResponse
import com.pimenta.bestv.workdetail.domain.model.WorkMetadataDomainModel

fun MovieResponse.toWorkMetadataDomainModel() =
    WorkMetadataDomainModel(
        status = status,
        originalLanguage = originalLanguage,
        budget = budget,
        revenue = revenue,
        keywords = keywords?.keywords.orEmpty().mapNotNull { it.name?.takeIf(String::isNotBlank) },
    )

fun TvShowResponse.toWorkMetadataDomainModel() =
    WorkMetadataDomainModel(
        status = status,
        originalLanguage = originalLanguage,
        keywords = keywords?.results.orEmpty().mapNotNull { it.name?.takeIf(String::isNotBlank) },
    )
