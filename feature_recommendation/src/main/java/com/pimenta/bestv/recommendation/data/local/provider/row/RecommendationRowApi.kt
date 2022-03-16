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
import androidx.recommendation.app.ContentRecommendation
import com.bumptech.glide.Glide
import com.pimenta.bestv.model.domain.WorkDomainModel
import com.pimenta.bestv.model.presentation.mapper.toViewModel
import com.pimenta.bestv.recommendation.R
import com.pimenta.bestv.recommendation.data.local.provider.RecommendationProvider
import com.pimenta.bestv.route.workdetail.WorkDetailsRoute
import io.reactivex.Completable

/**
 * Created by marcus on 23-04-2019.
 */
class RecommendationRowApi constructor(
    private val application: Application,
    private val notificationManager: NotificationManager,
    private val workDetailsRoute: WorkDetailsRoute
) : RecommendationProvider {

    override fun loadRecommendations(works: List<WorkDomainModel>?): Completable =
        Completable.create {
            notificationManager.cancelAll()

            works?.map { work -> work.toViewModel() }
                ?.forEach { workViewModel ->
                    val cardBitmap = Glide.with(application)
                        .asBitmap()
                        .load(workViewModel.posterUrl)
                        .submit(
                            application.resources.getDimensionPixelSize(R.dimen.movie_card_width),
                            application.resources.getDimensionPixelSize(R.dimen.movie_card_height)
                        )
                        .get()

                    val contentRecommendation = ContentRecommendation.Builder()
                        .setAutoDismiss(true)
                        .setIdTag(workViewModel.id.toString())
                        .setGroup(application.getString(R.string.app_name))
                        .setBadgeIcon(R.drawable.movie)
                        .setTitle(workViewModel.title)
                        .setContentImage(cardBitmap)
                        .setContentTypes(arrayOf(ContentRecommendation.CONTENT_TYPE_MOVIE))
                        .setBackgroundImageUri(workViewModel.backdropUrl)
                        .setText(application.getString(R.string.popular))
                        .setContentIntentData(
                            ContentRecommendation.INTENT_TYPE_ACTIVITY,
                            workDetailsRoute.buildWorkDetailRoute(workViewModel).intent.apply {
                                // Ensure a unique PendingIntents, otherwise all
                                // recommendations end up with the same PendingIntent
                                action = workViewModel.id.toString()
                            },
                            0, null
                        )
                        .build()

                    notificationManager.notify(workViewModel.id, contentRecommendation.getNotificationObject(application))
                }
            it.onComplete()
        }
}
