package com.pimenta.bestv.dagger.module;

import android.app.Application;

import com.google.gson.GsonBuilder;
import com.pimenta.bestv.R;
import com.pimenta.bestv.repository.api.ip.InfoApi;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by marcus on 24/05/18.
 */
@Module
public class IpModule {

    @Provides
    @Singleton
    @Named("Ip")
    Retrofit provideIpRetrofit(Application application, OkHttpClient okHttpClient) {
        return new Retrofit.Builder()
                .baseUrl(application.getString(R.string.ip_base_url_api))
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create(new GsonBuilder().create()))
                .build();
    }

    @Provides
    @Singleton
    InfoApi provideMovieApi(@Named("Ip") Retrofit retrofit) {
        return retrofit.create(InfoApi.class);
    }
}