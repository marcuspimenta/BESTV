package com.pimenta.bestv.dagger;

import android.app.Application;
import android.util.DisplayMetrics;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.pimenta.bestv.connectors.TmdbConnector;
import com.pimenta.bestv.connectors.TmdbConnectorImpl;

import javax.inject.Singleton;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;

/**
 * Created by marcus on 07-02-2018.
 */
@Singleton
@Module(includes = {ApplicationModule.Impls.class})
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

    @Provides
    @Singleton
    public Gson provideGson() {
        return new GsonBuilder().create();
    }

    @Module
    public interface Impls {

        @Binds
        @Singleton
        TmdbConnector provideTmdbConnector(TmdbConnectorImpl connector);
    }

}