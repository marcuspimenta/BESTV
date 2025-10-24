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
    val kotlin = "2.2.20"
    val kotlinx_coroutines = "1.10.2"
    val kotlinx_coroutines_test = "1.10.2"
    val build_gradle = "8.13.0"
    val google_service = "4.4.4"
    val ktlint = "10.2.1"
    val ben_mane_gralde = "0.53.0"

    val androidx_leanback = "1.2.0"
    val androidx_core = "1.17.0"
    val androidx_fragment = "1.8.9"
    val androidx_lifecycle = "2.10.0-alpha05"
    val androidx_recommendation = "1.0.0"
    val androidx_tvprovider = "1.1.0"
    val androidx_room = "2.8.2"
    val androidx_work = "2.8.0-alpha02"
    val compose_bom = "2024.12.01"
    val compose_compiler = "1.5.15"
    val androidx_tv_foundation = "1.0.0-alpha12"
    val androidx_tv_material = "1.0.0"
    val androidx_activity_compose = "1.10.0"
    val coil_compose = "2.7.0"

    val dagger = "2.57.2"
    val dagger_compile = "2.57.2"
    val retrofit = "3.0.0"
    val retrofit_converter_gson = "3.0.0"
    val okhttp = "5.2.1"
    val logging_interceptor = "5.2.1"
    val gson = "2.13.2"
    val retrofit2_rxjava2_adapter = "1.0.0"
    val timber = "5.0.1"
    val glide = "5.0.5"
    val lottie = "6.6.1"
    val rxandroid = "2.1.1"
    val junit = "4.13.2"
    val mockito_inline = "5.2.0"
    val mockito_core = "4.5.0"
    val mockito_kotlin = "2.2.0"
    val turbine = "1.2.0"
    val robolectric = "4.15"
}

object Libs {
    val kotlin = "org.jetbrains.kotlin:kotlin-stdlib-jdk7:${Versions.kotlin}"
    val kotlinx_coroutines_core = "org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.kotlinx_coroutines}"
    val kotlinx_coroutines_android = "org.jetbrains.kotlinx:kotlinx-coroutines-android:${Versions.kotlinx_coroutines}"
    val kotlinx_coroutines_test = "org.jetbrains.kotlinx:kotlinx-coroutines-test:${Versions.kotlinx_coroutines_test}"
    val kotlin_test_junit = "org.jetbrains.kotlin:kotlin-test-junit:${Versions.kotlin}"

    val build_gradle = "com.android.tools.build:gradle:${Versions.build_gradle}"
    val kotlin_plugin = "org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.kotlin}"
    val google_services = "com.google.gms:google-services:${Versions.google_service}"
    val ktlint = "org.jlleitschuh.gradle:ktlint-gradle:${Versions.ktlint}"
    val ben_mane_gralde = "com.github.ben-manes:gradle-versions-plugin:${Versions.ben_mane_gralde}"

    val androidx_leanback = "androidx.leanback:leanback:${Versions.androidx_leanback}"
    val androidx_core = "androidx.core:core-ktx:${Versions.androidx_core}"
    val androidx_fragment = "androidx.fragment:fragment-ktx:${Versions.androidx_fragment}"
    val androidx_lifecycle = "androidx.lifecycle:lifecycle-viewmodel:${Versions.androidx_lifecycle}"
    val androidx_lifecycle_viewmodel_ktx = "androidx.lifecycle:lifecycle-viewmodel-ktx:${Versions.androidx_lifecycle}"
    val androidx_lifecycle_runtime_ktx = "androidx.lifecycle:lifecycle-runtime-ktx:${Versions.androidx_lifecycle}"
    val androidx_recommendation = "androidx.recommendation:recommendation:${Versions.androidx_recommendation}"
    val androidx_tvprovider = "androidx.tvprovider:tvprovider:${Versions.androidx_tvprovider}"
    val androidx_room = "androidx.room:room-runtime:${Versions.androidx_room}"
    val androidx_room_rxjava = "androidx.room:room-rxjava2:${Versions.androidx_room}"
    val androidx_room_ktx = "androidx.room:room-ktx:${Versions.androidx_room}"
    val androidx_room_compiler = "androidx.room:room-compiler:${Versions.androidx_room}"
    val androidx_work = "androidx.work:work-runtime-ktx:${Versions.androidx_work}"
    val androidx_work_rxjava2 = "androidx.work:work-rxjava2:${Versions.androidx_work}"

    // Compose for TV
    val compose_bom = "androidx.compose:compose-bom:${Versions.compose_bom}"
    val compose_ui = "androidx.compose.ui:ui"
    val compose_ui_tooling = "androidx.compose.ui:ui-tooling"
    val compose_ui_tooling_preview = "androidx.compose.ui:ui-tooling-preview"
    val compose_foundation = "androidx.compose.foundation:foundation"
    val compose_material3 = "androidx.compose.material3:material3"
    val androidx_tv_foundation = "androidx.tv:tv-foundation:${Versions.androidx_tv_foundation}"
    val androidx_tv_material = "androidx.tv:tv-material:${Versions.androidx_tv_material}"
    val androidx_activity_compose = "androidx.activity:activity-compose:${Versions.androidx_activity_compose}"
    val coil_compose = "io.coil-kt:coil-compose:${Versions.coil_compose}"

    val dagger = "com.google.dagger:dagger:${Versions.dagger}"
    val dagger_compile = "com.google.dagger:dagger-compiler:${Versions.dagger_compile}"
    val retrofit = "com.squareup.retrofit2:retrofit:${Versions.retrofit}"
    val retrofit_converter_gson = "com.squareup.retrofit2:converter-gson:${Versions.retrofit_converter_gson}"
    val okhttp = "com.squareup.okhttp3:okhttp:${Versions.okhttp}"
    val logging_interceptor = "com.squareup.okhttp3:logging-interceptor:${Versions.logging_interceptor}"
    val gson = "com.google.code.gson:gson:${Versions.gson}"
    val retrofit2_rxjava2_adapter = "com.jakewharton.retrofit:retrofit2-rxjava2-adapter:${Versions.retrofit2_rxjava2_adapter}"
    val timber = "com.jakewharton.timber:timber:${Versions.timber}"
    val glide = "com.github.bumptech.glide:glide:${Versions.glide}"
    val lottie = "com.airbnb.android:lottie:${Versions.lottie}"
    val rxandroid = "io.reactivex.rxjava2:rxandroid:${Versions.rxandroid}"
    val junit = "junit:junit:${Versions.junit}"
    val mockito_inline = "org.mockito:mockito-inline:${Versions.mockito_inline}"
    val mockito_core = "org.mockito:mockito-core:${Versions.mockito_core}"
    val mockito_kotlin = "com.nhaarman.mockitokotlin2:mockito-kotlin:${Versions.mockito_kotlin}"
    val turbine = "app.cash.turbine:turbine:${Versions.turbine}"
    val robolectric = "org.robolectric:robolectric:${Versions.robolectric}"
}
