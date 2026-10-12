# Feature Modularization and Activity Navigation

Use this reference when splitting an Android app into feature Gradle modules, deciding what belongs in shared modules, or routing between feature-owned Activities.

## Split by user capability

Organize feature modules around cohesive user capabilities or product areas, not technical layer names alone. A feature module should own the code that changes together for that capability: its Activity or entry point, UI, ViewModel/state, domain operations, data implementation, and feature DI registration when those concerns are present.

```text
:app                         app startup, application config, root DI composition
:feature_<capability>        one cohesive user-facing capability
:shared_data                 reusable network/database infrastructure
:shared_ui                   reusable visual components and presentation utilities
:navigation                  route contracts/builders and shared argument definitions
:shared_domain_or_models     only stable concepts genuinely shared by features
```

This is a menu of responsibilities, not a required module list. A small feature may keep `data`, `domain`, and `presentation` as packages inside one Gradle module. Split those layers into separate modules only when independent compilation, ownership, reuse, or dependency enforcement justifies the extra graph and build maintenance.

### Dependency direction

- The app module is the composition layer: it depends on the feature modules it launches and aggregates shared infrastructure/DI definitions.
- A feature may depend on shared modules it actually uses, such as navigation contracts, shared UI, or infrastructure APIs.
- A feature should not depend on the app module. Keep shared modules independent of feature implementations.
- Avoid direct feature-to-feature dependencies. When one feature needs to open another, depend on a stable navigation contract or a higher-level app coordinator instead of importing the destination's Activity or ViewModel.
- Keep the Gradle graph acyclic. Declare real edges in module build files and verify included modules in Gradle settings; do not assume package separation enforces module boundaries.

Shared modules should have a clear owner and responsibility. Do not turn a shared model or utilities module into a dumping ground for unrelated feature behavior, API/Room types, domain entities, and UI models. Share a model only when consumers need the same stable meaning and doing so does not invert dependencies.

## Centralize route contracts

For Activity-based navigation, a shared navigation/route module can own route builders and the stable URI/argument contract, while each feature owns its Activity and its Android manifest declaration. This lets callers navigate without depending on the destination module's implementation.

The contract has several pieces that must agree:

1. A route builder creates an `Intent` with the expected action and URI (scheme plus host/path) and sets arguments.
2. The destination Activity's manifest declares an intent filter that matches that action and URI.
3. The Activity reads and validates the incoming URI and arguments before initializing the feature screen.
4. The app includes both the route module and destination feature so manifest merging and DI composition can resolve the Activity and route builder.

Do not treat a helper that creates a URI as the whole deep-link registration. Inspect the manifest filter and merged manifest too. Keep route constants/argument keys centralized or otherwise generated from one source so callers and destination filters cannot silently drift.
If route builders are provided through dependency injection, register them in the navigation module and include that module at the app composition root. The navigation module should expose route contracts without depending on feature Activity or ViewModel implementations.

```kotlin
import android.content.Intent
import androidx.core.net.toUri

class ItemDetailsRoute(private val packageName: String) {
    fun create(itemId: String): Intent =
        Intent(Intent.ACTION_VIEW, "sample-app://items/details".toUri())
            .setPackage(packageName)
            .putExtra(ARG_ITEM_ID, itemId)
}
```

The example shows an in-app custom-scheme route. For external web entry points, use the platform's supported verified App Links/deep-link configuration when appropriate, and validate all incoming values as untrusted input. Set `android:exported` according to whether other apps must be able to launch the Activity; do not expose an Activity merely to support internal routing. Apply authorization and input checks in the destination, not only in the caller's route builder.

Prefer stable identifiers and small primitive arguments over passing an entire screen/presentation model between Activities. Re-load or resolve the destination data from its ID. Pass a Parcelable object only when the contract intentionally requires a snapshot and its compatibility/lifecycle behavior is understood. Handle absent, malformed, stale, and unsupported arguments with a defined recovery path.

If the project uses Compose Navigation or another navigation framework instead of separate Activities, apply the same boundary principles to typed destinations: keep route contracts stable, keep feature implementation details private, and validate arguments at the receiving screen.

## Verify module and route behavior

- Draw the direct Gradle project dependency graph and check for cycles or accidental feature coupling.
- Confirm every target Activity is included by the app and its manifest filter matches the route URI/action.
- Inspect the merged manifest for exported status, merged intent filters, and duplicated/conflicting declarations.
- Test route construction (action, scheme/host/path, package, and arguments) and destination argument parsing.
- Add Activity/instrumentation coverage when manifest dispatch or platform deep-link behavior is part of the change; unit-test route construction where possible.
- Verify navigation works from each calling feature without introducing a dependency on destination UI or ViewModel classes.
