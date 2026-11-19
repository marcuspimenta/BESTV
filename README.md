<h1 align="center">
  <img src="common/src/androidMain/res/drawable/bestv_mark.png" alt="" height="52" align="middle">
  <img src="common/src/androidMain/res/drawable/bestv_wordmark.png" alt="BESTV" height="40" align="middle">
</h1>

BESTV is a Kotlin Multiplatform app for discovering movies and TV shows on **Android TV, Android mobile, and iOS**, powered by [TMDb](https://www.themoviedb.org/).

The app shares its touch-first Compose Multiplatform screens between Android and iOS. Android TV keeps a dedicated remote-friendly interface. The platforms share the data and domain layers.

## TV and mobile showcase

The composite bellow pairs the TV and mobile experiences. Each platform adapts browsing and discovery to its screen and input method.

<p align="center">
  <img src="docs/screenshots/tv-mobile-showcase.png" alt="BESTV shown on an Android TV and a mobile phone" width="100%">
</p>

## Features

- Browse popular, top-rated, upcoming, and genre-based movies and TV shows.
- Search movies and TV shows by title.
- View work details, cast, videos, streaming providers, similar works, and recommendations.
- View cast biographies and credits.
- Save favorites.
- Use a TV interface designed for remote control or mobile layouts designed for touch.

## Technology

- Kotlin
- Compose Multiplatform for Android and iOS mobile screens
- Jetpack Compose for the Android TV interface
- Kotlin Lifecycle ViewModel and Compose lifecycle APIs shared through KMP, plus SQLDelight local storage
- Kotlin Coroutines and Flow
- Ktor Client for network requests
- Coil 3 for Android and iOS image loading
- Koin for dependency injection
- Lottie for animations

## Architecture

BESTV uses modular Clean Architecture with MVI presentation state and events.

- [Clean Architecture](CLEAN_ARCHITECTURE.md)
- [MVI Architecture](MVI_ARCHITECTURE.md)
- [Koin dependency injection](KOIN_DI.md)

The app entry points follow a platform-first naming pattern:

| Project area | Responsibility |
|---|---|
| `androidApp` | Android application startup, Android Navigation 3 host, resources, and app packaging. |
| `iosApp` | Native SwiftUI/Xcode application and iOS lifecycle entry point. |
| `iosAppFramework` | iOS-only Kotlin umbrella module that assembles shared feature screens and iOS bindings into the `ComposeApp` framework. |
| `common` | Shared KMP models, Ktor setup, SQLDelight schema and persistence, presentation primitives, route contracts, and shared DI. |
| Mobile `feature_*` | Shared data/domain/ViewModels/mobile Compose screens in `commonMain`; Android navigation adapters and TV UI in `androidMain`. |

The `common` and mobile feature modules target Android and iOS; `feature_recommendation` remains Android-only. Mobile screens are implemented in feature `commonMain` source sets, so Android and iOS render the same Compose Multiplatform screens. Android TV keeps its remote-focused screens in Android source sets. Android-specific drivers, resources, network engines, and Intent adapters also remain in Android source sets; Android app startup and Navigation 3 hosting belong to `androidApp`. The iOS Xcode app consumes the `iosAppFramework` umbrella framework, which combines the shared screens with iOS platform bindings.

Open `iosApp/iosApp.xcodeproj` in Xcode to run the iOS app on a simulator. Set `TMDB_ACCESS_TOKEN` in the Xcode scheme environment to enable authenticated TMDb requests. The Xcode build phase creates the Kotlin XCFramework before linking.

## TMDb API

BESTV uses [version 3 of the TMDb API](https://developers.themoviedb.org/3/getting-started/introduction). It is not endorsed or certified by TMDb.

To run the app, configure a TMDb read access token and keep it out of tracked files. For local Android builds, add it to the root `local.properties` file (which is ignored by Git):

```properties
TMDB_ACCESS_TOKEN=your_tmdb_access_token
```

For Android CI builds, store the token in the CI secret manager and expose it to Gradle as the `TMDB_ACCESS_TOKEN` environment variable. The environment variable takes precedence over `local.properties`. If the token is not configured, Gradle logs a warning and builds without TMDb authentication. For iOS, provide the token as `TMDB_ACCESS_TOKEN` in the Xcode scheme environment.

The Ktor client sends `TMDB_ACCESS_TOKEN` on TMDb requests as `Authorization: Bearer <token>`. Android uses the OkHttp engine for its 20 MiB disk cache.

The Android app includes its configured TMDb credential in the APK, and the iOS app receives it at runtime from its scheme environment. A credential in either distributed client can be extracted; keeping it out of Git protects the repository, while a backend proxy is needed to keep it out of client apps.

## References

- [Compose Multiplatform](https://www.jetbrains.com/compose-multiplatform/)
- [Jetpack Compose for TV](https://developer.android.com/jetpack/compose/tv)
- [Android TV recommendations](https://developer.android.com/training/tv/discovery/recommendations)
- [Kotlin Coroutines guide](https://kotlinlang.org/docs/coroutines-guide.html)
- [Koin documentation](https://insert-koin.io/docs/quickstart/android/)

## License

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
