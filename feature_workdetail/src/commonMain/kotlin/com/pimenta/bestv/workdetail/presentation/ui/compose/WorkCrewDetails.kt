package com.pimenta.bestv.workdetail.presentation.ui.compose

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import com.pimenta.bestv.presentation.theme.BESTVTheme
import com.pimenta.bestv.workdetail.presentation.model.CrewViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun WorkCrewDetails(
    crew: List<CrewViewModel>,
    modifier: Modifier = Modifier,
) {
    if (crew.isEmpty()) return

    FlowRow(
        maxItemsInEachRow = 2,
        horizontalArrangement = Arrangement.spacedBy(BESTVTheme.scale.s080),
        verticalArrangement = Arrangement.spacedBy(BESTVTheme.scale.s100),
        modifier = modifier.fillMaxWidth(),
    ) {
        crew.forEach { credit ->
            Column(
                verticalArrangement = Arrangement.spacedBy(BESTVTheme.scale.s020),
                modifier = Modifier
                    .fillMaxWidth(0.44f)
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
