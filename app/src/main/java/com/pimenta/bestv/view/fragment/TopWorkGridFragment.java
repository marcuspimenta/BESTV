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

import android.os.Bundle;

import com.pimenta.bestv.BesTV;
import com.pimenta.bestv.repository.MediaRepository;
import com.pimenta.bestv.repository.entity.Work;

import java.util.List;

/**
 * Created by marcus on 11-02-2018.
 */
public class TopWorkGridFragment extends AbstractWorkGridFragment {

    private static final String TYPE = "TYPE";

    private MediaRepository.WorkType mWorkType;

    public static TopWorkGridFragment newInstance(MediaRepository.WorkType workType, boolean showProgress) {
        Bundle args = new Bundle();
        args.putSerializable(TYPE, workType);
        args.putBoolean(SHOW_PROGRESS, showProgress);

        TopWorkGridFragment topMovieGridFragment = new TopWorkGridFragment();
        topMovieGridFragment.setArguments(args);
        topMovieGridFragment.mWorkType = workType;
        topMovieGridFragment.mShowProgress = showProgress;
        return topMovieGridFragment;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (mWorkType == null) {
            mWorkType = (MediaRepository.WorkType) getArguments().getSerializable(TYPE);
            mShowProgress = getArguments().getBoolean(SHOW_PROGRESS);
        }
    }

    @Override
    void loadData() {
        mPresenter.loadMoviesByType(mWorkType);
    }

    @Override
    public void loadMorePages() {
        if (!mWorkType.equals(MediaRepository.WorkType.FAVORITES_MOVIES)) {
            super.loadMorePages();
        }
    }

    @Override
    public void refreshDada() {
        if (mWorkType.equals(MediaRepository.WorkType.FAVORITES_MOVIES)) {
            super.loadMorePages();
        }
    }

    @Override
    public void onWorksLoaded(final List<? extends Work> works) {
        if (mWorkType.equals(MediaRepository.WorkType.FAVORITES_MOVIES)) {
            /*mRowsAdapter.setItems(works, new DiffCallback<Movie>() {
                @Override
                public boolean areItemsTheSame(@NonNull final Movie oldItem, @NonNull final Movie newItem) {
                    return oldItem.equals(newItem);
                }

                @Override
                public boolean areContentsTheSame(@NonNull final Movie oldItem, @NonNull final Movie newItem) {
                    return oldItem.equals(newItem);
                }
            });*/
            if (works != null) {
                mRowsAdapter.setItems(works, null);
            }
            getProgressBarManager().hide();
            getMainFragmentAdapter().getFragmentHost().notifyDataReady(getMainFragmentAdapter());
            return;
        }
        super.onWorksLoaded(works);
    }

    @Override
    protected void injectPresenter() {
        BesTV.getApplicationComponent().inject(this);
    }
}