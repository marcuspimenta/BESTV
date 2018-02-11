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

package com.pimenta.bestv.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v17.leanback.app.BackgroundManager;
import android.support.v17.leanback.app.BrowseFragment;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.FocusHighlight;
import android.support.v17.leanback.widget.ImageCardView;
import android.support.v17.leanback.widget.OnItemViewClickedListener;
import android.support.v17.leanback.widget.OnItemViewSelectedListener;
import android.support.v17.leanback.widget.Presenter;
import android.support.v17.leanback.widget.Row;
import android.support.v17.leanback.widget.RowPresenter;
import android.support.v17.leanback.widget.VerticalGridPresenter;
import android.support.v4.app.ActivityOptionsCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pimenta.bestv.activities.MovieDetailsActivity;
import com.pimenta.bestv.fragments.bases.BaseVerticalGridFragment;
import com.pimenta.bestv.models.Movie;
import com.pimenta.bestv.presenters.MovieGridCallback;
import com.pimenta.bestv.presenters.MovieGridPresenter;
import com.pimenta.bestv.widget.MovieCardPresenter;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by marcus on 09-02-2018.
 */
public abstract class AbstractMovieGridFragment extends BaseVerticalGridFragment<MovieGridPresenter> implements MovieGridCallback, BrowseFragment.MainFragmentAdapterProvider {

    private static final String TAG = "AbstractMovieGridFragment";
    private static final int ERROR_FRAGMENT_REQUEST_CODE = 1;

    private static final int BACKGROUND_UPDATE_DELAY = 300;
    private static final int NUMBER_COLUMNS = 4;

    private final Handler mHandler = new Handler();
    private ArrayObjectAdapter mRowsAdapter;
    private final VerticalGridPresenter mVerticalGridPresenter = new VerticalGridPresenter(FocusHighlight.ZOOM_FACTOR_MEDIUM);
    private final BrowseFragment.MainFragmentAdapter<AbstractMovieGridFragment> mMainFragmentAdapter = new BrowseFragment.MainFragmentAdapter<>(this);

    private Timer mBackgroundTimer;
    private BackgroundManager mBackgroundManager;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setupUI();
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        getProgressBarManager().setRootView(container);
        getProgressBarManager().enableProgressBar();
        getProgressBarManager().setInitialDelay(0);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(final View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getMainFragmentAdapter().getFragmentHost().notifyViewCreated(getMainFragmentAdapter());

        getProgressBarManager().show();
        loadData();
    }

    @Override
    public void onDestroy() {
        getProgressBarManager().hide();
        if (mBackgroundTimer != null) {
            mBackgroundTimer.cancel();
        }
        super.onDestroy();
    }

    @Override
    protected MovieGridPresenter getPresenter() {
        return new MovieGridPresenter();
    }

    @Override
    public BrowseFragment.MainFragmentAdapter getMainFragmentAdapter() {
        return mMainFragmentAdapter;
    }

    @Override
    public void onMoviesLoaded(final List<Movie> movies) {
        if (movies != null) {
            for (final Movie movie : movies) {
                mRowsAdapter.add(movie);
            }
        } else {
            final Fragment fragment = ErrorFragment.newInstance();
            fragment.setTargetFragment(this, ERROR_FRAGMENT_REQUEST_CODE);

            getActivity().getFragmentManager().beginTransaction()
                    .add(android.R.id.content, fragment)
                    .addToBackStack(ErrorFragment.TAG)
                    .commit();
        }

        getProgressBarManager().hide();
        getMainFragmentAdapter().getFragmentHost().notifyDataReady(getMainFragmentAdapter());
    }

    @Override
    public void onBackdropImageLoaded(final Drawable drawable) {
        mBackgroundManager.setDrawable(drawable);
    }

    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        switch (requestCode) {
            case ERROR_FRAGMENT_REQUEST_CODE:
                getActivity().getFragmentManager().popBackStackImmediate(ErrorFragment.TAG, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                if (resultCode == Activity.RESULT_OK) {
                    getProgressBarManager().show();
                    loadData();
                }
                break;
        }
    }

    private void setupUI() {
        mBackgroundManager = BackgroundManager.getInstance(getActivity());

        mVerticalGridPresenter.setNumberOfColumns(NUMBER_COLUMNS);
        setGridPresenter(mVerticalGridPresenter);

        mRowsAdapter = new ArrayObjectAdapter(new MovieCardPresenter());
        setAdapter(mRowsAdapter);

        setOnItemViewSelectedListener(new ItemViewSelectedListener());
        setOnItemViewClickedListener(new ItemViewClickedListener());
    }

    abstract void loadData();

    private final class ItemViewSelectedListener implements OnItemViewSelectedListener {

        @Override
        public void onItemSelected(Presenter.ViewHolder itemViewHolder, Object item, RowPresenter.ViewHolder rowViewHolder, Row row) {
            final Movie movie = (Movie) item;
            if (mBackgroundTimer != null) {
                mBackgroundTimer.cancel();
            }
            mBackgroundTimer = new Timer();
            mBackgroundTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    mHandler.post(() -> {
                        mPresenter.loadBackdropImage(movie);
                        mBackgroundTimer.cancel();
                    });
                }
            }, BACKGROUND_UPDATE_DELAY);
        }
    }

    private final class ItemViewClickedListener implements OnItemViewClickedListener {

        @Override
        public void onItemClicked(Presenter.ViewHolder itemViewHolder, Object item, RowPresenter.ViewHolder rowViewHolder, Row row) {
            Movie movie = (Movie) item;
            Intent intent = new Intent(getActivity(), MovieDetailsActivity.class);
            intent.putExtra(MovieDetailsFragment.MOVIE, movie);

            Bundle bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(),
                    ((ImageCardView) itemViewHolder.view).getMainImageView(), MovieDetailsFragment.SHARED_ELEMENT_NAME).toBundle();
            getActivity().startActivity(intent, bundle);
        }
    }
}