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
import android.app.NotificationManager
import android.content.Intent
import androidx.recommendation.app.ContentRecommendation
import com.bumptech.glide.Glide
import com.pimenta.bestv.BuildConfig
import com.pimenta.bestv.R
import com.pimenta.bestv.repository.entity.Work
import io.reactivex.Single
import timber.log.Timber
import java.util.concurrent.ExecutionException
import javax.inject.Inject

/**
 * Created by marcus on 06-03-2018.
 */
class RecommendationManagerImpl @Inject constructor(
        private val application: Application,
        private val notificationManager: NotificationManager
) : RecommendationManager {

    override fun <T : Work> loadRecommendations(works: List<T>?): Single<Boolean> = Single.create {
        notificationManager.cancelAll()
        var count = 0
        works?.forEach { work ->
            try {
                val id = java.lang.Long.valueOf(work.id.toLong()).hashCode()

                val cardBitmap = Glide.with(application)
                        .asBitmap()
                        .load(String.format(BuildConfig.TMDB_LOAD_IMAGE_BASE_URL, work.posterPath))
                        .submit(application.resources.getDimensionPixelSize(R.dimen.movie_card_width),
                                application.resources.getDimensionPixelSize(R.dimen.movie_card_height))
                        .get()

                val contentRecommendation = ContentRecommendation.Builder()
                        .setAutoDismiss(true)
                        .setIdTag(Integer.toString(id))
                        .setGroup(application.getString(R.string.app_name))
                        .setBadgeIcon(R.drawable.movie)
                        .setTitle(work.title!!)
                        .setContentImage(cardBitmap)
                        .setContentTypes(arrayOf(ContentRecommendation.CONTENT_TYPE_MOVIE))
                        .setBackgroundImageUri(String.format(BuildConfig.TMDB_LOAD_IMAGE_BASE_URL, work.backdropPath))
                        .setText(application.getString(R.string.popular))
                        //.setContentIntentData(ContentRecommendation.INTENT_TYPE_ACTIVITY, buildIntent(work, id), 0, null)
                        .build()

                notificationManager.notify(id, contentRecommendation.getNotificationObject(application))
                count++
                if (count == RECOMMENDATION_NUMBER) {
                    it.onSuccess(true)
                }
            } catch (exception: InterruptedException) {
                Timber.e(exception, "Failed to create a recommendation.")
                it.onError(exception)
            } catch (exception: ExecutionException) {
                Timber.e(exception, "Failed to create a recommendation.")
                it.onError(exception)
            }
        }
        it.onSuccess(false)
    }

    /**
     * Builds a [Intent] to open the movie details when click in a notification
     *
     * @param work           [Work]
     * @param notificationId Notification ID
     * @return [Intent]
     */
    /*private fun buildIntent(work: Work, notificationId: Int): Intent {
        val detailsIntent = WorkDetailsActivity.newInstance(application, work)
        detailsIntent.action = Integer.toString(notificationId)
        return detailsIntent
    }*/

    companion object {

        private const val RECOMMENDATION_NUMBER = 5

    }
}