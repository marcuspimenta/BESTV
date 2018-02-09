/*
 * Copyright (C) 2017 The Android Open Source Project
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

package com.pimenta.bestv.fragments;

import android.os.Bundle;
import android.support.v17.leanback.media.MediaPlayerGlue;

import com.pimenta.bestv.fragments.bases.BaseVideoSupportFragment;
import com.pimenta.bestv.models.Movie;
import com.pimenta.bestv.presenters.PlaybackVideoPresenter;

/**
 * Created by marcus on 08-02-2018.
 */
public class PlaybackVideoFragment extends BaseVideoSupportFragment<PlaybackVideoPresenter> {

    private static final String TAG = "PlaybackVideoFragment";

    private MediaPlayerGlue mTransportControlGlue;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final Movie movie = (Movie) getActivity().getIntent().getSerializableExtra(MovieDetailsFragment.MOVIE);

        /*VideoSupportFragmentGlueHost glueHost = new VideoSupportFragmentGlueHost(PlaybackVideoFragment.this);
        mTransportControlGlue = new MediaPlayerGlue(getContext());
        mTransportControlGlue.setMode(MediaPlayerGlue.NO_REPEAT);
        mTransportControlGlue.setHost(glueHost);
        mTransportControlGlue.setTitle(movie.getTitle());
        mTransportControlGlue.setArtist(movie.getDescription());
        mTransportControlGlue.addPlayerCallback(new PlaybackGlue.PlayerCallback() {
            @Override
            public void onPreparedStateChanged(PlaybackGlue glue) {
                if (glue.isPrepared()) {
                    glue.play();
                }
            }
        });
        mTransportControlGlue.setVideoUrl(movie.getVideoUrl());*/
    }

    @Override
    public void onPause() {
        super.onPause();
        /*if (mTransportControlGlue != null) {
            mTransportControlGlue.pause();
        }*/
    }

    @Override
    protected PlaybackVideoPresenter getPresenter() {
        return new PlaybackVideoPresenter();
    }
}