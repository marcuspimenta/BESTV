# Dependency Injection

Use this reference when adding a feature to the DI graph, changing implementation bindings, choosing object lifetimes, or verifying that a feature can be constructed in isolation.

## Composition root and feature modules

Keep framework object construction at an auditable composition root. The app-level root aggregates shared infrastructure and feature registrations. A feature module can group its API clients, data sources, repository implementations, domain use cases, and ViewModels, while shared modules register resources such as network clients, databases, or app-wide presentation services.

When a project uses domain-owned ports or other inner-layer interfaces, bind each contract to a concrete implementation outside that layer. Business operations should depend on the contract rather than request concrete data repositories from a container or service-locate dependencies. Projects without that architecture can still use the same composition-root principle for the boundaries they choose to keep.

Conceptually:

```text
application root
├── shared infrastructure registrations
└── feature registration
    ├── data implementation
    ├── domain contract -> data implementation binding
    ├── use cases
    └── ViewModels
```

With Koin, an interface binding can be expressed explicitly (illustrative only):

```kotlin
factoryOf(::DefaultItemRepository)
factory<ItemRepository> { get<DefaultItemRepository>() }
factoryOf(::GetItemsUseCase)
viewModelOf(::ItemsViewModel)
```

Use the active project's DI framework and conventions. With any framework, keep definitions close to the feature they construct, avoid duplicate registrations and dependency cycles, and make interface-to-implementation bindings easy to find.

## Lifetimes and parameters

Choose lifetimes from ownership and state-sharing needs, not from a blanket rule:

- App/process-wide immutable infrastructure may be a singleton when it is safe to share.
- Stateless use cases and short-lived data helpers are often factories.
- ViewModels should use the framework's lifecycle-aware ViewModel binding.
- Use a narrower scope when an object belongs to a user/session/feature lifecycle and must share state within that scope.

Review what a singleton retains. A singleton should not capture an Activity, View, or short-lived scope. Pass runtime values such as identifiers or navigation arguments through the framework's supported parameter mechanism instead of registering a new global object for every value. Use qualifiers when multiple bindings share the same type, and give qualifiers clear semantic names.

Prefer constructor injection so dependencies and test seams remain visible. Avoid injecting a container into business classes or retrieving dependencies globally. For Android framework dependencies, use the DI framework's Android integration or a narrow wrapper rather than passing `Context` through domain APIs.

When logic depends on device capabilities, locale/region, dispatchers, or time, use the narrow value/provider the project needs and bind the Android implementation at the composition root. Keep dynamic UI configuration out of process-wide cached values. For workers and receivers, verify framework creation and cold-start initialization as described in [background work and platform adapters](background-work-and-platform-adapters.md).

## Wiring verification

For a feature with meaningful DI bindings, create an isolated container or component using the production feature registrations and substitute infrastructure dependencies with fakes/mocks or a harmless test configuration. Resolve the domain contract and verify it is backed by the intended implementation. This is a wiring smoke test; it does not prove repository behavior, endpoint correctness, or database mapping.

When a graph cannot be instantiated without the real network or database, identify which provider leaks infrastructure setup and supply a test binding at that boundary. Do not add a test-only DI library unless the project's build and conventions warrant it.

Test the changed graph path, not only repository ports. Resolve a parameterized screen state holder or worker through its supported creation mechanism when that wiring changes, supplying realistic runtime arguments and capability bindings. A port-resolution test does not prove every lazy definition can be constructed. Check app-level composition when shared registrations change, and close isolated containers in cleanup even if an assertion fails.
