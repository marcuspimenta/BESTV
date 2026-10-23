# Background Work and Platform Adapters

Use this reference for scheduled refreshes, workers, broadcast receivers, TV recommendation providers, notifications, or other Android services outside a screen's lifecycle.

## Keep scheduling at the platform boundary

Separate the domain operation from how Android invokes it. A worker can call a use case; a domain use case should not import a worker class, `WorkManager`, `Context`, or platform result types. If business logic needs to request scheduling, use a narrow domain-owned scheduling contract implemented by an outer adapter. If scheduling is purely startup plumbing, keep it at that outer entry point without inventing a domain abstraction.

Keep receivers short: validate the action and delegate persistent work to the scheduler. Trace scheduling from actual startup/user/boot entry points, not just from the existence of a DI binding. Verify cold-process initialization and manifest declarations; a worker may run without any Activity being opened first.

Use application-scoped dependencies for background work. Prefer the project's supported worker factory/DI integration when constructor dependencies are needed, and verify its initialization. Adding a WorkManager DI library or binding a worker in a container alone does not wire Android's worker creation.

## Make scheduling and execution repeatable

- Use a stable unique work name for a logical task and choose the existing-work policy deliberately. Tags group work; they do not ensure uniqueness. Repeated startup/boot calls should not accidentally duplicate jobs or continually reset their schedule. See [managing unique work](https://developer.android.com/develop/background-work/background-tasks/persistent/how-to/manage-work).
- Specify relevant constraints, interval units, and retry/backoff behavior. Periodic work is inexact; check the installed WorkManager API's supported intervals and policies rather than copying constants from an example. Return retry for recoverable failures when retry is intended, and distinguish them from invalid input or permanent failures. See [work request configuration](https://developer.android.com/develop/background-work/background-tasks/persistent/getting-started/define-work).
- Preserve coroutine cancellation instead of converting a stopped worker into an ordinary failure or success. Inspect blocking provider calls even inside a `CoroutineWorker`; a suspending entry point does not make every operation non-blocking. See [CoroutineWorker execution](https://developer.android.com/develop/background-work/background-tasks/persistent/threading/coroutineworker).
- Assume a job can run again after partial completion. Use stable external identities and reconcile existing records before inserting duplicates. Persist a returned provider ID only after a successful write, update the URI for that exact record, and handle stale IDs if the platform record has disappeared.

For TV channels/programs, distinguish channel IDs from program IDs and from app/domain IDs. Scope updates and cleanup to records owned by the feature; broad notification cancellation or provider deletion can affect unrelated app behavior. Keep intent identity consistent with the intended item so different recommendations do not launch the same destination.

## Capabilities and ownership

Check product capability separately from OS API availability: a sufficiently new Android version does not imply a TV launcher/provider is available. Keep SDK/package-manager checks in platform adapters or the composition root; expose narrow capability values to consumers that need them. Do not turn a static device capability into a substitute for changing window-size or input-mode state in Compose.

Map domain values to the platform's payload at this adapter boundary. Prefer a stable route containing an item ID so publishing background content does not require a screen's Parcelable model. If an existing route contract requires a presentation payload, keep that conversion at the platform adapter edge, document it as route coupling, and avoid moving screen rendering into the worker. Migrate the route contract to an ID when that API can be changed safely.

Verify repeated scheduling, worker construction after a cold start, success/retry/permanent failure/cancellation, repeated execution after a partial write, stale provider IDs, and unsupported devices when those behaviors change. Scheduler mocks verify invocation only; use a focused worker/provider test for execution behavior.
