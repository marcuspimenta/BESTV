package com.pimenta.bestv.presenters;

/**
 * Created by marcus on 06-02-2018.
 */
public interface BasePresenter<T extends BasePresenter.Callback> {

    void onAttach(T callback);

    void onDetach();

    interface Callback {

    }
}