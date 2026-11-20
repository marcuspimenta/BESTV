/*
 * Copyright (C) 2018 Marcus Pimenta
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.pimenta.bestv.di

import com.pimenta.bestv.application.AndroidDeviceCapabilities
import com.pimenta.bestv.presentation.platform.DeviceCapabilities
import com.pimenta.bestv.presentation.route.di.androidRouteModule
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val platformModule = module {
    single<DeviceCapabilities> { AndroidDeviceCapabilities(androidContext()) }

    includes(
        networkModule,
        databaseDriverModule,
        presentationModule,
        androidRouteModule,
    )
}
