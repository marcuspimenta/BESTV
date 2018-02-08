package com.pimenta.bestv.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v17.leanback.app.VideoSupportFragment;
import android.util.Log;
import android.view.View;

import com.pimenta.bestv.presenters.BasePresenter;

/**
 * Created by marcus on 08-02-2018.
 */
public abstract class BaseVideoSupportFragment<T extends BasePresenter> extends VideoSupportFragment implements BasePresenter.Callback {

    private final String TAG = "BaseVideoSupportFragment";

    protected final T mController = getController();

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "[onViewCreated] view=" + view + ", savedInstanceState=" + savedInstanceState);
        super.onViewCreated(view, savedInstanceState);
        mController.onAttach(this);
    }

    @Override
    public void onDestroyView() {
        Log.d(TAG, "[onDestroyView]");
        mController.onDetach();
        super.onDestroyView();
    }

    protected abstract T getController();

}
