/*
 * Copyright (C) 2018 Marcus Pimenta
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 */

package com.pimenta.bestv.workdetail.presentation.mapper

import com.pimenta.bestv.workdetail.domain.model.WorkMetadataDomainModel
import com.pimenta.bestv.workdetail.presentation.model.WorkMetadataViewModel
import com.pimenta.bestv.platform.currentLocaleTag
import com.pimenta.bestv.platform.localizedLanguageName
import com.pimenta.bestv.platform.formatUsdAmount

fun WorkMetadataDomainModel.toViewModel(
    localeTag: String = currentLocaleTag(),
    fallbackOriginalLanguage: String? = null,
): WorkMetadataViewModel {
    val languageCode = originalLanguage?.takeIf(String::isNotBlank) ?: fallbackOriginalLanguage
    val languageName = languageCode
        ?.takeIf(String::isNotBlank)
        ?.let { localizedLanguageName(it, localeTag) }
        ?.takeIf(String::isNotBlank)

    return WorkMetadataViewModel(
        status = status?.takeIf(String::isNotBlank),
        originalLanguage = languageName,
        budget = budget?.takeIf { it > 0 }?.let(::formatUsdAmount),
        revenue = revenue?.takeIf { it > 0 }?.let(::formatUsdAmount),
        keywords = keywords.map(String::trim).filter(String::isNotBlank).distinct(),
    )
}
