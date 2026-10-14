# Data Operations and Persistence

Use this reference when a feature reads from remote/local sources, saves user choices, maps imperfect source data, or changes a database schema. Apply only the concerns present in the requested change; a network-only read does not require a cache or an offline architecture.

## Execution context and cancellation

A `suspend` declaration does not move blocking work off the caller's thread. The adapter that performs blocking file, provider, or legacy network access should switch to an appropriate dispatcher; substantial CPU work needs a suitable computation context. Reuse the project's dispatcher injection mechanism so tests can control execution. Do not wrap every Retrofit or Room suspend call in another dispatcher without checking what the library already handles. See Android's [coroutine guidance](https://developer.android.com/kotlin/coroutines/coroutines-best-practices).

Apply cancellation rules throughout the call chain, including nullable data-source fallbacks and optional concurrent branches. `runCatching { suspendingCall() }.getOrNull()` catches cancellation too; rethrow `CancellationException` before converting an expected failure. Log or translate an error where recovery is decided instead of repeatedly logging it in every layer.

When loading details for many IDs or sections, inspect the request count as well as structured concurrency. Serial per-item requests can be slow; unbounded `async` fan-out can overload a service. Prefer batch APIs where available or bounded concurrency when the collection can grow, preserving the required order and failure policy.

## Transport and mapping contracts

- Keep queries and identifiers in their domain meaning until the transport boundary. Encode URL parameters exactly once. Retrofit's [`@Query`](https://github.com/square/retrofit/blob/trunk/retrofit/src/main/java/retrofit2/http/Query.java) encodes values by default; pre-encoding a search string in a use case can change its meaning. Use the same representation for the first and later pages, and test spaces, `+`, `&`, and non-ASCII text at the HTTP boundary.
- Separate absent data, successful empty results, optional-section failure, and failure of the whole operation when callers need different recovery behavior. Do not silently turn every network failure into an empty favorites list or treat missing optional metadata as an invalid entity.
- Parse transport formats predictably; format dates, numbers, and localized labels at the presentation boundary. Handle malformed dates and unknown enum/provider values deliberately so one bad record does not unexpectedly discard an entire page. Keep locale or region policy explicit where it changes results or formatting.
- Use complete identity across sources. For example, a movie and a TV show can have the same numeric ID: use `(type, id)` when combining them, and preserve that distinction in persistence, deduplication, navigation, and UI keys.

## Local and remote consistency

Decide which source owns each fact. A saved favorite ID and remotely fetched metadata are different facts: a failed detail lookup should not erase the user's saved choice. If the store contains only IDs, say whether unavailable metadata is omitted, shown as unavailable, or served from a cache; do not claim offline content support from ID persistence alone.

Define how other screens learn about successful writes. An observable repository/DAO stream can keep them synchronized; an explicit refresh on return can also fit a small existing app. Avoid independent mutable copies that disagree after a save, refresh, or navigation round trip. UI refreshes should merge into the latest state, preserving newer selection and filters.

## Writes and schema changes

- Express writes with domain IDs/values, mapping to Room entities in data. Prefer a desired state such as `setFavorite(itemId, true)` when retrying a toggle based on a stale UI snapshot would repeat or reverse the operation.
- Decide how overlapping writes are serialized or coalesced. A UI busy flag can prevent accidental repeated taps, but repository/database conflict handling must also match the operation's semantics. Use a transaction when multiple local changes must succeed together; do not hold it open during remote work.
- Publish confirmed state after success, or define rollback/reconciliation for an optimistic update. A failure should leave a recoverable state and release the pending-operation indicator.
- For a Room schema change, inspect the current version, exported schemas, supported upgrade paths, and migration tests. Preserve user-owned data such as favorites; destructive recreation is appropriate only when that data is intentionally disposable. A fresh-install test does not verify an upgrade.

Test the boundary that can fail: request serialization, mapping malformed records, duplicate writes, failed persistence, concurrent operations, or migration of existing rows. Mocking a use case or resolving a repository through DI does not exercise these behaviors.
