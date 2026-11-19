/*
 * Copyright (C) 2018 Marcus Pimenta
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 */

package com.pimenta.bestv.workdetail.domain.model

data class WorkMetadataDomainModel(
    val status: String? = null,
    val originalLanguage: String? = null,
    val budget: Long? = null,
    val revenue: Long? = null,
    val keywords: List<String> = emptyList(),
)
