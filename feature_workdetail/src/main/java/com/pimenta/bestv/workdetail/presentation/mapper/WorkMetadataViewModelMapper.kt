/*
 * Copyright (C) 2018 Marcus Pimenta
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 */

package com.pimenta.bestv.workdetail.presentation.mapper

import com.pimenta.bestv.workdetail.domain.model.WorkMetadataDomainModel
import com.pimenta.bestv.workdetail.presentation.model.WorkMetadataViewModel
import java.text.NumberFormat
import java.util.Locale

fun WorkMetadataDomainModel.toViewModel(
    locale: Locale,
    fallbackOriginalLanguage: String? = null,
): WorkMetadataViewModel {
    val languageCode = originalLanguage?.takeIf(String::isNotBlank) ?: fallbackOriginalLanguage
    val languageName = languageCode
        ?.takeIf(String::isNotBlank)
        ?.let { Locale.forLanguageTag(it).getDisplayLanguage(locale) }
        ?.takeIf(String::isNotBlank)

    return WorkMetadataViewModel(
        status = status?.takeIf(String::isNotBlank),
        originalLanguage = languageName,
        budget = budget?.takeIf { it > 0 }?.formatUsd(),
        revenue = revenue?.takeIf { it > 0 }?.formatUsd(),
        keywords = keywords.map(String::trim).filter(String::isNotBlank).distinct(),
    )
}

private fun Long.formatUsd(): String =
    NumberFormat
        .getCurrencyInstance(Locale.US)
        .apply {
            minimumFractionDigits = 0
            maximumFractionDigits = 0
        }.format(this)
