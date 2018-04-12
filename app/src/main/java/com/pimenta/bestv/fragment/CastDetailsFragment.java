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

package com.pimenta.bestv.fragment;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.ClassPresenterSelector;
import android.support.v17.leanback.widget.DetailsOverviewRow;
import android.support.v17.leanback.widget.FullWidthDetailsOverviewRowPresenter;
import android.support.v17.leanback.widget.FullWidthDetailsOverviewSharedElementHelper;
import android.support.v17.leanback.widget.ListRow;
import android.support.v17.leanback.widget.ListRowPresenter;
import android.support.v17.leanback.widget.RowPresenter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.pimenta.bestv.BesTV;
import com.pimenta.bestv.R;
import com.pimenta.bestv.fragment.base.BaseDetailsFragment;
import com.pimenta.bestv.model.Cast;
import com.pimenta.bestv.presenter.CastDetailsCallback;
import com.pimenta.bestv.presenter.CastDetailsPresenter;
import com.pimenta.bestv.widget.CastDetailsDescriptionPresenter;

/**
 * Created by marcus on 04-04-2018.
 */
public class CastDetailsFragment extends BaseDetailsFragment<CastDetailsPresenter> implements CastDetailsCallback {

    public static final String TAG = "CastDetailsFragment";
    public static final String SHARED_ELEMENT_NAME = "SHARED_ELEMENT_NAME";
    public static final String CAST = "CAST";

    private ArrayObjectAdapter mAdapter;
    private ClassPresenterSelector mPresenterSelector;
    private DetailsOverviewRow mDetailsOverviewRow;

    private Cast mCast;

    public static MovieDetailsFragment newInstance() {
        return new MovieDetailsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (mCast == null) {
            mCast = (Cast) getActivity().getIntent().getSerializableExtra(CAST);
        }

        setupDetailsOverviewRow();
        setupDetailsOverviewRowPresenter();
        setAdapter(mAdapter);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        getProgressBarManager().setRootView(container);
        getProgressBarManager().enableProgressBar();
        getProgressBarManager().setInitialDelay(0);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getProgressBarManager().show();
        mPresenter.loadCastDetails(mCast);
    }

    @Override
    public void onCastLoaded(final Cast cast) {
        if (cast != null) {
            mCast = cast;
            mDetailsOverviewRow.setItem(cast);
            mAdapter.notifyArrayItemRangeChanged(0, mAdapter.size());
            getProgressBarManager().hide();
        }
    }

    @Override
    public void onCardImageLoaded(final Drawable resource) {
        mDetailsOverviewRow.setImageDrawable(resource);
        mAdapter.notifyArrayItemRangeChanged(0, mAdapter.size());
    }

    @Override
    protected void injectPresenter() {
        BesTV.getApplicationComponent().inject(this);
    }

    private void setupDetailsOverviewRow() {
        mPresenterSelector = new ClassPresenterSelector();
        mPresenterSelector.addClassPresenter(ListRow.class, new ListRowPresenter());
        mAdapter = new ArrayObjectAdapter(mPresenterSelector);

        mDetailsOverviewRow = new DetailsOverviewRow(mCast);
        mPresenter.loadCastImage(mCast);

        mAdapter.add(mDetailsOverviewRow);
    }

    private void setupDetailsOverviewRowPresenter() {
        // Set detail background.
        final FullWidthDetailsOverviewRowPresenter detailsPresenter = new FullWidthDetailsOverviewRowPresenter(new CastDetailsDescriptionPresenter()) {

            private ImageView mDetailsImageView;

            @Override
            protected RowPresenter.ViewHolder createRowViewHolder(final ViewGroup parent) {
                RowPresenter.ViewHolder viewHolder = super.createRowViewHolder(parent);
                mDetailsImageView = viewHolder.view.findViewById(R.id.details_overview_image);
                ViewGroup.LayoutParams lp = mDetailsImageView.getLayoutParams();
                lp.width = getResources().getDimensionPixelSize(R.dimen.movie_card_width);
                lp.height = getResources().getDimensionPixelSize(R.dimen.movie_card_height);
                mDetailsImageView.setLayoutParams(lp);
                return viewHolder;
            }
        };
        detailsPresenter.setActionsBackgroundColor(getResources().getColor(R.color.detail_view_actionbar_background, getActivity().getTheme()));
        detailsPresenter.setBackgroundColor(getResources().getColor(R.color.detail_view_background, getActivity().getTheme()));

        // Hook up transition element.
        final FullWidthDetailsOverviewSharedElementHelper sharedElementHelper = new FullWidthDetailsOverviewSharedElementHelper();
        sharedElementHelper.setSharedElementEnterTransition(getActivity(), SHARED_ELEMENT_NAME);
        detailsPresenter.setListener(sharedElementHelper);
        detailsPresenter.setParticipatingEntranceTransition(true);
        mPresenterSelector.addClassPresenter(DetailsOverviewRow.class, detailsPresenter);
    }
}