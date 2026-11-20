# Java Production Operations and Cloud

[Back to index](index.md)

**Audience:** Senior Java developers and tech leads  
**Scope:** Operating Java services, observability, deployment, runtime behavior in containers, reliability, and cloud operations. This bank does not cover high-level system design or distributed-systems design.

Every question is followed immediately by an answer. Questions are original; interview guides below were used only to identify recurring topic patterns.

## Questions and answers

### Q1. What is observability, and how do logs, metrics, and traces contribute?

**Answer:** Observability is the ability to infer system behavior from its outputs. Logs describe selected events, metrics summarize numeric behavior over time, and traces connect work across request steps. They complement one another: metrics show a broad symptom, traces help locate a slow path, and logs provide event details. None should be collected without limits or purpose.

### Q2. What is the difference between an SLI, an SLO, and an SLA?

**Answer:** An SLI is a measured indicator of service behavior, such as the proportion of successful requests. An SLO is an internal target for that indicator over a time window. An SLA is a commitment to customers, often with consequences if it is missed. Define them from user outcomes rather than from a metric that is easy to collect.

### Q3. What makes an alert actionable?

**Answer:** An alert should identify a user-impacting or imminent problem that needs a human response, include enough context to begin diagnosis, and link to a runbook or dashboard. Page on symptoms and objectives rather than every low-level anomaly. Non-urgent conditions can be ticketed or reviewed without interrupting on-call staff.

### Q4. What is an error budget?

**Answer:** An error budget is the allowed unreliability implied by an SLO over a defined period. It helps teams balance release speed and reliability work. If the budget is being consumed too quickly, the team can reduce risky releases and invest in corrective work. It is a decision aid, not a target for intentionally causing failures.

### Q5. What should a Spring Boot Actuator deployment expose?

**Answer:** Expose only the management endpoints needed for operations, and protect them with network controls and authentication appropriate to the environment. Health and metrics can help orchestration and monitoring; configuration, environment, and other diagnostic endpoints may reveal sensitive information. Separate management traffic from public application traffic when practical.

### Q6. How do Kubernetes readiness, liveness, and startup probes differ?

**Answer:** Readiness says whether a container should receive traffic. Liveness indicates whether it should be restarted. A startup probe gives a slow-starting application time to initialize before liveness checks begin. A failed readiness probe removes a pod from ready service without restarting it; an overly aggressive liveness probe can create restart loops.

### Q7. What should a readiness check verify?

**Answer:** It should indicate whether the instance can safely serve its intended work, such as whether initialization is complete and essential local dependencies are usable. Keep the check bounded and avoid making every optional dependency a reason to remove all instances from service. A readiness check is not a general diagnostic endpoint or a liveness test.

### Q8. Why can a dependency-heavy liveness check cause an outage?

**Answer:** If the database or another shared service is briefly unhealthy, every application instance may fail liveness and restart even though the application process itself is healthy. This amplifies an external incident and delays recovery. Use liveness for process-level irrecoverable failure; represent inability to serve through readiness or a separate alert.

### Q9. How should a Java service shut down gracefully?

**Answer:** Stop accepting new work, allow in-flight requests to finish within a bounded grace period, close managed resources, and exit before the platform force-kills the process. The grace period must fit deployment termination settings. Test shutdown during traffic and ensure long-running requests cannot block termination indefinitely.

### Q10. How do Java heap memory and container memory limits relate?

**Answer:** The process uses more memory than the Java heap: metaspace, thread stacks, direct buffers, code cache, native libraries, and runtime overhead also consume memory. A container limit that only accounts for `-Xmx` can still cause an out-of-memory kill. Measure real workload use and leave headroom for non-heap allocations and bursts.

### Q11. Why can a Java process be killed without an `OutOfMemoryError`?

**Answer:** The operating system or container runtime can terminate a process that exceeds its memory limit before the JVM can report a Java heap error. Native memory pressure can also cause failures unrelated to the Java heap. Check container events, cgroup metrics, process memory, JVM logs, and native-memory diagnostics rather than looking only for a heap dump.

### Q12. How do CPU limits affect Java application behavior?

**Answer:** A restrictive CPU quota can throttle the process, increasing latency and delaying garbage collection, compilation, request handling, and health checks. The JVM may see a constrained processor count, but configuration and runtime version matter. Compare actual throttling and latency with resource settings before increasing replicas or thread counts.

### Q13. What is the difference between a thread pool and a connection pool?

**Answer:** A thread pool limits and reuses worker threads for tasks. A connection pool limits and reuses database or other network connections. They protect different resources, and their capacities must be considered together. Too many request threads waiting for a small connection pool can increase queueing and timeouts without increasing throughput.

### Q14. How would you investigate a sudden increase in request latency?

**Answer:** Establish when it began and which endpoints or user groups are affected. Compare latency percentiles, traffic, error rates, CPU throttling, memory, thread and connection-pool saturation, and dependency timings. Use traces and recent deployment or configuration changes to narrow the cause. Mitigate safely first if impact is severe, then preserve evidence and test the root-cause fix.

### Q15. Why should production monitoring use latency percentiles rather than only averages?

**Answer:** Averages can hide a slow tail: most users may be fast while a smaller group waits much longer. Percentiles such as p95 or p99 show parts of that tail, but are still aggregates and must be interpreted with request volume and endpoint context. Use consistent histogram or distribution measurements where possible.

### Q16. What is a useful incident response process?

**Answer:** Assign someone to coordinate, state the impact and current facts, prioritize mitigation, and communicate updates. Keep investigation separate from uncontrolled changes, record actions and timestamps, and preserve evidence. After recovery, hold a blameless review focused on contributing conditions and corrective actions with owners.

### Q17. What is a blameless postmortem?

**Answer:** It is a structured review that seeks to understand how system conditions and decisions led to an incident, rather than finding one person to blame. It records impact, timeline, contributing factors, what worked, and concrete follow-up actions. “Blameless” does not remove accountability for completing actions or learning from the event.

### Q18. How do you choose a deployment strategy?

**Answer:** Choose based on risk, rollback needs, database compatibility, capacity, and operational complexity. Rolling deployment replaces instances gradually; blue/green keeps two environments and switches traffic; canary exposes a limited share before wider rollout. Each strategy requires health signals and a defined rollback decision, and none guarantees safe changes by itself.

### Q19. What makes a rollback difficult?

**Answer:** Database schema changes, irreversible data transformations, external side effects, and configuration incompatibility can make old and new versions unable to coexist. Prefer additive schema changes followed by later cleanup, use explicit compatibility plans, and test rollback or roll-forward procedures. Reverting a binary alone is not always a safe rollback.

### Q20. What is the expand-and-contract migration pattern?

**Answer:** First expand the schema in a backward-compatible way, then deploy code that can work with old and new shapes, migrate data, and finally remove obsolete schema after all old code is gone. It supports rolling changes and rollback, but requires careful sequencing and monitoring of data correctness.

### Q21. How should database backups be validated?

**Answer:** A successful backup job does not prove that data can be restored. Regularly test restores in an isolated environment, verify integrity and application usability, and measure restore time against recovery objectives. Document ownership, access control, encryption, retention, and how to proceed during an incident.

### Q22. What do RTO and RPO mean?

**Answer:** Recovery Time Objective (RTO) is the target time to restore a service after disruption. Recovery Point Objective (RPO) is the maximum acceptable amount of data loss measured in time. They guide backup, replication, and recovery choices; they should reflect business impact and be tested, not merely written in a plan.

### Q23. How should an application handle timeouts and retries operationally?

**Answer:** Set bounded timeouts based on the operation and end-to-end budget. Retry only failures likely to be transient, cap attempts, add backoff and jitter, and avoid retries for unsafe non-idempotent operations unless there is a deduplication strategy. Observe retry volume because retries can amplify an overloaded dependency.

### Q24. How do you prevent a retry storm?

**Answer:** Use strict attempt and time budgets, exponential backoff with jitter, concurrency limits, and clear behavior when the dependency is unhealthy. Coordinate retries across layers so that several libraries do not each retry independently. Monitor retry rates and ensure the service degrades safely rather than generating more load.

### Q25. How should secrets and cloud identities be handled in production?

**Answer:** Prefer workload identity or short-lived credentials over long-lived keys embedded in images or files. Grant the runtime identity only the permissions it needs, separate it from build and deployment identities, and rotate or revoke credentials through the cloud provider’s supported mechanisms. Audit access and avoid exposing secrets in logs or diagnostics.

### Q26. What is a Kubernetes resource request versus a limit?

**Answer:** A request is the resource amount used for scheduling and related resource decisions. A limit caps use; exceeding the CPU limit can cause throttling, while exceeding a memory limit can lead to process termination. Set both from observed workload behavior and load tests, and watch for contention and throttling after rollout.

### Q27. What is a Pod Disruption Budget, and what does it not guarantee?

**Answer:** A Pod Disruption Budget limits how many selected pods may be voluntarily disrupted at once, helping preserve availability during supported maintenance actions. It does not prevent every involuntary failure, and it cannot compensate for incorrect selectors, insufficient replicas, or an application that cannot start. Check that the budget matches the deployment and recovery process.

### Q28. How do you prepare an application for autoscaling?

**Answer:** Select a signal tied to capacity or user experience, such as CPU, request concurrency, or queue depth, and verify that new instances become ready quickly enough. Ensure downstream pools and quotas can handle added replicas. Scaling out does not fix a shared bottleneck and can overload a database if each instance creates an oversized connection pool.

### Q29. What operational checks belong in a release checklist?

**Answer:** Confirm the artifact and configuration, security changes, migration compatibility, health and alert coverage, rollback plan, resource expectations, and accountable operator. Verify the release against the appropriate tests and environment. Automate repeatable checks, but retain a clear decision point for high-risk changes.

### Q30. A new release has normal CPU but rising errors and growing connection-pool wait time. What do you inspect?

**Answer:** Compare pool usage, active and idle connections, wait duration, database latency, transaction duration, and request concurrency with the previous release. Check for leaked connections, longer transactions, changed query patterns, or a pool-size/configuration change. Mitigate by rolling back or reducing load if needed; increasing pool size without checking database capacity can make the incident worse.

## Sources

### Official and technical references

- [Spring Boot Actuator](https://docs.spring.io/spring-boot/reference/actuator/index.html) — health, metrics, and management endpoint concepts.
- [Spring Boot Graceful Shutdown](https://docs.spring.io/spring-boot/reference/web/graceful-shutdown.html) — graceful application termination.
- [Kubernetes: Liveness, Readiness, and Startup Probes](https://kubernetes.io/docs/concepts/workloads/pods/probes/) — probe purposes and configuration.
- [Kubernetes: Resource Management for Pods and Containers](https://kubernetes.io/docs/concepts/configuration/manage-resources-containers/) — CPU and memory requests and limits.
- [Kubernetes: Deployments](https://kubernetes.io/docs/concepts/workloads/controllers/deployment/) — rolling update behavior.
- [Kubernetes: Pod Disruptions](https://kubernetes.io/docs/concepts/workloads/pods/disruptions/) — voluntary disruption and disruption budgets.
- [Google SRE Book: Monitoring Distributed Systems](https://sre.google/sre-book/monitoring-distributed-systems/) — monitoring and alerting principles.
- [Google SRE Book: Incident Management](https://sre.google/sre-book/managing-incidents/) — incident response practices.
- [Google SRE Book: Postmortem Culture](https://sre.google/sre-book/postmortem-culture/) — learning from incidents.
- [AWS Well-Architected Framework: Operational Excellence](https://docs.aws.amazon.com/wellarchitected/latest/operational-excellence-pillar/welcome.html) — cloud operational practices.
- [AWS Well-Architected Framework: Reliability](https://docs.aws.amazon.com/wellarchitected/latest/reliability-pillar/welcome.html) — recovery and reliability practices.
- [Google Cloud: Workload Disruption Readiness](https://docs.cloud.google.com/kubernetes-engine/docs/how-to/workload-disruption-readiness) — GKE and Kubernetes disruption readiness.

### Interview-topic pattern evidence only

- [Baeldung: Spring Boot Interview Questions](https://www.baeldung.com/spring-boot-interview-questions) — recurring Spring Boot operations topic patterns; not used as answer text.
- [Baeldung: Java Interview Questions](https://www.baeldung.com/java-interview-questions) — recurring Java interview topic patterns; not used as answer text.
