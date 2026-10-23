# Layered Feature Architecture

Use this reference when choosing feature boundaries, introducing a use case or repository, tracing data flow, or reviewing whether a layer depends on the right abstractions.

## Layer responsibilities

| Layer | Owns | Should not own |
|---|---|---|
| Presentation | UI events, ViewModels, screen state, Compose/UI components, navigation decisions, UI formatting and resources | Network/database access or business rules that should be reusable outside a screen |
| Domain | Business operations, domain policies, domain models, and contracts that express the data operations those policies need | Android framework types, Compose, API response types, Room entities, or concrete data implementations |
| Data | Repository implementations, remote/local data sources, API/database models, and source-to-domain mapping | Screen rendering decisions or Android string-resource labels exposed as domain values |

These are dependency and responsibility boundaries, not mandatory directory names. A small app may keep layers in packages within one Gradle module; a larger app may use separate Gradle modules where ownership, build isolation, or reuse makes that valuable. Module boundaries should express actual constraints and avoid cycles; do not create modules just to reproduce a diagram.

## Dependency rule and repository ports

Dependencies should point toward stable inner contracts. Presentation depends on domain use cases and values; presentation should not depend directly on data implementations. Data depends inward on domain-owned repository interfaces and domain values, and a data-layer class implements each interface. Data may also use outward infrastructure such as Retrofit, Room, and platform APIs. Domain must not import presentation, data, or framework types.

The compile-time arrow from data to a domain contract is intentional: the implementation imports the interface it implements. At runtime, a use case calls that interface and dependency injection supplies the data or platform implementation. The interface contains only operations the domain needs and uses domain types in its method signatures. It should not expose Retrofit responses, Room entities, UI models, Android `Context`, or data-source details.

```kotlin
// Domain-owned contract
interface ItemRepository {
    suspend fun getItems(query: String): List<Item>
}

class GetItemsUseCase(
    private val repository: ItemRepository,
) {
    suspend operator fun invoke(query: String): List<Item> =
        repository.getItems(query)
}

// Data implementation
class DefaultItemRepository(
    private val remote: ItemRemoteDataSource,
    private val local: ItemLocalDataSource,
) : ItemRepository {
    override suspend fun getItems(query: String): List<Item> {
        val responses = remote.fetchItems(query)
        return responses.map { response -> response.toDomainModel() }
    }
}
```

Keep a port feature-local when only that feature owns its behavior. Put a contract in a shared module only when multiple consumers genuinely share a stable domain concept and the dependency direction remains clear. Prefer small, capability-focused interfaces over a catch-all repository that every use case must depend on.

## Feature organization

A feature commonly has packages for `data`, `domain`, `presentation`, and `di`. Their exact subpackages depend on the project; possible concerns include APIs, remote/local data sources, response models, repository implementations, domain use cases, UI models/mappers, ViewModels, and Compose screens. A directory is not evidence of a true architectural boundary: inspect imports and Gradle dependencies.

Shared modules can hold infrastructure or genuinely shared types, but avoid mixing unrelated API, persistence, domain, and presentation models into one shared model package. If an existing project already has an aggregate shared module, document that it weakens Gradle-level enforcement, preserve package/import boundaries, and avoid widening its public API. Keep separate types where the distinction prevents framework or UI details from leaking across a boundary. Reuse should not erase ownership or force a feature to depend on unrelated UI or data code.

## Kotlin implementation conventions

Prefer constructor injection and explicit, typed operations. A focused use case is commonly a class with one `operator fun invoke(...)`; mark it `suspend` when it performs suspending work. Descriptive named methods are also appropriate when a class exposes more than one operation. Keep simple delegations small, and put decisions, validation, coordination, or a meaningful application boundary in the use case rather than creating a class for every line of code.

Keep remote API interfaces and local DAO access behind narrow data-source adapters. Repositories coordinate the sources, apply source-selection/cache policy, and return domain values. Map at the point where the source boundary is crossed. Top-level extension functions such as `toDomainModel()` or `toUiModel()` are a lightweight option when the project already uses them; group them by source and direction so their ownership is clear. A dedicated mapper type is equally valid when it carries dependencies or complex policy.

Use a generic domain page type such as `Page<T>` when page metadata and results travel together. Convert that metadata to presentation pagination state at the UI boundary. If a mapper can reject incomplete source data, make that explicit in its result type or nullable return, then decide visibly whether the caller should filter, report, or provide a fallback. Use `mapNotNull` only when dropping invalid items is intentional and safe for that screen.

Distinguish source normalization from product and display policy. Parsing a provider's role field belongs in data; choosing which crew roles a screen displays and in what order belongs in domain or presentation according to who owns that rule. Domain enums and identifiers should express stable concepts rather than reusing a presentation enum merely because it has matching values. For execution, failure, and write semantics, consult [data operations and persistence](data-operations-and-persistence.md).

When a use case combines independent requests, use structured concurrency (`coroutineScope` with `async`/`awaitAll`) so the work is cancelled with its caller. Perform prerequisites first, start independent calls together, and await each result once. Define whether a failure should fail the whole operation or whether a particular result is optional; catch only the optional branch's expected failure and always preserve cancellation. Avoid wrapping a synchronous calculation in `async`, or immediately awaiting a single `async` call, because neither creates useful concurrency.

## Look for residual boundary leaks

A repository interface does not make a feature independent if its method signatures or use cases still expose outer-layer types. Review imports and signatures for presentation models, Android framework or WorkManager types, Retrofit responses, and Room entities inside domain. Also check for data code importing presentation resources to create localized labels, or presentation mappers converting UI models into persistence entities. Treat any such occurrence as a boundary violation: use stable domain identifiers/value types, keep platform scheduling behind an outer-layer adapter or port, resolve localized text in presentation, and map persistence types at the data boundary.

Likewise, a shared Gradle module may contain packages named `data`, `domain`, and `presentation` without enforcing those boundaries at compile time. Check the imports inside that module and its public API; package naming alone does not prevent a presentation mapper from depending on Room or an inner layer from depending on UI code.

## Data flow

Trace the full path when adding or reviewing a feature:

1. A Compose callback sends a descriptive event to the ViewModel.
2. The ViewModel validates the event against current state and invokes a domain use case.
3. The use case applies business rules and calls one or more domain repository ports.
4. A data implementation coordinates remote/local sources and maps source models to domain results.
5. The ViewModel or a presentation mapper converts domain results into screen-ready values and publishes a new state.
6. Compose renders the state and forwards subsequent user actions.

Data owns mapping from source-specific models to domain models. Presentation owns mapping from domain results to screen models, formatted labels, and resource identifiers. Keep localization and Android resources at the presentation/platform edge. Map only where a boundary or meaningful transformation exists; avoid boilerplate transformations between identical types.

Use cases can be small delegations when they provide a meaningful domain operation or application boundary. Composite use cases may combine multiple operations, including concurrent independent requests, but keep orchestration and failure behavior explicit. Do not move screen layout choices or string resources into a use case merely because the ViewModel currently calls it.

## Review checklist

- Can the domain compile or be tested without Android and data-layer implementations?
- Do use cases depend on domain contracts rather than concrete repositories?
- Do repository signatures use domain types instead of transport/persistence/UI types?
- Are response, persistence, domain, and presentation models kept distinct where their semantics differ?
- Is the real Gradle dependency graph acyclic and no broader than necessary, with no accidental feature-to-feature coupling?
- Does each mapper live at the boundary it translates?
- Are existing legacy exceptions documented as current behavior instead of presented as the intended pattern?
