# BESTV
[![Kotlin Version](https://img.shields.io/badge/Kotlin-1.3.61-blue.svg)](https://kotlinlang.org)

<img src="/app/src/main/res/drawable/app_icon.png"   height="150" width="150">

Android TV App powered by [TMDb](https://www.themoviedb.org/)

It is a easy way to find the best TV content, the top movies, series... all of that in your TV.

The goal of the project is to be a guide line about Android TV, that shows how to use the Leanback library from Google. The project also uses the popular libraries and tools from the Android ecosystem. 

The main widgets from Android TV are used: `BrowseSupportFragment`, `SearchSupportFragment`, `DetailsSupportFragment`, `VerticalSupportFragment`.

<img src="google_play_badge/en_get.svg" align="left" hspace="20" height="100" width="200">

Get it on [Google Play](https://play.google.com/store/apps/details?id=com.pimenta.bestv)

## Project
- 100% [Kotlin](https://kotlinlang.org/)
- Android TV
- Clean Architecture
- Android Architecture Components
- Dependency Injection

## Tech-stack
This project uses the popular libraries and tools from the Android ecosystem.
- Tech-stack
    - [Kotlin](https://kotlinlang.org/)
    - Android TV
        - [Leanback library](https://developer.android.com/tv)
        - [Recommend TV content](https://developer.android.com/training/tv/discovery/recommendations)
    - Android Architecture Components
        - [ROOM](https://developer.android.com/topic/libraries/architecture/room) local data storage
        - [Lifecycle-aware components](https://developer.android.com/topic/libraries/architecture/lifecycle)
    - [Retrofit](https://square.github.io/retrofit/) networking 
    - [Dagger](https://dagger.dev/android.html) dependency injection
    - [RxAndroid](https://github.com/ReactiveX/RxAndroid) reactive components
    - [Glide](https://github.com/bumptech/glide) image loading library
    - [Lottie](http://airbnb.io/lottie) animation library 
- Architecture
    - MVP
    - [Clean Architecture](https://proandroiddev.com/kotlin-clean-architecture-1ad42fcd97fa)
- Tests
    - [Unit Tests](https://en.wikipedia.org/wiki/Unit_testing)
    - [Mockito](https://github.com/mockito/mockito) 
    - [Mockito-Kotlin](https://github.com/nhaarman/mockito-kotlin)
- Gradle
    - [Ktlint plugin](https://github.com/JLLeitschuh/ktlint-gradle)

## API Keys
BesTV uses [The Movie DB](https://www.themoviedb.org/) API in order to fetch all the data, but is not endorsed or certified by TMDb.
To be able to run this application you have to create an API KEY from The Movie DB and place it in your gradle file.
<br>
```
buildConfigField "String", "TMDB_API_KEY", "YOUR API KEY HERE"
```

## Features
<ul>
<li>Show the top movies and tv shows</li>
<li>Show the top movies and tv shows by genre</li>
<li>Details about a work including the casts, the videos, the similar and recommended works</li>
<li>Details about a cast including the credits</li>
<li>Search the movies and the tv shows by title</li>
</ul>

<p align="center">
  <img src="gif/app.gif">
</p>

## TODO
- Improve the test coverage
- Give support to more languages

## References
- [Android TV Leanback](https://github.com/googlesamples/androidtv-Leanback)
- [Google Codelab TV Recommendations](https://github.com/googlecodelabs/tv-recommendations)
- [Android TV application hands on tutorial](https://corochann.com/android-tv-application-hands-on-tutorial)
- [Unit Testing asynchronous RxJava code](https://medium.com/@PaulinaSadowska/writing-unit-tests-on-asynchronous-events-with-rxjava-and-rxkotlin-1616a27f69aa)
- [Lifecycle Architecture Component](https://medium.com/mindorks/autodisposable-for-rxjava-with-lifecycle-architecture-component-23dfcfa83a2)
- [Dagger2: @Component.Factory and @SubComponent.Factory](https://android.jlelse.eu/dagger2-component-factory-and-subcomponent-factory-b181ec96b213)
- [Why you need Use Cases/Interactors](https://proandroiddev.com/why-you-need-use-cases-interactors-142e8a6fe576)

## Licence
```
Copyright (c) 2018 Marcus Pimenta

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
