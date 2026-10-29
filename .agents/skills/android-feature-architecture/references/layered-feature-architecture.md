# Layered Feature Architecture

Use this reference when choosing feature boundaries, introducing a repository or use case, tracing data flow, or reviewing dependencies between layers. Android's [architecture guidance](https://developer.android.com/topic/architecture/recommendations) treats the data and UI layers as core and the domain layer as optional; adapt these recommendations to the active project's architecture and constraints.

## Layer responsibilities

| Layer | Owns | Should not own |
|---|---|---|
| Presentation | UI events, ViewModels, screen state, Compose/UI components, navigation decisions, UI formatting and resources | Network/database access or business rules that should be reusable outside a screen |
| Domain (optional) | Business operations, policies, and domain models that benefit from reuse or separation; contracts for data operations when the project uses inner-layer ports | Android framework types, Compose, API response types, Room entities, or concrete data implementations when the chosen architecture keeps domain independent |
| Data | Repositories, remote/local data sources, API/database models, and source-to-app/domain mapping | Screen rendering decisions or Android string-resource labels exposed as domain values |

These are responsibility boundaries, not mandatory directory names or a requirement to use every layer. A small app may keep data and UI types in packages within one Gradle module; a larger app may use separate Gradle modules where ownership, build isolation, or reuse makes that valuable. Module boundaries should express actual constraints and avoid cycles; do not create modules just to reproduce a diagram.

## Dependency rule and repository ports

Keep UI and data-source details separated: UI or ViewModels should access application data through repositories, not directly through database/network data sources. When a project uses Clean Architecture or another strict inner-layer boundary, dependencies point toward stable inner contracts: domain owns the repository interfaces it needs, data implements them, and the composition root binds them. In other architectures, use interfaces at meaningful module or testing boundaries without adding a domain layer solely to hold trivial forwarding operations. Data may use infrastructure such as Retrofit, Room, and platform APIs; when an independent domain layer exists, keep those types out of it.

When inner-layer ports are used, the compile-time arrow from data to a domain contract is intentional: the implementation imports the interface it implements. At runtime, an operation calls that interface and dependency injection supplies the data or platform implementation. The interface contains only operations the inner layer needs and uses its own types in method signatures. It should not expose Retrofit responses, Room entities, UI models, Android `Context`, or data-source details.

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

A feature commonly has packages for `data`, `presentation`, and sometimes `domain` and `di`. Their exact subpackages depend on the project; possible concerns include APIs, remote/local data sources, response models, repository implementations, business operations, UI models/mappers, state holders, and UI screens. A directory is not evidence of a true architectural boundary: inspect imports and Gradle dependencies.

Shared modules can hold infrastructure or genuinely shared types, but avoid mixing unrelated API, persistence, domain, and presentation models into one shared model package. If an existing project already has an aggregate shared module, document that it weakens Gradle-level enforcement, preserve package/import boundaries, and avoid widening its public API. Keep separate types where the distinction prevents framework or UI details from leaking across a boundary. Reuse should not erase ownership or force a feature to depend on unrelated UI or data code.

## Kotlin implementation conventions

Prefer constructor injection and explicit, typed operations. When a use case is useful, it commonly has one focused operation; `operator fun invoke(...)` and descriptive named methods are both reasonable. Keep simple delegations small, and put decisions, validation, or coordination in a use case only when it provides a meaningful business or application boundary rather than adding a class for every line of code.

Keep remote API interfaces and local DAO access behind narrow data-source adapters. Repositories coordinate the sources, apply source-selection/cache policy, and return app-owned values. If the project has a domain layer, repositories should expose domain values and map at the source boundary. Top-level extension functions such as `toDomainModel()` or `toUiModel()` are a lightweight option when the project already uses them; group them by source and direction so their ownership is clear. A dedicated mapper type is equally valid when it carries dependencies or complex policy.

Use an app-owned page type such as `Page<T>` when page metadata and results travel together, if that abstraction fits the codebase. Convert metadata to presentation pagination state at the UI boundary. If a mapper can reject incomplete source data, make that explicit in its result type or nullable return, then decide visibly whether the caller should filter, report, or provide a fallback. Use `mapNotNull` only when dropping invalid items is intentional and safe for that screen.

Distinguish source normalization from product and display policy. Parsing a provider's role field belongs in data; choosing which roles a screen displays and in what order belongs where the project owns that rule, often in a use case when shared or in presentation when screen-specific. Keep stable app concepts distinct from presentation enums when their semantics can diverge. For execution, failure, and write semantics, consult [data operations and persistence](data-operations-and-persistence.md).

When an operation combines independent requests, use structured concurrency (`coroutineScope` with `async`/`awaitAll`) so the work is cancelled with its caller. Perform prerequisites first, start independent calls together, and await each result once. Define whether a failure should fail the whole operation or whether a particular result is optional; catch only the optional branch's expected failure and always preserve cancellation. Avoid wrapping a synchronous calculation in `async`, or immediately awaiting a single `async` call, because neither creates useful concurrency.

## Look for residual boundary leaks

A repository interface does not make a feature independent if its signatures expose types owned by another layer. In projects with a separate domain layer, review imports and signatures for presentation models, Android framework or WorkManager types, Retrofit responses, and Room entities inside domain. Also check for data code importing presentation resources to create localized labels, or presentation mappers converting UI models into persistence entities. Keep platform scheduling behind an outer-layer adapter or meaningful port, resolve localized text in presentation, and map persistence types at the data boundary.

Likewise, a shared Gradle module may contain packages named `data`, `domain`, and `presentation` without enforcing those boundaries at compile time. Check the imports inside that module and its public API; package naming alone does not prevent a presentation mapper from depending on Room or an inner layer from depending on UI code.

## Data flow

Trace the relevant path when adding or reviewing a feature; a domain/use-case step is present only when the project uses that layer:

1. A UI action sends a descriptive event to the screen state holder.
2. The state holder validates the event against current state and requests the needed operation.
3. An optional domain operation applies business rules and calls repository contracts when the project uses that boundary.
4. A repository coordinates remote/local sources and maps source models to app-owned results.
5. The state holder or a presentation mapper converts results into screen-ready values and publishes new state.
6. The UI renders the state and forwards subsequent user actions. In Compose projects, a callback is a common way to send those actions.

Data owns mapping from source-specific models to app-owned values. Presentation owns mapping from operation results to screen models, formatted labels, and resource identifiers. When a distinct domain model exists, map to it in data and then map to presentation as needed. Keep localization and Android resources at the presentation/platform edge. Map only where a boundary or meaningful transformation exists; avoid boilerplate transformations between identical types.

Use cases can be small delegations when they provide a meaningful domain operation or application boundary. Composite use cases may combine multiple operations, including concurrent independent requests, but keep orchestration and failure behavior explicit. Do not move screen layout choices or string resources into a use case merely because the ViewModel currently calls it.

## Review checklist

- If the project has a domain layer, can it be compiled or tested without Android and data-layer implementations?
- When inner-layer ports are part of the architecture, do business operations depend on those contracts rather than concrete infrastructure?
- Do repository signatures use app-owned types instead of transport, persistence, or UI-only types?
- Are response, persistence, domain, and presentation models kept distinct where their semantics differ?
- Is the real Gradle dependency graph acyclic and no broader than necessary, with no accidental feature-to-feature coupling?
- Does each mapper live at the boundary it translates?
- Are existing legacy exceptions documented as current behavior instead of presented as the intended pattern?
