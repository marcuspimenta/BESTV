/*
 * Copyright (C) 2018 Marcus Pimenta
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 */

package com.pimenta.bestv.workdetail.presentation.ui.compose

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import org.jetbrains.compose.resources.stringResource
import androidx.compose.ui.text.style.TextAlign
import com.pimenta.bestv.presentation.theme.BESTVTheme
import com.pimenta.bestv.workdetail.resources.Res
import com.pimenta.bestv.workdetail.presentation.model.WorkMetadataViewModel
import com.pimenta.bestv.workdetail.resources.budget
import com.pimenta.bestv.workdetail.resources.keywords
import com.pimenta.bestv.workdetail.resources.original_language
import com.pimenta.bestv.workdetail.resources.revenue
import com.pimenta.bestv.workdetail.resources.work_status

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun WorkMetadataDetails(
    metadata: WorkMetadataViewModel,
    modifier: Modifier = Modifier,
    alignToEnd: Boolean = false,
) {
    if (!metadata.hasContent) return

    val horizontalAlignment = if (alignToEnd) Alignment.End else Alignment.Start
    val chipArrangement = if (alignToEnd) {
        Arrangement.spacedBy(BESTVTheme.scale.s040, Alignment.End)
    } else {
        Arrangement.spacedBy(BESTVTheme.scale.s040)
    }

    Column(
        horizontalAlignment = horizontalAlignment,
        verticalArrangement = Arrangement.spacedBy(BESTVTheme.scale.s080),
        modifier = modifier.fillMaxWidth(),
    ) {
        MetadataField(stringResource(Res.string.work_status), metadata.status, alignToEnd)
        MetadataField(stringResource(Res.string.original_language), metadata.originalLanguage, alignToEnd)
        MetadataField(stringResource(Res.string.budget), metadata.budget, alignToEnd)
        MetadataField(stringResource(Res.string.revenue), metadata.revenue, alignToEnd)

        if (metadata.keywords.isNotEmpty()) {
            Column(
                horizontalAlignment = horizontalAlignment,
                verticalArrangement = Arrangement.spacedBy(BESTVTheme.scale.s040),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(
                    text = stringResource(Res.string.keywords),
                    style = MaterialTheme.typography.titleSmall,
                    color = BESTVTheme.colors.white,
                    textAlign = if (alignToEnd) TextAlign.End else TextAlign.Start,
                )
                FlowRow(
                    horizontalArrangement = chipArrangement,
                    verticalArrangement = Arrangement.spacedBy(BESTVTheme.scale.s040),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    metadata.keywords.forEach { keyword ->
                        Surface(
                            color = BESTVTheme.colors.searchFieldSurface,
                            shape = RoundedCornerShape(BESTVTheme.scale.s040),
                        ) {
                            Text(
                                text = keyword,
                                style = MaterialTheme.typography.bodySmall,
                                color = BESTVTheme.colors.white,
                                modifier = Modifier.padding(
                                    horizontal = BESTVTheme.scale.s060,
                                    vertical = BESTVTheme.scale.s020,
                                ),
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MetadataField(
    label: String,
    value: String?,
    alignToEnd: Boolean,
) {
    if (value.isNullOrBlank()) return

    Column(
        horizontalAlignment = if (alignToEnd) Alignment.End else Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(BESTVTheme.scale.s020),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.titleSmall,
            color = BESTVTheme.colors.white,
            textAlign = if (alignToEnd) TextAlign.End else TextAlign.Start,
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = BESTVTheme.colors.secondaryInfoText,
            textAlign = if (alignToEnd) TextAlign.End else TextAlign.Start,
        )
    }
}
