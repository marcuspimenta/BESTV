package com.pimenta.bestv;

import android.app.Application;
import android.util.Log;

import com.pimenta.bestv.dagger.ApplicationComponent;
import com.pimenta.bestv.dagger.ApplicationModule;
import com.pimenta.bestv.dagger.DaggerApplicationComponent;

/**
 * Created by marcus on 07-02-2018.
 */
public class BesTV extends Application {

    private static final String TAG = "BesTV";

    private static volatile ApplicationComponent sApplicationComponent;

    public static BesTV get() {
        return (BesTV) sApplicationComponent.getApplication();
    }

    public static ApplicationComponent getApplicationComponent() {
        return sApplicationComponent;
    }

    @Override
    public void onCreate() {
        Log.d(TAG, "[onCreate]");
        super.onCreate();

        sApplicationComponent = DaggerApplicationComponent
                .builder()
                .applicationModule(new ApplicationModule(this))
                .build();
    }
}