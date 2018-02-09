package com.pimenta.bestv.dagger;

import com.pimenta.bestv.connectors.TmdbConnector;
import com.pimenta.bestv.connectors.TmdbConnectorImpl;

import javax.inject.Singleton;

import dagger.Binds;
import dagger.Module;

/**
 * Created by marcus on 09-02-2018.
 */
@Module
public interface ImplModule {

    @Binds
    @Singleton
    TmdbConnector provideTmdbConnector(TmdbConnectorImpl connector);

}