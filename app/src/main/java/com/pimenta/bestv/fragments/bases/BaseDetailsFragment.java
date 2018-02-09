package com.pimenta.bestv.fragments.bases;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v17.leanback.app.DetailsFragment;
import android.util.Log;
import android.view.View;

import com.pimenta.bestv.presenters.BasePresenter;

/**
 * Created by marcus on 07-02-2018.
 */
public abstract class BaseDetailsFragment<T extends BasePresenter> extends DetailsFragment implements BasePresenter.Callback {

    private final String TAG = "BaseDetailsFragment";

    protected final T mPresenter = getPresenter();

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "[onViewCreated] view=" + view + ", savedInstanceState=" + savedInstanceState);
        super.onViewCreated(view, savedInstanceState);
        mPresenter.onAttach(this);
    }

    @Override
    public void onDestroyView() {
        Log.d(TAG, "[onDestroyView]");
        mPresenter.onDetach();
        super.onDestroyView();
    }

    protected abstract T getPresenter();

}