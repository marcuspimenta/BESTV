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

package com.pimenta.bestv.manager.recommendation.channel

import android.app.Application
import android.content.ContentUris
import android.net.Uri
import androidx.tvprovider.media.tv.Channel
import androidx.tvprovider.media.tv.PreviewProgram
import androidx.tvprovider.media.tv.TvContractCompat
import com.pimenta.bestv.R
import com.pimenta.bestv.common.presentation.model.WorkViewModel
import com.pimenta.bestv.common.setting.Const
import com.pimenta.bestv.manager.preference.PreferenceManager
import com.pimenta.bestv.manager.recommendation.RecommendationManager
import io.reactivex.Completable


/**
 * Created by marcus on 23-04-2019.
 */
class RecommendationChannelApi constructor(
        private val application: Application,
        private val preferenceManager: PreferenceManager
) : RecommendationManager {

    override fun loadRecommendations(works: List<WorkViewModel>?): Completable =
            Completable.create {
                val channelId = getChannelId()

                works?.forEach { workViewModel ->
                    val programBuilder = PreviewProgram.Builder()
                    programBuilder.setChannelId(channelId)
                            .setType(TvContractCompat.PreviewPrograms.TYPE_CLIP)
                            .setTitle(workViewModel.title)
                            .setDescription(workViewModel.overview)
                            .setPosterArtUri(Uri.parse(workViewModel.backdropUrl))
                            .setIntentUri(buildProgramUri(workViewModel))
                            .setInternalProviderId(workViewModel.id.toString())

                    preferenceManager.getLongFromPersistence(workViewModel.id.toString(), 0L)
                            .takeUnless { it == 0L }
                            ?.let {
                                application.contentResolver.update(
                                        TvContractCompat.buildProgramUri(channelId),
                                        programBuilder.build().toContentValues(),
                                        null,
                                        null
                                )
                            }
                            ?: run {
                                val programUri = application.contentResolver.insert(
                                        TvContractCompat.PreviewPrograms.CONTENT_URI,
                                        programBuilder.build().toContentValues()
                                )
                                val programId = ContentUris.parseId(programUri)

                                preferenceManager.applyLongToPersistence(workViewModel.id.toString(), programId)
                            }

                }
                it.onComplete()
            }

    private fun getChannelId() =
            preferenceManager.getLongFromPersistence(CHANNEL_ID_KEY, 0L)
                    .takeUnless { it == 0L }
                    ?: run {
                        val channelBuilder = Channel.Builder()
                        channelBuilder.setType(TvContractCompat.Channels.TYPE_PREVIEW)
                                .setDisplayName(application.getString(R.string.app_name))

                        val channelUri = application.contentResolver.insert(
                                TvContractCompat.Channels.CONTENT_URI,
                                channelBuilder.build().toContentValues()
                        )

                        val channelId = ContentUris.parseId(channelUri)

                        TvContractCompat.requestChannelBrowsable(application, channelId)

                        preferenceManager.applyLongToPersistence(CHANNEL_ID_KEY, channelId)
                        channelId
                    }

    private fun buildProgramUri(workViewModel: WorkViewModel): Uri =
            Uri.parse(Const.SCHEMA_URI_PREFIX.plus(Const.WORK))
                    .buildUpon()
                    .appendQueryParameter(Const.ID, workViewModel.id.toString())
                    .appendQueryParameter(Const.LANGUAGE, workViewModel.originalLanguage)
                    .appendQueryParameter(Const.OVERVIEW, workViewModel.overview)
                    .appendQueryParameter(Const.BACKGROUND_URL, workViewModel.backdropUrl)
                    .appendQueryParameter(Const.POSTER_URL, workViewModel.posterUrl)
                    .appendQueryParameter(Const.TITLE, workViewModel.title)
                    .appendQueryParameter(Const.ORIGINAL_TITLE, workViewModel.originalTitle)
                    .appendQueryParameter(Const.RELEASE_DATE, workViewModel.releaseDate)
                    .appendQueryParameter(Const.FAVORITE, workViewModel.isFavorite.toString())
                    .appendQueryParameter(Const.TYPE, workViewModel.type.toString())
                    .build()

    companion object {

        const val CHANNEL_ID_KEY = "CHANNEL_ID_KEY"

    }
}