package com.pimenta.bestv.workdetail.presentation.ui.compose

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import com.pimenta.bestv.presentation.theme.BESTVTheme
import com.pimenta.bestv.workdetail.presentation.model.CrewViewModel

@Composable
fun WorkCrewDetails(
    crew: List<CrewViewModel>,
    modifier: Modifier = Modifier,
) {
    if (crew.isEmpty()) return

    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(BESTVTheme.scale.s320),
        modifier = modifier,
    ) {
        itemsIndexed(
            items = crew,
            key = { index, credit -> "${credit.id}-${credit.role}-$index" },
        ) { _, credit ->
            Column(
                verticalArrangement = Arrangement.spacedBy(BESTVTheme.scale.s020),
                modifier = Modifier
                    .widthIn(max = BESTVTheme.scale.s1250)
                    .padding(end = BESTVTheme.scale.s040),
            ) {
                Text(
                    text = credit.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = BESTVTheme.colors.white,
                    fontWeight = FontWeight.Bold,
                    textDecoration = TextDecoration.Underline,
                )
                Text(
                    text = credit.role,
                    style = MaterialTheme.typography.bodyMedium,
                    color = BESTVTheme.colors.white,
                )
            }
        }
    }
}
