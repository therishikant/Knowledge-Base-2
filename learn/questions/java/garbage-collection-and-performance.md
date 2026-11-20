# Java Garbage Collection and Performance — Interview Question Bank

[Back to index](index.md)

This bank focuses on how the JVM reclaims memory, the trade-offs among collectors, and a disciplined approach to measuring and fixing performance problems. It reflects recurring senior interview formats: explain a mechanism, compare options, interpret symptoms/logs, and work through a production incident. Collector behavior and flags vary by JDK and vendor; check the documentation for the exact runtime you deploy.

## Questions and answers

### 1. What does garbage collection do, and what does it not do?
**Answer:** Garbage collection (GC) automatically reclaims heap memory occupied by objects that the running program can no longer reach. It reduces manual-memory-management errors, but it does not prevent unbounded retention, guarantee when an object is reclaimed, or manage every kind of resource. Files, sockets, database connections, and native resources still need deterministic cleanup, usually with `try`-with-resources.

### 2. How does a tracing collector decide whether an object is live?
**Answer:** The collector starts from roots such as active thread stacks, static references, and JVM-maintained references, then follows references reachable from those roots. An object not reachable from the roots can be reclaimed. Reachability is not the same as “will the business logic use this again”: a cache can retain an object indefinitely even if the application has no practical use for it.

### 3. What is the generational hypothesis?
**Answer:** Many workloads create numerous short-lived objects and fewer long-lived ones. A generational collector tries to reclaim young objects often and scan older objects less frequently. Objects that survive collections may be promoted. This can reduce work, but long-lived objects and references from old regions into young ones require additional tracking.

### 4. Explain the main GC performance measures.
**Answer:** **Throughput** is the share of elapsed time available to application work rather than GC. **Latency** is the delay seen by an operation or user, including pauses. **Footprint** is memory consumption. These goals compete: a collector may use more CPU or memory to reduce pauses, while a throughput-oriented setup may accept longer pauses. State the service-level objective before choosing a collector.

### 5. What is a stop-the-world pause?
**Answer:** It is a period when application threads are stopped so the JVM can perform an operation that needs a consistent view of the heap. “Concurrent” collectors do much work alongside the application, but still have short stop-the-world phases. A concurrent collector does not mean “no pauses.”

### 6. What are safepoints, and why can they matter?
**Answer:** A safepoint is a JVM state where threads have reached known locations and the runtime can safely perform certain operations. GC commonly uses safepoints, but reaching one can take time if a thread is slow to cooperate. When a pause appears long, distinguish time spent reaching the safepoint from time spent doing GC work; unified GC logs and JFR can help.

### 7. Compare Serial, Parallel, G1, and ZGC at a high level.
**Answer:** Serial uses one GC thread and suits small heaps or constrained environments. Parallel (throughput) GC uses multiple threads and aims for high throughput, accepting pauses. G1 divides the heap into regions and balances pause-time goals with throughput; it is the general-purpose default in many modern HotSpot configurations. ZGC is designed for very low pause times and large heaps, doing much work concurrently at additional CPU and implementation cost. Measure on the target JDK and workload instead of choosing by reputation.

### 8. How does G1 work, and what does its pause-time goal mean?
**Answer:** G1 divides the heap into regions, tracks remembered sets for cross-region references, and collects selected regions. It performs young collections and concurrent marking, then can reclaim regions with much garbage. A pause-time goal such as `-XX:MaxGCPauseMillis` is a target used by the collector’s heuristics, not a hard real-time guarantee. If the goal is too aggressive, throughput may fall or the collector may fail to meet it.

### 9. What is a remembered set?
**Answer:** It records references into a region from outside that region. When collecting only selected regions, the collector needs to find incoming references without scanning the entire heap. Remembered-set maintenance and its associated barriers consume CPU and memory, so a region-based collector trades this bookkeeping for more selective collection.

### 10. What is an allocation failure, and does it mean the heap is exhausted?
**Answer:** It means an allocation could not be satisfied by the expected fast allocation path or available space. The JVM may trigger collection or use a slower path; it is not automatically equivalent to `OutOfMemoryError`. Repeated allocation failures, evacuation failures, or full collections in logs can signal that allocation rate, live data, or collection progress is out of balance.

### 11. What is an evacuation failure in G1?
**Answer:** During evacuation, G1 copies live objects out of regions it wants to reclaim. If it cannot reserve enough destination space, evacuation can fail and the collector may need a more expensive recovery or full collection. Look at the log context, heap occupancy, allocation rate, and humongous-object activity. Increasing the heap may help only if the container has room and retained live data is not the underlying cause.

### 12. What are humongous objects in G1, and why can they be costly?
**Answer:** In G1, an object larger than a region-size threshold (roughly half a region) is treated as humongous and occupies one or more contiguous regions. Such allocations can create fragmentation pressure and trigger collection earlier than expected. Large byte arrays or buffers are common examples. Avoid repeated large temporary allocations where possible, and verify the diagnosis with GC logs before changing region or heap settings.

### 13. What are the main differences between young and mixed collections in G1?
**Answer:** A young collection evacuates young regions. A mixed collection also includes selected old regions after concurrent marking has identified reclaimable space. G1 uses these choices to manage both short-lived allocation and old-generation occupancy. The exact collection sequence is adaptive rather than a fixed schedule controlled by application code.

### 14. How does ZGC reduce pause times?
**Answer:** ZGC performs most work concurrently and uses load/store barriers to maintain correctness as objects move or references change. Its pauses are designed to remain short across large heaps, but application threads still pay barrier costs and the collector needs CPU to keep up. Compare allocation rate, CPU headroom, memory footprint, and tail latency under realistic load.

### 15. What is a GC barrier?
**Answer:** A barrier is runtime-generated work around a memory operation, such as reading or writing a reference. Collectors use barriers to track references or coordinate concurrent relocation and marking. They are usually invisible to application source but can add CPU cost. Avoid hand-optimizing around a presumed barrier cost without profiling.

### 16. What is a full GC, and why is it often a warning sign?
**Answer:** A full GC is a major collection that typically examines or compacts a much larger part of the heap and may stop application threads for longer. Repeated full GCs can indicate excessive live data, a high allocation rate, insufficient collection time, fragmentation, or unsuitable sizing. One event during startup or an explicit diagnostic action is not by itself proof of a production problem.

### 17. How do you read unified GC logs in Java 17 or 21?
**Answer:** Start by enabling unified logging, for example `-Xlog:gc*:file=gc.log:time,uptime,level,tags`. Correlate timestamps with request latency and CPU. Examine collection type, pause duration, heap before/after, allocation and promotion behavior, concurrent-cycle timing, and any evacuation or allocation failures. Use rotation/retention limits in production so diagnostics do not fill disks.

### 18. Which GC-related JVM options are useful, and what should you avoid?
**Answer:** Common starting points include `-Xms`/`-Xmx` for heap bounds, `-Xlog:gc*` for diagnostics, and collector selection such as `-XX:+UseG1GC` or `-XX:+UseZGC` when justified. Some collectors expose options that change between releases. Avoid copying old tuning recipes, setting many interacting flags at once, or assuming a pause target is a guarantee. Change one hypothesis at a time and measure.

### 19. How would you tune a service with a throughput objective?
**Answer:** Establish a stable workload and baseline throughput, latency, CPU, and allocation rate. Inspect whether GC actually consumes enough time to limit throughput. For a throughput-oriented service, a parallel collector may be appropriate if longer pauses are acceptable. Confirm the heap leaves OS/container headroom, then test one change at a time and compare the same load and warm-up conditions.

### 20. How would you tune a latency-sensitive service?
**Answer:** Define the tail-latency target and measure pauses alongside application and scheduler delays. Reduce allocation bursts and retained live data first; then test a collector suited to latency goals, such as G1 or ZGC, under production-like load. Ensure enough CPU and memory for concurrent work, and check p99/p999 rather than just average pause time. A GC change cannot fix lock contention or slow downstream services.

### 21. What is allocation rate, and why is it useful?
**Answer:** Allocation rate is the volume of new objects created per unit time. A high rate can cause frequent young collections even when objects die quickly. JFR allocation events, profilers, and GC logs help identify hot allocation sites. Reducing unnecessary temporary objects can help, but reuse pools can create contention or retain memory, so compare actual results.

### 22. Does calling `System.gc()` force a collection?
**Answer:** No. It requests that the JVM consider performing GC; the request may be ignored or treated differently depending on options and runtime. It is not a reliable memory-management strategy and can cause an unwanted pause. Use explicit resource management and diagnose why memory is retained instead.

### 23. What are strong, soft, weak, and phantom references?
**Answer:** A strong reference keeps an object reachable. A soft reference may be cleared under memory pressure, though it is not a predictable cache policy. A weak reference does not keep an otherwise unreachable object alive and is useful for certain association patterns. A phantom reference supports post-mortem cleanup coordination through a reference queue; it does not allow retrieving the object. For resources, prefer `AutoCloseable` over finalization or reachability tricks.

### 24. Why should finalizers not be used for cleanup?
**Answer:** Finalization timing is unpredictable, can delay reclamation, and can create security and reliability problems through object resurrection. Finalization is deprecated for removal. Use `try`-with-resources for deterministic cleanup; use `Cleaner` only for carefully designed fallback cleanup, not as the primary lifecycle mechanism.

### 25. How can you distinguish a memory leak from a high but temporary allocation rate?
**Answer:** Compare heap usage after comparable collections over time. A leak often shows a rising post-GC live set and retaining paths from roots; allocation pressure can produce frequent collections while post-GC occupancy stays broadly stable. Capture heap dumps or JFR recordings when safe, compare dominators/retained sizes, and tie findings to workload and deployment changes.

### 26. What is a heap dump useful for, and what can it not tell you?
**Answer:** It is a snapshot of heap objects and references that helps identify large retained graphs, duplicate data, and roots keeping objects alive. It does not show the full history of allocations, and the snapshot can be large, sensitive, and disruptive to capture. Protect it like production data, capture with operational safeguards, and interpret it alongside time-series metrics or allocation profiles.

### 27. What is the difference between Java heap and native memory?
**Answer:** Java objects normally live in the managed heap. The process also uses native memory for thread stacks, metaspace, JIT code, direct buffers, GC structures, libraries, and other runtime data. A process can exceed its container limit even when heap usage is modest. Use Native Memory Tracking where enabled, OS/container metrics, thread counts, and direct-buffer metrics to investigate the non-heap portion.

### 28. What is metaspace, and what can cause `OutOfMemoryError: Metaspace`?
**Answer:** Metaspace stores class metadata in native memory. A leak of class loaders, repeated dynamic class generation, or an unsuitable limit can cause growth. Find which loaders/classes are retained and why; simply increasing `MaxMetaspaceSize` may postpone rather than solve the problem. Class unloading depends on class-loader reachability and collector/runtime behavior.

### 29. How do container memory limits affect JVM sizing?
**Answer:** The JVM may use container-aware ergonomics, but heap is only part of the process footprint. Leave space for native memory, stacks, code cache, and the operating system. Verify the actual JVM’s detected limits and effective flags, and monitor the container’s working set and OOM-kill events. A heap sized to the entire container limit is unsafe.

### 30. How can JFR help with GC and performance diagnosis?
**Answer:** Java Flight Recorder can capture low-overhead runtime events such as GC pauses, allocation samples, thread activity, locks, and CPU usage. It can connect a pause or allocation spike to application behavior. Choose recording settings and duration appropriate to the environment, protect recordings, and use JFR together with logs and service-level metrics.

### 31. A service has high p99 latency but GC pauses are short. What do you investigate next?
**Answer:** Do not keep tuning GC without evidence. Correlate slow requests with CPU saturation, lock contention, thread scheduling, blocking I/O, queue buildup, JIT warm-up, allocation stalls, and downstream latency. JFR, distributed tracing, thread dumps, and per-stage request timings can isolate the cause. Short pauses do not prove GC has zero impact, but they make other causes important to test.

### 32. Case study: after a release, old-generation occupancy rises and full GCs become frequent. What is your response?
**Answer:** First check the release diff, workload mix, heap/container limits, and GC logs. Compare post-collection live set and allocation/promotions with the previous version. Use a heap dump to find newly retained objects and their root paths; use allocation profiling if the issue is churn rather than retention. Roll back or mitigate if service objectives are at risk, then fix the owning cache, lifecycle, or data-volume behavior. Do not start by blindly raising `-Xmx`.

### 33. Case study: G1 misses a strict latency target during traffic bursts. How do you proceed?
**Answer:** Confirm whether the outliers coincide with pauses, allocation bursts, evacuation failures, or unrelated work. Inspect pause phases, heap occupancy, humongous allocations, CPU headroom, and concurrent marking progress. Consider reducing burst allocations or live-set size, adjusting capacity, and benchmarking a latency-oriented collector such as ZGC. Validate tail latency and throughput under the same burst profile; do not treat `MaxGCPauseMillis` as an SLA.

### 34. Case study: a heap dump shows a large in-memory cache, but the service has no `OutOfMemoryError`. Is this a GC problem?
**Answer:** Not necessarily. The cache may be intentionally retaining strongly reachable entries, so the collector is behaving correctly. Check cache bounds, expiration, key cardinality, and retained size against the service’s memory budget. Fix the ownership or eviction policy if the cache is unbounded; changing collectors cannot reclaim reachable cache entries.

## Sources

- [Oracle Java 17 GC Tuning Guide: GC implementation](https://docs.oracle.com/en/java/javase/17/gctuning/garbage-collector-implementation.html)
- [Oracle Java 17 GC Tuning Guide: G1 tuning](https://docs.oracle.com/en/java/javase/17/gctuning/garbage-first-garbage-collector-tuning.html)
- [Oracle Java 21 GC Tuning Guide](https://docs.oracle.com/en/java/javase/21/gctuning/)
- [OpenJDK JEP 333: ZGC: A Scalable Low-Latency Garbage Collector](https://openjdk.org/jeps/333)
- [OpenJDK JEP 439: Generational ZGC](https://openjdk.org/jeps/439)
- [Oracle Java Flight Recorder documentation](https://docs.oracle.com/en/java/javase/21/jfapi/)
