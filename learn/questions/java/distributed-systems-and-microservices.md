# Distributed Systems and Microservices — Senior Java Question Bank

[Back to index](index.md)

**Audience:** Senior Java developer / tech lead.  
**Version frame:** Principles apply to Java 8 systems and modern Java 17/21 services. Java 21 virtual threads can change local execution costs, not network or consistency guarantees. Java 25 does not remove partial failure. Kafka details must be checked against the deployed client, broker, and consumer protocol.

**Scope:** Service boundaries, consistency, coordination, messaging, Kafka, cross-service workflows, resilience, and distributed diagnosis. Local SQL/JPA mechanics belong in the persistence bank; Spring annotations belong in the Spring bank; complete product designs belong in the system-design bank.

**Research basis:** Public interview guides repeatedly cover CAP, consistency, service communication, Kafka partitions, delivery semantics, sagas, idempotency, and failure recovery. These original questions use official Kafka, AWS architecture, and Google SRE references for technical reasoning. Guide themes are not a measured frequency ranking.

## Boundaries and communication

### Q1. What makes a system distributed?
**Answer:** Components communicate across process or machine boundaries and cannot assume one shared clock, instantaneous communication, or all-or-nothing failure. One component can remain healthy while another is unreachable. The hard part is not merely deploying more processes; it is making correct decisions with delayed, duplicated, reordered, or missing information. Even two services using HTTP already face these problems.

### Q2. When are microservices better than a modular monolith?
**Answer:** Microservices can help when independently owned capabilities need different deployment, scaling, or failure boundaries. A modular monolith is often simpler while the domain and team structure are still changing. Microservices add network dependencies, distributed data, operational tooling, and compatibility work. Choose them for a concrete organizational or workload benefit, not because a large number of repositories is considered mature architecture.

### Q3. How do you identify a good service boundary?
**Answer:** Group behavior and data around a cohesive business capability, with clear ownership and vocabulary. Changes should usually stay within one boundary rather than requiring coordinated changes everywhere. A boundary that requires many synchronous calls to complete one ordinary action may be too fragmented. Start from domain invariants and team ownership; splitting services by technical layers often creates a distributed version of a tightly coupled application.

### Q4. Why is a shared database between services risky?
**Answer:** It allows one service to depend on another's private schema, transaction assumptions, and release timing. A schema change can then break independent deployment. Separate ownership does not always require a separate physical database immediately, but write permissions and contracts should be explicit. Shared reporting needs can use governed exports or derived views rather than giving every service unrestricted access to every table.

### Q5. When should communication be synchronous or asynchronous?
**Answer:** Synchronous communication fits a caller that needs a direct result now, but couples availability and latency. Asynchronous communication buffers work and supports independent processing, but introduces lag, duplicate handling, and more complex user states. Select based on the business interaction. An asynchronous acknowledgment usually means accepted, not completed; expose status or completion notification rather than pretending the operation is already finished.

### Q6. How do REST, gRPC, and messaging compare?
**Answer:** REST is widely understood and works well with HTTP tooling and resource-oriented APIs. gRPC provides strongly described contracts and efficient streaming or RPC, with proxy and client-compatibility considerations. Messaging supports temporal decoupling and replay or buffering depending on the broker. Protocol choice does not solve ownership, idempotency, or authorization. Compare client ecosystems, browser access, payloads, and failure behavior before selecting a tool.

### Q7. What do service discovery, an API gateway, and a service mesh each do?
**Answer:** Discovery finds current service instances. A gateway handles an entry boundary, often routing, authentication integration, and client-facing policies. A mesh can manage some service-to-service transport policies and telemetry through infrastructure. These roles can overlap but are not interchangeable. Business authorization and transaction correctness remain application responsibilities. Extra infrastructure adds its own availability, configuration, and debugging burden.

### Q8. What is an anti-corruption layer?
**Answer:** It translates an external or legacy system's model into the application's own domain model. This prevents a provider's field names, status codes, and workflow assumptions from spreading through every service. It can also normalize errors and isolate compatibility changes. It should not become an unexplained universal integration service. Keep the translation close to the domain that owns the relationship.

## Consistency, coordination, and time

### Q9. What does CAP actually say?
**Answer:** During a network partition, a distributed shared-data system cannot guarantee both linearizable consistency and availability for every request in the affected operation. Availability here is the formal requirement that requests to non-failed nodes eventually receive responses, not a marketing uptime percentage. Partition tolerance is not normally optional when networks can fail. Discuss what a particular read or write does during partition, rather than labeling an entire company “CP” or “AP.”

### Q10. How is CAP consistency different from ACID consistency?
**Answer:** CAP's consistency refers to a single-copy style guarantee, commonly linearizability. ACID consistency refers to preserving declared data invariants across transactions. A database can enforce unique constraints and still expose stale replica reads. Use precise names for the guarantee being discussed: business invariant, read freshness, transaction isolation, ordering, or durability. Using one word for all of them hides the actual trade-off.

### Q11. How do linearizability, sequential consistency, and eventual consistency differ?
**Answer:** Linearizability makes each operation appear atomic between its invocation and completion while respecting real-time order. Sequential consistency preserves a global order compatible with each participant's program order, but need not respect real time. Eventual consistency promises convergence once updates stop and required propagation succeeds, under the system's assumptions. It does not promise a fixed convergence deadline or resolve conflicting business intent by itself.

### Q12. What are read-your-writes, monotonic reads, and causal consistency?
**Answer:** Read-your-writes lets a client see its successful writes in later reads. Monotonic reads avoid moving backward to an older observed version. Causal consistency preserves relevant cause-and-effect order, so a response is not visible without its prerequisite. These guarantees can improve user experience without requiring every read to be globally linearizable. Implementations need session or version information, not merely a vague promise of eventual consistency.

### Q13. What are the trade-offs of synchronous and asynchronous replication?
**Answer:** Synchronous replication waits for required acknowledgments, increasing latency and potentially reducing write availability when replicas are unavailable. Asynchronous replication acknowledges earlier but risks lag and loss of recent writes during failover. Define exactly which replicas acknowledge and what they persist. A replica count alone does not specify durability. Read-routing and failover policies must match the promised user-facing guarantee.

### Q14. Does `R + W > N` automatically guarantee linearizable reads?
**Answer:** No. Quorum overlap is useful, but correctness also depends on replica membership, failure handling, concurrent writes, version ordering, and the read/write protocol. Sloppy quorums and last-write-wins policies complicate the claim further. State the algorithm and assumptions rather than treating one inequality as a complete consistency proof. Membership changes can be especially dangerous if different clients use incompatible replica sets.

### Q15. What problem does consensus solve?
**Answer:** Consensus lets participants agree on a value or ordered decisions despite a defined set of failures. Protocols such as Raft use terms, leaders, and quorum rules to preserve safety under their assumptions. They do not make an unavailable majority reachable or guarantee fast progress during arbitrary delay. Use a well-tested coordination system rather than implementing a new election protocol inside ordinary business services.

### Q16. How do leader election and fencing differ?
**Answer:** Election selects who should lead; fencing prevents a previous leader from continuing to modify a protected resource. A paused leader may resume after its lease expires and a replacement takes over. A monotonically increasing fencing token lets the resource reject older owners, if that resource enforces it. A distributed lock by itself does not necessarily stop stale work already in progress.

### Q17. Why is wall-clock time dangerous for ordering?
**Answer:** Clocks can drift, jump, or disagree. Comparing timestamps from different machines may not reflect causal order, and last-write-wins can discard legitimate changes. Use monotonic time for local elapsed duration and appropriate logical versions or ordering protocols for distributed decisions. Time synchronization is useful operationally, but it does not turn timestamps into a complete conflict-resolution or mutual-exclusion algorithm.

### Q18. How do consistent hashing and range partitioning differ?
**Answer:** Consistent hashing can reduce key movement when nodes change, while range partitioning groups nearby keys and can support efficient range scans. Both can suffer skew. Virtual nodes or many logical partitions help redistribute data but do not fix a single hot key. Choose based on access patterns, ordering requirements, and rebalancing needs. Repartitioning must account for routing versions and concurrent writes.

## Messaging and Kafka

### Q19. How does a durable log differ from a traditional work queue?
**Answer:** A log retains an ordered history for a configured period, and consumers track their positions independently. A work queue focuses on assigning work and acknowledging completion, though actual products overlap. Kafka normally deletes data according to retention or compaction rules, not because every consumer has processed it. This enables replay but makes retention planning and lag monitoring part of correctness.

### Q20. What are Kafka topics, partitions, offsets, and consumer groups?
**Answer:** A topic groups records. Partitions are ordered logs within that topic. An offset identifies a position within one partition, not a global event ID. A consumer group divides partition consumption among its members; separate groups can process the same topic independently. Under the conventional consumer-group model, a partition is assigned to one member at a time, though ownership changes and in-flight work still need care.

### Q21. What ordering does Kafka guarantee?
**Answer:** Kafka provides log ordering within a partition, not a total order across the topic. Related events usually need a key that routes them together. Correct application processing also depends on producer settings and consumer concurrency; parallel handlers can complete out of order. Increasing partitions can change key routing for future records, so do not assume one business key's entire history remains in one place forever.

### Q22. How should a partition key be chosen?
**Answer:** Choose a key matching the unit that needs order, such as an order or account identifier, while distributing load reasonably. A tenant key may create a huge hot partition for one large customer. Random keys spread traffic but lose per-entity ordering. Key changes affect compatibility and routing. Document whether order, load distribution, or locality is the main requirement rather than optimizing one silently.

### Q23. How do at-most-once and at-least-once processing arise?
**Answer:** Recording progress before an external effect can lose work if the consumer crashes between them: at-most-once behavior. Performing the effect before recording progress allows replay after a crash: at-least-once behavior. An acknowledgment policy is not the whole design; include producer and storage failure cases. Most business consumers prefer possible redelivery plus atomic deduplication over silently losing a required effect.

### Q24. Does Kafka exactly-once processing prevent duplicate payments?
**Answer:** Not by itself. Kafka transactions can atomically publish Kafka records and commit consumed offsets, with cooperating consumers using the proper isolation. An external payment provider or ordinary database is not automatically part of that transaction. Use an idempotency contract, transactional inbox, or another supported atomic integration. Always state the boundary of the exactly-once claim instead of extending it to the whole business workflow.

### Q25. How do `acks=all`, replication factor, and `min.insync.replicas` interact?
**Answer:** Replication factor sets the replica count. With `acks=all`, the producer waits for the required in-sync replica acknowledgment behavior, and `min.insync.replicas` can reject writes when too few replicas remain in sync. `acks=all` does not mean every configured replica is always healthy or involved. Trade availability against the desired durability and consider election policies, storage, and the relevant failure model.

### Q26. What does an idempotent Kafka producer guarantee?
**Answer:** It uses producer identity and sequence information to prevent duplicate log entries caused by supported producer retries. It does not deduplicate two independent business requests or every new producer session. Broker acknowledgment, retry, and in-flight settings must satisfy the client's requirements. The consumer may still perform the same external effect twice, so producer idempotence is only one part of end-to-end correctness.

### Q27. When should a consumer commit offsets?
**Answer:** Commit progress only after the work covered by those offsets has completed under the intended durability contract. Committing early can lose work; committing later can cause replay. With asynchronous processing, do not advance past an unfinished earlier record in a partition. Decide how partition ownership changes are handled, and remember that a commit failure after a successful side effect requires safe redelivery.

### Q28. What causes consumer rebalances, and why do they matter?
**Answer:** Group membership changes, failures, partition changes, and protocol events can alter partition assignments. Rebalances can pause work and move state or in-flight ownership. Handle revocation deliberately, keep poll and processing behavior within the selected protocol's requirements, and make effects safe on replay. Static membership or cooperative approaches can reduce some disruption, but do not eliminate every rebalance or correctness risk.

### Q29. Why might consumer lag grow while application CPU stays low?
**Answer:** The consumer may be waiting on a database, remote dependency, connection pool, throttling, or retry delay. Partition skew can overload one handler while others sit idle. Examine lag by partition, event age, processing duration, rebalance behavior, and downstream waits. Adding consumers cannot increase ordinary group parallelism beyond assigned partitions and cannot fix a single hot key without changing the model.

### Q30. How should retry topics and dead-letter queues be used?
**Answer:** Separate transient failures from invalid or permanently unsupported messages. Use bounded retries and capture the original event, safe failure metadata, and attempt history. A dead-letter queue needs an owner, alerts, retention, and a controlled replay process. Moving a record aside can break per-key order, so state the business consequences. It is not a successful outcome merely because the main consumer keeps moving.

### Q31. How do retention and log compaction differ?
**Answer:** Time or size retention removes older log segments according to configuration. Compaction eventually retains a useful latest value per key while removing superseded records; it is not immediate deduplication. Tombstones represent deletion and have their own retention implications. Compaction is not a complete audit history, and consumers rebuilding state must understand when deletion markers can disappear.

### Q32. How should event schemas evolve?
**Answer:** Add optional fields or fields with suitable defaults when the serialization format permits it, and verify compatibility in the actual producer-consumer direction. Renaming, removing, or changing the meaning of a field may break old consumers even when a schema check passes. Use schema governance, compatibility tests, and explicit deprecation. Events already retained in the log must remain readable for the promised replay window.

### Q33. How do event time and processing time differ?
**Answer:** Event time records when the business event occurred; processing time records when a system handled it. Late or out-of-order arrival makes them differ. Windowed processing needs a policy for lateness, correction, and watermarks or equivalent progress signals. A watermark is an estimate about event-time progress, not proof that no old event can ever arrive. Explain how reports change when a late event is accepted.

### Q34. How would you safely replay old events?
**Answer:** Define the purpose, destination, schema handling, and expected effects. Rebuilding a projection should not resend every historical email or payment request. Use isolated consumer groups or controlled offsets, bounded throughput, deterministic handlers, and clear deduplication policy. Check source retention and deletion obligations. Compare rebuilt results before switching traffic, and monitor the replay so it does not overwhelm live dependencies.

## Cross-service workflows

### Q35. What is the dual-write problem?
**Answer:** A service updates its database and publishes a message as two independent operations. A crash or error between them can leave stored state without an event, or an event for state that never committed. Reversing the order does not remove the problem. A local transaction around database code does not automatically include a broker. Use a deliberate atomicity pattern rather than hoping both calls succeed together.

### Q36. How does the transactional outbox solve this problem?
**Answer:** Write the business change and an outbox event in one local database transaction. A relay later publishes committed outbox records. A crash before commit leaves neither; a crash afterward still leaves the event available for delivery. The relay can publish duplicates, so consumers need idempotency. Manage per-entity ordering, relay lag, retention, and cleanup; an outbox is a durable handoff, not automatic exactly-once delivery.

### Q37. How do polling and change data capture compare for outbox delivery?
**Answer:** Polling is straightforward but needs efficient selection, ownership, and cleanup to avoid excessive database work. Change data capture can stream committed changes from a log with low polling overhead, but adds connector operations, log-retention requirements, and schema-handling concerns. Both require recovery positions and duplicate handling. Reading arbitrary table changes is not the same as publishing stable business events with clear semantics.

### Q38. How does a transactional inbox prevent duplicate effects?
**Answer:** The consumer inserts a unique event identifier and applies its database effect in the same local transaction. A repeated event conflicts with the already recorded identifier and is treated as completed. A separate “check then act” is racy, and recording the identifier after an unprotected effect is unsafe. Choose deduplication retention to match replay requirements. External effects still need their own idempotency contract.

### Q39. What is a saga?
**Answer:** A saga coordinates a business workflow using local transactions and explicit follow-up or compensating actions. It gives up one all-or-nothing distributed database transaction and instead models intermediate states and recovery. Compensation is a new business action, not time travel: refunding a payment is different from pretending it never happened. Some actions cannot be undone and require escalation or a forward-recovery policy.

### Q40. How do saga orchestration and choreography compare?
**Answer:** Orchestration puts workflow decisions in a coordinator, improving visibility and explicit state handling but centralizing control. Choreography lets participants react to events, which can reduce direct coupling but make the overall workflow hard to discover and change. Neither is automatically more resilient. Evaluate team ownership, number of steps, timeout handling, debugging, and the need to know the current business state.

### Q41. When might two-phase commit be appropriate?
**Answer:** Two-phase commit coordinates participants that support a distributed transaction protocol so they agree on commit or rollback. It can suit controlled environments requiring strong atomicity, but introduces coordinator and participant recovery complexity and can hold resources during failures. Many remote APIs do not participate. Do not dismiss it as impossible or apply it universally; compare guarantees, supported infrastructure, latency, and availability costs.

### Q42. How do CQRS and event sourcing differ?
**Answer:** CQRS separates command and query models; it does not require separate databases or an event store. Event sourcing stores domain events as the authoritative history from which state is derived. They can be used together or independently. Event sourcing adds replay, evolution, deletion, and projection-management costs. A table containing audit messages is not necessarily a complete, replayable source of truth.

### Q43. What should an idempotent API contract define?
**Answer:** Define key scope, who generates it, accepted retry behavior, payload matching, in-progress handling, result storage, and retention. Atomically claim the key with the business effect or use a recoverable state machine. Return the original result for the same operation; reject a reused key with a different payload. Idempotency does not mean every retry receives success immediately while the first attempt is still running.

## Resilience and overload

### Q44. Why is a timeout an ambiguous outcome?
**Answer:** It tells the caller that a timely response was not received, not that the server did nothing. The operation may have committed and the response may have been lost. The caller needs a status query or safe idempotent retry for side-effecting requests. This ambiguity exists even on a reliable-looking internal network and is a core reason distributed workflows need operation identifiers.

### Q45. How should timeouts be chosen?
**Answer:** Start with the caller's end-to-end deadline, downstream latency distribution, network cost, and acceptable false-timeout rate. Include connection establishment and pool waiting where relevant. Pass remaining deadline information across calls rather than giving each hop the original full budget. Different operations can need different policies. A timeout is also a resource-control mechanism, so an excessively long timeout can cause saturation.

### Q46. Why can retries cause an outage?
**Answer:** They add traffic precisely when a dependency may already be overloaded. Retries at several layers multiply: three attempts at each of five layers can generate up to 243 leaf attempts. Retry only suitable transient errors, bound attempts by the deadline, use backoff with jitter, and limit total retry traffic. Validation failures and non-idempotent effects should not receive generic automatic retries.

### Q47. How does a circuit breaker differ from a retry policy?
**Answer:** A retry repeats an operation under defined conditions. A circuit breaker stops or limits calls after evidence of failure, then probes recovery. It can protect resources and shorten failures but adds state and tuning complexity. It does not fix a bad dependency or replace timeouts. Avoid treating a successful fallback as proof that the original business operation completed.

### Q48. What are bulkheads?
**Answer:** Bulkheads isolate limited resources so one failure does not consume all capacity. Examples include separate connection pools, concurrency limits, or worker budgets for different dependencies or tenants. Isolation costs spare capacity and configuration. Separate thread pools do not help if every pool still competes for the same exhausted database connections. Identify the actual constrained resource and test whether the isolation survives a slow dependency.

### Q49. How do backpressure, rate limiting, and load shedding differ?
**Answer:** Backpressure communicates reduced processing capacity upstream. Rate limiting enforces an allowed arrival rate or quota. Load shedding rejects work to protect useful service when capacity is exhausted. They can cooperate, but an unbounded queue is none of them. Define overload responses and client behavior, and preserve critical traffic or tenant fairness rather than letting the fastest sender consume every resource.

### Q50. Why are bounded queues important?
**Answer:** A queue absorbs temporary bursts, but if arrivals keep exceeding completions, it grows without bound in delay or memory. A bounded queue makes overload visible and creates a point for rejection or deferral. Monitor event age as well as length. Define whether expired jobs should still run; processing a timed-out request much later can waste resources or cause surprising side effects.

### Q51. When is a fallback safe?
**Answer:** A fallback is safe when it preserves the operation's business promise or explicitly returns degraded behavior. A stale product description may be acceptable; a fabricated successful payment is not. Define freshness limits, user messaging, and recovery. Cached authorization or inventory decisions can be especially risky. Availability is not improved honestly by returning a success code for work that was never done.

### Q52. How should cancellation work across services?
**Answer:** Propagate deadlines or cancellation where supported, release local resources, and avoid starting work that cannot complete usefully. Cancellation is best effort: a remote service may already have committed or may not observe the signal. Distinguish cancelling the wait from cancelling the business action. Use durable operation state when a user needs a reliable cancellation outcome and model races between completion and cancellation.

### Q53. Do virtual threads remove the need for resilience limits?
**Answer:** No. They reduce the cost of many blocked Java tasks but can make it easier to create excessive concurrent downstream calls. Database connections, remote quotas, sockets, and memory remain finite. Keep explicit concurrency budgets, timeouts, and overload policies on Java 21 and later. The correct limit comes from resource capacity and the business workload, not from how many Java threads can be created.

## Contracts, security, and diagnosis

### Q54. How can services deploy independently without breaking each other?
**Answer:** Make additive contract changes, tolerate compatible extra fields, and keep old behavior through an agreed migration window. Run consumer-contract and integration tests for important interactions. Deploy expansions before requiring new fields, then remove old behavior after consumers migrate. Structural compatibility is not semantic compatibility: changing a status meaning can break a consumer even if the JSON schema is unchanged.

### Q55. What does zero trust mean for internal service calls?
**Answer:** Do not assume a request is safe because it comes from an internal network. Authenticate workloads, encrypt transport where required, authorize the requested operation, and limit access to data and secrets. Mutual TLS authenticates peers but does not by itself decide whether one service may act on a particular customer's account. Separate service identity from delegated user identity and avoid forwarding unlimited authority.

### Q56. How do logs, metrics, and traces complement each other?
**Answer:** Metrics show aggregate health and saturation. Traces connect work across boundaries and expose where time is spent. Logs provide detailed events for diagnosis. Propagate trace context through HTTP and messages, but do not treat an untrusted incoming trace ID as an authorization claim. Control sensitive content, label cardinality, and sampling. A sampled trace set is not automatically an unbiased count of every failure.

### Q57. Why can fan-out make tail latency much worse?
**Answer:** A request waiting for many dependencies completes at the slowest required branch. Even moderately rare slow responses become common when many branches are involved. Bound fan-out, remove unnecessary critical-path calls, and define partial-result behavior. Hedged requests can reduce some read tails but consume extra capacity and require cancellation and safety controls. Do not duplicate side-effecting calls just to make a chart look faster.

### Q58. What should a chaos experiment prove?
**Answer:** State a hypothesis about an observable user outcome under a bounded failure, such as one unavailable dependency. Define safe scope, stop conditions, monitoring, and recovery. Test timeouts, retries, queues, and isolation rather than merely killing a process. Start in controlled environments and expand carefully. An experiment is useful when it verifies a reliability claim and produces a specific corrective action.

## Case studies

### Q59. A payment request timed out. Should the order service submit another payment?
**Answer:** Treat the result as unknown. Query by the original operation identifier or retry with the same provider-supported idempotency key and unchanged payload. Keep the order in a pending state while reconciling. Do not generate a new payment key merely because the HTTP call timed out. Escalate unresolved cases through a recoverable workflow with an audit trail.

### Q60. The order committed, but its event never arrived. How do you recover and prevent recurrence?
**Answer:** Compare authoritative orders with durable publishing state and repair missing events using stable identifiers. Diagnose the dual-write gap rather than blaming the consumer first. Introduce a transactional outbox with monitored relay lag and safe replay. A consumer cannot process an event that never existed. Reconciliation remains useful even after prevention, because configuration and operational mistakes can still occur.

### Q61. A consumer updates a database and crashes before committing its offset. What happens?
**Answer:** Kafka can redeliver the record because progress was not committed. Without protection, the database effect may happen twice. Put an event-ID claim and the effect in one local database transaction, then commit the offset after success. If the record is already recorded, safely advance progress. This gives one committed database effect under the stated deduplication policy, not universal exactly-once external execution.

### Q62. One service is slow and every upstream service is failing. What would you do?
**Answer:** Reduce amplification first: cap or disable harmful retries, shed nonessential work, and isolate the dependency. Inspect caller deadlines, connection waits, queue ages, and concurrency budgets. Use safe degradation where defined and recover gradually to avoid a synchronized surge. After containment, find why the original dependency slowed and run a failure test demonstrating that the same chain cannot collapse again.

### Q63. A saga's compensation keeps failing. What is the correct state?
**Answer:** Persist an explicit unresolved or compensating state with the original operation and attempt history. Retry suitable failures with bounds and escalate cases needing human or business intervention. Do not mark the saga rolled back just because compensation was requested. Make compensation idempotent and monitor its age. Users and support teams need a truthful current state and a path to resolution.

### Q64. Two regions both accept a reservation for the last item. How would you fix the design?
**Answer:** If overselling is forbidden, define one serialization point for that item's allocation, such as an authoritative owner or a supported strongly consistent conditional operation. A partitioned region may then need to reject or defer the request. If availability is more important, the business must accept overselling and define reconciliation. Eventual convergence cannot retroactively make both conflicting promises valid.

### Q65. What should a tech lead require in a distributed-service design review?
**Answer:** Require owned contracts, explicit consistency needs, operation identifiers, timeout budgets, bounded retries, duplicate handling, durable workflow states, and a failure matrix. Include deployment compatibility, telemetry, data retention, replay, and operator recovery. Ask what happens after a crash at every durable boundary. A successful demonstration of the happy path is not evidence that the cross-service protocol preserves its promises.

## Sources and further reading

Official references support technical reasoning; interview guides were used only to identify recurring topics.

- [AWS: microservices](https://aws.amazon.com/microservices/) — service boundaries and communication context.
- [Google SRE: managing critical state with distributed consensus](https://sre.google/sre-book/managing-critical-state/) — consensus and coordination.
- [etcd: API guarantees](https://etcd.io/docs/v3.5/learning/api_guarantees/) — concrete consistency and operation guarantees.
- [Apache Kafka 4.1: design](https://kafka.apache.org/41/design/design/) — logs, groups, replication, delivery semantics, compaction, and transactions. This is a versioned reference, not a claim that 4.1 is the latest release.
- [Apache Kafka 4.1: producer configuration](https://kafka.apache.org/41/configuration/producer-configs/) and [consumer configuration](https://kafka.apache.org/41/configuration/consumer-configs/) — acknowledgments, idempotence, offsets, and protocol-sensitive settings.
- [AWS: transactional outbox](https://docs.aws.amazon.com/prescriptive-guidance/latest/cloud-design-patterns/transactional-outbox.html) and [saga orchestration](https://docs.aws.amazon.com/prescriptive-guidance/latest/cloud-design-patterns/saga-orchestration.html) — Q35–Q41.
- [AWS Builders' Library: timeouts, retries, backoff, and jitter](https://aws.amazon.com/builders-library/timeouts-retries-and-backoff-with-jitter/) and [making retries safe with idempotent APIs](https://aws.amazon.com/builders-library/making-retries-safe-with-idempotent-APIs/) — Q43–Q53, Q59–Q62.
- [Google SRE: handling overload](https://sre.google/sre-book/handling-overload/) and [addressing cascading failures](https://sre.google/sre-book/addressing-cascading-failures/) — Q46–Q53, Q62.
- [OpenTelemetry: context propagation](https://opentelemetry.io/docs/concepts/context-propagation/) — Q56.
- [Interview topic outline: distributed-system advanced topics](https://spawn08.github.io/system-design-interview/advanced/) and [Kafka interview topic guide](https://www.quizcluster.com/knowledge/kafka-interview-guide) — topic discovery only.
