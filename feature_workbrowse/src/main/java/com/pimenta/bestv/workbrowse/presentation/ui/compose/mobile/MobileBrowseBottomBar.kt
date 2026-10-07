/*
 * Copyright (C) 2018 Marcus Pimenta
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 */

package com.pimenta.bestv.workbrowse.presentation.ui.compose.mobile

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.pimenta.bestv.presentation.theme.BESTVTheme
import com.pimenta.bestv.workbrowse.presentation.model.WorkBrowseState.Section
import com.pimenta.bestv.workbrowse.presentation.model.WorkBrowseState.Section.Search

@Composable
internal fun MobileBrowseBottomBar(
    sections: List<Section>,
    selectedSection: Section?,
    onSectionClick: (Section) -> Unit,
    modifier: Modifier = Modifier,
) {
    val visibleSections = sections.filterNot { it is Search }
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(BESTVTheme.scale.s600),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            BESTVTheme.colors.transparent,
                            BESTVTheme.colors.mobileNavigationScrim,
                        ),
                    ),
                ),
        )
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(bottom = BESTVTheme.scale.s040),
            horizontalArrangement = Arrangement.Center,
        ) {
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxWidth(0.60f)
                    .height(BESTVTheme.scale.s320)
                    .clip(RoundedCornerShape(percent = 50))
                    .background(BESTVTheme.colors.mobileNavigationSurface)
                    .padding(BESTVTheme.scale.s020),
            ) {
                val itemWidth = maxWidth / visibleSections.size.coerceAtLeast(1)
                val selectedIndex = visibleSections
                    .indexOf(selectedSection)
                    .coerceAtLeast(0)
                val indicatorOffset by animateDpAsState(
                    targetValue = itemWidth * selectedIndex,
                    animationSpec = tween(durationMillis = 300),
                    label = "selectedNavigationBackground",
                )
                Box(
                    modifier = Modifier
                        .offset(x = indicatorOffset)
                        .width(itemWidth)
                        .fillMaxSize()
                        .clip(RoundedCornerShape(percent = 50))
                        .background(BESTVTheme.colors.mobileNavigationSelectedSurface),
                )
                Row(
                    modifier = Modifier
                        .fillMaxSize(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    visibleSections.forEach { section ->
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxSize()
                                .clip(RoundedCornerShape(percent = 50))
                                .clickable { onSectionClick(section) }
                                .padding(vertical = BESTVTheme.scale.s030),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                        ) {
                            Icon(
                                painter = painterResource(section.iconRes),
                                contentDescription = stringResource(section.titleRes),
                                tint = BESTVTheme.colors.mobileNavigationContent,
                                modifier = Modifier
                                    .size(BESTVTheme.scale.s100),
                            )
                            Text(
                                text = stringResource(section.titleRes),
                                color = BESTVTheme.colors.mobileNavigationContent,
                                style = MaterialTheme.typography.labelMedium,
                                maxLines = 1,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun MobileBrowseBottomBarPreview() {
    BESTVTheme {
        MobileBrowseBottomBar(
            sections = listOf(
                Section.Movies(emptyList()),
                Section.TvShows(emptyList()),
                Section.Favorites(emptyList()),
            ),
            selectedSection = Section.Movies(emptyList()),
            onSectionClick = {},
        )
    }
}
