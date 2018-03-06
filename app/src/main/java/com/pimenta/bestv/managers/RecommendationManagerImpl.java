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

package com.pimenta.bestv.managers;

import android.app.Application;
import android.app.NotificationManager;
import android.graphics.Bitmap;
import android.support.app.recommendation.ContentRecommendation;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.pimenta.bestv.R;
import com.pimenta.bestv.activities.MovieDetailsActivity;
import com.pimenta.bestv.connectors.TmdbConnector;
import com.pimenta.bestv.models.Movie;
import com.pimenta.bestv.models.MovieList;

import java.util.concurrent.ExecutionException;

import javax.inject.Inject;

/**
 * Created by marcus on 06-03-2018.
 */
public class RecommendationManagerImpl implements RecommendationManager {

    private static final String TAG = "RecommendationManager";

    private static final int RECOMMENDATION_NUMBER = 5;

    @Inject
    Application mApplication;

    @Inject
    NotificationManager mNotificationManager;

    @Inject
    TmdbConnector mTmdbConnector;

    @Inject
    public RecommendationManagerImpl() {
    }

    @Override
    public void loadRecommendations() {
        final MovieList movieList = mTmdbConnector.getTopRatedMovies(1);
        if (movieList != null && movieList.getPage() <= movieList.getTotalPages()) {
            int count = 0;
            for (final Movie movie : movieList.getMovies()) {
                try {
                    final Bitmap cardBitmap = Glide.with(mApplication)
                            .asBitmap()
                            .load(String.format(mApplication.getString(R.string.tmdb_load_image_url_api_w780), movie.getPosterPath()))
                            .submit(mApplication.getResources().getDimensionPixelSize(R.dimen.movie_card_width),
                                    mApplication.getResources().getDimensionPixelSize(R.dimen.movie_card_height))
                            .get();

                    final ContentRecommendation contentRecommendation = new ContentRecommendation.Builder()
                            .setIdTag(Integer.toString(movie.getId()))
                            .setGroup(mApplication.getString(R.string.app_name))
                            .setBadgeIcon(R.drawable.movie)
                            .setTitle(movie.getTitle())
                            .setContentImage(cardBitmap)
                            .setBackgroundImageUri(movie.getBackdropPath())
                            .setText(mApplication.getString(R.string.top_rated))
                            .setContentIntentData(ContentRecommendation.INTENT_TYPE_ACTIVITY, MovieDetailsActivity.newInstance(mApplication, movie),
                                    0, null)
                            .build();

                    mNotificationManager.notify(movie.getId(), contentRecommendation.getNotificationObject(mApplication));
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
}