/*
 * Copyright (C) 2018 Marcus Pimenta
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package com.pimenta.bestv.recommendation.platform.provider.channel

import android.app.Application
import android.content.ContentUris
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.tvprovider.media.tv.Channel
import androidx.tvprovider.media.tv.ChannelLogoUtils
import androidx.tvprovider.media.tv.PreviewProgram
import androidx.tvprovider.media.tv.TvContractCompat
import com.pimenta.bestv.model.domain.WorkDomainModel
import com.pimenta.bestv.presentation.mapper.toViewModel
import com.pimenta.bestv.presentation.platform.Resource
import com.pimenta.bestv.recommendation.domain.repository.RecommendationRepository
import com.pimenta.bestv.recommendation.data.local.sharedpreferences.LocalSettings
import com.pimenta.bestv.route.workbrowse.WorkBrowseRoute
import com.pimenta.bestv.route.workdetail.WorkDetailsRoute
import com.pimenta.bestv.presentation.R as presentationR
import com.pimenta.bestv.recommendation.R as recommendationR

/**
 * Created by marcus on 23-04-2019.
 */
private const val CHANNEL_ID_KEY = "CHANNEL_ID_KEY"

class RecommendationChannelApi constructor(
    private val application: Application,
    private val localSettings: LocalSettings,
    private val workDetailsRoute: WorkDetailsRoute,
    private val workBrowseRoute: WorkBrowseRoute,
    private val resource: Resource,
) : RecommendationRepository {

    override suspend fun loadRecommendations(works: List<WorkDomainModel>?) {
        val channelId = getChannelId()
        if (channelId == 0L) return

        works
            ?.mapNotNull { work -> work.toViewModel(resource) }
            ?.forEach { workViewModel ->
                val programBuilder = PreviewProgram
                    .Builder()
                    .setChannelId(channelId)
                    .setType(TvContractCompat.PreviewPrograms.TYPE_CLIP)
                    .setTitle(workViewModel.title)
                    .setDescription(workViewModel.overview)
                    .setPosterArtUri(Uri.parse(workViewModel.backdropCardUrl))
                    .setIntent(workDetailsRoute.buildWorkDetailIntent(workViewModel))
                    .setInternalProviderId(workViewModel.id.toString())

                val savedProgramId = localSettings.getLongFromPersistence(workViewModel.id.toString(), 0L)
                val values = programBuilder.build().toContentValues()
                val updated = savedProgramId != 0L &&
                    application.contentResolver.update(
                        ContentUris.withAppendedId(TvContractCompat.PreviewPrograms.CONTENT_URI, savedProgramId),
                        values,
                        null,
                        null,
                    ) > 0
                if (!updated) {
                    application.contentResolver
                        .insert(TvContractCompat.PreviewPrograms.CONTENT_URI, values)
                        ?.let { programUri ->
                            localSettings.applyLongToPersistence(
                                workViewModel.id.toString(),
                                ContentUris.parseId(programUri),
                            )
                        }
                }
            }
    }

    private fun getChannelId() =
        localSettings
            .getLongFromPersistence(CHANNEL_ID_KEY, 0L)
            .takeUnless { it == 0L }
            ?: run {
                val channelBuilder = Channel
                    .Builder()
                    .setType(TvContractCompat.Channels.TYPE_PREVIEW)
                    .setDisplayName(application.getString(recommendationR.string.popular))
                    .setAppLinkIntent(workBrowseRoute.buildWorkBrowseIntent())

                application.contentResolver
                    .insert(
                        TvContractCompat.Channels.CONTENT_URI,
                        channelBuilder.build().toContentValues()
                    )?.let {
                        val channelId = ContentUris.parseId(it)

                        TvContractCompat.requestChannelBrowsable(
                            application,
                            channelId
                        )
                        ChannelLogoUtils.storeChannelLogo(
                            application,
                            channelId,
                            BitmapFactory.decodeResource(application.resources, presentationR.drawable.app_icon)
                        )

                        localSettings.applyLongToPersistence(CHANNEL_ID_KEY, channelId)
                        channelId
                    } ?: 0
            }
}
