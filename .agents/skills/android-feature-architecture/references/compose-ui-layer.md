# Compose UI Layer

Use this reference when creating or changing Jetpack Compose screens, reusable UI components, responsive layouts, accessibility behavior, previews, or Compose tests.

## Responsibilities and composition structure

Keep UI rendering in composables and screen orchestration in the ViewModel. A composable describes UI from its inputs; it should not reach into repositories, start network/database work, or decide business rules.

A useful feature structure separates the platform entry point, screen-level state/effect wiring, visual screen content, and reusable components. Names vary between projects (`Route`, `Screen`, `Content`, `Section`, `Card`), so follow local terminology while keeping responsibilities clear:

```text
Activity / navigation destination
└── stateful route or screen adapter
    ├── collect ViewModel state and one-time effects
    ├── translate UI callbacks into ViewModel events
    └── stateless screen content
        ├── state-specific layouts
        ├── feature components
        └── shared design-system components
```

The stateful boundary typically receives a ViewModel and platform callbacks. It collects state lifecycle-aware, handles effects, and passes immutable state plus callbacks to stateless content. Leaf and reusable composables should usually receive data and lambdas rather than a ViewModel or DI container. This keeps them previewable, testable, and usable from more than one screen.

```kotlin
@Composable
fun ItemsRoute(
    viewModel: ItemsViewModel,
    onOpenDetails: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val lifecycleOwner = LocalLifecycleOwner.current
    val currentOnOpenDetails by rememberUpdatedState(onOpenDetails)

    LaunchedEffect(viewModel, lifecycleOwner) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.effects.collect { effect ->
                when (effect) {
                    is ItemsEffect.OpenDetails -> currentOnOpenDetails(effect.itemId)
                }
            }
        }
    }

    ItemsScreen(
        state = state,
        onRetry = { viewModel.handleEvent(ItemsEvent.Retry) },
        onItemClick = { viewModel.handleEvent(ItemsEvent.ItemClicked(it)) },
        modifier = modifier,
    )
}
```

The exact effect mechanism and delivery guarantee depend on the ViewModel contract. Keep one-time navigation/snackbar/platform work outside the composable body so recomposition cannot repeat it. Do not place an effect in a reusable child when it can be handled at the screen boundary.

`LaunchedEffect` is composition-scoped; it does not itself stop collection when the Activity is backgrounded. The example uses `repeatOnLifecycle(STARTED)`; choose the active state appropriate to the action and use APIs supported by the installed Lifecycle version. Use `collect` for actions that must finish in order; use `collectLatest` only when a new effect should cancel the previous handler. Keep changing callbacks current with effect keys or `rememberUpdatedState`. See [lifecycle-aware coroutines](https://developer.android.com/topic/libraries/architecture/coroutines) and [repeatOnLifecycle](https://developer.android.com/reference/androidx/lifecycle/RepeatOnLifecycleKt).

## Stateless screen content and component APIs

Prefer explicit state and callbacks:

```kotlin
@Composable
fun ItemsScreen(
    state: ItemsUiState,
    onRetry: () -> Unit,
    onItemClick: (ItemUiModel) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(modifier = modifier) { padding ->
        Box(Modifier.padding(padding)) {
            when (val result = state.result) {
                ItemsResult.Loading -> LoadingContent()
                ItemsResult.Empty -> EmptyContent()
                is ItemsResult.Error -> ErrorContent(onRetry = onRetry)
                is ItemsResult.Loaded -> ItemList(
                    items = result.items,
                    onItemClick = onItemClick,
                )
            }
        }
    }
}
```

Component API guidance:

- Accept the values a component renders and lambdas for user actions. Keep navigation, DI, and use-case calls out of cards, rows, buttons, and other leaf components.
- Provide a `Modifier` parameter with a neutral default on reusable composables and apply it to the component's root layout. Preserve the modifier chain supplied by the caller.
- Name callbacks by intent (`onItemClick`, `onQueryChange`, `onRetry`) and give optional behavior a clear default. Avoid callback parameters that expose internal implementation steps.
- Keep components cohesive and small enough to reason about; extract repeated visual patterns or independently meaningful sections, not every row or modifier into a new abstraction.
- Share components when their behavior and visual contract are actually common. Avoid a generic component with many flags that combines unrelated platform or feature behavior.
- Use immutable UI models and collections. Pass already-mapped values from presentation when formatting or resource lookup is feature-specific; simple visual transformations can remain local to the composable.

## Render all meaningful states

Render from the screen state rather than inferring business state from local Compose variables. Handle initial loading, loaded content, successful empty results, whole-screen errors, and retry. For complex screens, keep loaded content visible while a refresh, per-section load, pagination request, or partial error is represented separately.

Keep local UI-only state in Compose when it does not belong to the screen's business contract, such as whether a tooltip is expanded, a local animation progress, or a scroll position. Use `remember` for composition-scoped objects and `rememberSaveable` when a small UI value should survive Activity recreation. Put state in a ViewModel when it is screen-level state shared across composables, produced by business work, or must survive configuration changes according to product behavior. Avoid maintaining two mutable sources of truth for the same value.

Do not perform side effects directly while composing. Use the correct effect API for lifecycle-bound work, keyed to the values it depends on (`LaunchedEffect(key)`, `DisposableEffect`, or another scoped mechanism). Use `rememberCoroutineScope` for user-triggered UI work such as showing a snackbar or running a scroll animation; business operations remain in the ViewModel/domain flow.

## Lists, images, and rendering cost

- Use lazy containers for large or unbounded collections. Supply stable keys for items so remembered state and animations remain associated with the same item when data changes.
- Keys must remain stable when content changes and unique within their list. Avoid a data object's `hashCode()` or list index; combine source/type with ID for mixed collections and use semantic category/genre IDs for rows. When filtering or reordering sections, associate focus/scroll state with these identities rather than only the list size.
- Keep paging decisions idempotent. A list can report proximity to its end, but the ViewModel/state holder should guard duplicate requests, stale page responses, and errors.
- Use the project’s image-loading component for remote images. Define placeholder/error behavior, crop/aspect ratio, and useful image semantics; do not block composition while loading.
- Keep expensive transformations out of frequently recomposed bodies. Hoist stable objects, use `remember` with correct keys where it avoids real repeated work, and use derived state for high-frequency reads only when it meaningfully reduces work.
- Avoid premature optimization and unnecessary `remember`/`derivedStateOf`. First make state ownership and stable list identity correct, then profile when performance is a concern.

Check captured inputs in remembered calculations and long-lived effects. A keyless `remember { derivedStateOf { ... } }` that captures a plain `isLoadingMore` parameter keeps its original value. Key the calculation on changing inputs or read current state, and make pagination react to page/item-count changes as well as proximity to the end. Test repeated appends, failures/retry, and replacement of the list while it remains near the end. See [Compose effect keys and updated values](https://developer.android.com/develop/ui/compose/side-effects).

## Theme, resources, and design consistency

Use the active design system or Material theme for semantic colors, typography, shapes, and component defaults. Prefer shared tokens over raw colors, dimensions, and typography copied across screens. Keep a consistent theme boundary around previews and application content.

Put user-visible text in localized resources and resolve it through Compose resource APIs at the UI boundary. Use plural resources and formatting APIs for quantities and formatted messages. Keep resource identifiers out of domain models and repository contracts. For icons, provide a meaningful content description when the icon conveys information or action; use a null description for purely decorative imagery.

Respect window insets, system bars, keyboard/IME, font scaling, and layout changes. Test text wrapping and long localized strings rather than assuming one language or screen size.

## Adaptive layouts and input modes

Choose layouts from window size, posture, input method, and product requirements rather than treating a device label as the only responsive signal. A feature can share one state holder and domain/data flow while rendering different layouts for compact touch screens, large touch screens, or television. Share business behavior; let layout and interaction adapt to the form factor.

When form factors have genuinely different interaction models (for example, remote-driven focus versus touch input and an IME), a small presentation wrapper can choose separate TV/mobile screen implementations while reusing the same ViewModel, state model, and event contract. Collect state and effects once at that wrapper/route, then pass the current state and callbacks to the selected screen. Avoid collecting the same state or effect stream again in both the wrapper and its child. When the content hierarchy and interaction are mostly shared, prefer one screen with adaptive layout decisions over parallel copies.

For touch layouts, provide comfortable hit areas, clear hierarchy, scroll behavior, and sensible keyboard/IME actions. For keyboard or remote-driven layouts, implement and visibly communicate focus, deterministic initial focus, directional focus movement, selected states, and back behavior. Do not rely on hover or touch as the only way to discover or activate controls. If focus must move after a state change, request it from a lifecycle-aware effect rather than during composition.

Focus callbacks can update which item is selected or which details are emphasized, but avoid starting expensive work for every focus movement. If focused-item changes trigger requests, deduplicate or debounce them and use latest-wins cancellation so rapid directional navigation does not leave stale content on screen.

For an `AndroidView` or other embedded platform component, handle changing inputs/callbacks and release owned listeners or media resources when it leaves use. Tie playback to the intended lifecycle and provide an error/skip outcome for media used in loading transitions; a completion callback is not guaranteed to arrive.

## Accessibility and semantics

- Prefer standard Material/Compose controls because they provide interaction semantics and accessibility behavior.
- Give icon-only actions a localized label. Use meaningful descriptions for informative images and omit redundant descriptions when adjacent text already communicates the same information.
- Preserve semantic roles, click actions, selected/disabled state, and logical traversal order. Group or merge semantics only when it makes the control clearer to assistive technology.
- Do not communicate status only through color, animation, focus, or position. Provide text/semantic state where needed and maintain readable contrast and scalable text.
- Support keyboard/remote and accessibility-service navigation where the product targets those input modes.

## Previews

Add `@Preview` coverage for reusable components and important screen states when the project's Compose tooling supports it. A useful preview is self-contained: use representative immutable sample data, the product theme, and no ViewModel, DI, network, or database dependency. Add variants for important content extremes such as empty, long text, loading/error, or selected/focused appearance.

Previews are a rapid layout aid, not a substitute for runtime testing. Keep sample data safe, deterministic, and small. For parameterized previews, use the project’s supported preview-provider pattern when it materially improves review.

## Compose UI testing

Use the project's Compose testing dependencies and conventions. Set content with deterministic state and fake callbacks/dependencies, then assert through semantics rather than private implementation details or fragile pixel coordinates.

Useful cases include:

- Correct rendering for initial loading, empty, error/retry, loaded, and partial/refresh states.
- User actions call the expected event/callback and do not trigger duplicate actions.
- Text, labels, selected/disabled state, image descriptions, and accessibility semantics are present.
- Long text, empty lists, larger text, and important layout variants remain usable.
- Focus starts in the intended place and moves correctly for keyboard/remote interfaces; back behavior works where customized.
- One-time effects invoke the provided platform callback and are not repeated just because the composable recomposed.

Use instrumentation when the behavior depends on Android lifecycle, manifest/navigation dispatch, platform focus, or other runtime integration. A screenshot/golden test can help protect visual output when the repository already has a stable system for it, but it does not replace interaction and semantics tests.
