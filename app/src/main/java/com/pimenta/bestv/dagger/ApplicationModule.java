package com.pimenta.bestv.dagger;

import android.app.Application;
import android.util.DisplayMetrics;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by marcus on 07-02-2018.
 */
@Singleton
@Module
public class ApplicationModule {

    private Application mApplication;

    public ApplicationModule(Application application) {
        this.mApplication = application;
    }

    @Provides
    @Singleton
    public Application provideApplication() {
        return mApplication;
    }

    @Provides
    @Singleton
    public DisplayMetrics provideDisplayMetrics() {
        return new DisplayMetrics();
    }

}