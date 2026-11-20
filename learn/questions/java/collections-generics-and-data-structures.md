# Java Collections, Generics, and Data Structures — Interview Question Bank

[Back to index](index.md)

This bank focuses on the Collections Framework, equality and hashing as used by collections, generic type safety, and choosing concurrent data structures. Recurring senior interview questions ask candidates to explain implementation trade-offs, reason about complexity and contracts, and select an appropriate structure for a workload. Complexity statements below are typical, not a substitute for checking an implementation’s API contract.

## Questions and answers

### 1. What is the difference between `Collection` and `Map`?
**Answer:** `Collection` represents a group of elements, with subinterfaces such as `List`, `Set`, and `Queue`. `Map` associates keys with values and is not a subtype of `Collection`; it provides collection views for keys, values, and entries. Choose the abstraction that expresses the operations callers need.

### 2. Compare `List`, `Set`, `Queue`, and `Deque`.
**Answer:** A `List` is an ordered sequence and generally permits duplicates. A `Set` disallows duplicate elements under its equality rule. A `Queue` supports holding elements for later processing, often with head-oriented operations. A `Deque` supports insertion and removal at both ends and can serve as a queue or stack.

### 3. Compare `ArrayList` and `LinkedList`.
**Answer:** `ArrayList` is a resizable array: indexed reads are constant-time, appending is amortized constant-time, and inserting/removing in the middle shifts elements. `LinkedList` stores linked nodes: accessing by index is linear, while insertion/removal at a known node is constant-time, but finding that node costs traversal. `ArrayList` is usually the better default due to memory locality and lower per-element overhead.

### 4. What does amortized constant time mean for `ArrayList.add`?
**Answer:** Most appends add one element in constant time. Occasionally the backing array must grow and existing elements are copied, making that individual operation linear. Across many appends, the average cost per append remains constant under the dynamic resizing strategy.

### 5. How does `HashMap` work at a high level?
**Answer:** It computes a hash from the key, uses that hash to choose a bucket, and compares keys within the bucket using identity or equality. Collisions place multiple entries in the same bucket; modern implementations can treeify some heavily collided buckets under particular capacity and implementation conditions. Resizing redistributes entries when the table grows. The API does not promise iteration order.

### 6. Why must `equals()` and `hashCode()` agree?
**Answer:** If two objects are equal according to `equals()`, they must return the same hash code. A hash collection first uses hash codes to narrow where it searches, then equality to identify a matching key. Violating the contract can make logically present keys appear missing or allow unexpected duplicates. Unequal objects may share a hash code; that is legal but can hurt performance.

### 7. What is the `equals()` contract?
**Answer:** It must be reflexive, symmetric, transitive, and consistent while relevant state is unchanged, and it must return false for `null`. A custom implementation should use a stable domain definition and usually work with `hashCode()`. Equality across class hierarchies is subtle: adding subclass state can break symmetry or transitivity, so composition or carefully designed value types may be safer.

### 8. Why are mutable map keys dangerous?
**Answer:** If fields used by `equals()` or `hashCode()` change after insertion, the key may now map to a different bucket than the one in which it was stored. Lookup/removal can fail even though the entry remains present. Use immutable key state or remove the entry before changing equality-relevant fields.

### 9. What is a hash collision, and how should a system handle many collisions?
**Answer:** A collision occurs when distinct keys map to the same bucket/hash. Correctness is preserved by equality checks, but operations can become slower. Use well-distributed hash codes and immutable keys; avoid adversarial keys where relevant. Modern `HashMap` may use balanced trees for certain crowded buckets, but this is an implementation detail, not a reason to ignore poor hashing.

### 10. How does `HashSet` relate to `HashMap`?
**Answer:** A typical `HashSet` implementation uses a backing map and stores set elements as keys with a shared placeholder value. This gives hash-based membership and duplicate detection, with no iteration-order guarantee. Rely on the `Set` contract rather than internal representation.

### 11. Compare `HashMap`, `LinkedHashMap`, and `TreeMap`.
**Answer:** `HashMap` offers expected constant-time basic operations and no defined order. `LinkedHashMap` maintains encounter order, usually insertion order, or can be configured for access order for LRU-like use. `TreeMap` keeps keys sorted by natural order or a comparator and typically offers logarithmic operations. Each adds different memory and ordering costs.

### 12. When would you use `EnumMap` or `EnumSet`?
**Answer:** Use them when keys/elements come from one enum type. They are compact and efficient because enum constants form a fixed finite set. They also express the domain more clearly than a general-purpose map or set.

### 13. What does fail-fast iteration mean?
**Answer:** Some iterators detect structural modification outside the iterator and may throw `ConcurrentModificationException`. This is a best-effort bug-detection mechanism, not synchronization and not a thread-safety guarantee. Programs must not rely on the exception always occurring.

### 14. How do you safely remove elements while iterating?
**Answer:** Use the iterator’s own `remove()` operation when supported, or use an appropriate bulk operation such as `removeIf`. Do not structurally modify an ordinary collection through a separate reference while iterating. For concurrent workloads, choose a collection whose iteration semantics match the required consistency.

### 15. Is `Arrays.asList(array)` a normal resizable list?
**Answer:** No. It returns a fixed-size list backed by the array: element replacement is reflected both ways, but adding or removing elements is unsupported. Also, a primitive array is one object, so `Arrays.asList(intArray)` produces a list with the entire `int[]` as one element rather than a list of boxed integers.

### 16. What is the difference between an unmodifiable view and an immutable collection?
**Answer:** An unmodifiable view rejects changes through that reference but can reflect changes made through another reference to its backing collection. An immutable collection does not expose a mutable backing collection through its API. Neither term automatically makes mutable elements immutable. Make defensive copies when isolation is required.

### 17. What do `List.of`, `Set.of`, and `Map.of` provide?
**Answer:** Since Java 9, these factory methods create unmodifiable collections. They reject `null`; set/map factories also reject duplicate elements/keys. They are useful for constants and safe snapshots, but should not be treated as mutable containers.

### 18. What is a generic type parameter?
**Answer:** A type parameter lets a class, interface, or method describe how it uses types while allowing the compiler to check calls. For example, `List<String>` expresses a list whose elements are strings and prevents inserting an integer through that type. Generics improve compile-time safety but do not make all runtime type information available.

### 19. Why are Java generics invariant?
**Answer:** `List<Integer>` is not a subtype of `List<Number>`. If it were, code receiving a `List<Number>` could add a `Double` to a list intended for integers. Wildcards express safe variance at use sites: `List<? extends Number>` can be read as numbers, while `List<? super Integer>` can accept integers.

### 20. Explain PECS.
**Answer:** “Producer extends, consumer super.” Use `? extends T` when an API reads values as `T`; use `? super T` when it writes `T` values. A wildcard can restrict which operations are safe, so this is a design aid rather than a rule to add wildcards everywhere.

### 21. What is type erasure?
**Answer:** The compiler largely removes generic type parameters from runtime representations, replacing them with bounds or `Object` and inserting casts where needed. As a result, `List<String>` and `List<Integer>` do not have distinct runtime classes, and code cannot normally write `new T()` or test `instanceof List<String>`. The compiler may generate bridge methods to preserve polymorphism after erasure.

### 22. What is heap pollution, and how can it occur?
**Answer:** Heap pollution occurs when a variable of a parameterized type refers to an object that is not of the expected parameterized type. Raw types, unchecked casts, and unsafe generic varargs can cause it. Compiler warnings point to places where type safety cannot be verified; do not suppress them without a reasoned proof.

### 23. What is the difference between a raw type and an unbounded wildcard?
**Answer:** A raw `List` bypasses generic checking and can accept arbitrary objects, producing unchecked warnings and possible runtime `ClassCastException`. `List<?>` says the element type is unknown while retaining type safety; it can be read as `Object`, but you cannot safely add an arbitrary non-null value.

### 24. When should a collection be returned as an interface rather than a concrete class?
**Answer:** Return the narrowest useful interface, such as `List` or `Set`, when callers need only that contract. This reduces coupling to a particular implementation. If ordering, mutability, null handling, or snapshot semantics matter, document them because the interface name alone does not fully specify them.

### 25. What is the difference between `Comparable` and `Comparator`?
**Answer:** `Comparable` defines an object’s natural ordering in the type itself. `Comparator` defines an external or alternative ordering and can be supplied to sorting or sorted collections. A comparator used in a `TreeSet` or `TreeMap` should be consistent with equality when the application expects distinct-but-equal keys to coexist according to `equals()`.

### 26. Why can a `TreeSet` contain fewer elements than expected?
**Answer:** A sorted set treats elements as duplicates when its comparator or natural ordering returns zero, even if `equals()` says they differ. If that ordering is inconsistent with equality, set membership semantics can surprise callers. Define and test the ordering contract.

### 27. What does `computeIfAbsent` do, and what should its mapping function avoid?
**Answer:** It computes and stores a value when the key is absent (or mapped to `null` where supported). It is useful for lazy initialization such as grouping. Keep the mapping function short and avoid recursive updates to the same map; for concurrent maps, follow the API’s atomicity and callback restrictions.

### 28. Compare `ConcurrentHashMap` with `Collections.synchronizedMap`.
**Answer:** `ConcurrentHashMap` is designed for concurrent access and supports atomic compound operations such as `compute` and `putIfAbsent`; it does not allow null keys or values. A synchronized wrapper serializes operations on a shared lock, which may be adequate for low contention or compatibility. Iterating a synchronized wrapper requires synchronizing on the wrapper for a consistent traversal. Choose based on the required atomicity and contention profile.

### 29. Is a sequence of operations on `ConcurrentHashMap` automatically atomic?
**Answer:** No. Each documented operation has its own concurrency guarantees, but a sequence such as `if (!containsKey(k)) put(k, v)` can race. Use a single atomic operation such as `putIfAbsent` or `computeIfAbsent` when it expresses the required behavior.

### 30. What are the iteration semantics of `ConcurrentHashMap`?
**Answer:** Its iterators are weakly consistent: they can proceed during concurrent updates, do not throw `ConcurrentModificationException`, and may reflect some updates that occur during traversal without representing one atomic snapshot. If a consistent snapshot is required, coordinate writers or copy under an appropriate synchronization strategy.

### 31. When is `CopyOnWriteArrayList` a good choice?
**Answer:** It is useful when reads and iterations greatly outnumber writes, such as a listener registry. A write copies the backing array, making writes expensive and memory-intensive, while iterators see a stable snapshot. It is a poor choice for frequent updates or very large collections.

### 32. When would you choose `ConcurrentSkipListMap` over `ConcurrentHashMap`?
**Answer:** Choose `ConcurrentSkipListMap` when you need a concurrent sorted map, ordered traversal, or range operations. Choose `ConcurrentHashMap` for high-throughput key lookup when order is unnecessary. The sorted structure generally has higher per-operation costs but provides useful ordering semantics.

### 33. Compare `ArrayBlockingQueue`, `LinkedBlockingQueue`, and `PriorityBlockingQueue`.
**Answer:** `ArrayBlockingQueue` is bounded and array-backed, providing explicit backpressure when full. `LinkedBlockingQueue` is optionally bounded and node-based, with different allocation and throughput trade-offs. `PriorityBlockingQueue` returns elements by priority, is not bounded by a fixed capacity, and does not guarantee FIFO among equal-priority elements. Pick queue semantics to match the producer/consumer design.

### 34. What is a `BlockingQueue` useful for?
**Answer:** It supports producer-consumer handoff with blocking insertion/removal, which can simplify coordination. A bounded queue limits in-flight work and can provide backpressure. Define what happens when full, how shutdown is signaled, and whether tasks may be lost or retried.

### 35. What are `WeakHashMap` and `IdentityHashMap` for?
**Answer:** `WeakHashMap` uses weak keys so an entry can disappear when its key is no longer strongly reachable elsewhere; it is not a deterministic cache eviction policy. `IdentityHashMap` compares keys by `==` rather than `equals()` and is useful for specialized identity-based algorithms, not ordinary domain maps.

### 36. How should you choose a collection for a new feature?
**Answer:** Start with required semantics: ordering, uniqueness, null policy, mutability, concurrency, snapshot consistency, and range queries. Then consider workload size, read/write mix, memory overhead, and expected complexity. Prefer a standard implementation, document observable behavior, and benchmark only after profiling shows the collection choice matters.

### 37. Case study: a `HashMap` lookup stops finding a key after the key object is updated. Why?
**Answer:** The key likely changed a field used by `equals()` or `hashCode()` after insertion. The map placed it using the old hash, while lookup uses the new one. Make key equality state immutable or remove/reinsert the key around a change.

### 38. Case study: many threads update one shared counter stored in a map. Is `map.get(k) + 1; map.put(k, next)` safe?
**Answer:** No. The read-modify-write sequence can lose updates even if the map itself is thread-safe. Use a value type with atomic operations, a map operation that computes atomically, or external synchronization. Also ensure the chosen approach has a defined overflow policy if counter range matters.

### 39. Case study: a service needs a bounded work queue. What design questions come before choosing a class?
**Answer:** Define capacity, producer behavior when full (block, reject, drop, or persist), consumer count, ordering, shutdown, retries, and fairness requirements. A bounded blocking queue can provide backpressure, but the caller must handle interruption and saturation. Queue capacity should be tied to service latency and memory budgets, not guessed in isolation.

### 40. Case study: an API returns a `List` that callers must not change. What should the implementation do?
**Answer:** Return an unmodifiable defensive copy if callers must neither mutate the collection nor observe future internal changes. An unmodifiable wrapper around a shared mutable list prevents writes through that wrapper but still exposes later mutations. Also clarify whether list elements themselves are mutable.

## Sources

- [Oracle Java 17 Collections Framework Overview](https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/util/doc-files/coll-overview.html)
- [Oracle Java 17 Collections Framework Outline](https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/util/doc-files/coll-reference.html)
- [Oracle Java API: `Map`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/Map.html)
- [Oracle Java API: `HashMap`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/HashMap.html)
- [Oracle Java API: `ConcurrentHashMap`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/concurrent/ConcurrentHashMap.html)
- [Oracle Java API: `Collection`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/Collection.html)
- [Java Language Specification, Java SE 21: Type Erasure](https://docs.oracle.com/javase/specs/jls/se21/html/jls-4.html#jls-4.6)
- [Oracle Java Tutorials: Generics](https://docs.oracle.com/javase/tutorial/java/generics/)
