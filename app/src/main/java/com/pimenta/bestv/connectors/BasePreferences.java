/*
 * Copyright (C) 2018 Marcus Pimenta
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

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