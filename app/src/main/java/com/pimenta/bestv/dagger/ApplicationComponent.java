package com.pimenta.bestv.dagger;

import android.app.Application;

import com.pimenta.bestv.presenters.MainPresenter;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by marcus on 07-02-2018.
 */
@Singleton
@Component(modules = {ApplicationModule.class})
public interface ApplicationComponent {

    Application getApplication();

    void inject(MainPresenter presenter);

}