# System Design — Senior Java Question Bank

[Back to index](index.md)

**Audience:** Senior Java developer / tech lead.  
**Version frame:** Use Java 8 experience as background and Java 17/21 for modern implementation choices. Mention Java 25 only when a verified runtime or framework requirement changes the decision. A JDK upgrade does not replace capacity planning, correctness rules, or recovery design.

**Scope:** End-to-end architecture, estimation, component selection, security, operations, object/module design, tech-lead judgment, and product case studies. This bank applies mechanisms; it does not repeat the detailed Spring, SQL/JPA, or distributed-protocol explanations in the companion banks.

**Research basis:** Public system-design guides repeatedly use requirements, rough estimates, APIs, data models, high-level architecture, bottlenecks, and explicit trade-offs, followed by cases such as short links, feeds, chat, rate limiting, bookings, and payments. The original answers below use AWS architecture and Google SRE principles. Recurrence in guides is not a statistical claim about interview frequency.

## Framing and estimation

### Q1. How should you start an ambiguous system-design interview?
**Answer:** Clarify the users, three or four main actions, business invariants, expected scale, and important quality targets. Ask what is out of scope. Restate the agreed problem before drawing components. A useful starting statement is “we must never sell the same seat twice, while search may be slightly stale.” This gives the design a decision rule instead of an unbounded list of technologies.

### Q2. How do functional and nonfunctional requirements differ?
**Answer:** Functional requirements describe actions, such as creating an order or retrieving a message. Nonfunctional requirements describe qualities and constraints, such as latency, availability, privacy, cost, and maintainability. Make them measurable and operation-specific. “Fast and scalable” is not enough; “95% of eligible reads complete within 200 ms at the agreed peak load” provides a testable target and a basis for trade-offs.

### Q3. What is the difference between an SLI, an SLO, and an SLA?
**Answer:** An SLI measures service behavior, such as the fraction of valid checkout requests completed successfully. An SLO is the target for that measure over a defined window. An SLA is an agreement with consequences for missing a promised level. Choose user-visible measures, not merely server uptime. A healthy API process is not sufficient if users cannot finish the business action.

### Q4. How would you estimate traffic from user activity?
**Answer:** Start with active users, actions per user, and the time period. Ten million daily users making twenty reads each produce 200 million reads per day, about 2,315 reads per second on average. Apply an explicit peak assumption, such as five times average, giving about 11,575 per second. Separate read and write traffic and validate time-of-day concentration; the peak factor is an assumption, not a law.

### Q5. How would you estimate storage and bandwidth?
**Answer:** Multiply item count by realistic serialized size and retention, then add indexes, replication, logs, backups, and headroom separately. At one million items per day and 2 KB per item, raw new payload is roughly 2 GB per day using decimal units. Bandwidth is payload size times transfer rate, including response fan-out. Do not mix compressed network size, in-memory object size, and persisted row size.

### Q6. How does Little's Law help with capacity planning?
**Answer:** For a stable system, average work in progress is average throughput multiplied by average time in the system: `L = λW`. At 1,000 requests per second and 0.2 seconds average duration, about 200 requests are in progress on average. This is not a safe thread-pool size by itself. Bursts, tail latency, dependency waits, and the actual constrained resources need separate budgets.

### Q7. Why are latency percentiles more useful than averages alone?
**Answer:** Averages can hide a slow minority. p99 means 99% of observations are at or below that value for the measured population and window. Specify operation, payload, load, and measurement location. Do not average p99 values from servers to obtain a global p99; combine appropriate distributions. A good design budgets latency across the critical path and includes client-visible errors and timeouts.

### Q8. What does 99.9% availability allow?
**Answer:** For a time-based target over a 30-day month, 0.1% unavailability is about 43.2 minutes. A request-based target instead allows a proportion of failed eligible requests and may tell a different story during partial outages. Define the denominator, window, and excluded cases. An error budget is a tool for balancing reliability and change, not automatic permission for one large outage.

### Q9. How do dependent components affect availability?
**Answer:** A required dependency can make an otherwise healthy service fail. Multiplying component availabilities is only a rough model when failure assumptions, including independence, are justified. Shared networks, credentials, deployment errors, and regions create correlated failures. Reduce required critical-path dependencies and define safe degraded modes. Adding replicas inside the same failure domain does not address every source of unavailability.

### Q10. How should consistency requirements be stated?
**Answer:** State them per action and invariant. Search results may tolerate a minute of indexing delay; an account debit may require an authoritative conditional update. Specify read-your-writes expectations, tolerated stale data, and behavior when the authority is unreachable. Avoid saying the whole product is either “strong” or “eventual.” The detailed implementation belongs to the data or protocol design, but the requirement begins with the business promise.

### Q11. What belongs in an initial high-level diagram?
**Answer:** Show clients, entry points, major processing components, authoritative stores, derived stores, queues, and external dependencies. Label important synchronous versus asynchronous flows and ownership boundaries. Add a short narrative for one write and one read. Do not draw twenty services before explaining the core use case. The first diagram should make it easy to ask where state lives and what happens when one component fails.

### Q12. How should APIs and data models be designed together?
**Answer:** Define important operations, identities, validation, authorization, error outcomes, and pagination before choosing every storage detail. Then model authoritative facts and query access patterns. Keep transport DTOs separate from database internals. Include a way to observe long-running work and retry safely where relevant. An API that promises immediate completion can force a very different architecture from one that truthfully returns an accepted operation.

### Q13. How do you identify the first bottleneck?
**Answer:** Follow the most demanding operation and estimate its resource cost: CPU, memory, I/O, network, lock contention, connections, or downstream quota. Compare it with measured component capacity and workload skew. “Add a cache” is not a diagnosis. If the limiting resource is a serialized business decision or a provider quota, adding application servers alone will not improve throughput.

### Q14. When should you scale vertically or horizontally?
**Answer:** Vertical scaling can be simpler and useful while one machine has headroom, but has practical limits and larger unit failures. Horizontal scaling adds instances and can improve capacity or resilience when work partitions well. It requires externalized or coordinated state and careful downstream budgets. A hybrid approach is normal. Choose from measured bottlenecks and recovery needs, not a rule that horizontal is always superior.

### Q15. How should uncertainty be handled during a design?
**Answer:** Make assumptions explicit, give a plausible range, and explain which decision changes if the estimate is wrong. Identify the riskiest unknown and propose a small test, such as a realistic database benchmark or provider-limit check. Avoid false precision and avoid hiding behind “it depends.” A useful answer names what it depends on and gives a default for the stated constraints.

## Components and quality trade-offs

### Q16. What does a load balancer solve, and what does it not solve?
**Answer:** It distributes eligible traffic across backends and can remove unhealthy instances. It does not create database capacity, fix shared-state correctness, or guarantee every instance is equally loaded. Long-lived connections and uneven request costs can cause imbalance. Define health criteria, timeouts, draining, and trusted proxy headers. If sessions require affinity, explain the recovery and scaling consequences rather than assuming routing alone provides session durability.

### Q17. When should a CDN be used?
**Answer:** A content delivery network can serve cacheable content near users, reducing origin traffic and latency. It is valuable for static assets and suitable public responses. Cache keys, expiry, authorization, invalidation, and geographic requirements determine safety. Personalized or private content needs deliberate controls. A CDN cannot make a highly dynamic authoritative write local merely by sitting in front of the API.

### Q18. How should a cache be introduced into a design?
**Answer:** Name the data, authority, expected hit rate, key structure, expiry, invalidation, and stale-data policy. Estimate memory and include cold-start behavior. Cache-aside is a common option, but consistency races still need thought. Plan what happens if the cache is down or empty: unlimited fallback may overwhelm the database. A cache is an optimization with a failure model, not an unexplained box between every component.

### Q19. How do you prevent a cache stampede?
**Answer:** Combine suitable expiry spreading, request coalescing for the same key, bounded regeneration concurrency, and stale-while-revalidate when business rules allow it. Prewarm a small critical set if useful. Protect the authoritative store during a full cache loss. Distributed coordination can add complexity, so use it only where necessary. Negative caching can reduce repeated misses, but needs short, safe expiry and careful authorization-aware keys.

### Q20. How should storage technologies be selected?
**Answer:** Begin with access patterns, invariants, item size, growth, retention, and recovery needs. Relational storage fits many transactional workflows; object storage fits large blobs; search indexes fit ranking and text retrieval; specialized stores can fit time-series or key access. Every additional store adds ownership and synchronization costs. Choose a minimal set and say which data is authoritative and which representation can be rebuilt.

### Q21. How should a shard key be evaluated?
**Answer:** Evaluate distribution, hot keys, common queries, locality, growth, and movement cost. A tenant key simplifies tenant-local operations but can concentrate one large tenant. Time ranges simplify lifecycle management but can concentrate current writes. Cross-shard operations add coordination and query cost. Include resharding and directory evolution in the plan; a shard key is difficult to change after it leaks into every API and assumption.

### Q22. When does asynchronous processing improve a design?
**Answer:** It helps when work can finish later, bursts need buffering, or slow providers should not occupy an interactive request. Persist acceptance before acknowledging it, expose completion state, and define delay objectives. Include retry, duplicate, expiry, and failure handling. A queue shifts work and time; it does not create unlimited capacity. Track oldest job age and arrival versus completion rates.

### Q23. What does an active-active multi-region design cost?
**Answer:** It can improve locality and survive some regional failures, but adds routing, data replication, conflict handling, operations, and cost. Decide whether all regions can write the same business entity and how invariants survive partitions. Active-passive may meet recovery goals with less complexity. “Multi-region” is not a complete availability answer unless failover capacity, dependencies, data loss, and drills are addressed.

### Q24. How should disaster recovery be designed?
**Answer:** Set RPO and RTO by business operation, identify required data and configuration, and choose backup and replication mechanisms accordingly. Include keys, permissions, infrastructure, DNS, third-party access, and operational procedures. Test restoration and traffic switching, not just backup creation. Recovery capacity must exist or be provisionable in time. Keep a path back to a stable topology after the emergency.

### Q25. How should a system be designed for cost?
**Answer:** Estimate major cost drivers such as storage growth, replicated data, network egress, always-on capacity, and operational effort. Relate cost to a business unit, such as cost per completed order. Use retention, compression, batching, caching, and right-sizing where they preserve requirements. A cheaper resource that requires fragile custom operations may be more expensive overall. Optimize measured waste rather than weakening a critical guarantee accidentally.

### Q26. What security questions belong in the first design pass?
**Answer:** Identify trust boundaries, callers, sensitive data, tenant isolation, authorization decisions, and abuse cases. Plan secret management, encryption, key ownership, audit, and deletion. Think about object-level access, not only login. Use least privilege for services and operators. Security controls should be visible in the flows; adding a box labeled “authentication” at the gateway does not secure every stored object and internal action.

### Q27. How do privacy requirements change architecture?
**Answer:** They influence data minimization, retention, access, regional storage, exports, and deletion across caches, logs, indexes, replicas, and backups. Track where personal data flows and who owns removal. Avoid copying sensitive fields into every event when an identifier would suffice. Define what can be deleted immediately and what follows a documented retention process. Privacy must shape the data model, not just a user-interface setting.

### Q28. What should observability show in a product design?
**Answer:** Show whether users can complete the main action, how long it takes, and which constraints are saturated. Include business success counts, error categories, latency distributions, queue age, dependency health, and correlation across flows. Build alerts around user impact and actionable conditions. A dashboard containing CPU and heap alone cannot show whether a payment workflow is stuck or a notification is two days late.

### Q29. How should a new design be tested before launch?
**Answer:** Test business invariants, API contracts, production-like data sizes, load shape, and expected failures. Include a cold cache, a slow provider, capacity limits, failover, backup restore, and mixed application versions. Measure latency and errors at the user boundary. A short benchmark at steady average traffic does not establish burst handling, long-running stability, or recovery behavior. Record assumptions and acceptance thresholds.

### Q30. How should rollout and rollback work?
**Answer:** Use staged exposure, measurable success criteria, and a rollback or forward-recovery plan. Keep data and API changes compatible with overlapping versions. Feature flags can reduce exposure but introduce their own lifecycle and testing needs. Reverting a binary cannot always reverse data already transformed or external effects already executed. Define stop conditions and who can act before a high-risk release begins.

## Object design and tech-lead judgment

### Q31. How do high-level and low-level design differ?
**Answer:** High-level design explains components, data ownership, flows, and deployment or failure boundaries. Low-level design explains module interfaces, domain objects, state transitions, and implementation collaboration. They should support each other. A clean class diagram does not prove a system can recover from a region loss, and a distributed diagram does not prove that the order state machine prevents an illegal transition.

### Q32. How should SOLID principles be used in a real Java design?
**Answer:** Use them to improve change boundaries, substitutability, and dependency direction, not to maximize interface count. A service should have a cohesive responsibility; a replacement implementation should honor the promised contract. Depend on narrow ports where infrastructure variation matters. An interface for every class can add ceremony without isolation. Evaluate a design by how a realistic change propagates and how important behavior is tested.

### Q33. When should composition be preferred over inheritance?
**Answer:** Prefer composition when behavior varies independently or a dependency can be replaced without claiming an “is-a” relationship. For example, an order application service can compose a pricing policy and payment port. Inheritance is useful for a true stable subtype contract, but shared code alone is a weak reason. Deep hierarchies often spread hidden assumptions and make small business changes affect unrelated subclasses.

### Q34. How would you choose between Strategy, Factory, and Adapter patterns?
**Answer:** Strategy selects among interchangeable behaviors, such as pricing rules. A Factory encapsulates construction when creation has meaningful policy or complexity. An Adapter translates an incompatible external interface into a local contract. Use a pattern when it removes a specific source of coupling. Several patterns can coexist, but naming them is not the answer; explain what change becomes safer or simpler.

### Q35. When is a state machine better than scattered status checks?
**Answer:** Use it when an entity has meaningful transitions, guards, terminal states, and side effects. An order may move from pending to confirmed or cancelled, but not from refunded to shipped. Centralize legal transitions and persist enough state for recovery. A state machine does not itself solve concurrent updates; enforce version or authoritative transition conditions when multiple callers can act.

### Q36. How does domain-driven design help without requiring a large framework?
**Answer:** It gives the team shared language and boundaries around business meaning. Identify aggregates that protect invariants, value objects that express valid concepts, and bounded contexts where terms differ. Use these ideas only where domain complexity justifies them. A small CRUD service may need much less structure. The goal is understandable business behavior, not a directory tree filled with pattern names.

### Q37. What should an architecture decision record capture?
**Answer:** Capture the problem, constraints, options considered, chosen trade-off, consequences, and conditions for revisiting it. Include a concrete reason for rejecting a simpler alternative. Keep it short enough to maintain and link evidence such as a load test. An ADR records reasoning rather than declaring one technology permanently superior. Future teams should understand both the decision and when its assumptions no longer apply.

### Q38. How should a tech lead balance delivery speed and architectural quality?
**Answer:** Protect correctness, security, and recoverability while allowing reversible simplifications. Distinguish a deliberate limitation with a trigger for change from an unowned shortcut. Favor a small vertical slice that proves a risky assumption. Make ownership, rollout, and follow-up visible. A highly general platform delivered late can be worse than a focused design that meets current needs and has a credible growth path.

## Product and platform case studies

### Q39. Design a URL-shortening service.
**Answer:** Provide create and redirect operations, persist a unique code-to-target mapping, and define expiry, custom aliases, and ownership. Generate codes using an allocated identifier encoded compactly or random values with collision checks. Scale the read-heavy redirect path through caching and edge delivery where safe. Choose redirect status according to mutability and caching needs. Keep analytics off the critical path, and include malicious-link controls and deletion behavior.

### Q40. Design a distributed API rate limiter.
**Answer:** Clarify whether the limit is per user, tenant, token, endpoint, or IP, and whether bursts are allowed. A token bucket fits a sustained rate with controlled bursts. Use atomic state operations in an appropriate shared store or allocate local budgets with a known approximation. Define multi-region behavior, key expiry, response metadata, and store failure policy. Security-sensitive limits may fail closed; low-risk traffic may tolerate a bounded fail-open policy.

### Q41. Design a notification platform.
**Answer:** Accept a durable notification request with recipient, channel, template version, priority, and deduplication identity. Separate scheduling from channel workers and provider adapters. Apply preferences, consent, quiet hours, quotas, and per-provider concurrency limits. Record accepted, attempted, provider-accepted, and delivered states distinctly; provider acceptance is not final delivery. Include bounded retries, expiry, callback validation, and support tools for failures without resending every successful message.

### Q42. Design a one-to-one chat service.
**Answer:** Use an authenticated real-time connection for online delivery and durable message storage for history and offline clients. Assign stable message identities and a conversation ordering rule. Acknowledge durable acceptance separately from device delivery and read receipts. Route to connected devices and let clients resume from a cursor after reconnecting. Include duplicate suppression, multi-device behavior, authorization, abuse controls, retention, and a realistic connection-capacity estimate.

### Q43. Design presence and typing indicators without overbuilding them.
**Answer:** Treat them as short-lived hints, not durable business facts. Use heartbeats or connection activity with expiry, and aggregate multiple devices according to a clear policy. A lost disconnect event should eventually expire. Keep update volume bounded and avoid writing every keystroke to durable history. During failure, showing “status unknown” is more honest than a strong online/offline promise the architecture cannot maintain.

### Q44. Design a social news feed.
**Answer:** Separate post storage, relationship data, feed generation, ranking, and media delivery. Fan-out on write gives fast reads but can be expensive for authors with many followers; fan-out on read shifts cost to readers. A hybrid can handle ordinary and celebrity accounts differently. Use stable cursors, define freshness, and filter deleted or unauthorized content. Cache ranking inputs carefully and keep timeline reconstruction possible after derived-state loss.

### Q45. Design product search and autocomplete.
**Answer:** Keep the transactional catalog authoritative and build a search index asynchronously. Define indexing delay, supported filters, ranking, language handling, and how deletions propagate. Autocomplete can use a separate compact suggestion representation rather than full search for every keystroke. Bound request rates and query complexity. Display authoritative availability at checkout, not a promise based only on the search index. Plan reindexing with versioned indexes and a controlled switch.

### Q46. Design a large-file upload service.
**Answer:** Authorize creation of an upload session and issue tightly scoped, time-limited object-storage access. Upload directly to object storage using multipart or resumable mechanisms rather than proxying every byte through application servers. Verify completion, size, checksum, ownership, and malware policy before publishing the object. Track abandoned uploads and clean them up. A presigned URL can be reused until expiry, so it is not inherently a one-time token.

### Q47. Design video processing and delivery.
**Answer:** Store the original upload, persist a processing job, and run bounded workers to produce required formats and thumbnails. Make steps restartable and record versioned outputs so retries do not expose partial results. Deliver through object storage and a CDN, with access rules and signed delivery where needed. Separate processing latency from playback latency objectives. Budget storage, transcoding cost, region placement, and removal across all derived copies.

### Q48. Design an online checkout workflow.
**Answer:** Create a durable order state machine and define when price, inventory, and delivery promises become binding. Coordinate reservation, payment authorization, and confirmation with explicit pending and failure states. Use stable operation identities and durable event handoffs rather than a chain of untracked calls. Provide an order-status API for ambiguous outcomes. Include reservation expiry, provider reconciliation, compensation, and a support path for cases that cannot complete automatically.

### Q49. Design a payment ledger.
**Answer:** Separate payment-provider workflow state from the authoritative ledger. Use an append-oriented double-entry model where balanced entries represent the economic event, with exact monetary representation and currency rules. Enforce unique business operation identities and tightly controlled posting. Corrections normally add reversing or adjusting entries rather than silently editing history. Include audit, reconciliation, access control, and recovery. A successful provider callback alone is not proof that every ledger entry is correct.

### Q50. Design ticket booking for a popular event.
**Answer:** Model seat allocation through an authoritative state transition from available to held to sold, with explicit expiry and ownership. Use conditional updates or another proven serialization mechanism for the seat invariant. Treat payment and hold expiry races deliberately. Cache the seating display but revalidate allocation at the authority. Use admission control for the launch burst; adding request servers cannot increase the supply of seats or eliminate contention for the same seat.

### Q51. Design a shopping cart.
**Answer:** Store a versioned cart keyed by the user or a secure anonymous identity, with item quantities and selected options. Define how devices merge edits and how an anonymous cart joins an authenticated cart. Keep checkout responsible for authoritative price, inventory, and promotion validation; a cart display is not a permanent reservation. Set retention and size limits, handle removed products, and make “add one” versus “set quantity” semantics explicit for retries.

### Q52. Design flash-sale inventory.
**Answer:** Put admission control and bounded concurrency ahead of the inventory authority. Allocate stock through atomic reservation decisions, with unique operation identifiers and durable outcomes. A fast approximate counter can help reject obvious overload but must not be the only authority if overselling is forbidden. Define payment and reservation expiry, fairness, and bot controls. Reconcile counters and reservations, and test the hottest single product rather than only uniform traffic.

### Q53. Design a delayed-job scheduler.
**Answer:** Persist job identity, due time, payload reference, state, and retry policy. Claim due work using a recoverable ownership mechanism, then dispatch to bounded workers. Assume a worker may execute and crash before reporting completion, so handlers need a repeat-safe contract. Define lateness objectives, timezone rules, cancellation races, and large bursts at common times. Monitor oldest overdue work and make abandoned claims recoverable.

### Q54. Design a webhook-delivery platform.
**Answer:** Persist each intended delivery, sign requests using a versioned secret, and provide stable event identifiers. Bound concurrency and retry with expiry, classifying responses according to the agreed contract. Expose delivery history and controlled redelivery. Validate destinations to prevent server-side request forgery, including private-address and redirect handling. A receiver must tolerate duplicates; successful delivery means a valid acknowledgment, not proof that the receiver completed every downstream effect.

### Q55. Design an analytics ingestion pipeline.
**Answer:** Accept versioned events, validate size and identity, durably buffer them, and process into suitable analytical stores. Partition for throughput while preserving any required per-key order. Define lateness, duplicate treatment, retention, and correction rules. Keep expensive exploratory queries away from the transactional database. Monitor end-to-end freshness and data quality, not only broker throughput. Reprocessing should create comparable results without repeating unrelated business side effects.

### Q56. Design a top-K or leaderboard service.
**Answer:** Clarify exact versus approximate ranking, update rate, tie handling, time windows, and whether users need their own rank. An ordered structure can serve a bounded exact leaderboard; large streaming workloads may need partitioned aggregation or approximate candidates followed by exact selection. Define how late corrections and fraud removals affect results. A global hot counter can become a bottleneck, so estimate update concentration and recovery of derived state.

### Q57. Design a log-search platform.
**Answer:** Collect structured logs with bounded local buffering, transport them durably where required, and index them by time and useful fields. Separate searchable hot retention from cheaper archival retention. Limit index cardinality and ingestion volume, and redact secrets before they spread. Decide which logs may be dropped under pressure and which audit events require stronger durability. Search availability and ingestion availability are different objectives with different bottlenecks.

### Q58. Design a metrics platform.
**Answer:** Ingest time-series samples with bounded label sets, partition storage, and support retention and downsampling. Treat high-cardinality labels as a capacity risk. Decide whether clients push or are scraped based on network and workload constraints. Separate alert evaluation from ad hoc dashboards and define behavior during ingestion gaps. Replicated collection can create duplicate samples, so use the platform's supported deduplication semantics rather than summing everything.

### Q59. Design a feature-flag service.
**Answer:** Keep flag definitions and targeting rules in an authoritative control plane and distribute versioned snapshots to application SDKs. Local evaluation can avoid a remote call per request. Define defaults, update freshness, deterministic percentage assignment, audit, and emergency disable behavior. Do not use client-visible flags as authorization. Expire obsolete flags and test significant combinations; otherwise rollout safety gradually turns into permanent configuration complexity.

### Q60. Design a multi-tenant SaaS backend.
**Answer:** Identify isolation, compliance, customization, and noisy-neighbor requirements. Compare shared tables, separate schemas, and separate databases, including per-tenant backup and deletion. Carry trusted tenant context through authorization, queries, caches, jobs, and metrics without leaking data. Apply quotas and resource budgets. Provide a way to move unusually large tenants without changing their public identity. Shared infrastructure reduces cost only if isolation remains enforceable and testable.

### Q61. Design an audit trail.
**Answer:** Define which actions require evidence, which actor identities are trusted, and what before/after information is necessary. Persist critical audit facts reliably with the operation or through a durable handoff. Restrict changes, detect tampering according to the threat model, and control access and retention. Ordinary application logs are not automatically a compliant audit trail. Include clock uncertainty, correlation, and how authorized deletion obligations interact with retention requirements.

### Q62. How would you migrate a legacy monolith incrementally?
**Answer:** Identify a cohesive capability and put a routing or abstraction boundary around it. Move a thin vertical slice, establish data ownership, compare behavior, and shift traffic gradually. Keep compatibility and rollback explicit while old and new implementations coexist. Avoid indefinite dual writes without a correctness protocol. Use the strangler approach only where extraction has a real benefit; improving module boundaries inside the monolith may be the safer first step.

### Q63. A design must handle ten times more traffic next year. What would you change now?
**Answer:** Measure current resource cost and identify limits that are expensive to reverse, such as poor keys, unbounded collections, global serialization, and hidden shared state. Add observability, load tests, explicit quotas, and a growth path. Do not immediately build ten times the current infrastructure. Invest early in correct boundaries and recoverability, then scale measured bottlenecks when growth justifies the cost.

### Q64. Two teams propose different architectures. How should a tech lead decide?
**Answer:** Agree on requirements and evaluation criteria first, including correctness, delivery time, operability, cost, and team capability. Compare each proposal against the same realistic workload and failure cases. Test the highest-risk disputed assumption with a bounded prototype or benchmark. Record the decision and revisit triggers. The most sophisticated diagram should not win automatically; prefer the simplest design that can meet the important promises.

### Q65. What should the final five minutes of a design discussion cover?
**Answer:** Summarize the core flows, authoritative state, main invariant, scale assumptions, and hardest trade-off. Walk through one failure and its recovery. State the next bottleneck, security boundary, operational metrics, and unresolved risk. Explain why the design is sufficient now and how it can evolve. A clear account of limitations is stronger than claiming the system is infinitely scalable, always available, and perfectly consistent.

## Sources and further reading

Official architecture references support the reasoning; interview guides were used only to discover recurring themes and case types. The case-study architectures are original worked proposals, not claims that these sources prescribe one universal answer.

- [AWS Well-Architected: six pillars](https://docs.aws.amazon.com/wellarchitected/latest/framework/the-pillars-of-the-framework.html) — cross-cutting architecture trade-offs, including operational excellence, security, reliability, performance, cost, and sustainability.
- [Google SRE: service-level objectives](https://sre.google/sre-book/service-level-objectives/), [embracing risk](https://sre.google/sre-book/embracing-risk/), and [availability table](https://sre.google/sre-book/availability-table/) — Q2–Q9.
- [Google SRE: handling overload](https://sre.google/sre-book/handling-overload/) and [addressing cascading failures](https://sre.google/sre-book/addressing-cascading-failures/) — Q13–Q22, Q29, Q40, Q50–Q52.
- [AWS Well-Architected: reliability pillar](https://docs.aws.amazon.com/wellarchitected/latest/reliability-pillar/welcome.html) — Q23–Q24, Q29–Q30.
- [AWS: caching best practices](https://aws.amazon.com/builders-library/caching-challenges-and-strategies/) — Q18–Q19.
- [AWS S3: presigned URLs](https://docs.aws.amazon.com/AmazonS3/latest/userguide/using-presigned-url.html) — Q46.
- [AWS: transactional outbox](https://docs.aws.amazon.com/prescriptive-guidance/latest/cloud-design-patterns/transactional-outbox.html) and [saga orchestration](https://docs.aws.amazon.com/prescriptive-guidance/latest/cloud-design-patterns/saga-orchestration.html) — durable workflows in Q41, Q48–Q49, Q54, Q61.
- [AWS: strangler fig](https://docs.aws.amazon.com/prescriptive-guidance/latest/cloud-design-patterns/strangler-fig.html) — Q62.
- [Oracle Java 21: records](https://docs.oracle.com/en/java/javase/21/language/records.html) — modern Java value-oriented implementation context; records do not supply deep immutability or distributed correctness.
- [Interview topic guide: DesignGurus system-design framework](https://www.designgurus.io/blog/complete-guide-sys-design) and [Techoral system-design topic index](https://techoral.com/systemdesign.html) — topic and case discovery only.
