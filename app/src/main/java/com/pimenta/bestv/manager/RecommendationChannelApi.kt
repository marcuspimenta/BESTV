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

package com.pimenta.bestv.manager

import android.app.Application
import android.content.ContentUris
import android.net.Uri
import androidx.tvprovider.media.tv.Channel
import androidx.tvprovider.media.tv.PreviewProgram
import androidx.tvprovider.media.tv.TvContractCompat
import com.pimenta.bestv.R
import com.pimenta.bestv.common.presentation.model.WorkViewModel
import com.pimenta.bestv.feature.workdetail.ui.WorkDetailsActivity
import io.reactivex.Single


/**
 * Created by marcus on 23-04-2019.
 */
class RecommendationChannelApi constructor(
        private val application: Application,
        private val preferenceManager: PreferenceManager
) : RecommendationManager {

    override fun loadRecommendations(works: List<WorkViewModel>?): Single<Boolean> = Single.create {
        works?.forEach { workViewModel ->
            val programBuilder = PreviewProgram.Builder()
            programBuilder.setChannelId(getChannelId())
                    .setType(TvContractCompat.PreviewPrograms.TYPE_CLIP)
                    .setTitle(workViewModel.title)
                    .setDescription(workViewModel.overview)
                    .setPosterArtUri(Uri.parse(workViewModel.backdropUrl))
                    .setIntent(WorkDetailsActivity.newInstance(application, workViewModel).apply {
                        action = workViewModel.id.toString()
                    })
                    .setInternalProviderId(workViewModel.id.toString())

            application.contentResolver.insert(TvContractCompat.PreviewPrograms.CONTENT_URI,
                    programBuilder.build().toContentValues())
        }
        it.onSuccess(true)
    }

    private fun getChannelId() = preferenceManager.getLongFromPersistence(CHANNEL_ID_KEY, 0L)
            .takeIf { it != 0L }
            ?: run {
                val channelBuilder = Channel.Builder()
                channelBuilder.setType(TvContractCompat.Channels.TYPE_PREVIEW)
                        .setDisplayName(application.getString(R.string.app_name))

                val channelUri = application.contentResolver.insert(
                        TvContractCompat.Channels.CONTENT_URI,
                        channelBuilder.build().toContentValues()
                )

                val channelId = ContentUris.parseId(channelUri)
                preferenceManager.applyLongToPersistence(CHANNEL_ID_KEY, channelId)
                channelId
            }

    companion object {
        const val CHANNEL_ID_KEY = "CHANNEL_ID_KEY"
    }
}