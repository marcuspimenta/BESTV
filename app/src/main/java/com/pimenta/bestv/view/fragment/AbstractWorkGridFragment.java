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

package com.pimenta.bestv.view.fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v17.leanback.app.BackgroundManager;
import android.support.v17.leanback.app.BrowseSupportFragment;
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
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pimenta.bestv.presenter.WorkGridContract;
import com.pimenta.bestv.presenter.WorkGridPresenter;
import com.pimenta.bestv.repository.entity.Movie;
import com.pimenta.bestv.repository.entity.Work;
import com.pimenta.bestv.view.activity.MovieDetailsActivity;
import com.pimenta.bestv.view.fragment.base.BaseVerticalGridFragment;
import com.pimenta.bestv.view.widget.WorkCardPresenter;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by marcus on 09-02-2018.
 */
public abstract class AbstractWorkGridFragment extends BaseVerticalGridFragment<WorkGridPresenter> implements WorkGridContract,
        BrowseSupportFragment.MainFragmentAdapterProvider {

    private static final String TAG = "AbstractWorkGridFragment";
    protected static final String SHOW_PROGRESS = "SHOW_PROGRESS";

    private static final int ERROR_FRAGMENT_REQUEST_CODE = 1;
    private static final int BACKGROUND_UPDATE_DELAY = 300;
    private static final int NUMBER_COLUMNS = 6;

    private final Handler mHandler = new Handler();
    private final BrowseSupportFragment.MainFragmentAdapter<AbstractWorkGridFragment> mMainFragmentAdapter = new BrowseSupportFragment.MainFragmentAdapter<>(this);

    private Timer mBackgroundTimer;
    private BackgroundManager mBackgroundManager;
    protected ArrayObjectAdapter mRowsAdapter;

    private Work mWorkSelected;
    protected boolean mShowProgress;

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

        if (mShowProgress) {
            getProgressBarManager().show();
        }
        loadData();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mWorkSelected != null) {
            loadBackdropImage(false);
            refreshDada();
        }
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
    public BrowseSupportFragment.MainFragmentAdapter getMainFragmentAdapter() {
        return mMainFragmentAdapter;
    }

    @Override
    public void onWorksLoaded(final List<? extends Work> works) {
        if (works != null) {
            for (final Work work : works) {
                if (mRowsAdapter.indexOf(work) == -1) {
                    mRowsAdapter.add(work);
                }
            }
        } else if (mRowsAdapter.size() == 0) {
            final ErrorFragment fragment = ErrorFragment.newInstance();
            fragment.setTarget(this, ERROR_FRAGMENT_REQUEST_CODE);
            addFragment(fragment, ErrorFragment.TAG);
        }

        getProgressBarManager().hide();
        getMainFragmentAdapter().getFragmentHost().notifyDataReady(getMainFragmentAdapter());
    }

    @Override
    public void onBackdropImageLoaded(final Bitmap bitmap) {
        mBackgroundManager.setBitmap(bitmap);
    }

    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        switch (requestCode) {
            case ERROR_FRAGMENT_REQUEST_CODE:
                popBackStack(ErrorFragment.TAG, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                if (resultCode == Activity.RESULT_OK) {
                    getProgressBarManager().show();
                    loadData();
                }
                break;
        }
    }

    public void loadMorePages() {
        getProgressBarManager().show();
        loadData();
    }

    public void refreshDada() {
        getProgressBarManager().show();
        loadData();
    }

    private void setupUI() {
        mBackgroundManager = BackgroundManager.getInstance(getActivity());

        final VerticalGridPresenter verticalGridPresenter = new VerticalGridPresenter(FocusHighlight.ZOOM_FACTOR_MEDIUM);
        verticalGridPresenter.setNumberOfColumns(NUMBER_COLUMNS);
        setGridPresenter(verticalGridPresenter);

        final WorkCardPresenter workCardPresenter = new WorkCardPresenter();
        workCardPresenter.setLoadWorkPosterListener((movie, imageView) -> mPresenter.loadWorkPosterImage(movie, imageView));
        mRowsAdapter = new ArrayObjectAdapter(workCardPresenter);
        setAdapter(mRowsAdapter);

        setOnItemViewSelectedListener(new ItemViewSelectedListener());
        setOnItemViewClickedListener(new ItemViewClickedListener());
    }

    private void loadBackdropImage(boolean delay) {
        if (mBackgroundTimer != null) {
            mBackgroundTimer.cancel();
        }
        mBackgroundTimer = new Timer();
        mBackgroundTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                mHandler.post(() -> {
                    mPresenter.loadBackdropImage(mWorkSelected);
                    mBackgroundTimer.cancel();
                });
            }
        }, delay ? BACKGROUND_UPDATE_DELAY : 0);
    }

    abstract void loadData();

    private final class ItemViewSelectedListener implements OnItemViewSelectedListener {

        @Override
        public void onItemSelected(Presenter.ViewHolder itemViewHolder, Object item, RowPresenter.ViewHolder rowViewHolder, Row row) {
            mWorkSelected = (Work) item;
            loadBackdropImage(true);

            if (mRowsAdapter.indexOf(mWorkSelected) >= mRowsAdapter.size() - NUMBER_COLUMNS) {
                loadMorePages();
            }
        }
    }

    private final class ItemViewClickedListener implements OnItemViewClickedListener {

        @Override
        public void onItemClicked(Presenter.ViewHolder itemViewHolder, Object item, RowPresenter.ViewHolder rowViewHolder, Row row) {
            if (item instanceof Movie) {
                final Movie movie = (Movie) item;
                final Bundle bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(),
                        ((ImageCardView) itemViewHolder.view).getMainImageView(), MovieDetailsFragment.SHARED_ELEMENT_NAME).toBundle();
                startActivity(MovieDetailsActivity.newInstance(getContext(), movie), bundle);
            }
        }
    }
}