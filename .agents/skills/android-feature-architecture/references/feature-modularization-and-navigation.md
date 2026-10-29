# Feature Modularization and Navigation 3

Use this reference when splitting Android features into Gradle modules or changing the navigation flow in BESTV. The app uses a single `NavigationActivity` as its Android entry point and Jetpack Navigation 3 for screen-to-screen navigation.

## Split by user capability

Organize feature modules around cohesive user capabilities or product areas, not technical layer names alone. A feature module owns the code that changes together for that capability: UI, ViewModel/state, domain operations, data implementation, feature navigation entry, and DI registration when those concerns are present.

```text
:app                         startup, NavigationActivity, NavigationHost, root DI composition
:feature_<capability>        capability UI, ViewModel, entry provider, and feature DI
:route_shared                serializable destination keys, route contracts, entry-provider API
:shared_data                 reusable network/database infrastructure
:shared_ui                   reusable visual components and presentation utilities
:shared_domain_or_models     only stable concepts genuinely shared by features
```

This is a menu of responsibilities, not a required module list. A small feature may keep `data`, `domain`, and `presentation` as packages inside one Gradle module. Split those layers into separate modules only when independent compilation, ownership, reuse, or dependency enforcement justifies the extra graph and build maintenance.

### Dependency direction

- The app module is the composition layer: it depends on the feature modules it launches and aggregates shared infrastructure and DI definitions.
- A feature may depend on shared modules it actually uses, such as route contracts, shared UI, or infrastructure APIs.
- A feature should not depend on the app module. Keep shared modules independent of feature implementations.
- Avoid direct feature-to-feature dependencies. To open another feature, emit a shared `NavKey`; do not import its screen, ViewModel, or entry-builder function.
- Keep the Gradle graph acyclic. Declare real edges in module build files and verify included modules in Gradle settings; package separation alone does not enforce module boundaries.

## Navigation ownership and provider registration

`NavigationActivity` is the only screen-host Activity. It resolves an incoming Android `Intent` to an initial destination, defaults to `WorkBrowseDestination` for launcher startup, and supplies the injected `List<NavigationEntryProvider>` to `NavigationHost`. The host owns the Navigation 3 back stack, pushes typed keys for forward navigation, pops for Back, and exits the Activity when the root entry is reached.

Keep responsibilities separated:

- `route_shared` owns serializable `NavKey` destination types, shared route/argument contracts, and the `NavigationEntryProvider` interface. It must not depend on feature presentation implementations.
- Each feature owns its `NavigationEntryProvider` implementation and `EntryProviderScope` builder. The provider installs that feature's typed `entry<Destination>` and passes the host's `navigate` and `onBack` callbacks to the screen.
- Each feature registers its provider as a `NavigationEntryProvider` in its own Koin module. The app aggregates providers with Koin `getAll<NavigationEntryProvider>()` and injects the resulting list into `NavigationActivity`.
- `NavigationHost` installs the injected providers into one `entryProvider` block and applies Navigation 3 state/ViewModel entry decorators. Use navigation-entry scoping for destination ViewModels and unique entry identity when the same destination can be pushed more than once.
- Keep the feature builder and screen local to their feature. Do not manually resolve providers from a global Koin instance in the host or recreate a parallel registry in the app.

Internal navigation uses typed keys, not Activity intents. For new destinations, prefer small stable identifiers and load feature state in the destination. Keep any existing payload-based contract only while compatibility requires it; changing it should be an explicit migration.

## Android entry points and deep links

Android intents remain appropriate at platform boundaries: launcher startup, custom-scheme links, notifications, recommendation `PendingIntent`s, and other system entry points. In BESTV, preserve the existing `bestv://` scheme and route hosts. The manifest directs these entry points to `NavigationActivity`; `Intent.toBestvDestination()` maps the incoming contract to the initial Navigation 3 key. Do not add one Activity per screen for internal navigation.

Treat incoming intent data and extras as untrusted. Validate required values while mapping to a destination and define recovery for absent or malformed input, such as falling back to Browse or showing a recoverable error. Preserve existing URI and Parcelable contracts unless the task explicitly changes them. For new public links, prefer stable IDs over passing an entire presentation model.

Do not treat a URI helper as the complete deep-link registration. Check that the manifest filter, scheme/host/path, argument parser, and route builder agree, and inspect the merged manifest when declarations come from multiple modules. Set `android:exported` according to whether external apps need to launch the host; apply authorization and input checks at the receiving boundary.

## Preserve and verify navigation behavior

- Use `rememberNavBackStack` with serializable destination keys so the Navigation 3 stack can be saved and restored. Keep saveable-state and ViewModel-store entry decorators on `NavDisplay`; avoid Activity-scoped singleton ViewModels for screen state.
- Give repeatable destinations distinct entry IDs when their route key includes per-entry state, so repeated detail/search screens do not collide and popping restores the prior entry's state.
- Keep Back behavior consistent across system/mobile Back and TV remote Back. Check D-pad focus restoration and TV launcher startup when changing the host or transitions.
- Run `:app:verifyNavigationEntries` when adding or changing destinations. The test resolves the production Koin provider list, installs providers into the entry registry, and checks expected destination keys, missing providers, and duplicate registrations. The app build and `check` tasks depend on this verification.
- Test launcher startup and each custom URI route; test recommendation intents; test feature-to-feature pushes, repeated detail pushes, and Back at each stack depth. Verify recreation and state restoration after configuration change and process recreation where the platform test setup permits.
- Verify there is no accidental feature-to-feature Gradle dependency and that the app composes every feature module containing a registered provider.
