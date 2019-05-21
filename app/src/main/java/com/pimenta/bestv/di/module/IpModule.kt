package com.pimenta.bestv.di.module

import com.google.gson.Gson
import com.pimenta.bestv.BuildConfig
import com.pimenta.bestv.data.remote.api.ip.InfoApi
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named
import javax.inject.Singleton

/**
 * Created by marcus on 24/05/18.
 */
@Module
class IpModule {

    @Provides
    @Singleton
    @Named("Ip")
    fun provideIpRetrofit(okHttpClient: OkHttpClient, gson: Gson) = Retrofit.Builder()
            .baseUrl(BuildConfig.IP_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()

    @Provides
    @Singleton
    fun provideMovieApi(@Named("Ip") retrofit: Retrofit) =
            retrofit.create(InfoApi::class.java)
}