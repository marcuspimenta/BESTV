package com.pimenta.bestv.presenters;

import io.reactivex.disposables.CompositeDisposable;

/**
 * Created by marcus on 06-02-2018.
 */
public class AbstractPresenter<T extends BasePresenter.Callback> implements BasePresenter<T> {

    protected T mCallback;
    protected final CompositeDisposable mDisposables = new CompositeDisposable();

    @Override
    public void onAttach(T callback) {
        mCallback = callback;
    }

    @Override
    public void onDetach() {
        mCallback = null;
        mDisposables.dispose();
    }

}