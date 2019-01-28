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

package com.pimenta.bestv.feature.workbrowse.ui;

import android.app.Activity;
import android.content.Context;
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

import com.pimenta.bestv.feature.base.BaseVerticalGridFragment;
import com.pimenta.bestv.feature.error.ErrorFragment;
import com.pimenta.bestv.feature.widget.render.WorkCardRenderer;
import com.pimenta.bestv.feature.workbrowse.presenter.WorkGridPresenter;
import com.pimenta.bestv.feature.workbrowse.presenter.WorkGridPresenter.WorkGridView;
import com.pimenta.bestv.feature.workdetail.ui.WorkDetailsActivity;
import com.pimenta.bestv.feature.workdetail.ui.WorkDetailsFragment;
import com.pimenta.bestv.repository.entity.Work;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.inject.Inject;

/**
 * Created by marcus on 09-02-2018.
 */
public abstract class AbstractWorkGridFragment extends BaseVerticalGridFragment implements WorkGridView,
        BrowseSupportFragment.MainFragmentAdapterProvider {

    protected static final String SHOW_PROGRESS = "SHOW_PROGRESS";

    private static final int ERROR_FRAGMENT_REQUEST_CODE = 1;
    private static final int BACKGROUND_UPDATE_DELAY = 300;
    private static final int NUMBER_COLUMNS = 6;

    private static final Handler sHandler = new Handler();
    private final BrowseSupportFragment.MainFragmentAdapter<AbstractWorkGridFragment> mMainFragmentAdapter =
            new BrowseSupportFragment.MainFragmentAdapter<>(this);

    private Timer mBackgroundTimer;
    private BackgroundManager mBackgroundManager;
    protected ArrayObjectAdapter mRowsAdapter;

    private Work mWorkSelected;
    protected boolean mShowProgress;

    @Inject
    WorkGridPresenter mPresenter;

    @Override
    public void onAttach(@Nullable Context context) {
        super.onAttach(context);
        mPresenter.register(this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupUI();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getProgressBarManager().setRootView(container);
        getProgressBarManager().enableProgressBar();
        getProgressBarManager().setInitialDelay(0);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
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
    public void onDetach() {
        mPresenter.unRegister();
        super.onDetach();
    }

    @Override
    public BrowseSupportFragment.MainFragmentAdapter getMainFragmentAdapter() {
        return mMainFragmentAdapter;
    }

    @Override
    public void onWorksLoaded(List<? extends Work> works) {
        if (works != null) {
            for (final Work work : works) {
                if (mRowsAdapter.indexOf(work) == -1) {
                    mRowsAdapter.add(work);
                }
            }
        } else if (mRowsAdapter.size() == 0) {
            final ErrorFragment fragment = ErrorFragment.Companion.newInstance();
            fragment.setTarget(this, ERROR_FRAGMENT_REQUEST_CODE);
            addFragment(fragment, ErrorFragment.TAG);
        }

        getProgressBarManager().hide();
        getMainFragmentAdapter().getFragmentHost().notifyDataReady(getMainFragmentAdapter());
    }

    @Override
    public void onBackdropImageLoaded(Bitmap bitmap) {
        mBackgroundManager.setBitmap(bitmap);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
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

        VerticalGridPresenter verticalGridPresenter = new VerticalGridPresenter(FocusHighlight.ZOOM_FACTOR_MEDIUM);
        verticalGridPresenter.setNumberOfColumns(NUMBER_COLUMNS);
        setGridPresenter(verticalGridPresenter);

        WorkCardRenderer workCardRenderer = new WorkCardRenderer();
        mRowsAdapter = new ArrayObjectAdapter(workCardRenderer);
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
                sHandler.post(() -> {
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
            Work work = (Work) item;
            Bundle bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(),
                    ((ImageCardView) itemViewHolder.view).getMainImageView(), WorkDetailsFragment.SHARED_ELEMENT_NAME).toBundle();
            startActivity(WorkDetailsActivity.newInstance(getContext(), work), bundle);
        }
    }
}