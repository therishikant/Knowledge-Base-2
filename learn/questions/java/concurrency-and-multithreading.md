# Java Interview Questions — Concurrency and Multithreading

[Back to index](index.md)

**Target:** Senior Java developer / tech lead  
**Language:** Simple English; technical terms are explained in the answers.  
**Version focus:** Java 8 fundamentals, Java 17/21 interview practice, with Java 25 changes called out.  
**Format:** Each question is followed immediately by an answer.  

These are original practice questions based on recurring topics in public Java interview guides and checked against Java specifications and API documentation. They are not a measured ranking of how often individual questions are asked.

## 1. Concurrency foundations

### Q1. What is concurrency?

**Answer:** Concurrency means a program can make progress on more than one task during the same period. The tasks may take turns on one processor or run at the same time on multiple processors.

### Q2. What is the difference between concurrency and parallelism?

**Answer:** Concurrency is about handling multiple tasks in overlapping time. Parallelism means actually executing multiple tasks at the same time, usually on different processor cores. A program can be concurrent without being parallel.

### Q3. What is a process, and how is it different from a thread?

**Answer:** A process is a running program with its own operating-system resources and memory space. A thread is one path of execution inside a process. Threads in the same process share heap objects, so communication is easier but shared state must be protected.

### Q4. What is the difference between a task and a thread?

**Answer:** A task is work to be done; a thread is one way to run that work. Separating them lets an executor choose when and on which thread a task runs.

### Q5. Why use multiple threads?

**Answer:** Threads can improve responsiveness, overlap waiting with useful work, and use multiple CPU cores for independent CPU work. They also add costs: coordination, scheduling, memory use, and harder-to-reproduce bugs.

### Q6. What is shared mutable state?

**Answer:** It is data that more than one thread can access and at least one thread can change. It is the main source of race conditions. Prefer immutable data or ownership by one thread when possible.

### Q7. What is a race condition?

**Answer:** A race condition occurs when the correctness of a result depends on the timing or order of concurrent operations. For example, two threads may both read a balance before either writes its update.

### Q8. What is a data race in Java?

**Answer:** A data race is a conflicting access to the same variable by multiple threads, where at least one access is a write and the accesses are not ordered by a happens-before relationship. A data race is a strong warning that the program's behavior is not properly synchronized.

### Q9. Is every race condition a data race?

**Answer:** No. A race condition is a broader correctness problem. A program can have a race condition even when each individual variable access is synchronized—for example, when two separately thread-safe operations must happen together as one transaction.

### Q10. What do atomicity, visibility, and ordering mean?

**Answer:** **Atomicity** means an operation appears indivisible. **Visibility** means one thread can see another thread's update. **Ordering** means operations occur in an order that satisfies the Java Memory Model. Correct concurrent code must address the properties it needs.

### Q11. What is thread safety?

**Answer:** A component is thread-safe if it continues to behave correctly when used by multiple threads at the same time, according to its documented contract. That may be achieved with immutability, confinement, locks, atomics, or concurrent data structures.

### Q12. What are safety and liveness?

**Answer:** **Safety** means nothing incorrect happens, such as a corrupted counter. **Liveness** means the program can continue making progress, rather than being stuck forever in deadlock or waiting.

## 2. Threads, lifecycle, and interruption

### Q13. What is the difference between calling `start()` and calling `run()` on a `Thread`?

**Answer:** `start()` asks the JVM to schedule a new thread, which then invokes `run()`. Calling `run()` directly is an ordinary method call on the current thread; it does not start a new thread. A `Thread` can be started only once.

### Q14. What are the Java thread states?

**Answer:** The states are `NEW`, `RUNNABLE`, `BLOCKED`, `WAITING`, `TIMED_WAITING`, and `TERMINATED`. `RUNNABLE` includes a thread that is running and one that is ready to run; Java does not expose a separate “running” state.

### Q15. What is the difference between `BLOCKED`, `WAITING`, and `TIMED_WAITING`?

**Answer:** `BLOCKED` means waiting to acquire a monitor lock. `WAITING` means waiting without a time limit, such as in `wait()` or `join()`. `TIMED_WAITING` means waiting with a time limit, such as in `sleep`, timed `wait`, or timed `join`.

### Q16. Does `Thread.sleep()` release a monitor lock?

**Answer:** No. Sleeping pauses the current thread but keeps any monitors it already owns. `Object.wait()` releases the monitor for the object on which it is called while the thread waits.

### Q17. What does `Thread.join()` do?

**Answer:** `join()` makes the current thread wait for another thread to finish. When `join()` returns normally because the target thread terminated, the target thread's actions happen-before the successful return.

### Q18. What is a daemon thread?

**Answer:** A daemon thread is background work that does not keep the JVM alive by itself. When only daemon threads remain, the JVM may exit. Do not use daemon status as a substitute for orderly shutdown or cleanup.

### Q19. What is interruption in Java?

**Answer:** Interruption is a cooperative request that a thread should stop waiting or change what it is doing. It is not a forced kill. The code receiving the request must decide how to respond, often by stopping work or propagating the interruption.

### Q20. What happens to the interrupt flag when `InterruptedException` is thrown?

**Answer:** Methods such as `sleep`, `wait`, and `join` clear the interrupt status when they throw `InterruptedException`. If the current method cannot propagate the exception, it commonly restores the status with `Thread.currentThread().interrupt()` and then exits or handles cancellation.

### Q21. What is the difference between `Thread.interrupted()` and `isInterrupted()`?

**Answer:** `Thread.interrupted()` checks and clears the current thread's interrupt status. `isInterrupted()` checks a thread's status without clearing it.

### Q22. How should a long-running loop respond to interruption?

**Answer:** It should periodically check the interrupt status or use interruptible operations. When interruption means cancellation, stop promptly and release resources. Do not silently swallow `InterruptedException`.

### Q23. Why is `Thread.stop()` unsafe?

**Answer:** It can stop a thread at an arbitrary point while the thread holds locks or is updating shared state. Other threads may then observe corrupted or inconsistent data. Use cooperative cancellation, usually interruption, instead.

## 3. Java Memory Model and happens-before

### Q24. What is the Java Memory Model (JMM)?

**Answer:** The JMM is the set of rules that describes how threads may observe memory actions. It lets the JVM and processor optimize code while defining what correctly synchronized programs must observe. It is not simply a description of a physical “main memory” and “thread-local cache.”

### Q25. What does “happens-before” mean?

**Answer:** If action A happens-before action B, the effects of A are guaranteed to be visible to B, and the ordering rules between them must be respected. It is a reasoning rule, not necessarily a statement that A physically ran earlier in wall-clock time.

### Q26. Name common happens-before relationships.

**Answer:** Program order within one thread; monitor unlock before a later lock of the same monitor; a volatile write before a later read of that field; `Thread.start()` before actions in the started thread; and a thread's actions before another thread successfully returns from `join()`.

### Q27. How does `synchronized` provide visibility?

**Answer:** Releasing a monitor happens-before a later acquisition of that same monitor. If one thread updates data inside a synchronized region and releases the lock, a later thread acquiring that lock can see those updates.

### Q28. How does `volatile` provide visibility?

**Answer:** A write to a volatile field happens-before a later read of that same field. This makes the written value visible and also provides ordering guarantees around volatile accesses.

### Q29. What is safe publication?

**Answer:** Safe publication means making an object available to other threads in a way that ensures they see its correctly initialized state. Examples include storing the reference in a properly synchronized field, a volatile field, or a concurrent collection, or publishing an immutable object through a safely initialized `final` reference.

### Q30. What can go wrong if an object escapes from its constructor?

**Answer:** Another thread may obtain the reference before construction has finished and observe fields before they have their intended values. Avoid publishing `this` from a constructor, including by starting a thread or registering a callback that can use the object before construction completes.

### Q31. What guarantees do `final` fields provide?

**Answer:** If an object is constructed correctly and `this` does not escape during construction, the JMM gives special visibility guarantees for its `final` fields after construction. `final` does not make referenced mutable objects immutable, and it does not replace safe publication for all other state.

### Q32. Can instruction reordering cause surprising results?

**Answer:** Yes. The compiler or processor may reorder operations when the resulting behavior is allowed by the JMM. Without a happens-before relationship, another thread may observe effects in an unexpected order. Correct synchronization removes reliance on a particular machine's behavior.

### Q33. Does `volatile` make an entire object thread-safe?

**Answer:** No. A volatile reference makes reads and writes of that reference visible and ordered. It does not automatically make mutations to the referenced object's fields safe.

### Q34. How would you safely stop a worker using a flag?

**Answer:** Use a volatile flag when one thread writes it and another reads it, and each read/write is a simple independent action. For example:

```java
private volatile boolean running = true;

void stopWorker() {
    running = false;
}

void work() {
    while (running) {
        doOneUnitOfWork();
    }
}
```

If the worker can block for a long time, interruption or a blocking API with a cancellation mechanism may be needed as well.

## 4. `synchronized`, monitors, and `wait`/`notify`

### Q35. What does `synchronized` do?

**Answer:** It acquires an object's intrinsic monitor before entering the protected region and releases it when the region exits, including when an exception is thrown. It provides mutual exclusion for code using the same monitor and establishes visibility through unlock/lock ordering.

### Q36. What does a synchronized instance method lock?

**Answer:** It locks the monitor of the current receiver, `this`. Two threads calling synchronized instance methods on the same object cannot execute those protected method bodies at the same time.

### Q37. What does a static synchronized method lock?

**Answer:** It locks the monitor of the `Class` object for the class that declares the method. It is not the same monitor as an instance method's `this` lock.

### Q38. Do two synchronized methods on different objects block each other?

**Answer:** Not just because they are synchronized. They block each other only if they need the same monitor, or if another part of the program creates a dependency between their locks.

### Q39. What is a synchronized block, and why use one?

**Answer:** A synchronized block locks a specific object for only the statements that need protection. It can reduce the time a lock is held and make the lock identity explicit:

```java
synchronized (lock) {
    updateSharedState();
}
```

Use a private, stable lock object when callers should not be able to acquire the lock themselves.

### Q40. Are Java intrinsic locks reentrant?

**Answer:** Yes. A thread that already owns an object's monitor can acquire that same monitor again. It must release the monitor the same number of times before another thread can acquire it.

### Q41. Is `synchronized` released if the protected code throws?

**Answer:** Yes. The monitor is released when execution leaves the synchronized method or block, whether normally or abruptly.

### Q42. What is the difference between a monitor and a lock?

**Answer:** In Java language discussions, an intrinsic lock (monitor) is the built-in synchronization mechanism associated with every object. `Lock` is also the name of an explicit API interface in `java.util.concurrent.locks`. Both coordinate access, but their APIs and features differ.

### Q43. What does `Object.wait()` do?

**Answer:** The calling thread must own that object's monitor. `wait()` adds the thread to the object's wait set and releases its monitor while waiting. Before it returns or throws, the thread must reacquire the monitor.

### Q44. Why must `wait()` be called in a loop?

**Answer:** A thread can wake up without the condition becoming true, another thread may consume the condition first, or a spurious wake-up may occur. Recheck the condition while holding the same monitor:

```java
synchronized (lock) {
    while (!conditionIsTrue()) {
        lock.wait();
    }
    useAvailableState();
}
```

### Q45. What is the difference between `notify()` and `notifyAll()`?

**Answer:** `notify()` wakes one arbitrary thread from that object's wait set. `notifyAll()` wakes all of them; they then compete to reacquire the monitor and must recheck their conditions. `notifyAll()` is often safer when different conditions share a monitor, though it may wake extra threads.

### Q46. Must `notify()` be called while holding the object's monitor?

**Answer:** Yes. The caller must own the monitor for the object on which it calls `notify()` or `notifyAll()`, just as it must own that monitor to call `wait()`.

### Q47. Does calling `notify()` immediately give the lock to the awakened thread?

**Answer:** No. The notifying thread still owns the monitor until it releases it. The awakened thread can continue only after it reacquires that monitor.

### Q48. What is a lost notification?

**Answer:** It is a coordination bug where a signal happens before a waiting thread begins waiting, and the waiter then waits forever because the signal is not remembered. Guarding a condition and waiting on it under the same monitor—or using a suitable higher-level synchronizer—prevents this pattern.

### Q49. Why are `wait()` and `notify()` considered low-level tools?

**Answer:** The programmer must manage the shared condition, monitor ownership, wake-ups, interruption, and notification discipline correctly. Higher-level tools such as `BlockingQueue`, `CountDownLatch`, and `Condition` are usually clearer.

## 5. `volatile`

### Q50. What does `volatile` guarantee?

**Answer:** Reads and writes of that field have volatile memory semantics: writes become visible to later reads of that same field, with required ordering. A volatile read or write is atomic for that variable.

### Q51. What does `volatile` not guarantee?

**Answer:** It does not make a compound operation atomic. For example, `count++` still consists of a read, an addition, and a write, so concurrent increments can be lost.

### Q52. When is a volatile field a good choice?

**Answer:** It is useful for simple state or status fields where threads independently read or write the field, such as a shutdown flag. If correctness depends on a multi-step update or invariant across several fields, use a lock or another atomic design.

### Q53. `volatile int` or `AtomicInteger` for a shared counter?

**Answer:** Use `AtomicInteger` for independent atomic operations such as `incrementAndGet()`. `volatile int` only makes individual reads and writes visible; it does not make `count++` atomic. For extremely high-contention statistics, consider `LongAdder`.

### Q54. Does `volatile` make `if (flag) { value++; }` atomic?

**Answer:** No. The condition and increment are multiple actions. Another thread can change the state between them. Protect the whole invariant with a lock or design it with suitable atomic operations.

### Q55. Why is `volatile` used in double-checked locking?

**Answer:** In the standard double-checked-locking pattern, the instance reference must be volatile so that a thread cannot observe the reference before the object's initialization writes are visible. The pattern is valid in modern Java, but an initialization-on-demand holder or dependency injection is often simpler.

### Q56. Can a volatile reference point to a mutable object?

**Answer:** Yes, but `volatile` protects the reference assignment and reading, not the object's internal mutable state. The object's state still needs its own synchronization or immutability.

### Q57. Are volatile reads and writes atomic for `long` and `double`?

**Answer:** Yes. Volatile reads and writes are atomic for all types, including `long` and `double`. The historical concern about non-atomic `long`/`double` applies to non-volatile accesses on some older JVM implementations; do not use that as a reason to rely on unsynchronized shared state.

## 6. Explicit locks and `ReentrantLock`

### Q58. What is `ReentrantLock`?

**Answer:** It is an explicit mutual-exclusion lock from `java.util.concurrent.locks`. Like `synchronized`, it is reentrant and provides mutual exclusion and memory-synchronization effects. It adds options such as timed acquisition, interruptible acquisition, fairness policy, and multiple `Condition` objects.

### Q59. What is the most important difference between `synchronized` and `ReentrantLock`?

**Answer:** `synchronized` releases its monitor automatically when its block exits. `ReentrantLock` must be explicitly released, usually in a `finally` block. In return, it offers features such as `tryLock`, `lockInterruptibly`, and `Condition`.

### Q60. How should `ReentrantLock` be used safely?

**Answer:** Always release it in `finally` after a successful acquisition:

```java
lock.lock();
try {
    updateSharedState();
} finally {
    lock.unlock();
}
```

If an exception skips `unlock()`, other threads may remain blocked forever.

### Q61. What is the difference between `lock()` and `lockInterruptibly()`?

**Answer:** `lock()` waits to acquire the lock and does not provide an interruption point while waiting. `lockInterruptibly()` allows a waiting thread to stop waiting by responding to interruption.

### Q62. What does `tryLock()` do?

**Answer:** It attempts to acquire the lock immediately and returns `true` if successful, otherwise `false`. Its timed overload waits up to a limit and can respond to interruption. It is useful when the program has a fallback or retry strategy—not as a reason to spin forever.

### Q63. What is a fair `ReentrantLock`?

**Answer:** A fair lock favors the longest-waiting thread when there is contention. This can reduce starvation risk, but may lower throughput. Fairness is not a guarantee of fair operating-system scheduling, and the untimed `tryLock()` may still acquire a fair lock when it is free.

### Q64. What is a `Condition`?

**Answer:** A `Condition` is a wait/notify-style condition associated with a `Lock`. It lets one lock have multiple waiting conditions, such as “not empty” and “not full.” The thread must hold the associated lock when awaiting or signaling, and it must recheck its condition in a loop.

### Q65. What is a lock downgrade?

**Answer:** With `ReentrantReadWriteLock`, a thread holding the write lock can acquire the read lock and then release the write lock, continuing with a read lock. This is called downgrading. Attempting to upgrade from a read lock to the write lock can block indefinitely if the thread keeps its read lock.

### Q66. Should lock-state methods such as `isLocked()` control program correctness?

**Answer:** Usually not. These methods are intended mainly for monitoring, and the state may change immediately after it is checked. Use the actual acquisition operation to make a synchronization decision.

## 7. Read/write locks and `StampedLock`

### Q67. What is a read-write lock?

**Answer:** It provides a shared read lock and an exclusive write lock. Several readers can hold the read lock together, but a writer needs exclusive access.

### Q68. When can `ReentrantReadWriteLock` improve performance?

**Answer:** It may help when reads are frequent, writes are relatively rare, and read operations take long enough for concurrent readers to matter. It can be slower than a simple mutex for short operations or frequent writes, so measure before adopting it.

### Q69. What is `StampedLock`?

**Answer:** It is a lock with write, read, and optimistic-read modes. Each successful locking operation returns a stamp that must be used to release or validate that operation.

### Q70. What is an optimistic read in `StampedLock`?

**Answer:** It reads without acquiring a normal read lock, then validates the stamp. If a writer changed the state during the read, validation fails and the code must retry under a real read lock. Treat values read before validation as potentially inconsistent.

### Q71. What is the basic optimistic-read pattern?

**Answer:** Read the data under an optimistic stamp, validate the stamp, and if validation fails, repeat the read under a read lock. Do not use the first values unless validation succeeds.

### Q72. Is `StampedLock` reentrant?

**Answer:** No. A thread cannot safely acquire it again as though it were a reentrant lock. Code that calls another method which reacquires the same `StampedLock` can deadlock.

### Q73. Does `StampedLock` track which thread owns a lock?

**Answer:** No. It is stamp-based rather than thread-owned. The code must keep and correctly use the stamp returned by the acquisition operation.

### Q74. What are important `StampedLock` limitations?

**Answer:** It is not reentrant, does not support `Condition`, and optimistic reads can observe inconsistent intermediate state until validated. Stamps are capabilities, not security tokens, and should not be stored indefinitely. Use it only when its pattern is understood and benchmarks show a benefit.

### Q75. `ReentrantReadWriteLock` or `StampedLock`?

**Answer:** Prefer `ReentrantReadWriteLock` when you need familiar reentrant read/write locking or conditions. Consider `StampedLock` for carefully designed read-heavy code where optimistic reads may help. Choose based on correctness and measured performance, not on the assumption that a newer lock is always faster.

## 8. Atomic variables, CAS, and `VarHandle`

### Q76. What is an atomic variable?

**Answer:** It is a variable with operations that act atomically, such as compare-and-set or increment-and-get. Java provides classes such as `AtomicInteger`, `AtomicReference`, and `LongAdder`.

### Q77. What is compare-and-set (CAS)?

**Answer:** CAS checks whether a value still equals an expected value and, only if it does, replaces it with a new value as one atomic operation. It is commonly used to implement lock-free updates.

### Q78. What is a CAS retry loop?

**Answer:** It repeatedly reads the current value, calculates a new value, and attempts compare-and-set. If another thread wins the update first, CAS fails and the loop tries again with the newer value.

### Q79. Does CAS mean an algorithm is lock-free?

**Answer:** Not by itself. An algorithm is lock-free only if system-wide progress is guaranteed—some thread can complete in a finite number of steps. A CAS loop can repeatedly lose under contention, and a badly designed algorithm can still block elsewhere.

### Q80. What is the ABA problem?

**Answer:** A thread reads value A, another thread changes it from A to B and back to A, and the first thread's CAS sees A and assumes nothing changed. Version stamps, sequence numbers, or an algorithm that does not depend on detecting intermediate changes can address ABA.

### Q81. `AtomicLong` or `LongAdder` for a highly contended counter?

**Answer:** `AtomicLong` provides an exact atomic value for operations such as compare-and-set. `LongAdder` spreads updates across cells to reduce contention and is often better for statistics, but its sum is not an atomic snapshot during concurrent updates.

### Q82. What is `AtomicReference` useful for?

**Answer:** It atomically updates or swaps a reference. It can support immutable-state replacement, such as repeatedly calculating a new state and publishing it with compare-and-set.

### Q83. What is `VarHandle`?

**Answer:** `VarHandle`, added in Java 9, provides typed access to fields and array elements with different memory-ordering modes and atomic operations. It is a lower-level tool than atomic classes; use it when the access modes are needed and well understood.

## 9. Designing thread-safe code

### Q84. What is immutability, and why does it help concurrency?

**Answer:** An immutable object cannot change after construction. Threads can share it without coordinating updates, as long as it is safely published and its reachable state is also effectively immutable.

### Q85. What is thread confinement?

**Answer:** Thread confinement means mutable state is used by only one thread. Since no other thread can access it at the same time, it may not need locking. Examples include method-local state and objects owned by a single-threaded executor.

### Q86. What is the difference between stateless and stateful services for concurrency?

**Answer:** A stateless service keeps no request-specific mutable state in shared fields, so concurrent calls are easier to make safe. A stateful service needs a clear strategy for isolating or protecting each piece of shared state.

### Q87. What is `ThreadLocal`?

**Answer:** It gives each thread its own value for a variable. It can isolate per-thread context, but thread pools reuse threads, so values must be removed or reset after a task to avoid leaking data between requests.

### Q88. Why can `ThreadLocal` be risky with virtual threads?

**Answer:** Virtual threads support thread-local variables, but an application can create very many virtual threads. Large thread-local values can therefore consume substantial memory. Use thread locals only when their per-thread storage is appropriate.

### Q89. What are scoped values?

**Answer:** Scoped values provide a way to share immutable context with callees and, in supported contexts, child threads, within a bounded scope. They became a final API in Java 25. They do not automatically replace every `ThreadLocal` use case.

### Q90. What does “minimize shared mutable state” mean in practice?

**Answer:** Prefer local variables, immutable values, ownership, message passing, and concurrent collections. When mutable state must be shared, give it one clear synchronization policy and protect the whole invariant—not just individual fields.

## 10. Executors and thread pools

### Q91. What is the `Executor` abstraction?

**Answer:** `Executor` separates submitting a task from deciding how it runs. The executor may run it in a new thread, a pool thread, or even the calling thread, depending on the implementation.

### Q92. What does `ExecutorService` add?

**Answer:** It can submit `Runnable` and `Callable` tasks, return `Future` handles, execute groups of tasks, and manage shutdown and termination.

### Q93. What is the difference between `Runnable` and `Callable`?

**Answer:** `Runnable.run()` returns no result and cannot declare a checked exception. `Callable.call()` returns a result and can throw an exception.

### Q94. Why use a thread pool?

**Answer:** A pool reuses worker threads and controls how much work runs concurrently. It avoids repeatedly creating threads and can limit resource use, but its queue and rejection policy must be chosen carefully.

### Q95. What are the main parts of `ThreadPoolExecutor`?

**Answer:** Core and maximum pool sizes, keep-alive time, a work queue, a thread factory, and a rejection handler. Together they control worker creation, task buffering, idle-worker removal, thread setup, and overload behavior.

### Q96. How does `ThreadPoolExecutor` decide whether to create a worker or queue a task?

**Answer:** It creates workers up to the core size first. After that it generally queues tasks; if the queue cannot accept a task, it may create workers up to the maximum size. If that also fails, it invokes the rejection handler.

### Q97. Why can an unbounded queue make `maximumPoolSize` ineffective?

**Answer:** Once the core workers are busy, an unbounded queue can keep accepting tasks, so the executor has no reason to grow toward the maximum size. The queue can also grow until memory or latency becomes a problem.

### Q98. Why is a bounded queue useful?

**Answer:** It limits queued work and makes overload visible. Once full, the configured rejection policy can apply backpressure, fail fast, or run the task in the caller, depending on the chosen policy and requirements.

### Q99. What is a rejection policy?

**Answer:** It defines what happens when the executor cannot accept a task. Common policies include throwing `RejectedExecutionException`, discarding a task, discarding an older task, or running the task in the submitting thread. Silent discard is unsafe when callers assume every task will complete.

### Q100. How do you size a pool for CPU-bound work?

**Answer:** Start near the number of available processor cores, then benchmark the real workload. More threads than cores may increase context switching instead of throughput.

### Q101. How do you size a pool for blocking I/O work?

**Answer:** It may need more concurrent workers than CPU cores because workers spend time waiting. Estimate from the workload's waiting-to-computing ratio, resource limits, and latency targets, then measure. Do not blindly apply a formula.

### Q102. What is the difference between `shutdown()` and `shutdownNow()`?

**Answer:** `shutdown()` rejects new tasks but lets already submitted tasks finish. `shutdownNow()` attempts to interrupt active tasks and returns tasks that were waiting to start; interruption is cooperative, so it does not guarantee immediate termination.

### Q103. What is the correct executor shutdown pattern?

**Answer:** Stop accepting work with `shutdown()`, wait for a bounded time with `awaitTermination()`, then use `shutdownNow()` if needed. If the waiting thread is interrupted, preserve or propagate that interruption and continue cleanup deliberately.

### Q104. What happens if a task submitted with `submit()` throws?

**Answer:** The exception is captured by the returned `Future`. It is usually observed when `Future.get()` throws `ExecutionException`. If the future is ignored, the failure may go unnoticed.

### Q105. What is a common thread-pool deadlock?

**Answer:** A task occupies a worker, submits another task to the same small pool, and blocks waiting for the child task. If every worker does this, no worker is free to run the queued children.

### Q106. How can you prevent executor overload?

**Answer:** Bound concurrency and queues, set deadlines, reject or shed work intentionally, use backpressure, and monitor queue depth, task duration, active workers, and rejection counts. A larger queue alone often increases latency rather than solving overload.

## 11. `Future` and `CompletableFuture`

### Q107. What does a `Future` represent?

**Answer:** It represents a result that may become available later. It can be queried, waited on, or cancelled, but it does not provide a fluent way to compose dependent work.

### Q108. What is the difference between `get()` and `join()` on a `CompletableFuture`?

**Answer:** Both can wait for completion. `get()` declares checked exceptions such as `InterruptedException` and `ExecutionException`; `join()` reports exceptional completion using unchecked `CompletionException`.

### Q109. What is `CompletableFuture`?

**Answer:** It is both a `Future` and a `CompletionStage`. It allows dependent tasks to be composed into a pipeline, including transformations, combinations, and error handling.

### Q110. What is the difference between `thenApply()` and `thenCompose()`?

**Answer:** `thenApply()` transforms a result into another value. `thenCompose()` transforms a result into another asynchronous stage and flattens it, avoiding a nested `CompletableFuture<CompletableFuture<T>>`.

### Q111. What is the difference between `thenCombine()` and `thenCompose()`?

**Answer:** `thenCombine()` joins results from two independent stages after both complete. `thenCompose()` starts a later asynchronous operation whose input depends on the earlier result.

### Q112. What is the difference between `thenApply()` and `thenApplyAsync()`?

**Answer:** A non-async continuation may run on the thread that completes the previous stage or another thread that calls it. An async continuation uses an executor; if none is supplied, it generally uses the common `ForkJoinPool`. Supply an executor when you need explicit resource or thread control.

### Q113. Why should blocking work be handled carefully in `CompletableFuture` pipelines?

**Answer:** Blocking a worker can consume capacity needed by other stages, especially when using the common pool. Use an appropriate executor for blocking operations, or use virtual threads for suitable blocking workloads.

### Q114. How do `exceptionally()`, `handle()`, and `whenComplete()` differ?

**Answer:** `exceptionally()` can recover from a failure by producing a replacement result. `handle()` receives either the result or failure and can transform either case. `whenComplete()` is mainly for observing completion; it normally preserves the existing result or failure unless its action itself fails.

### Q115. What does `CompletableFuture.allOf()` return?

**Answer:** It returns a `CompletableFuture<Void>` that completes when all supplied futures complete. It does not collect their individual results; callers must read those results separately after completion.

### Q116. Does cancelling a `CompletableFuture` always stop the underlying work?

**Answer:** No. Cancellation marks the future as cancelled, but it may not interrupt or stop the computation that was already started. Design cancellation explicitly, often with interruption, deadlines, or a task-specific cancellation mechanism.

### Q117. How should an asynchronous service call handle timeouts and failures?

**Answer:** Set a deadline that matches the request budget, handle both timeout and exceptional completion, and cancel or stop unnecessary work where possible. Do not turn every failure into a successful-looking default without recording or exposing the failure.

## 12. Concurrent collections

### Q118. Is `HashMap` safe for concurrent writes?

**Answer:** No. Concurrent mutation without external synchronization can produce incorrect behavior. Use a concurrent map or protect the map and its related invariants with a lock.

### Q119. Is `Collections.synchronizedMap()` enough for safe iteration?

**Answer:** Not by itself. The caller must synchronize on the returned map while iterating, as required by its documentation. A concurrent map may be a better fit when updates and iteration happen concurrently.

### Q120. What is `ConcurrentHashMap`?

**Answer:** It is a thread-safe map designed for concurrent access. Reads and updates can proceed concurrently under its documented guarantees; it does not lock the entire map for every operation.

### Q121. Does `ConcurrentHashMap` allow null keys or values?

**Answer:** No. Rejecting null avoids ambiguity between “no mapping” and a mapping whose value is null during concurrent lookups and computations.

### Q122. Are `ConcurrentHashMap` operations such as `computeIfAbsent()` atomic?

**Answer:** The mapping operation is atomic according to the method's contract for a key. Keep the mapping function short and avoid recursively updating the same map from that function, because it can block other updates or fail.

### Q123. What does weakly consistent iteration mean?

**Answer:** An iterator over a concurrent collection can proceed while updates happen and may reflect some updates without throwing `ConcurrentModificationException`. It is not necessarily a snapshot of the collection at one exact instant.

### Q124. When is `CopyOnWriteArrayList` useful?

**Answer:** It can work well for a small collection that is read or iterated often and changed rarely. Each write copies the backing array, so it is usually a poor choice for frequent updates or large lists.

### Q125. What is a `BlockingQueue`?

**Answer:** It is a thread-safe queue with operations that can wait for an item to arrive or for space to become available. It is a common way to pass work between producer and consumer threads.

### Q126. Why might you choose a bounded `BlockingQueue`?

**Answer:** A bounded queue limits buffered work and can make producers slow down or fail when consumers cannot keep up. This is a form of backpressure and helps protect memory.

### Q127. What is the difference between `ArrayBlockingQueue` and `LinkedBlockingQueue`?

**Answer:** `ArrayBlockingQueue` uses a fixed-size array and is bounded. `LinkedBlockingQueue` uses linked nodes and can be bounded or effectively unbounded depending on construction. Choose based on memory and backpressure needs, then measure.

## 13. Coordination utilities

### Q128. What is `CountDownLatch`?

**Answer:** It lets one or more threads wait until a count reaches zero. It is one-shot: once it reaches zero, it cannot be reset. It is useful for “wait until these tasks finish” or “start after setup is ready.”

### Q129. What is `CyclicBarrier`?

**Answer:** It lets a fixed group of threads wait for each other at a point, then continue together. It can be reused for later phases. If a party fails or times out, the barrier can become broken.

### Q130. What is the difference between `CountDownLatch` and `CyclicBarrier`?

**Answer:** A latch is usually one-way: workers count down and another thread waits for zero. A barrier is a rendezvous: a group of participating threads all wait for the same phase to complete, and the barrier can be reused.

### Q131. What is a `Semaphore`?

**Answer:** It controls access to a limited number of permits. A thread acquires a permit before using a limited resource and releases it afterward. Unlike a lock, a semaphore does not require the releasing thread to be the thread that acquired the permit.

### Q132. When would you use a semaphore instead of a lock?

**Answer:** Use a semaphore to limit how many operations may use a resource at once, such as allowing at most ten concurrent calls to a service. Use a lock to protect a critical section that must have only one owner.

### Q133. What is a `Phaser`?

**Answer:** It coordinates tasks over multiple phases and allows the number of registered parties to change. It is more flexible than a fixed-party barrier or one-shot latch, but that flexibility adds complexity.

### Q134. What is an `Exchanger`?

**Answer:** It lets two threads meet and exchange values. It is useful for paired producer/consumer designs, but less common than queues or other coordination utilities.

### Q135. When should you choose a high-level synchronizer over `wait()`/`notify()`?

**Answer:** Choose a high-level synchronizer when its contract matches the problem: latch for completion, barrier for phase rendezvous, semaphore for permits, or blocking queue for work transfer. It communicates intent and reduces manual coordination errors.

## 14. Deadlock, livelock, starvation, and contention

### Q136. What is deadlock?

**Answer:** Deadlock occurs when threads wait forever for resources held by one another, so none can make progress.

### Q137. What are the four classic conditions for deadlock?

**Answer:** Mutual exclusion, hold-and-wait, no forced preemption, and circular wait. Breaking at least one condition can prevent the corresponding deadlock pattern.

### Q138. How can lock ordering prevent deadlock?

**Answer:** Define one global order for acquiring locks and always acquire multiple locks in that order. This removes circular wait if every code path follows the same order.

### Q139. Can `tryLock()` prevent deadlock?

**Answer:** It can help a design avoid waiting forever if failed acquisition causes the thread to release held locks and retry or take another path. Simply using `tryLock()` without a correct retry policy can create livelock.

### Q140. What is livelock?

**Answer:** Threads remain active and keep reacting to one another, but no useful work completes. For example, two workers may repeatedly back off and retry in sync. Randomized or increasing backoff can help.

### Q141. What is starvation?

**Answer:** Starvation occurs when a thread is repeatedly denied CPU time or a resource while other threads continue. Unfair scheduling, lock contention, or a high-priority workload can contribute.

### Q142. What is lock contention?

**Answer:** Contention happens when many threads compete for the same lock. It can reduce throughput because threads wait instead of doing useful work. Shorten critical sections, reduce shared state, or partition work where appropriate.

### Q143. How do you investigate a suspected deadlock in production?

**Answer:** Capture thread dumps and look for threads blocked on locks, especially a cycle where each thread owns a lock another needs. Use tools such as `jcmd` or JVM monitoring tools, correlate with application logs, and identify the lock acquisition paths before changing code.

### Q144. Why should blocking I/O usually not happen inside a lock?

**Answer:** The I/O may take an unpredictable amount of time while other threads need the lock, increasing latency and reducing throughput. Keep critical sections small; if consistency requires holding the lock, assess the trade-off explicitly.

## 15. Fork/join, parallel streams, and modern Java

### Q145. What is `ForkJoinPool`?

**Answer:** It is a pool designed for tasks that split into smaller tasks and later combine their results. Workers use work-stealing queues to balance tasks. It is useful for divide-and-conquer CPU work, but blocking operations need care.

### Q146. What is work stealing?

**Answer:** When a worker runs out of local tasks, it can take work from another worker's queue. This helps balance recursive tasks whose sizes are not known in advance.

### Q147. What are common risks of parallel streams?

**Answer:** They may use shared common-pool resources, perform poorly for small or blocking workloads, and produce bugs if lambdas mutate shared state. Use them only when the operation is suitable, side effects are controlled, and performance has been measured.

### Q148. What is a virtual thread?

**Answer:** It is a lightweight Java `Thread` scheduled by the Java runtime rather than being permanently tied to one operating-system thread. Virtual threads became a permanent feature in Java 21.

### Q149. Do virtual threads make code run faster?

**Answer:** No. They aim to support more concurrent tasks and improve throughput for workloads that spend much of their time waiting, especially blocking I/O. They do not make CPU-bound code execute faster or reduce the latency of one unit of CPU work.

### Q150. Should virtual threads be pooled like platform threads?

**Answer:** Usually not. The typical approach is to create a virtual thread per task. If a downstream resource is limited—such as a database connection pool—limit access to that resource with a semaphore or pool rather than limiting the number of virtual threads.

### Q151. What is a carrier thread?

**Answer:** It is a platform thread on which a virtual thread is temporarily mounted to run Java code. When the virtual thread blocks in a supported operation, it can unmount so the carrier can run another virtual thread.

### Q152. What does “pinning” mean for virtual threads?

**Answer:** Pinning means a virtual thread cannot unmount from its carrier during a blocking operation, so the carrier is also stuck. Current Java 25 documentation identifies native or foreign-function execution as cases where pinning can occur. JEP 491, delivered in Java 24, removed nearly all pinning caused by blocking inside `synchronized` code; older Java 21 advice may not reflect that change.

### Q153. Are virtual threads suitable for CPU-heavy work?

**Answer:** They can run CPU work, but they do not create more CPU capacity. A large number of CPU-bound virtual threads can still oversubscribe the available cores. Use bounded parallelism for CPU-heavy workloads.

### Q154. What is structured concurrency?

**Answer:** Structured concurrency treats related concurrent subtasks as one unit whose lifetime, cancellation, and failure handling are connected to the parent task. It aims to make concurrent task relationships easier to manage and observe.

### Q155. Is structured concurrency a permanent Java API in Java 25?

**Answer:** No. In Java 25, the structured concurrency API is a preview feature (JEP 505). Preview APIs can change and require explicit enablement; check the target JDK before using them in production.

### Q156. What is the difference between structured concurrency and `ExecutorService`?

**Answer:** `ExecutorService` schedules and manages tasks, but the parent-child relationship between submitted tasks is mostly managed by application code. Structured concurrency makes that relationship and its lifetime more explicit. It does not replace all executor use cases.

### Q157. Are scoped values permanent in Java 25?

**Answer:** Yes. Scoped values became a permanent API in Java 25. They are intended for sharing immutable data across a bounded call or task scope, often as an alternative to some thread-local context patterns.

## 16. Senior-level case studies

### Q158. A shared counter is lower than expected after many threads increment it. What is wrong, and how would you fix it?

**Answer:** `counter++` is a read-modify-write sequence, so concurrent increments can overwrite one another. Use `AtomicInteger`/`AtomicLong`, `LongAdder` for highly contended statistics, or a lock if the counter must change atomically with other state.

### Q159. A worker thread does not stop after the application sets `running = false`. What would you check?

**Answer:** Check whether `running` is safely published, such as being `volatile` or guarded consistently by a lock. Also check whether the worker is blocked in I/O or a queue operation; a flag alone may not wake it. Use interruption or a timed/interruptible blocking operation where appropriate.

### Q160. A request-handling thread pool is saturated and the queue keeps growing. How would you diagnose and respond?

**Answer:** Check task arrival rate, task duration, blocking time, worker count, queue capacity, downstream latency, and rejection count. Bound the queue, set timeouts, apply backpressure or reject work intentionally, and remove the bottleneck. Increasing threads without checking CPU and downstream limits can make the system worse.

### Q161. An application has intermittent deadlocks involving two account locks. How would you fix the design?

**Answer:** Acquire account locks in a stable global order, such as ascending account ID, for every transfer path. Alternatively, use a higher-level transaction or a design that avoids holding multiple locks. Add timeouts or detection as safeguards, not as the only correctness mechanism.

### Q162. A cache uses `ConcurrentHashMap`, but duplicate expensive loads still happen. Why?

**Answer:** A separate `get`, load, and `put` is not one atomic operation, so multiple threads can all observe a miss and load the value. Use `computeIfAbsent()` when its semantics fit, keep the mapping function short, and consider a dedicated asynchronous loading cache if loading is slow.

### Q163. A read-heavy shared object has a `StampedLock`, but results are occasionally invalid. What is the likely bug?

**Answer:** The optimistic read may be using data before checking `validate(stamp)`, or it may not retry under a real read lock after validation fails. Copy the needed state, validate it, and discard/re-read when validation fails. Do not treat an optimistic read as a normal locked read.

### Q164. A `CompletableFuture` request sometimes hangs and blocks common-pool workers. What would you investigate?

**Answer:** Look for blocking calls inside async stages, missing timeouts, futures that never complete, or a pipeline waiting on work queued to the same constrained executor. Use explicit executors for blocking work, propagate deadlines, and inspect thread dumps and stage completion paths.

### Q165. A service switches from a fixed platform-thread pool to virtual threads. The database still overloads. Why did virtual threads not solve it?

**Answer:** Virtual threads make it cheaper to represent waiting tasks; they do not increase database capacity. Keep the database connection pool and downstream concurrency within safe limits, apply backpressure or a semaphore, and measure throughput and tail latency.

### Q166. A `ThreadLocal` request ID appears on the next request handled by the same worker. What happened?

**Answer:** The pool reused a thread, and the prior task did not clear the thread-local value. Set and remove request context in a `try/finally` boundary, or use the framework's supported context propagation.

### Q167. A thread waits forever after another thread signals “ready.” What synchronization bug would you suspect?

**Answer:** Suspect a lost notification or an incorrect condition check. Protect the condition with the same monitor, check it in a `while` loop, and wait only while it is false. A latch can be a simpler fit for a one-time readiness signal.

### Q168. A fair lock was enabled, but some requests still have high latency. Does fairness guarantee low latency?

**Answer:** No. Fairness influences lock acquisition order under contention; it does not guarantee operating-system scheduling, short critical sections, or low end-to-end latency. Measure queue wait time and time spent inside the critical section.

## Quick comparison table

| Need | Common starting point |
|---|---|
| Protect a small critical section | `synchronized` |
| Timed/interruptible lock acquisition or multiple conditions | `ReentrantLock` |
| Many reads and few writes | Benchmark `ReadWriteLock`; consider `StampedLock` only with careful validation |
| Atomic update of one value | An `Atomic*` class or `VarHandle` |
| High-contention statistics | `LongAdder` |
| Wait for a fixed set of work to finish once | `CountDownLatch` |
| Coordinate a fixed group at repeated phases | `CyclicBarrier` |
| Limit concurrent access to a resource | `Semaphore` |
| Pass work between producers and consumers | `BlockingQueue` |
| Schedule ordinary tasks on platform threads | `ExecutorService` |
| Many blocking I/O tasks on Java 21+ | Virtual threads, while still limiting downstream resources |

## Sources

- [Java Language Specification, Java SE 21 — Chapter 17: Threads and Locks](https://docs.oracle.com/javase/specs/jls/se21/html/jls-17.html) — monitors, wait sets, interruption, the memory model, and happens-before.
- [Oracle Java Tutorials — Intrinsic Locks and Synchronization](https://docs.oracle.com/javase/tutorial/essential/concurrency/locksync.html) — monitor basics and synchronization.
- [Oracle Java Tutorials — Interrupts](https://docs.oracle.com/javase/tutorial/essential/concurrency/interrupt.html) — cooperative interruption and interrupt status.
- [Java SE 25 Concurrency Overview](https://docs.oracle.com/en/java/javase/25/core/concurrency.html) — executors, concurrent collections, atomics, synchronizers, and locks.
- [Java SE 25 `ReentrantLock` API](https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/util/concurrent/locks/ReentrantLock.html), [ReadWriteLock API](https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/util/concurrent/locks/ReadWriteLock.html), and [StampedLock API](https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/util/concurrent/locks/StampedLock.html).
- [Java SE 25 `ExecutorService` API](https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/util/concurrent/ExecutorService.html), [ThreadPoolExecutor API](https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/util/concurrent/ThreadPoolExecutor.html), and [CompletableFuture API](https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/util/concurrent/CompletableFuture.html).
- [Java SE 25 `ConcurrentHashMap` API](https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/util/concurrent/ConcurrentHashMap.html), [CountDownLatch API](https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/util/concurrent/CountDownLatch.html), [Semaphore API](https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/util/concurrent/Semaphore.html), and [Phaser API](https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/util/concurrent/Phaser.html).
- [Oracle Java SE 25 — Virtual Threads](https://docs.oracle.com/en/java/javase/25/core/virtual-threads.html) and [JEP 444](https://openjdk.org/jeps/444) — virtual-thread design and intended workloads.
- [JEP 491](https://openjdk.org/jeps/491) — virtual threads and synchronization changes delivered in Java 24.
- [JEP 505](https://openjdk.org/jeps/505) — structured concurrency preview in Java 25.
- [JEP 506](https://openjdk.org/jeps/506) — scoped values, finalized in Java 25.
- Public interview-topic cross-check: [CodeBegun — Java Multithreading Interview Questions](https://www.codebegun.com/interview/java/multithreading) and [JavaMakeUse — Java Multithreading and Concurrency Interview Questions](https://www.javamakeuse.com/2026/10/java-multithreading-and-concurrency.html). These guides help identify common interview angles; the Java specifications and API docs above are the authority for technical answers.
