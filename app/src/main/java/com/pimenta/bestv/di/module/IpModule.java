package com.pimenta.bestv.di.module;

import com.google.gson.Gson;
import com.pimenta.bestv.BuildConfig;
import com.pimenta.bestv.repository.remote.api.ip.InfoApi;

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
    Retrofit provideIpRetrofit(OkHttpClient okHttpClient, Gson gson) {
        return new Retrofit.Builder()
                .baseUrl(BuildConfig.IP_BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
    }

    @Provides
    @Singleton
    InfoApi provideMovieApi(@Named("Ip") Retrofit retrofit) {
        return retrofit.create(InfoApi.class);
    }
}