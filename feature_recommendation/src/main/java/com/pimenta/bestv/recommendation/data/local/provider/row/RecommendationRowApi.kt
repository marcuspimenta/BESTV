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

package com.pimenta.bestv.recommendation.data.local.provider.row

import android.app.Application
import android.app.NotificationManager
import android.graphics.drawable.BitmapDrawable
import androidx.recommendation.app.ContentRecommendation
import coil.ImageLoader
import coil.request.ImageRequest
import com.pimenta.bestv.model.domain.WorkDomainModel
import com.pimenta.bestv.model.presentation.mapper.toViewModel
import com.pimenta.bestv.presentation.R as presentationR
import com.pimenta.bestv.recommendation.R as recommendationR
import com.pimenta.bestv.recommendation.data.local.provider.RecommendationProvider
import com.pimenta.bestv.route.workdetail.WorkDetailsRoute

/**
 * Created by marcus on 23-04-2019.
 */
class RecommendationRowApi constructor(
    private val application: Application,
    private val notificationManager: NotificationManager,
    private val workDetailsRoute: WorkDetailsRoute
) : RecommendationProvider {

    override suspend fun loadRecommendations(works: List<WorkDomainModel>?) {
        notificationManager.cancelAll()

        val imageLoader = ImageLoader(application)

        works?.mapNotNull { work -> work.toViewModel() }
            ?.forEach { workViewModel ->
                val request = ImageRequest.Builder(application)
                    .data(workViewModel.posterUrl)
                    .size(
                        application.resources.getDimensionPixelSize(presentationR.dimen.movie_card_width),
                        application.resources.getDimensionPixelSize(presentationR.dimen.movie_card_height)
                    )
                    .build()

                val result = imageLoader.execute(request)
                val cardBitmap = (result.drawable as? BitmapDrawable)?.bitmap

                val contentRecommendation = ContentRecommendation.Builder()
                    .setAutoDismiss(true)
                    .setIdTag(workViewModel.id.toString())
                    .setGroup(application.getString(presentationR.string.app_name))
                    //.setBadgeIcon(recommendationR.drawable.movie_icon)
                    .setTitle(workViewModel.title)
                    .setContentImage(cardBitmap)
                    .setContentTypes(arrayOf(ContentRecommendation.CONTENT_TYPE_MOVIE))
                    .setBackgroundImageUri(workViewModel.backdropUrl)
                    .setText(application.getString(recommendationR.string.popular))
                    .setContentIntentData(
                        ContentRecommendation.INTENT_TYPE_ACTIVITY,
                        workDetailsRoute.buildWorkDetailIntent(workViewModel).apply {
                            // Ensure a unique PendingIntents, otherwise all
                            // recommendations end up with the same PendingIntent
                            action = workViewModel.id.toString()
                        },
                        0, null
                    )
                    .build()

                notificationManager.notify(workViewModel.id, contentRecommendation.getNotificationObject(application))
            }
    }
}
