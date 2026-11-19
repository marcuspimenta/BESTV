# iOS app host

Open `iosApp.xcodeproj` in Xcode and run the `iosApp` scheme on an iOS simulator. The `iosAppFramework` Gradle module contains the iOS Compose entry point and platform bindings; the Xcode build phase runs `:iosAppFramework:assembleComposeAppDebugXCFramework`, then Xcode links the matching simulator or device slice from `ComposeApp.xcframework`.

The Xcode build reads `TMDB_ACCESS_TOKEN` from the build environment or the root `local.properties` file and bundles it for the iOS host. The app also accepts a `TMDB_ACCESS_TOKEN` Xcode scheme environment override. The app registers the `bestv://` URL scheme and forwards incoming links to the shared Compose navigation host.
