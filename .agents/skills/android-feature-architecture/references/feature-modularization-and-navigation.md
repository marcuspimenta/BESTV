# Feature Modularization and Navigation 3

Use this reference when deciding whether to split feature code into Gradle modules or when changing navigation in a project that already uses Jetpack Navigation 3. Navigation 3 and its APIs evolve; inspect the resolved library version and its current official documentation before applying a sample.

## Split by capability when it helps

Organize modules around cohesive user capabilities or product areas when ownership, reuse, independent compilation, or dependency enforcement justifies the build and maintenance cost. A feature may keep its UI, state holder, domain operations, data implementation, navigation entry, and registration in packages inside one module. Do not create modules only to mirror architectural layer names.

Possible responsibilities include:

```text
:app                         startup and top-level composition
:feature_<capability>        capability UI, state, operations, and registrations
:shared_navigation           shared destination contracts, if features need them
:shared_data                 reusable network/database infrastructure
:shared_ui                   reusable visual components and presentation utilities
:shared_domain_or_models     stable concepts genuinely shared by features
```

This is an example, not a required module list. Avoid shared modules that become catch-alls. Check `settings.gradle[.kts]` and module build files for the actual dependency graph; package names alone do not establish Gradle boundaries.

When modules are used, keep the graph acyclic. The app or another deliberate composition layer can aggregate feature registrations. Shared modules should not depend on feature implementations. Avoid direct feature-to-feature dependencies when a small shared contract or navigation key can express the interaction.

## Keep navigation ownership clear

Navigation 3 models destinations as keys and renders them through `NavEntry` content. A project can define entries in one host or let feature modules contribute entry providers that the host composes. Use the approach that fits the existing module graph; provider registration is an option for modular ownership, not a requirement for every app.

Keep destination UI and entry construction close to the feature that owns them. Put shared keys or route contracts in a module that does not depend on feature UI only when multiple modules need those contracts. Do not add a navigation module solely to pass a key between two packages in one module.

Use a back stack appropriate to the product's navigation behavior. If the project needs a saveable Navigation 3 back stack, use serializable keys with the persistence API supported by its resolved Navigation 3 version. Prefer small stable identifiers over transporting whole UI or persistence models. Scope saveable UI state and ViewModels to navigation entries only as required, using the current Navigation 3 entry decorator APIs and lifecycle rules.

Internal navigation should update the chosen navigation state. Keep Android `Intent`s at platform boundaries such as launcher startup, notifications, app links, or other external entry points. Validate incoming URI data and extras before converting them into an internal destination, and define a recovery behavior for invalid input. Preserve existing public URI and argument contracts unless the task includes a migration.

See Android's [Navigation 3 basics](https://developer.android.com/guide/navigation/navigation-3/basics), [saveable back stack recipe](https://developer.android.com/guide/navigation/navigation-3/recipes/basicsaveable), and [entry decorators](https://developer.android.com/guide/navigation/navigation-3/naventrydecorators) for APIs and lifecycle behavior supported by the current library.

## Preserve and verify navigation behavior

Before changing navigation, trace how startup, external links, feature-to-feature transitions, Back, and state restoration work in the active app. Check manifest declarations and the merged manifest when entry points are contributed by multiple modules. Keep behavior consistent across the input modes and form factors the app supports.

Choose verification from the changed behavior and the project's existing setup. Useful cases include:

- Each registered key resolves to the intended entry, with missing or duplicate registrations handled deliberately.
- Launcher and external-link inputs map to valid destinations; malformed or incomplete inputs have a recoverable outcome.
- Forward navigation and Back work at each relevant stack depth, including repeated instances of destinations that carry per-entry state.
- State restoration works across configuration change and process recreation when the product requires it.
- Platform intents preserve their intended identity and data.
- The Gradle graph remains acyclic and features do not gain accidental implementation dependencies on one another.

Prefer the project's focused navigation checks and tests. Do not assume a task name or test mechanism from another repository.
