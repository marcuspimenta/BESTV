# ViewModel and UI State

Use this reference when designing screen state, events, effects, asynchronous work, or Compose/ViewModel interaction.

## Unidirectional flow

Treat the ViewModel as the owner of screen transitions:

```text
user action -> event -> ViewModel -> use case -> result -> new state -> UI render
                                      └──────────────> one-time effect
```

The UI renders state and forwards callbacks. It should not independently fetch feature data or become a second source of truth. Keep business rules in use cases/domain, screen orchestration in the ViewModel, and rendering in Compose.

## State, events, and effects

- **State** is the durable, complete, immutable snapshot needed to render the screen now. Expose it as `StateFlow`; keep the mutable flow private and publish replacements with atomic updates such as `MutableStateFlow.update`.
- **Events** are descriptive inputs from the user or platform. Prefer sealed types handled exhaustively in one public event entry point. Name them for intent (`QueryChanged`, `ItemSelected`, `Retry`) instead of setter mechanics.
- **Effects** are one-time actions that should not be replayed as durable screen facts, such as navigation or launching an external action. Keep them separate from state and define buffering/replay/consumption semantics intentionally.

If an action must survive process recreation or remain visible until acknowledged, model that requirement in durable state rather than relying on a fire-and-forget effect. A Channel, SharedFlow, or another effect mechanism can work; choose based on delivery guarantees and lifecycle behavior, not convention alone.

If the project has a shared base ViewModel, keep it limited to presentation mechanics such as exposing a read-only `StateFlow`, applying atomic state updates, and publishing effects. Keep feature transitions in the feature ViewModel. Document the effect stream's buffering and subscriber behavior; for example, a `Channel` exposed with `receiveAsFlow()` is a consuming stream, not a broadcast to every collector.

## Choose a state shape from screen behavior

Use sealed variants for mutually exclusive whole-screen modes, such as `Loading`, initial `Error`, and `Loaded`. Use a data class around a sealed primary result when the screen also has independent values that remain meaningful across those modes.

```kotlin
data class ItemsState(
    val query: String = "",
    val selectedId: String? = null,
    val result: Result = Result.Initial,
    val isRefreshing: Boolean = false,
    val page: PageState = PageState(),
) {
    sealed interface Result {
        data object Initial : Result
        data object Loading : Result
        data class Empty(val message: UiText) : Result
        data class Error(val reason: ErrorReason) : Result
        data class Loaded(val items: List<ItemUiModel>) : Result
    }
}
```

Use a simple sealed top-level state for simple screens with exclusive modes. Use a data class with nested states when query/filter selection, loaded content, refresh, pagination, or partial failures can coexist. Do not combine unrelated booleans into contradictory primary states (for example, initial loading, fatal error, and loaded content all true at once).

Decide the behavior for each transition:

| Situation | State design question |
|---|---|
| First load | Is there prior content, or should the UI show a full-screen loading mode? |
| Empty result | Is this a successful empty result, an untouched screen, or an error? Keep them distinct when the UI differs. |
| Refresh | Does refresh replace content or keep it visible with a refresh indicator? |
| Partial failure | Can successful sections remain usable while one section reports an error? Represent errors at the affected scope. |
| Pagination | Track progress and metadata per list/section; do not replace the whole screen with loading for a page request. |
| Retry | Preserve the values needed to repeat the failed operation and expose an explicit recovery transition. |

Keep state limited to UI-ready immutable values. Do not put Android views, repositories, mutable collections, or coroutine jobs into state. Avoid storing values that can be derived cheaply from the current state unless doing so clarifies the render contract.

For screens with multiple independently loaded rows or sections, model the loaded content as a collection of typed section variants when their data and interactions differ. Keep each section's items and pagination/error status together, and update only the affected section when a page request completes. Keep cross-section values such as query, current selection, or selected tab at the screen-state level. Use stable section/item identifiers for lazy-list keys; list positions are not identity.

## Asynchronous transitions and cancellation

Launch work in `viewModelScope`. For latest-wins work such as search-as-you-type, cancel/supersede previous requests with `collectLatest`, a managed `Job`, or an equivalent request-generation strategy. Ensure a stale completion cannot overwrite newer state. Guard duplicate pagination and events that no longer apply to the current screen mode.

For text search, a query flow can trim or classify blank input, debounce non-empty queries, suppress duplicate values, and use `collectLatest` so older searches are cancelled when a newer query arrives. Handle clear/blank input immediately when that matches the UI contract. A dedicated request processor is useful when these rules are shared or non-trivial; a simple query field does not need an extra abstraction by default.

Preserve cancellation. Catch expected exceptions at the right boundary, rethrow or separately handle `CancellationException`, and never turn coroutine cancellation into a visible screen failure by catching `Throwable` broadly. Define whether concurrent independent requests can update separate sections and use atomic transformations so one result does not erase another update.

Every asynchronous path should leave the screen renderable: success, empty, expected failure, retry, and cancellation should have intentional outcomes. Keep the existing content during refresh or partial failure when product behavior requires it.

## Compose integration

Collect state lifecycle-aware, commonly with `collectAsStateWithLifecycle`. Collect effects from a lifecycle-aware effect handler such as `LaunchedEffect(viewModel)`, and dispatch UI callbacks as events. Keep navigation and platform callbacks at the presentation/platform boundary. Verify effect delivery behavior when the collector is stopped or recreated.

## ViewModel test targets

Test observable state and effects rather than private methods:

- Initial state and every important event.
- Loading to loaded, empty, and error transitions.
- Retry and refresh, including whether prior content is retained.
- Partial failures and section-specific pagination.
- Latest query wins, cancellation, and stale-response protection.
- Effects are emitted for the right action and are not encoded as persistent screen state.
