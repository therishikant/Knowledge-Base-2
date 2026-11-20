# Modern Java Features — Interview Question Bank

[Back to index](index.md)

This bank covers Java 9 through Java 25, with emphasis on Java 17 and 21 and explicit preview/incubator status. A preview feature is not a stable Java SE feature: it may change or be removed, and typically requires `--enable-preview` for the matching release. An incubator API is also not a finalized permanent API. Confirm status against the JEP and release documentation for the exact JDK used in an interview or production system.

## Questions and answers

### 1. What is the Java module system, introduced in Java 9?
**Answer:** The Java Platform Module System groups packages into named modules with explicit dependencies (`requires`) and exposed packages (`exports`). It can improve encapsulation and make dependencies more explicit. Classpath applications remain possible, but modules add rules for readability and access that teams must understand during migration.

### 2. What is the difference between `exports` and `opens` in a module?
**Answer:** `exports` makes public types in a package accessible to other modules at compile time and runtime according to module readability. `opens` permits deep reflection into a package at runtime, often for frameworks. Opening packages broadly weakens encapsulation; prefer targeted opens where possible.

### 3. What is `var`, and what are its limits?
**Answer:** Local-variable type inference, introduced in Java 10, lets the compiler infer a local variable’s static type from its initializer: `var name = "Ada";`. It is not dynamic typing and cannot be used for fields, method parameters, or return types. The initializer must provide enough information, and readable code should make the inferred type clear.

### 4. How can `var` be used with lambda parameters?
**Answer:** Since Java 11, `var` can be used for implicitly typed lambda parameters, such as `(var x, var y) -> ...`. This permits annotations on lambda parameters while keeping inferred types. A lambda’s parameters must use `var` consistently rather than mixing it with implicit or explicit parameter types.

### 5. What problem do switch expressions solve?
**Answer:** Final in Java 14, switch expressions return a value and support arrow labels that avoid accidental fall-through. A `yield` statement can provide a value from a block arm. They make exhaustive, value-producing branching clearer; a traditional switch statement remains available when its control-flow style is appropriate.

### 6. What are text blocks?
**Answer:** Text blocks, final in Java 15, are multi-line string literals delimited by triple quotes. They reduce escaping and improve readability for embedded text such as JSON or SQL. Indentation normalization and escape processing still apply, so inspect the resulting string when exact whitespace matters.

### 7. What is a record, and what is it not?
**Answer:** Records, final in Java 16, are concise nominal data-carrier classes. The compiler provides component fields, accessors, a canonical constructor, and value-oriented `equals`, `hashCode`, and `toString` behavior. A record is still a class, can implement interfaces, and can define validation or methods; it is not automatically deeply immutable if a component refers to mutable state.

### 8. When should you not use a record?
**Answer:** Avoid one when the type needs mutable identity, complex inheritance from a class, or a public API whose component-based representation should remain hidden. Records make component names and shape part of the contract. Prefer an ordinary class when encapsulation and representation evolution matter more than concise data-carrier semantics.

### 9. What is pattern matching for `instanceof`?
**Answer:** Final in Java 16, it combines a type test with a pattern variable, for example `if (obj instanceof String s)`. The variable is in scope only where the test proves the match. It removes repetitive casts and can make flow-based logic clearer.

### 10. What are sealed classes and interfaces?
**Answer:** Final in Java 17, sealed types restrict which classes or interfaces may directly extend or implement them, using `permits` (which can sometimes be inferred). Each permitted subtype must declare itself `final`, `sealed`, or `non-sealed`. Sealing is useful for controlled domain models and exhaustive handling, but it creates an explicit extension boundary.

### 11. What became final in Java 21 for pattern matching?
**Answer:** Record patterns (JEP 440) and pattern matching for `switch` (JEP 441) became final in Java 21. Record patterns deconstruct record components; switch patterns allow type-based cases and more expressive matching. Together with sealed hierarchies they support readable exhaustive dispatch, while null and exhaustiveness rules must still be understood.

### 12. What are record patterns, and how do they help?
**Answer:** A record pattern matches a record and extracts its components, for example `case Point(int x, int y)`. Nested patterns can deconstruct nested records. This can reduce accessor boilerplate, but overusing deeply nested patterns can obscure control flow; keep domain logic readable.

### 13. What are sequenced collections in Java 21?
**Answer:** JEP 431 introduced interfaces such as `SequencedCollection`, `SequencedSet`, and `SequencedMap` for collections with a defined encounter order and common first/last/reversed-view operations. This unifies APIs that previously varied across ordered implementations. It does not make unordered collections ordered, and reversed views are not necessarily independent copies.

### 14. What are virtual threads?
**Answer:** Virtual threads, final in Java 21, are lightweight threads managed by the Java runtime rather than mapping one-to-one to an operating-system thread for their whole lifetime. They support a thread-per-task style that can scale well for high-concurrency blocking I/O. They do not make CPU-bound work faster, remove downstream capacity limits, or eliminate the need for backpressure.

### 15. How should a team adopt virtual threads?
**Answer:** Use them where many tasks spend time waiting on blocking operations, keep task lifetimes clear, and load-test the full system. Bound access to scarce resources such as database connections with appropriate limits. Monitor pinned-thread situations and library behavior for the target JDK; do not pool virtual threads merely to imitate platform-thread pools.

### 16. What is thread pinning with virtual threads?
**Answer:** Pinning occurs when a virtual thread cannot unmount from its carrier thread during a blocking operation, for example in certain synchronized/native interactions depending on JDK version. Pinning can reduce scalability if many threads block while pinned. Use JFR diagnostics and current JDK documentation; implementation behavior has evolved across releases.

### 17. What is structured concurrency?
**Answer:** Structured concurrency treats related concurrent tasks as a unit with a clear lexical lifetime, coordinated cancellation, and result/error handling. It aims to make concurrent code easier to reason about than detached tasks. It has remained a preview API through Java 25 (JEP 505, fifth preview); do not treat it as finalized Java SE API.

### 18. What are scoped values?
**Answer:** Scoped values provide a way to share immutable contextual data with callees and child tasks without passing it through every method parameter or using mutable thread-local state. They were finalized in Java 25 as JEP 506. Use them for bounded context such as request metadata, not as a general mutable global-variable replacement.

### 19. What is the status of the Foreign Function & Memory API?
**Answer:** The Foreign Function & Memory API became final in Java 22 (JEP 454). It provides a supported way to access off-heap memory and call native functions without relying on JNI for many use cases. Native access remains inherently risky; define lifetimes, bounds, and ownership carefully.

### 20. What are Stream Gatherers?
**Answer:** Stream Gatherers, final in Java 24 (JEP 485), let developers define intermediate stream operations with stateful, custom processing behavior. They extend the standard Stream API for transformations that were awkward to express with built-in operations. They should be used when they improve clarity, not simply because a custom operation is possible.

### 21. What is the Class-File API?
**Answer:** Final in Java 24 (JEP 484), it provides a standard API for parsing, generating, and transforming Java class files. It can reduce dependence on internal JDK APIs or third-party libraries for bytecode tooling. Framework and agent authors should still validate generated bytecode, version support, and compatibility requirements.

### 22. What happened to String Templates?
**Answer:** String Templates were a preview feature in Java 21 (JEP 430) and previewed again in Java 22 (JEP 459), but were withdrawn rather than finalized. Do not describe them as a stable Java feature or recommend them for production code without a future finalized replacement. For safe text construction, use established APIs and parameterized database queries.

### 23. Which Java 21 language features were preview-only?
**Answer:** Java 21 previewed unnamed patterns and variables (JEP 443), unnamed classes and instance `main` methods (JEP 445), and String Templates (JEP 430). Some related features later became final or changed: unnamed variables and patterns became final in Java 22 (JEP 456), while String Templates did not. Always cite the release-specific status rather than saying a feature is simply “new Java.”

### 24. What are unnamed variables and patterns?
**Answer:** Final in Java 22 as JEP 456, they let code mark an intentionally unused variable or pattern component with `_` in supported contexts. This reduces dummy names and clarifies that a value is deliberately ignored. The exact contexts are language-defined; underscore is not a universal replacement for any identifier.

### 25. What are compact source files and instance `main` methods in Java 25?
**Answer:** JEP 512 finalized compact source files and instance `main` methods in Java 25, building on previews that began in Java 21. They let simple programs avoid some boilerplate, including an explicit class declaration in supported cases and a static entry method. These are conveniences for learning and small programs; traditional class-based entry points remain important for normal applications and libraries.

### 26. What are module import declarations in Java 25?
**Answer:** JEP 511 finalized module import declarations in Java 25. A module import can make the exported packages of a module available without individual type imports, reducing boilerplate in suitable source files. It does not remove module dependency or access rules, and ordinary explicit imports remain useful for clarity.

### 27. What are flexible constructor bodies in Java 25?
**Answer:** JEP 513 finalized flexible constructor bodies in Java 25. They permit a restricted set of statements before an explicit superclass constructor invocation, allowing validation or preparation before superclass initialization while preserving safety rules. They do not allow arbitrary access to the not-yet-initialized object.

### 28. What is the status of primitive patterns in Java 25?
**Answer:** JEP 507, primitive types in patterns, `instanceof`, and `switch`, is a third preview in Java 25, not a final feature. It expands pattern matching to primitive values and conversions under defined rules. Compile with the matching JDK’s preview support only for evaluation; avoid making a stable API contract depend on preview syntax.

### 29. What does “preview feature” mean operationally?
**Answer:** A preview feature is offered for evaluation in a specific release and can change or disappear. Source compilation and execution generally require `--enable-preview` with the matching JDK release, and compiler/runtime compatibility must match. Preview code creates upgrade and deployment friction, so isolate it and document why the risk is acceptable.

### 30. What is an incubator API, and how is it different from preview language syntax?
**Answer:** An incubator API is an experimental API module that may change or be removed; a preview feature is a release-specific preview mechanism for language or API functionality. Both are non-final, but their packaging and enablement differ. Check the JEP and API docs for the specific feature instead of assuming they share one flag or lifecycle.

### 31. What changed in Java 17 that matters to application teams beyond language syntax?
**Answer:** Java 17 is an LTS release and includes sealed classes as a final language feature, pattern matching for `instanceof`, and records from earlier releases. It also strengthened encapsulation of JDK internals, so applications depending on unsupported internal APIs may fail or need migration. Inventory dependencies and test on the target runtime rather than relying on access flags indefinitely.

### 32. What should a Java 8 application team check when moving to Java 17 or 21?
**Answer:** Compile and test against the intended release; inventory removed/deprecated APIs, reflective access to JDK internals, dependency bytecode compatibility, TLS/crypto defaults, garbage collector flags, and container sizing. Replace unsupported internal APIs and review framework support. Exercise production-like startup, serialization, monitoring, and deployment scripts—not just unit tests.

### 33. How do you decide whether to adopt a new language feature?
**Answer:** Evaluate whether it makes the domain model, safety, or maintenance materially better; check final/preview status and supported JDKs; review team familiarity and tooling; and test runtime behavior. Prefer stable features for public libraries and long-lived services. A feature should reduce complexity rather than merely shorten syntax.

### 34. Case study: a service handles tens of thousands of blocking requests and has a large platform-thread pool. Would you use virtual threads?
**Answer:** They are a strong candidate if tasks are mostly blocked on I/O and the dependencies support the workload. Prototype with realistic traffic, bound database and downstream concurrency, inspect pinning and memory, and compare tail latency and throughput. Keep CPU-bound work and scarce-resource access controlled; virtual threads do not create more database capacity.

### 35. Case study: a team proposes String Templates in a Java 21 production service. What do you tell them?
**Answer:** In Java 21 String Templates are preview, not stable. The team would need preview compilation/runtime flags and accept upgrade risk; the feature was later withdrawn. Use established, safe alternatives, especially parameterized APIs for SQL and other executable input. Do not concatenate untrusted input into commands or queries.

### 36. Case study: a sealed hierarchy and pattern switch both exist. How can this improve a domain model, and what is the risk?
**Answer:** A sealed hierarchy makes the permitted variants explicit, and pattern matching can handle each variant with less casting boilerplate. This can make missing cases visible as the model evolves. The trade-off is restricted extension: external teams cannot add implementations unless permitted. Choose it when the set of variants is intentionally controlled.

### 37. Case study: a library wants to adopt a Java 25 preview feature but must support Java 17 consumers. What is the recommendation?
**Answer:** Do not put preview syntax in the shared library’s public implementation unless the support contract explicitly permits a separate, preview-enabled artifact and matching runtime. Prefer a stable Java 17-compatible implementation or isolate the experiment behind a separate module/source set. Verify compilation, runtime flags, and downstream consumer constraints.

## Sources

- [Oracle Java 25 Language Changes Summary](https://docs.oracle.com/en/java/javase/25/language/java-language-changes-summary.html)
- [Oracle Java 25 Language Changes by Release](https://docs.oracle.com/en/java/javase/25/language/java-language-changes-release.html)
- [OpenJDK JEP Index](https://openjdk.org/jeps/)
- [OpenJDK JEP 444: Virtual Threads](https://openjdk.org/jeps/444)
- [OpenJDK JEP 431: Sequenced Collections](https://openjdk.org/jeps/431)
- [OpenJDK JEP 440: Record Patterns](https://openjdk.org/jeps/440)
- [OpenJDK JEP 441: Pattern Matching for switch](https://openjdk.org/jeps/441)
- [OpenJDK JEP 505: Structured Concurrency (Fifth Preview)](https://openjdk.org/jeps/505)
- [OpenJDK JEP 506: Scoped Values](https://openjdk.org/jeps/506)
- [OpenJDK JEP 511: Module Import Declarations](https://openjdk.org/jeps/511)
- [OpenJDK JEP 512: Compact Source Files and Instance Main Methods](https://openjdk.org/jeps/512)
- [OpenJDK JEP 513: Flexible Constructor Bodies](https://openjdk.org/jeps/513)
- [OpenJDK JEP 507: Primitive Types in Patterns, instanceof, and switch (Third Preview)](https://openjdk.org/jeps/507)
