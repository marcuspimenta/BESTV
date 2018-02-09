package com.pimenta.bestv.connectors;

import android.support.annotation.StringRes;

import com.pimenta.bestv.BesTV;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Created by marcus on 08-02-2018.
 */
public abstract class BasePreferences {

    private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
    private static final int CORE_POOL_SIZE = CPU_COUNT + 1;
    private static final Executor THREAD_POOL_EXECUTOR = Executors.newFixedThreadPool(CORE_POOL_SIZE);

    protected Executor getThreadPool() {
        return THREAD_POOL_EXECUTOR;
    }

    protected String getString(@StringRes int id) {
        return BesTV.get().getString(id);
    }

}