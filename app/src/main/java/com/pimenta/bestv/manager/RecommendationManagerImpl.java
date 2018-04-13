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

package com.pimenta.bestv.manager;

import android.app.Application;
import android.app.NotificationManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.app.recommendation.ContentRecommendation;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.pimenta.bestv.R;
import com.pimenta.bestv.activity.MovieDetailsActivity;
import com.pimenta.bestv.connector.TmdbConnector;
import com.pimenta.bestv.model.Movie;
import com.pimenta.bestv.model.MovieList;

import java.util.concurrent.ExecutionException;

import javax.inject.Inject;

/**
 * Created by marcus on 06-03-2018.
 */
public class RecommendationManagerImpl implements RecommendationManager {

    private static final String TAG = "RecommendationManager";
    private static final int RECOMMENDATION_NUMBER = 5;

    private Application mApplication;
    private NotificationManager mNotificationManager;
    private TmdbConnector mTmdbConnector;

    @Inject
    public RecommendationManagerImpl(Application application, NotificationManager notificationManager, TmdbConnector tmdbConnector) {
        mApplication = application;
        mNotificationManager = notificationManager;
        mTmdbConnector = tmdbConnector;
    }

    @Override
    public void loadRecommendations() {
        mNotificationManager.cancelAll();
        final MovieList movieList = mTmdbConnector.getPopularMovies(1);
        if (movieList != null && movieList.getPage() <= movieList.getTotalPages() && movieList.getMovies() != null) {
            int count = 0;
            for (final Movie movie : movieList.getMovies()) {
                try {
                    int id = Long.valueOf(movie.getTmdbId()).hashCode();

                    final Bitmap cardBitmap = Glide.with(mApplication)
                            .asBitmap()
                            .load(String.format(mApplication.getString(R.string.tmdb_load_image_url_api), movie.getPosterPath()))
                            .submit(mApplication.getResources().getDimensionPixelSize(R.dimen.movie_card_width),
                                    mApplication.getResources().getDimensionPixelSize(R.dimen.movie_card_height))
                            .get();

                    final ContentRecommendation contentRecommendation = new ContentRecommendation.Builder()
                            .setAutoDismiss(true)
                            .setIdTag(Integer.toString(id))
                            .setGroup(mApplication.getString(R.string.app_name))
                            .setBadgeIcon(R.drawable.movie)
                            .setTitle(movie.getTitle())
                            .setContentImage(cardBitmap)
                            .setContentTypes(new String[]{ContentRecommendation.CONTENT_TYPE_MOVIE})
                            .setBackgroundImageUri(String.format(mApplication.getString(R.string.tmdb_load_image_url_api), movie.getBackdropPath()))
                            .setText(mApplication.getString(R.string.popular))
                            .setContentIntentData(ContentRecommendation.INTENT_TYPE_ACTIVITY, buildIntent(movie, id),
                                    0, null)
                            .build();

                    mNotificationManager.notify(id, contentRecommendation.getNotificationObject(mApplication));
                    count++;
                    if (count == RECOMMENDATION_NUMBER) {
                        return;
                    }
                } catch (InterruptedException | ExecutionException e) {
                    Log.e(TAG, "Failed to create a recommendation.", e);
                }
            }
        }
    }

    /**
     * Builds a {@link Intent} to open the movie details when click in a notification
     *
     * @param movie                 {@link Movie}
     * @param notificationId        Notification ID
     * @return                      {@link Intent}
     */
    private Intent buildIntent(Movie movie, int notificationId) {
        final Intent detailsIntent = MovieDetailsActivity.newInstance(mApplication, movie);
        detailsIntent.setAction(Integer.toString(notificationId));
        return detailsIntent;
    }
}