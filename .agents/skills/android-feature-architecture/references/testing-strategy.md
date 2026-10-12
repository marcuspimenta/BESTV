# Testing Strategy

Use this reference when deciding which tests give useful confidence for a feature or architecture change. First inspect the active project's test dependencies, dispatchers, naming/layout conventions, and existing coverage.

## Test the behavior at each changed seam

| Area | Useful scenarios |
|---|---|
| Domain/use cases | Delegation and composition, business decisions, empty input/results, success mapping, and intended failure propagation/conversion. Mock or fake domain-owned repository contracts. |
| Repository/data | Source selection, remote/local coordination, cache or persistence behavior, source-model mapping, and error handling. Use focused fakes or test doubles for data sources. Do not infer this coverage from use-case tests. |
| Mappers | Missing/optional fields, type conversion, formatting, resource-independent labels, URL construction, pagination metadata, and filtering rules that affect visible content. |
| ViewModel | Initial state; event handling; loading/success/empty/error; retry; refresh retention; partial failure; selection; independent pagination; and effect emission. Assert public state/effect behavior. |
| Async behavior | Latest request wins, cancellation, stale response prevention, duplicate-load guards, and independent concurrent updates. Use the project's test dispatcher and flow-testing tools. |
| Compose/UI | Important loading, empty, error, and loaded rendering; callbacks dispatch the expected events; lifecycle/effect behavior where user-visible. Use instrumentation only when platform behavior needs it. |
| DI wiring | A feature module/component resolves domain ports to intended implementations with infrastructure substituted. Treat this as a graph smoke test, not a data integration test. |
| Cross-layer integration | Add focused tests where a change crosses Retrofit/Room, serialization, navigation, or platform boundaries and the risk warrants it. |

## Keep tests meaningful

- Prefer tests that protect behavior or a real boundary over tests that mirror every private method or constructor line.
- Use a fake when the test needs a controllable stateful dependency; use a mock when verifying a narrow collaboration is the behavior under test.
- Assert cancellation and completion when testing flows or latest-wins requests, not only the final successful emission.
- Keep error assertions specific to the intended contract; do not make every exception indistinguishable if the UI or caller needs recovery information.
- Avoid claiming repository/data-source, DI, Compose, or integration coverage unless corresponding tests exist and exercise those paths.
- Select test frameworks from the active build. Common Kotlin Android tools include JUnit, coroutine-test, Flow assertions such as Turbine, Mockito or another mocking library, and Compose UI test APIs, but none should be assumed present until verified.

## Kotlin test implementation style

Follow the repository's source-set and package layout; a common arrangement mirrors production packages under `src/test` and uses a `*Test` class beside each behavior area. Prefer descriptive test names that state the outcome and condition. For suspending use cases and ViewModels, use `runTest` and the project's test dispatcher; advance virtual time or drain scheduled work deliberately rather than depending on wall-clock sleeps. Use a Flow assertion tool such as Turbine when the sequence of state/effect emissions is the behavior under test, and direct state assertions when only the final public state matters.

For DI wiring, load the production feature module in an isolated container and provide harmless infrastructure definitions or test doubles at the outer boundary. Assert that the domain port resolves to the intended concrete adapter, then close the container. This verifies registration and construction only; it does not exercise network, database, or repository behavior.

## Adapted feature checklist

For a typical read feature, consider tests for:

1. Use-case rules against a domain repository contract.
2. Repository mapping/coordination against data-source doubles when repository behavior changes.
3. Presentation mapping of domain results into UI models.
4. ViewModel state transitions, retry/refresh, and one-time effects.
5. DI construction if bindings or module composition change.
6. Compose interaction/rendering or platform integration only where the change affects those seams.

Not every feature needs every category. Use risk, changed behavior, and the existing suite to choose a focused set.
