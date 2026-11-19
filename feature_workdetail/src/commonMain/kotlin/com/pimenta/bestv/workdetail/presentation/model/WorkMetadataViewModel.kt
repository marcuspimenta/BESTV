/*
 * Copyright (C) 2018 Marcus Pimenta
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 */

package com.pimenta.bestv.workdetail.presentation.model

data class WorkMetadataViewModel(
    val status: String? = null,
    val originalLanguage: String? = null,
    val budget: String? = null,
    val revenue: String? = null,
    val keywords: List<String> = emptyList(),
) {
    val hasContent: Boolean
        get() = status != null || originalLanguage != null || budget != null || revenue != null || keywords.isNotEmpty()
}
