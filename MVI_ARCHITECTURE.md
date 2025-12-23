# MVI Architecture

This document describes the Model-View-Intent (MVI) architecture implementation used in the BESTV project.

## Overview

MVI is a unidirectional data flow architecture pattern that provides predictable state management and clear separation of concerns. In this project, MVI is implemented using Kotlin Coroutines and Flow for reactive state management.

```
┌─────────────────────────────────────────────────────────────┐
│                         View (Compose)                       │
│  ┌─────────────┐                         ┌───────────────┐  │
│  │   State     │◄────────────────────────│    Effects    │  │
│  │  (StateFlow)│                         │    (Flow)     │  │
│  └─────────────┘                         └───────────────┘  │
│         ▲                                       ▲           │
└─────────┼───────────────────────────────────────┼───────────┘
          │                                       │
          │                                       │
┌─────────┴───────────────────────────────────────┴───────────┐
│                        ViewModel                             │
│  ┌─────────────┐    ┌─────────────┐    ┌───────────────┐    │
│  │ updateState │    │handleEvent()│    │  emitEffect   │    │
│  └─────────────┘    └─────────────┘    └───────────────┘    │
│         ▲                  │                                 │
│         │                  ▼                                 │
│         │           ┌─────────────┐                         │
│         └───────────│   Events    │                         │
│                     │  (sealed)   │                         │
│                     └─────────────┘                         │
└─────────────────────────────────────────────────────────────┘
```

## Core Components

### 1. BaseViewModel

The foundation of the MVI implementation is the `BaseViewModel` class located at:
`presentation_shared/src/main/java/com/pimenta/bestv/presentation/presenter/BaseViewModel.kt`

```kotlin
abstract class BaseViewModel<ViewState, ViewEffect>(initialState: ViewState) : ViewModel() {

    // State management using StateFlow
    val state: StateFlow<ViewState>

    // One-time side effects using Channel
    val effects: Flow<ViewEffect>

    // Protected method to update state immutably
    protected fun updateState(function: (ViewState) -> ViewState)

    // Protected method to emit side effects
    protected fun emitEffect(effect: ViewEffect)

    // Access current state value
    protected val currentState: ViewState
}
```

### 2. State

State represents the complete UI state at any given moment. It is:
- **Immutable**: Updated only through `copy()` operations
- **Single source of truth**: One state object describes the entire screen
- **Observable**: Exposed as `StateFlow` for reactive UI updates

**Example:** `SearchState.kt`
```kotlin
data class SearchState(
    val query: String = "",
    val isSearching: Boolean = false,
    val state: State = Empty
) {
    sealed interface State {
        data object Empty : State
        data object Error : State
        data class Loaded(
            val selectedWork: WorkViewModel? = null,
            val contents: List<Content>
        ) : State
    }
}
```

**Naming convention:** `*State.kt`

### 3. Event (Intent)

Events represent user intentions or actions. They are:
- **Sealed interfaces**: Exhaustive handling in `when` expressions
- **Descriptive**: Named after user actions
- **Data carriers**: Can contain parameters needed to process the action

**Example:** `SearchEvent.kt`
```kotlin
sealed interface SearchEvent {
    data class SearchQueryChanged(val query: String) : SearchEvent
    data class SearchQuerySubmitted(val query: String) : SearchEvent
    data object ClearSearch : SearchEvent
    data object LoadMoreMovies : SearchEvent
    data object LoadMoreTvShows : SearchEvent
    data class WorkItemSelected(val work: WorkViewModel?) : SearchEvent
    data class WorkClicked(val work: WorkViewModel) : SearchEvent
}
```

**Naming convention:** `*Event.kt`

### 4. Effect (Side Effect)

Effects represent one-time actions that don't persist in state. They are:
- **Fire-and-forget**: Consumed once and not stored
- **Navigation**: Opening new screens, showing dialogs
- **External actions**: Launching intents, showing toasts

**Example:** `SearchEffect.kt`
```kotlin
sealed interface SearchEffect {
    data class OpenWorkDetails(val intent: Intent) : SearchEffect
}
```

**Naming convention:** `*Effect.kt`

## Implementation Guide

### Creating a New Feature

#### Step 1: Define the State

Create a state file that represents all possible UI states:

```kotlin
// feature_example/presentation/model/ExampleState.kt
data class ExampleState(
    val isLoading: Boolean = false,
    val state: State = State.Initial
) {
    sealed interface State {
        data object Initial : State
        data object Error : State
        data class Loaded(val data: List<Item>) : State
    }
}
```

#### Step 2: Define the Events

Create an event file with all possible user actions:

```kotlin
// feature_example/presentation/model/ExampleEvent.kt
sealed interface ExampleEvent {
    data object LoadData : ExampleEvent
    data object Retry : ExampleEvent
    data class ItemClicked(val item: Item) : ExampleEvent
}
```

#### Step 3: Define the Effects

Create an effect file for one-time side effects:

```kotlin
// feature_example/presentation/model/ExampleEffect.kt
sealed interface ExampleEffect {
    data class NavigateToDetails(val intent: Intent) : ExampleEffect
    data class ShowError(val message: String) : ExampleEffect
}
```

#### Step 4: Implement the ViewModel

Create the ViewModel extending `BaseViewModel`:

```kotlin
// feature_example/presentation/viewmodel/ExampleViewModel.kt
class ExampleViewModel(
    private val getDataUseCase: GetDataUseCase,
    private val detailsRoute: DetailsRoute
) : BaseViewModel<ExampleState, ExampleEffect>(ExampleState()) {

    fun handleEvent(event: ExampleEvent) {
        when (event) {
            is ExampleEvent.LoadData -> loadData()
            is ExampleEvent.Retry -> loadData()
            is ExampleEvent.ItemClicked -> handleItemClicked(event.item)
        }
    }

    private fun loadData() {
        updateState { it.copy(isLoading = true) }

        viewModelScope.launch {
            try {
                val data = getDataUseCase()
                updateState {
                    it.copy(
                        isLoading = false,
                        state = ExampleState.State.Loaded(data)
                    )
                }
            } catch (e: Exception) {
                updateState {
                    it.copy(
                        isLoading = false,
                        state = ExampleState.State.Error
                    )
                }
            }
        }
    }

    private fun handleItemClicked(item: Item) {
        val intent = detailsRoute.buildIntent(item)
        emitEffect(ExampleEffect.NavigateToDetails(intent))
    }
}
```

#### Step 5: Create the Composable Screen

Connect the ViewModel to the UI:

```kotlin
// feature_example/presentation/ui/compose/ExampleScreen.kt
@Composable
fun ExampleScreen(
    viewModel: ExampleViewModel,
    openIntent: (Intent) -> Unit,
    modifier: Modifier = Modifier
) {
    // Collect state with lifecycle awareness
    val state by viewModel.state.collectAsStateWithLifecycle()

    // Handle one-time effects
    LaunchedEffect(Unit) {
        viewModel.effects.collectLatest { effect ->
            when (effect) {
                is ExampleEffect.NavigateToDetails -> openIntent(effect.intent)
                is ExampleEffect.ShowError -> { /* Show toast/snackbar */ }
            }
        }
    }

    // Render UI based on state
    Box(modifier = modifier.fillMaxSize()) {
        when (val currentState = state.state) {
            is ExampleState.State.Initial -> {
                // Initial UI
            }
            is ExampleState.State.Error -> {
                ErrorScreen(
                    onRetryClick = { viewModel.handleEvent(ExampleEvent.Retry) }
                )
            }
            is ExampleState.State.Loaded -> {
                ItemList(
                    items = currentState.data,
                    onItemClick = { viewModel.handleEvent(ExampleEvent.ItemClicked(it)) }
                )
            }
        }

        if (state.isLoading) {
            Loading(modifier = Modifier.align(Alignment.Center))
        }
    }
}
```

## Best Practices

### State Management

1. **Keep state immutable** - Always use `copy()` to create new state instances
2. **Single source of truth** - All UI data should come from the state
3. **Avoid derived state in ViewModel** - Compute derived values in the state class using properties

```kotlin
data class ExampleState(val items: List<Item>) {
    // Derived property computed from state
    val isEmpty: Boolean get() = items.isEmpty()
    val itemCount: Int get() = items.size
}
```

### Event Handling

1. **Single entry point** - All events flow through `handleEvent()`
2. **Private handlers** - Keep individual event handlers private
3. **Descriptive naming** - Name events after user actions, not implementation details

```kotlin
// Good
sealed interface SearchEvent {
    data class SearchQueryChanged(val query: String) : SearchEvent
}

// Avoid
sealed interface SearchEvent {
    data class UpdateStateQuery(val query: String) : SearchEvent
}
```

### Async Operations

1. **Use viewModelScope** - Launch coroutines in `viewModelScope` for automatic cancellation
2. **Handle cancellation** - Filter out `CancellationException` in error handling
3. **Job management** - Cancel previous jobs when starting new operations (debouncing)

```kotlin
private var searchJob: Job? = null

private fun search(query: String) {
    searchJob?.cancel()  // Cancel previous search

    searchJob = viewModelScope.launch {
        try {
            val results = searchUseCase(query)
            updateState { it.copy(results = results) }
        } catch (e: Exception) {
            if (e !is CancellationException) {
                updateState { it.copy(state = Error) }
            }
        }
    }
}
```

### Effects

1. **Use for one-time actions** - Navigation, toasts, opening external apps
2. **Don't store in state** - Effects should not be part of the state
3. **Handle in LaunchedEffect** - Use `collectLatest` to consume effects

## File Structure

```
feature_example/
├── data/
│   ├── datasource/
│   └── repository/
├── domain/
│   └── usecase/
├── presentation/
│   ├── model/
│   │   ├── ExampleState.kt
│   │   ├── ExampleEvent.kt
│   │   └── ExampleEffect.kt
│   ├── viewmodel/
│   │   └── ExampleViewModel.kt
│   └── ui/
│       └── compose/
│           └── ExampleScreen.kt
└── di/
    └── ExampleModule.kt
```

## Existing Features

The following features in the project implement the MVI pattern:

| Feature | State | Event | Effect | ViewModel |
|---------|-------|-------|--------|-----------|
| Work Browse | `WorkBrowseState` | `WorkBrowseEvent` | `WorkBrowseEffect` | `WorkBrowseViewModel` |
| Search | `SearchState` | `SearchEvent` | `SearchEffect` | `SearchViewModel` |
| Work Details | `WorkDetailsState` | `WorkDetailsEvent` | `WorkDetailsEffect` | `WorkDetailsViewModel` |
| Cast Details | `CastDetailsState` | `CastDetailsEvent` | `CastDetailsEffect` | `CastDetailsViewModel` |

## Shared Components

### PaginationState

A reusable state component for handling pagination:

```kotlin
// presentation_shared/src/main/java/com/pimenta/bestv/presentation/model/PaginationState.kt
data class PaginationState(
    val currentPage: Int = 0,
    val totalPages: Int = 0,
    val isLoadingMore: Boolean = false
) {
    val canLoadMore: Boolean
        get() = currentPage in 1..<totalPages && !isLoadingMore
}
```

## References

- [MVI Architecture with Kotlin Flows and Channels](https://proandroiddev.com/mvi-architecture-with-kotlin-flows-and-channels-d36820b2028d)
- [Kotlin StateFlow and SharedFlow](https://kotlinlang.org/docs/flow.html#stateflow-and-sharedflow)
- [Guide to app architecture](https://developer.android.com/topic/architecture)
