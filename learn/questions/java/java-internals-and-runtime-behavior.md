# Java Interview Questions — JVM Internals and Runtime Behavior

[Back to index](index.md)

**Target:** Senior Java developer / tech lead  
**Language:** Simple English; technical terms are explained in the answers.  
**Version focus:** Java 8 fundamentals, Java 17/21 interview practice, with relevant Java 25 changes noted.  
**Format:** Every question is followed immediately by an answer. Scenario questions are grouped at the end.

These are original study questions synthesized from recurring topics in public Java interview guides and checked against the Java Language Specification (JLS), Java Virtual Machine Specification (JVMS), Oracle documentation, and OpenJDK JEPs. Public guides do not provide a reliable count of how often each question is asked, so “common” means repeated themes across those guides, not a statistical ranking.

## What interviewers commonly ask in this topic

1. **JVM foundations:** JVM vs. JRE vs. JDK; source, class files, bytecode, and portability.
2. **Class lifecycle:** loading, linking, verification, preparation, resolution, initialization, and unloading.
3. **Class loaders:** delegation, defining-loader identity, context class loaders, custom loaders, and loader leaks.
4. **Runtime data areas:** per-thread vs. shared areas, frames, local variables, operand stacks, and native memory.
5. **Execution engine:** interpretation, JIT compilation, tiered compilation, hot methods, and warm-up.
6. **Dispatch and bytecode:** method invocation, virtual dispatch, `invokedynamic`, lambdas, and `javap`.
7. **Runtime failures:** classpath and linkage errors, initialization failures, stack overflow, and memory-area exhaustion.
8. **Operational diagnosis:** startup, class loading, JIT behavior, thread stacks, native memory, and JVM diagnostic tools.
9. **Design trade-offs:** specification guarantees vs. HotSpot details, portability vs. implementation-specific tuning, and startup vs. steady-state performance.
10. **Scenarios:** production symptoms where the candidate must form a hypothesis, collect evidence, and choose a safe next step.

Garbage-collector algorithms, collector selection, and GC tuning are intentionally reserved for the separate **Garbage Collection and Performance** question bank. This file includes only the memory-area model needed to understand runtime behavior and errors.

## 1. JVM foundations and execution flow

### Q1. What is the JVM?

**Answer:** The Java Virtual Machine is the runtime environment that loads and executes Java class files. The JVM specification defines the behavior a compatible implementation must provide; HotSpot is one implementation.

### Q2. What is the difference between the JDK, JRE, and JVM?

**Answer:** The JVM executes bytecode. A runtime environment includes the JVM and libraries needed to run programs. The JDK adds development tools such as `javac`, `javadoc`, and diagnostic utilities. Packaging varies by vendor and release, so avoid treating “JRE” as a separate download that must always exist.

### Q3. What is bytecode?

**Answer:** Bytecode is the instruction format stored in `.class` files. Java compilers translate source into bytecode, which a compatible JVM can interpret or compile into native machine code.

### Q4. What happens from `java Main` until `main` starts?

**Answer:** The launcher starts a JVM, the JVM loads and links the requested class, initializes it when required, and then invokes its `main` method. Loading more classes may happen later as the program needs them.

### Q5. Why is Java described as platform-independent?

**Answer:** Java source is normally compiled to a standardized class-file format rather than directly to one operating system's machine code. A JVM implementation for each target platform can execute that class file. Native libraries, OS assumptions, and differing runtime versions can still affect portability.

### Q6. Is the JVM specification the same thing as the HotSpot implementation?

**Answer:** No. The specification defines observable rules and required behavior. HotSpot's data structures, JIT strategies, object layout, and many performance choices are implementation details and may differ in another JVM.

### Q7. Does the JVM execute Java source code?

**Answer:** Normally, no. A Java compiler translates source to class files first. The JVM executes the class-file instructions, and other languages can also target the JVM.

### Q8. Can Kotlin or Scala run on the JVM?

**Answer:** Yes. A compiler for another language can produce compatible class files and use Java libraries. The JVM is a platform and execution model, not a Java-source-only interpreter.

### Q9. What does “write once, run anywhere” not guarantee?

**Answer:** It does not guarantee identical behavior for programs that depend on native code, OS-specific paths, environmental settings, unsupported class-file versions, or different library/runtime behavior. It means compatible bytecode can be executed by compatible JVMs.

### Q10. What is the difference between compile-time and runtime errors?

**Answer:** Compile-time errors are reported while source is compiled, such as a type error. Runtime errors occur while the program runs, such as a missing class, an invalid symbolic reference, or an exception from executed code.

### Q11. Does `javac` verify every runtime dependency?

**Answer:** No. Compilation checks the source against the compile-time types and class path available to the compiler. Runtime packaging, class-loader behavior, and later dependency changes can still cause linkage or class-loading failures.

### Q12. What is a class file?

**Answer:** It is a binary representation of a class or interface. It includes a version, metadata, a constant pool, field and method descriptions, attributes, and bytecode for methods that have code.

### Q13. How can you inspect a class file?

**Answer:** Use `javap -c -v ClassName` to inspect bytecode and detailed class-file metadata. Use the exact class path or JAR when there may be multiple versions of the class.

### Q14. What is the class-file magic number?

**Answer:** It is the four-byte value `0xCAFEBABE` at the beginning of a class file. It helps identify the file format; it is not a checksum or proof that the rest of the file is valid.

### Q15. What is the constant pool used for?

**Answer:** The class-file constant pool stores constants and symbolic information such as class, field, method, name, and type references. Runtime resolution can turn symbolic references into references usable by the JVM.

### Q16. Why can the same class file behave differently on two JVMs?

**Answer:** The implementations can make different legal choices about interpretation, compilation, scheduling, memory layout, and other unspecified details. Program behavior should rely on the Java and JVM specifications, not on incidental HotSpot behavior.

## 2. Loading, linking, initialization, and unloading

### Q17. What are class loading, linking, and initialization?

**Answer:** **Loading** finds a class's binary form and creates its runtime class representation. **Linking** checks and prepares that representation and resolves references. **Initialization** runs the class's initialization code, including static field initializers and static blocks.

### Q18. What are the phases of linking?

**Answer:** Linking consists of verification, preparation, and resolution. Verification checks class-file validity; preparation creates static storage and assigns default values; resolution connects symbolic references to runtime entities.

### Q19. Does class loading always happen at program startup?

**Answer:** No. Classes are commonly loaded when first needed. The JVM and class loaders can load other classes later, and an implementation may load some classes earlier than the first active use.

### Q20. What happens during verification?

**Answer:** The JVM checks that a class file is structurally valid and that its bytecode satisfies required type-safety and consistency rules. Verification is not a guarantee that the application has no logic or security bugs.

### Q21. What happens during preparation?

**Answer:** The JVM creates storage for class variables and assigns their default values, such as `0`, `false`, or `null`. The explicit Java assignments in static field initializers normally run later during initialization.

### Q22. What does resolution do?

**Answer:** It turns symbolic references in the class file—such as references to another class or method—into runtime references. The specification allows resolution to happen eagerly or lazily, so the exact time an error appears can vary.

### Q23. What happens during class initialization?

**Answer:** The JVM executes the class initialization method, often called `<clinit>` in class-file terms. It runs static field initializers and static initializer blocks in their specified order, after the required superclass initialization.

### Q24. What is the difference between preparation and initialization?

**Answer:** Preparation assigns default values to static fields. Initialization executes the source-level static assignments and static blocks. For example, a static `int count = 7` receives `0` in preparation and `7` during initialization.

### Q25. What actively triggers class initialization?

**Answer:** Common triggers include creating an instance, invoking a static method declared by the class, assigning a static field declared by the class, or reading a non-constant static field declared by the class. The exact rules are in JLS §12.4. A compile-time constant can be inlined and may not trigger initialization.

### Q26. Does reading a compile-time constant initialize its declaring class?

**Answer:** Not necessarily. A `static final` primitive or `String` initialized with a constant expression can be inlined into the caller's class file. Reading that value may not actively use or initialize the declaring class.

### Q27. Does creating an array of a class initialize the class?

**Answer:** Creating an array of a reference type does not by itself initialize the component class. It creates an array whose component type is that class, but no instance of the component class is created.

### Q28. Does `Class.forName(name)` initialize the class?

**Answer:** The one-argument `Class.forName(String)` initializes the class. The overload `Class.forName(name, false, loader)` can load the class without initializing it. Be precise about which overload is used.

### Q29. Does initializing a class initialize its interfaces?

**Answer:** Not every implemented interface is automatically initialized just because a class initializes. Interfaces that declare default methods have special initialization rules when their implementing class initializes. The JLS defines the details.

### Q30. How does the JVM make class initialization thread-safe?

**Answer:** The JVM coordinates initialization so that one thread performs it and other threads that require the class wait for the outcome. This safely publishes the initialized class state. Circular initialization dependencies can still produce surprising default values or errors.

### Q31. What happens if a static initializer throws an exception?

**Answer:** If the thrown value is not an `Error`, the JVM may wrap it in `ExceptionInInitializerError`. The class becomes erroneous for that class loader, and later active uses commonly fail with `NoClassDefFoundError`.

### Q32. What is the initialization-on-demand holder idiom?

**Answer:** It uses a nested class whose static field holds the singleton. The nested class is initialized only when first actively used, and JVM class-initialization guarantees provide safe one-time initialization without explicit synchronization.

### Q33. Can a class be unloaded?

**Answer:** Yes, class unloading is possible, but it usually requires that the defining class loader and all classes it defined become unreachable. The JVM specification does not require an implementation to unload classes at a particular time.

### Q34. Can one class be unloaded while its defining class loader remains reachable?

**Answer:** In ordinary class-loader-based unloading, a loader and the classes it defines are generally reclaimed as a group when the loader becomes unreachable. Do not design application correctness around a precise unloading time.

## 3. Class loaders and class identity

### Q35. What is a class loader?

**Answer:** A class loader locates class bytes and defines runtime classes from them. It can obtain classes from directories, JAR files, modules, networks, or custom sources.

### Q36. What built-in class loaders are commonly discussed in modern Java?

**Answer:** The bootstrap loader loads core runtime classes; the platform loader loads platform classes; and the application/system loader loads application classes from the application class path or module path. Details can vary by runtime implementation.

### Q37. What is parent-first delegation?

**Answer:** A loader normally asks its parent to load a class before trying to define it itself. This helps applications share platform classes and avoids casually replacing core types with application copies. Some specialized loaders use different delegation policies.

### Q38. Is parent delegation an unbreakable JVM rule?

**Answer:** No. It is the common behavior of the built-in `ClassLoader` implementation, but custom loaders can change lookup behavior for specific systems, such as application servers or plugin environments. Such changes require careful isolation and compatibility reasoning.

### Q39. What is class identity in Java?

**Answer:** A runtime class is identified by its binary name and its defining class loader. Two classes with the same name but different defining loaders are different types and generally cannot be cast to each other.

### Q40. Why can a cast fail even when the class names look identical?

**Answer:** The two objects may have been created from classes with the same binary name but different defining class loaders. Print the class loader for each object's `getClass()` and inspect the origins of the class files.

### Q41. What is the difference between a defining loader and an initiating loader?

**Answer:** The defining loader is the loader that creates the runtime class with `defineClass`. An initiating loader is a loader that was involved in requesting or initiating the loading, even if it delegated to another loader that ultimately defined the class.

### Q42. What is the thread context class loader?

**Answer:** It is a class loader associated with a thread and commonly used by frameworks or libraries to find application-provided classes or resources. It supports patterns where platform/library code needs to load classes from an application or plugin.

### Q43. Why might a framework use the context class loader?

**Answer:** The framework's own classes may be loaded by a parent loader, while application implementations live in a child loader. Using the thread context class loader can let the framework discover those application classes without hard-coding the child loader.

### Q44. What is a custom class loader used for?

**Answer:** Common uses include plugin isolation, application servers, instrumentation, encrypted or generated classes, and specialized loading sources. Since Java 9, modules also provide strong structure for access and visibility; a custom loader is not automatically needed for every plugin design.

### Q45. What does `findClass()` normally do in a custom loader?

**Answer:** It locates the class bytes and calls `defineClass()` to define them. A custom loader commonly overrides `findClass()` while preserving the standard `loadClass()` delegation behavior, unless it has a specific reason to change delegation.

### Q46. What is a class-loader constraint?

**Answer:** It is a JVM rule that ensures types in method or field descriptors are resolved consistently across related classes. If separate loaders resolve a descriptor type incompatibly, loading or linkage can fail rather than allowing an invalid type relationship.

### Q47. What can cause `ClassCastException: X cannot be cast to X`?

**Answer:** It often means the two `X` types have the same name but were defined by different class loaders. Compare both class objects and their defining loaders; also confirm that duplicate copies of the JAR are not being loaded.

### Q48. How can a class-loader leak happen after redeploy?

**Answer:** Something still reachable from a long-lived parent loader or thread may reference an object, class, or static value from the old application loader. Common anchors include thread locals, unmanaged threads, static caches, callbacks, and registered drivers. The old loader then cannot be collected.

### Q49. How would you reduce class-loader leak risk in a plugin or web application?

**Answer:** Provide a clear shutdown lifecycle: stop owned threads, clear thread-local values, deregister callbacks and listeners, close resources, and remove global cache entries that reference plugin classes. Then verify that old loaders become unreachable using class-loader statistics and heap analysis.

### Q50. What are common class-loader-related runtime failures?

**Answer:** `ClassNotFoundException`, `NoClassDefFoundError`, `ClassCastException`, `LinkageError`, `NoSuchMethodError`, and `IncompatibleClassChangeError`. The first step is to identify the requesting and defining loaders and the actual class or JAR origins.

## 4. Runtime data areas and frames

### Q51. What are JVM runtime data areas?

**Answer:** They are memory and execution structures the JVM uses while running. The JVMS describes areas such as the heap, per-thread stacks, program counter, method area, runtime constant pools, and native method stacks.

### Q52. Which runtime data areas are per-thread?

**Answer:** Each JVM thread has its own program counter and JVM stack. A thread may also have a native method stack, depending on the JVM implementation. The heap and method area are shared among threads.

### Q53. What does the JVM heap hold?

**Answer:** The heap is the runtime area from which objects and arrays are allocated. It is shared by threads. Garbage collection of the heap is covered in the separate GC topic.

### Q54. What is a JVM stack?

**Answer:** Each JVM thread has a stack that stores frames for method invocations. Calling a method creates a frame; returning from it removes that frame. Deep recursion or very large frames can exhaust stack resources.

### Q55. What is a stack frame?

**Answer:** A frame is a method's runtime workspace. It contains the method's local variables, operand stack, and information needed to support dynamic linking and returning from the method.

### Q56. What is stored in a frame's local-variable array?

**Answer:** It stores method parameters and local values. In an instance method, slot zero is normally `this`; in a static method, there is no receiver in slot zero. Some values such as `long` and `double` consume two local-variable slots in the JVM model.

### Q57. What is an operand stack?

**Answer:** It is a per-frame stack used by bytecode instructions to pass operands and results. For example, an integer-add instruction consumes two integer values from it and pushes their sum.

### Q58. How is the operand stack different from the JVM stack?

**Answer:** The JVM stack is a per-thread sequence of frames. Each frame has its own operand stack. Therefore, a thread can have many operand stacks—one for each active method frame.

### Q59. What is the program counter (PC) register?

**Answer:** The JVM specification gives each thread a PC register that tracks the current JVM instruction for its current method. Its value is undefined while the thread executes a native method.

### Q60. What is the method area?

**Answer:** It is a logical runtime area described by the JVMS for per-class structures such as method data and runtime constant pools. It is a specification concept; the spec does not require every JVM to implement it as one contiguous physical memory region.

### Q61. Is Metaspace the same thing as the JVMS method area?

**Answer:** No. Metaspace is a HotSpot implementation detail used for class metadata, stored in native memory. It is commonly discussed as the implementation counterpart for some method-area data, but the terms are not specification-level synonyms.

### Q62. What is the runtime constant pool?

**Answer:** It is a per-class or per-interface runtime structure derived from the class-file constant pool. It supports literals and symbolic references used during execution and linkage.

### Q63. Where does a local variable live: stack or heap?

**Answer:** A local variable belongs to a method frame in the JVM model. If it holds an object reference, the referenced object is generally allocated on the heap. Optimizing compilers can change physical implementation details while preserving required behavior.

### Q64. Are Java objects always physically allocated on the heap?

**Answer:** The Java programming model treats objects as heap objects, but a JIT compiler may optimize away an allocation or replace an object with scalar values when it can prove this is unobservable. Do not assume every `new` creates a permanent physical heap allocation.

### Q65. What is the native method stack?

**Answer:** It supports execution of native methods or other implementation-specific native work. The JVMS permits implementations to omit or combine it with other stacks.

### Q66. What is the code cache in HotSpot?

**Answer:** It is memory used by HotSpot for compiled native code and related compiler data. It is a HotSpot implementation area, not one of the separately mandated JVMS runtime data areas.

### Q67. Does the JVMS dictate exact memory layout for Java objects?

**Answer:** No. It specifies behavior, not a universal object header, field ordering, pointer size, or alignment. Those can depend on JVM implementation, architecture, flags, and release.

### Q68. What is the difference between `StackOverflowError` and `OutOfMemoryError`?

**Answer:** `StackOverflowError` commonly indicates that a thread needs more stack for method frames than is available, often due to deep recursion. `OutOfMemoryError` indicates that the JVM or native environment could not satisfy a memory allocation request; its message identifies the area when available.

### Q69. Can a Java process run out of memory even when the Java heap is not full?

**Answer:** Yes. Metaspace, direct buffers, thread stacks, native libraries, code cache, and other native allocations can consume process memory. Container limits can also terminate a process before the Java heap reaches its maximum.

### Q70. What does `-Xss` control?

**Answer:** In HotSpot, it controls the stack size for each platform thread, subject to platform and runtime constraints. A smaller value may let a process create more threads but makes stack overflow easier; a larger value consumes more virtual/native memory per thread.

## 5. Bytecode, invocation, and dispatch

### Q71. What does a bytecode instruction contain?

**Answer:** It contains an opcode and may contain operands. The opcode describes an operation; operands provide values such as a constant-pool index or local-variable index.

### Q72. What does `javap -c` show?

**Answer:** It disassembles methods and shows their bytecode instructions. It helps connect source code to the class-file representation, but the bytecode is not necessarily the machine code the processor executes after JIT compilation.

### Q73. Name the five main method-invocation bytecodes.

**Answer:** `invokevirtual`, `invokeinterface`, `invokestatic`, `invokespecial`, and `invokedynamic`. Each has a different role in method invocation or dynamic call-site linkage.

### Q74. What is `invokevirtual` commonly used for?

**Answer:** It commonly invokes instance methods with virtual dispatch, where the runtime receiver type helps select an overriding implementation.

### Q75. What is `invokeinterface` commonly used for?

**Answer:** It invokes an interface method using the receiver's runtime class to select an implementation. The JIT can optimize frequent interface call sites when it can prove or speculate about the receiver types.

### Q76. What is `invokestatic` used for?

**Answer:** It invokes a static method. There is no receiver object for virtual dispatch.

### Q77. What is `invokespecial` commonly used for?

**Answer:** It is used for certain non-virtual calls, including constructors, private methods, and explicit superclass method invocations, according to the JVM rules.

### Q78. What is `invokedynamic`?

**Answer:** It is a bytecode instruction whose call site is linked dynamically using a bootstrap method. It supports flexible language features such as Java lambdas and method references.

### Q79. Does a Java lambda always compile to an anonymous inner class?

**Answer:** No. Modern Java compilers commonly use `invokedynamic` and the `LambdaMetafactory` machinery. The JVM is free to choose an implementation strategy, so do not depend on one generated class shape.

### Q80. Does `invokedynamic` invoke the lambda body every time the lambda is created?

**Answer:** No. The instruction links a call site using a bootstrap method. The resulting call-site behavior is then used for subsequent executions. Invoking a functional interface's method later is a separate operation.

### Q81. What is method resolution versus method selection?

**Answer:** Resolution determines the method referred to by a symbolic reference under JVM rules. Selection chooses the implementation for a particular invocation, taking the receiver type and overriding rules into account where applicable.

### Q82. What is virtual dispatch?

**Answer:** It is runtime selection of an overridden instance method based on the actual object's class, rather than only the reference variable's declared type. Static methods are hidden rather than overridden.

### Q83. How can the JIT optimize virtual calls?

**Answer:** If profiling shows a call site usually sees one or a few receiver types, the JIT may speculate and inline the target method. It must preserve correctness, often by guarding the assumption and deoptimizing if the assumption stops being valid.

### Q84. What is `invokedynamic` useful for beyond Java lambdas?

**Answer:** It allows a language runtime or framework to define call-site linkage using a bootstrap method. It is useful for dynamic languages, method references, and other features where the target behavior is not a simple fixed method reference in the class file.

## 6. Interpreter, JIT, and compilation behavior

### Q85. What is the bytecode interpreter?

**Answer:** It executes JVM bytecode instructions at runtime without first compiling the whole method into native machine code. It can begin executing code quickly but may execute hot code less efficiently than optimized native code.

### Q86. What is JIT compilation?

**Answer:** Just-in-time compilation converts frequently executed bytecode into native machine code while the application runs. The JVM can use runtime information to optimize code for observed behavior.

### Q87. Does the JIT compile every method?

**Answer:** No. Compilation is based on runtime policy and observed execution. Cold methods may remain interpreted or use lower compilation levels; hot methods are more likely to receive more optimization.

### Q88. What is a hot method?

**Answer:** It is a method or code region executed often enough to be considered important by the runtime's profiling and compilation policy. “Hot” is a runtime decision, not a Java language annotation.

### Q89. What is tiered compilation in HotSpot?

**Answer:** It combines interpretation and multiple compilation levels. In common HotSpot configurations, lower-level compilation can provide quicker compiled execution and profiling, while a more optimizing compiler can later produce faster code. Exact policies can vary by JVM version and configuration.

### Q90. What are C1 and C2?

**Answer:** In HotSpot, C1 is the client compiler, designed for relatively fast compilation and profiling, while C2 is the optimizing server compiler, designed for stronger peak optimizations. This is HotSpot terminology, not a requirement of the JVM specification.

### Q91. Why can a Java service become faster after running for a while?

**Answer:** The JVM may collect profiles and compile frequently used methods with optimizations. This warm-up behavior can make steady-state performance differ from startup performance.

### Q92. What is method inlining?

**Answer:** Inlining replaces a method call with the method's body at a call site. It can remove call overhead and expose more optimization opportunities, but it can increase compiled code size.

### Q93. What is escape analysis?

**Answer:** It is an optimization analysis that checks whether an object can be observed outside a method or thread. If the object does not escape, the JIT may remove the allocation or avoid some synchronization, when it can preserve program behavior.

### Q94. Does escape analysis mean Java objects are simply allocated on the stack?

**Answer:** Not necessarily. A common result is scalar replacement, where fields are represented as separate values and the object allocation disappears. “Escape analysis equals stack allocation” is an oversimplification and is not guaranteed by the Java specification.

### Q95. What is deoptimization?

**Answer:** It is when the runtime stops using optimized compiled code because an assumption or condition no longer holds, and resumes execution in less-optimized code while preserving the program's state. It is a normal mechanism, not automatically a JVM bug.

### Q96. Why do JIT optimizations rely on assumptions?

**Answer:** Runtime profiling can show that a method usually sees one receiver type or one branch outcome. The JIT can optimize for that common case and retain a guard or recovery path for cases that differ.

### Q97. What is a megamorphic call site?

**Answer:** It is a virtual or interface call site that sees many receiver types. This can make a single-target inline optimization less effective, though the JIT may still use other strategies.

### Q98. Why can an optimization cause deoptimization later?

**Answer:** The JIT may optimize based on profiling assumptions, such as a class hierarchy or observed receiver type. If the assumption becomes invalid—perhaps after another class is loaded—the runtime can invalidate the compiled code and return to a safer execution path.

### Q99. What is on-stack replacement (OSR)?

**Answer:** OSR allows the runtime to transfer a method that is already running, often in a loop, from interpreted execution to compiled code without waiting for the method to return and be invoked again.

### Q100. What is the code cache, and what happens if it is under pressure?

**Answer:** In HotSpot it stores generated native code. If it becomes full or fragmented, the JVM may be unable to compile more code or may remove compiled code. Investigate compiler/code-cache logs and JFR rather than assuming a Java heap problem.

### Q101. Why are microbenchmarks especially vulnerable to JIT effects?

**Answer:** A benchmark may measure startup, interpretation, compilation, dead-code elimination, or profile pollution instead of the operation being studied. Use JMH, warm-up iterations, forks, and realistic inputs; verify what the benchmark actually measures.

### Q102. Is `-Xcomp` a normal performance fix?

**Answer:** No. It forces compilation behavior that may increase startup cost and can expose different compiler paths. Use it for controlled investigation, not as a general production tuning shortcut.

## 7. Runtime errors and linkage failures

### Q103. What is `ClassNotFoundException`?

**Answer:** It is a checked exception commonly thrown when application code explicitly asks a loader to load a class by name and the loader cannot find it.

### Q104. What is `NoClassDefFoundError`?

**Answer:** It is an `Error` indicating that the JVM could not find or use a class definition that was expected during execution. It can happen because a class is missing at runtime or because earlier class initialization failed.

### Q105. `ClassNotFoundException` vs. `NoClassDefFoundError`?

**Answer:** `ClassNotFoundException` is typically an explicit lookup failure from APIs such as `Class.forName`. `NoClassDefFoundError` is a linkage/runtime error when execution needs a class definition that is unavailable or unusable.

### Q106. What is `ExceptionInInitializerError`?

**Answer:** It reports that a class or interface initialization failed because its static initialization code threw an exception or error. Inspect its cause and the earliest initialization failure.

### Q107. Why can the second use of a class fail with `NoClassDefFoundError` after its static initializer failed?

**Answer:** The class has been marked erroneous for that defining loader after initialization failed. Later active uses do not simply rerun the initializer; they commonly fail with `NoClassDefFoundError`.

### Q108. What is `VerifyError`?

**Answer:** It is a `LinkageError` raised when the JVM detects invalid or inconsistent bytecode during verification. It can result from malformed generated bytecode, incompatible instrumentation, or broken class transformation.

### Q109. What is `NoSuchMethodError`?

**Answer:** It is a `LinkageError` raised when runtime code expects a method that is absent from the class actually loaded. A common cause is compiling against one library version and running with an incompatible version.

### Q110. What is `IncompatibleClassChangeError`?

**Answer:** It indicates that a class changed incompatibly in a way detected during linkage or invocation, such as code expecting an instance method while the runtime class provides a static method.

### Q111. What is `UnsupportedClassVersionError`?

**Answer:** It occurs when a JVM cannot support the class-file version of a class it is asked to load. Check the build JDK, target release, runtime JDK, and the class-file version of the failing dependency.

### Q112. Why can a dependency update cause a linkage error even though compilation passed?

**Answer:** The compile-time and runtime class paths may differ, or runtime dependency resolution may select a binary-incompatible version. Compare the actual loaded JAR and class-loader origin with the version used at compile time.

### Q113. When can a linkage error appear: at startup or later?

**Answer:** It depends on when the relevant class or symbolic reference is loaded and resolved. Resolution can be eager or lazy, so an invalid reference may fail during startup or only when a particular code path is first executed.

## 8. Startup, class data sharing, and runtime configuration

### Q114. What is Class Data Sharing (CDS)?

**Answer:** CDS uses an archive of class metadata so a JVM can start more quickly and, in some deployments, share read-only metadata across JVM processes. It is a JVM feature, and support/details depend on runtime distribution and configuration.

### Q115. What is AppCDS?

**Answer:** Application Class Data Sharing extends CDS to selected application classes. It can reduce startup work and memory footprint in supported JVM configurations.

### Q116. Does CDS eliminate class loading?

**Answer:** No. It can let the JVM reuse archived class metadata and reduce some loading work, but the application still starts a JVM, verifies/uses classes as required, initializes classes when triggered, and executes application code.

### Q117. What is the difference between JVM startup time and application readiness time?

**Answer:** JVM startup time is the time to start the runtime process. Application readiness also includes class loading, initialization, dependency setup, network connections, framework startup, and readiness checks. Optimize the measured bottleneck, not just the launcher.

### Q118. What is a class-file version?

**Answer:** It records the target class-file format and features the compiler used. The runtime must support that version. `javap -verbose` can show the major version, and `UnsupportedClassVersionError` commonly signals a version mismatch.

### Q119. What is the difference between class path and module path?

**Answer:** The class path loads classes in the traditional unnamed-module model. The module path resolves named modules with declared dependencies and encapsulation rules. Both affect which classes are available, but module-path errors can involve readability or exports as well as missing classes.

### Q120. Does `--release` only set the bytecode version?

**Answer:** No. `javac --release` selects a target release's language rules, class-file target, and documented platform APIs. It helps avoid compiling against APIs unavailable in the intended runtime.

## 9. JVM diagnostics and production investigation

### Q121. What is `jcmd`?

**Answer:** It is a JDK tool for sending diagnostic commands to a running JVM. It can print threads, inspect VM flags, collect class-loader statistics, and request other diagnostic information supported by that JVM.

### Q122. What is a thread dump useful for?

**Answer:** It shows thread states and stack traces at a point in time. It can reveal blocked threads, deadlocks, hot call paths, thread-pool starvation, and where threads are waiting.

### Q123. How do you collect a thread dump with `jcmd`?

**Answer:** Find the target JVM process, then run `jcmd <pid> Thread.print`. Check the tool's help and permissions for the exact JDK and environment. For a changing incident, compare multiple dumps rather than relying on one snapshot.

### Q124. What is Java Flight Recorder (JFR)?

**Answer:** JFR is a JVM event-recording and profiling framework. It can capture timed events such as thread activity, locks, allocations, compiler activity, and GC events. Oracle documents it as suitable for many production investigations with low overhead when configured appropriately.

### Q125. What is JDK Mission Control (JMC)?

**Answer:** JMC is a tool for analyzing JFR recordings and monitoring JVMs. It helps explore timelines, hot methods, threads, locks, and other runtime events.

### Q126. How do you inspect class-loading activity?

**Answer:** Use unified JVM logging, for example `-Xlog:class+load=info` on modern HotSpot, or inspect class-loader statistics with `jcmd <pid> VM.classloader_stats` where supported. Confirm the exact option for the target JDK.

### Q127. How do you check the actual JVM command line and flags?

**Answer:** Use process-management tooling and supported `jcmd` commands such as `VM.command_line` and `VM.flags`. Compare the effective runtime settings with the deployment configuration; don't assume a container or service manager passed the flags you expect.

### Q128. How can you inspect the bytecode of the class actually being used?

**Answer:** First identify its defining loader and code source, then inspect that exact class file or JAR with `javap -classpath ... -c -v`. Inspecting a similarly named class from another JAR can lead to the wrong conclusion.

### Q129. How would you investigate high Metaspace use?

**Answer:** Separate normal class-metadata growth from a leak. Inspect class-loader statistics over time, compare loaded and unloaded class counts, examine redeploy/plugin behavior, and use heap analysis to find references retaining old loaders. Metaspace tuning belongs to the separate memory/GC investigation.

### Q130. How would you investigate native memory growth?

**Answer:** Determine whether the growth is heap, thread stacks, direct buffers, class metadata, JIT/code cache, JNI, or another native allocation. Where supported, enable Native Memory Tracking at startup and inspect it with `jcmd <pid> VM.native_memory`; also check process and container metrics.

### Q131. What is Native Memory Tracking (NMT)?

**Answer:** NMT is a HotSpot feature that tracks selected JVM native-memory allocations by category. It must generally be enabled at startup and adds overhead. It does not account for every allocation made by arbitrary native libraries.

### Q132. How do you investigate JIT compilation behavior?

**Answer:** Use JFR, unified logging such as compiler-related `-Xlog` tags supported by the target JDK, and relevant JVM diagnostics. Correlate compilation, deoptimization, code-cache state, and latency rather than changing compilation flags blindly.

### Q133. What is a safe approach to production diagnostics?

**Answer:** Start with low-risk, read-only evidence: metrics, logs, thread dumps, and a bounded JFR recording. Check overhead and disk impact before heap dumps or verbose logging, which can pause or stress a service. Preserve timestamps and correlate evidence with user-visible latency.

## 10. Scenario-based interview questions

### Q134. A class works in a developer's machine but throws `NoSuchMethodError` in production. How do you investigate?

**Answer:** Identify the exact class and method in the error. Inspect the runtime class origin and loader, dependency tree, packaged artifact, and class-file/API version used at compile time. Look for duplicate JARs, shading, container-provided libraries, or deployment drift. Reproduce with the production artifact and class path.

### Q135. A program prints a static-initializer message later than expected. What would you check?

**Answer:** Check whether the operation is an active use that triggers initialization. A compile-time constant read may not initialize the class. Also verify the actual class used and whether a different loader defines another copy.

### Q136. An application reports `ClassNotFoundException` for a plugin implementation. What evidence do you gather?

**Answer:** Find which code requested the class, which loader performed the lookup, the context class loader at that point, and whether the plugin JAR and dependencies are visible to that loader. Check module readability/exports if using named modules.

### Q137. A cast fails with `Plugin cannot be cast to Plugin`, but both objects print the same class name. What is your hypothesis?

**Answer:** They were probably defined by different class loaders. Compare `object.getClass().getClassLoader()` and the expected class's loader, then inspect the parent/child loader structure and duplicate JAR sources.

### Q138. Metaspace rises after every application redeploy, then the service eventually fails. How do you investigate?

**Answer:** Track class-loader counts and class counts across redeploys. Find old application loaders in a heap dump and identify their GC roots—often a thread local, thread, static registry, callback, or cache. Fix the retained reference and ensure the application shutdown lifecycle releases its resources.

### Q139. A service has slow response times immediately after deployment, then improves. Is the JIT definitely the cause?

**Answer:** No. JIT warm-up is one hypothesis. Compare startup and steady-state JFR recordings, compilation activity, class loading, initialization, connection pools, caches, DNS, and downstream behavior. Use a representative warm-up and load test before changing JIT flags.

### Q140. A short benchmark shows Java code is slower than expected. What would you ask before trusting it?

**Answer:** Ask whether it uses JMH, has warm-up and multiple forks, prevents dead-code elimination, uses representative data, avoids benchmark interference, and measures the intended operation. Also inspect whether compilation and allocation behavior differ from production.

### Q141. A team proposes `-Xcomp` to eliminate a slow warm-up. What trade-offs would you explain?

**Answer:** Forced compilation can increase startup time, compile cold code unnecessarily, and change resource use. First determine whether the delay is actually JIT warm-up. Consider CDS, application initialization improvements, realistic warm-up traffic, or targeted profiling before a broad compilation-mode change.

### Q142. The JVM process is killed by its container, but heap metrics are below `-Xmx`. What could explain this?

**Answer:** Process memory includes native memory outside the Java heap, such as thread stacks, Metaspace, direct buffers, code cache, JNI, and runtime structures. Container overhead and other processes may also count against the limit. Compare process RSS, container limits, NMT, thread count, and direct-memory usage.

### Q143. A class transformation agent causes `VerifyError` after a JDK upgrade. What do you check?

**Answer:** Capture the transformed class bytes and inspect them with `javap -v`. Check stack-map frames, bytecode version, verifier rules, agent compatibility, and transformation order. Reproduce with the exact JDK and minimal agent configuration.

### Q144. Application startup fails with `ExceptionInInitializerError`. What is the first useful step?

**Answer:** Inspect the cause chain for the original exception from the static initializer. Identify which class was initializing and why—configuration, missing dependency, recursive initialization, or external work. Fix the root cause rather than treating the later `NoClassDefFoundError` as a separate missing-JAR problem.

### Q145. A service's class-loading logs show many copies of one library class. What could be happening?

**Answer:** Multiple class loaders may each define the class, perhaps due to plugin isolation, duplicate libraries, redeploys, or an unexpected loader hierarchy. Determine whether the duplication is intentional and whether it is retaining memory or causing type incompatibility.

### Q146. A custom class loader hangs during startup under load. What class-loader issue would you consider?

**Answer:** Look for lock contention or deadlock during class loading, recursive loading dependencies, and whether the loader supports parallel class loading correctly. Capture thread dumps and inspect loader synchronization paths before changing delegation behavior.

### Q147. A framework cannot find an application provider even though the provider JAR is present. Why might that happen?

**Answer:** The framework may be using the wrong class loader, such as its own defining loader rather than the thread context class loader. Check service/provider configuration, module declarations, class-loader visibility, and the actual resource lookup path.

### Q148. An exception appears only the first time a rarely used feature runs. Why might that be related to resolution?

**Answer:** The JVM may resolve a symbolic reference lazily when the feature first uses it. A missing class, method, or incompatible dependency can therefore remain hidden until that path executes.

### Q149. A JIT optimization appears to make a bug disappear in debug mode. What would you investigate?

**Answer:** Suspect a data race, unsafe publication, timing-sensitive logic, or reliance on an implementation detail. Debugging changes timing and compilation. Reproduce under different optimization levels, but reason from JMM guarantees and use race-focused testing and diagnostics.

### Q150. A team says, “The JVM always allocates this object on the stack.” How would you correct the statement?

**Answer:** The Java and JVM specifications do not require a fixed physical allocation location for every object. The JIT may remove an allocation or scalar-replace it when safe, but that is an optimization, not a source-level guarantee. State the observable behavior, not an assumed memory layout.

## Quick comparison table

| Term | Simple meaning |
|---|---|
| Loading | Find class bytes and create the runtime class |
| Linking | Verify, prepare, and resolve the class |
| Initialization | Run static initializers when an active use requires it |
| Defining class loader | Loader that actually creates the runtime class |
| Runtime data area | JVM execution/memory structure described by the JVMS |
| Metaspace | HotSpot's native-memory class metadata area |
| Interpreter | Executes bytecode without compiling the whole method to native code |
| JIT | Compiles hot bytecode to native code while the program runs |
| Deoptimization | Leaves optimized code when an assumption or condition no longer holds |
| CDS/AppCDS | Archives class metadata to reduce startup work and/or memory footprint |

## Sources

### Primary specifications and official documentation

- [Java Language Specification, Java SE 21 — Chapter 12: Execution](https://docs.oracle.com/javase/specs/jls/se21/html/jls-12.html) — startup, loading, linking, initialization, class unloading, and program exit.
- [Java Virtual Machine Specification, Java SE 21 — Chapter 2: Structure](https://docs.oracle.com/javase/specs/jvms/se21/html/jvms-2.html) — runtime data areas, frames, and class-file model.
- [Java Virtual Machine Specification, Java SE 21 — Chapter 5: Loading, Linking, and Initializing](https://docs.oracle.com/javase/specs/jvms/se21/html/jvms-5.html) — class loaders, resolution, and initialization.
- [Java Virtual Machine Specification, Java SE 21 — Chapter 6: Instruction Set](https://docs.oracle.com/javase/specs/jvms/se21/html/jvms-6.html) — bytecode instructions.
- [Java SE 25 `ClassLoader` API](https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/lang/ClassLoader.html) — built-in and custom loader APIs.
- [Java SE 25 `java.lang.invoke` package](https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/lang/invoke/package-summary.html) — method handles, call sites, and `invokedynamic`.
- [Oracle Java SE 25 — Class Data Sharing](https://docs.oracle.com/en/java/javase/25/vm/class-data-sharing.html) — CDS and AppCDS.
- [Oracle Java SE 21 Troubleshooting Guide — Diagnostic Tools](https://docs.oracle.com/en/java/javase/21/troubleshoot/diagnostic-tools.html) — JFR, JMC, `jcmd`, and JVM diagnostics.
- [Oracle Java SE 25 documentation home](https://docs.oracle.com/en/java/javase/25/) — official runtime, tool, and API documentation.

### Interview-topic cross-check

- [CodeBegun — Java JVM Interview Questions](https://www.codebegun.com/interview/java/jvm) — class loading, runtime areas, JIT, and bytecode themes.
- [Techoral — Java Senior Interview Questions](https://techoral.com/java/java-senior-interview-questions.html) — senior-level JVM, memory, and performance themes.
- [Coderboi — Java Class Loading Explained](https://interview-prep.coderboi.com/blog/java-classloading) — class identity, delegation, initialization, and loader-leak scenarios.
- [CodeWithVenu — JIT Compiler Interview Questions](https://www.codewithvenu.com/blog/Interview-Tracks/Java-Interview/JVM/04-JIT-Compiler-QA) — JIT, tiered compilation, optimizations, code cache, and warm-up themes.
- [Senior Engineer Interview Reference — JVM Internals](https://senior-engineer.dev/01-foundations/jvm) — senior-oriented production scenarios and diagnostic angles.

Interview guides help identify question patterns; the specifications and official API documentation are authoritative for Java behavior. HotSpot-specific details are labeled as implementation details and should be checked against the JVM and JDK version used in the interview or production system.
