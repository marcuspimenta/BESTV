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

package com.pimenta.bestv.di.module

import com.pimenta.bestv.castdetail.di.CastDetailsActivityComponent
import com.pimenta.bestv.recommendation.di.BootBroadcastReceiverComponent
import com.pimenta.bestv.recommendation.di.RecommendationWorkerComponent
import com.pimenta.bestv.search.di.SearchActivityComponent
import com.pimenta.bestv.workbrowse.di.MainActivityComponent
import com.pimenta.bestv.workdetail.di.WorkDetailsActivityComponent
import dagger.Module

/**
 * Created by marcus on 24-02-2020.
 */
@Module(
    subcomponents = [
        CastDetailsActivityComponent::class,
        SearchActivityComponent::class,
        WorkDetailsActivityComponent::class,
        MainActivityComponent::class,
        BootBroadcastReceiverComponent::class,
        RecommendationWorkerComponent::class
    ]
)
class SubcomponentsModule
