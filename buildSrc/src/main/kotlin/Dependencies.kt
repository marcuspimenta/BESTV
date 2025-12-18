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

import org.gradle.api.JavaVersion

object Config {
    val minSdk = 26
    val compileSdk = 36
    val targetSdk = 36
    val javaVersion = JavaVersion.VERSION_1_8
    val jvmTarget = "1.8"
}

object Versions {
    val kotlin = "2.3.0"
    val kotlinx_coroutines = "1.10.2"
    val kotlinx_coroutines_test = "1.10.2"
    val ktlint = "10.2.1"

    val androidx_core = "1.17.0"
    val androidx_lifecycle = "2.10.0"
    val androidx_recommendation = "1.0.0"
    val androidx_tvprovider = "1.1.0"
    val androidx_room = "2.8.4"
    val androidx_work = "2.11.0"
    val compose_bom = "2025.12.00"
    val androidx_tv_foundation = "1.0.0-alpha12"
    val androidx_tv_material = "1.1.0-alpha01"
    val androidx_activity = "1.12.0"
    val androidx_activity_compose = "1.12.0"
    val coil_compose = "3.3.0"

    val retrofit = "3.0.0"
    val retrofit_converter_gson = "3.0.0"
    val okhttp = "5.3.2"
    val logging_interceptor = "5.3.2"
    val gson = "2.13.2"
    val retrofit2_rxjava2_adapter = "1.0.0"
    val timber = "5.0.1"
    val lottie_compose = "6.7.1"
    val junit = "4.13.2"
    val mockito_inline = "5.2.0"
    val mockito_core = "5.20.0"
    val mockito_kotlin = "6.1.0"
    val turbine = "1.2.1"
    val robolectric = "4.16"

    // Koin
    val koin = "4.0.0"
}

object Libs {
    val kotlin = "org.jetbrains.kotlin:kotlin-stdlib-jdk7:${Versions.kotlin}"
    val kotlinx_coroutines_core = "org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.kotlinx_coroutines}"
    val kotlinx_coroutines_android = "org.jetbrains.kotlinx:kotlinx-coroutines-android:${Versions.kotlinx_coroutines}"
    val kotlinx_coroutines_test = "org.jetbrains.kotlinx:kotlinx-coroutines-test:${Versions.kotlinx_coroutines_test}"
    val kotlin_test_junit = "org.jetbrains.kotlin:kotlin-test-junit:${Versions.kotlin}"
    val ktlint = "org.jlleitschuh.gradle:ktlint-gradle:${Versions.ktlint}"

    val androidx_core = "androidx.core:core-ktx:${Versions.androidx_core}"
    val androidx_lifecycle = "androidx.lifecycle:lifecycle-viewmodel:${Versions.androidx_lifecycle}"
    val androidx_lifecycle_runtime_ktx = "androidx.lifecycle:lifecycle-runtime-ktx:${Versions.androidx_lifecycle}"
    val androidx_recommendation = "androidx.recommendation:recommendation:${Versions.androidx_recommendation}"
    val androidx_tvprovider = "androidx.tvprovider:tvprovider:${Versions.androidx_tvprovider}"
    val androidx_room = "androidx.room:room-runtime:${Versions.androidx_room}"
    val androidx_room_ktx = "androidx.room:room-ktx:${Versions.androidx_room}"
    val androidx_room_compiler = "androidx.room:room-compiler:${Versions.androidx_room}"
    val androidx_work = "androidx.work:work-runtime-ktx:${Versions.androidx_work}"

    // Compose for TV
    val compose_bom = "androidx.compose:compose-bom:${Versions.compose_bom}"
    val compose_ui = "androidx.compose.ui:ui"
    val compose_ui_tooling = "androidx.compose.ui:ui-tooling"
    val compose_ui_tooling_preview = "androidx.compose.ui:ui-tooling-preview"
    val compose_foundation = "androidx.compose.foundation:foundation"
    val compose_material3 = "androidx.compose.material3:material3"
    val androidx_tv_foundation = "androidx.tv:tv-foundation:${Versions.androidx_tv_foundation}"
    val androidx_tv_material = "androidx.tv:tv-material:${Versions.androidx_tv_material}"
    val androidx_activity = "androidx.activity:activity-ktx:${Versions.androidx_activity}"
    val androidx_activity_compose = "androidx.activity:activity-compose:${Versions.androidx_activity_compose}"
    val coil_compose = "io.coil-kt.coil3:coil-compose:${Versions.coil_compose}"
    val coil_network_okhttp = "io.coil-kt.coil3:coil-network-okhttp:${Versions.coil_compose}"

    val retrofit = "com.squareup.retrofit2:retrofit:${Versions.retrofit}"
    val retrofit_converter_gson = "com.squareup.retrofit2:converter-gson:${Versions.retrofit_converter_gson}"
    val okhttp = "com.squareup.okhttp3:okhttp:${Versions.okhttp}"
    val logging_interceptor = "com.squareup.okhttp3:logging-interceptor:${Versions.logging_interceptor}"
    val gson = "com.google.code.gson:gson:${Versions.gson}"
    val retrofit2_rxjava2_adapter = "com.jakewharton.retrofit:retrofit2-rxjava2-adapter:${Versions.retrofit2_rxjava2_adapter}"
    val timber = "com.jakewharton.timber:timber:${Versions.timber}"
    val lottie_compose = "com.airbnb.android:lottie-compose:${Versions.lottie_compose}"
    val junit = "junit:junit:${Versions.junit}"
    val mockito_inline = "org.mockito:mockito-inline:${Versions.mockito_inline}"
    val mockito_core = "org.mockito:mockito-core:${Versions.mockito_core}"
    val mockito_kotlin = "org.mockito.kotlin:mockito-kotlin:${Versions.mockito_kotlin}"
    val turbine = "app.cash.turbine:turbine:${Versions.turbine}"
    val robolectric = "org.robolectric:robolectric:${Versions.robolectric}"

    // Koin
    val koin_core = "io.insert-koin:koin-core:${Versions.koin}"
    val koin_android = "io.insert-koin:koin-android:${Versions.koin}"
    val koin_android_compose = "io.insert-koin:koin-androidx-compose:${Versions.koin}"
    val koin_workmanager = "io.insert-koin:koin-androidx-workmanager:${Versions.koin}"
    val koin_test = "io.insert-koin:koin-test:${Versions.koin}"
}
