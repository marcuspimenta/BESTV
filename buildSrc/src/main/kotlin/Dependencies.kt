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
    val compileSdk = 31
    val targetSdk = 31
    val javaVersion = JavaVersion.VERSION_1_8
    val jvmTarget = "1.8"
}

object Versions {
    val kotlin = "1.6.21"
    val build_gradle = "7.3.0-alpha09"
    val google_service = "4.3.10"
    val ktlint = "10.2.1"
    val ben_mane_gralde = "0.42.0"

    val androidx_leanback = "1.2.0-alpha02"
    val androidx_core = "1.9.0-alpha01"
    val androidx_fragment = "1.5.0-beta01"
    val androidx_lifecycle = "2.5.0-beta01"
    val androidx_recommendation = "1.0.0"
    val androidx_tvprovider = "1.1.0-alpha01"
    val androidx_room = "2.5.0-alpha01"
    val androidx_work = "2.8.0-alpha02"

    val dagger = "2.41"
    val dagger_compile = "2.41"
    val retrofit = "2.9.0"
    val retrofit_converter_gson = "2.9.0"
    val okhttp = "5.0.0-alpha.6"
    val logging_interceptor = "5.0.0-alpha.7"
    val gson = "2.9.0"
    val retrofit2_rxjava2_adapter = "1.0.0"
    val timber = "5.0.1"
    val glide = "4.13.1"
    val lottie = "5.1.1"
    val rxandroid = "2.1.1"
    val junit = "4.13.2"
    val mockito_inline = "4.5.1"
    val mockito_core = "4.5.0"
    val mockito_kotlin = "2.1.0"
}

object Dependencies {
    val kotlin = "org.jetbrains.kotlin:kotlin-stdlib-jdk7:${Versions.kotlin}"

    val build_gradle = "com.android.tools.build:gradle:${Versions.build_gradle}"
    val kotlin_plugin = "org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.kotlin}"
    val google_services = "com.google.gms:google-services:${Versions.google_service}"
    val ktlint = "org.jlleitschuh.gradle:ktlint-gradle:${Versions.ktlint}"
    val ben_mane_gralde = "com.github.ben-manes:gradle-versions-plugin:${Versions.ben_mane_gralde}"

    val androidx_leanback = "androidx.leanback:leanback:${Versions.androidx_leanback}"
    val androidx_core = "androidx.core:core-ktx:${Versions.androidx_core}"
    val androidx_fragment = "androidx.fragment:fragment-ktx:${Versions.androidx_fragment}"
    val androidx_lifecycle = "androidx.lifecycle:lifecycle-viewmodel:${Versions.androidx_lifecycle}"
    val androidx_recommendation = "androidx.recommendation:recommendation:${Versions.androidx_recommendation}"
    val androidx_tvprovider = "androidx.tvprovider:tvprovider:${Versions.androidx_tvprovider}"
    val androidx_room = "androidx.room:room-runtime:${Versions.androidx_room}"
    val androidx_room_rxjava = "androidx.room:room-rxjava2:${Versions.androidx_room}"
    val androidx_room_ktx = "androidx.room:room-ktx:${Versions.androidx_room}"
    val androidx_room_compiler = "androidx.room:room-compiler:${Versions.androidx_room}"
    val androidx_work = "androidx.work:work-runtime-ktx:${Versions.androidx_work}"
    val androidx_work_rxjava2 = "androidx.work:work-rxjava2:${Versions.androidx_work}"

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
}
