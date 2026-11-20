# Java Architecture and Design Practices

[Back to index](index.md)

**Audience:** Senior Java developers and tech leads  
**Scope:** Application and codebase design, Java/Spring practices, and maintainability. This bank does not cover high-level system design or distributed-systems design.

Every question is followed immediately by an answer. Questions are original; interview guides below were used only to identify recurring topic patterns.

## Questions and answers

### Q1. What does “good architecture” mean for a Java application?

**Answer:** Good architecture makes important rules easy to find, change, test, and explain. It gives each part a clear responsibility and keeps dependencies pointing in useful directions. It is not a diagram, a framework choice, or the largest number of layers. A design is good when it solves today’s requirements while keeping likely changes affordable.

### Q2. How do you decide whether a class or package needs a separate responsibility?

**Answer:** Look for a single reason it changes and a clear job described without “and.” A class that parses input, applies business rules, writes to a database, and formats an HTTP response has several reasons to change. Split responsibilities where doing so creates meaningful boundaries; avoid tiny classes that only forward calls.

### Q3. Explain cohesion and coupling in practical terms.

**Answer:** Cohesion is how closely related the work inside a module is. Coupling is how much one module depends on the details of another. Prefer cohesive modules with few, explicit dependencies. Low coupling does not mean no dependencies; it means changes in one area do not unexpectedly ripple through unrelated areas.

### Q4. What is the Dependency Inversion Principle?

**Answer:** High-level policy should not depend directly on low-level implementation details; both should depend on abstractions owned by the policy. For example, an order rule can depend on a `PaymentAuthorizer` interface while an adapter implements it. Do not introduce an interface for every class: abstractions are useful when they protect a real boundary or allow meaningful substitution.

### Q5. How do dependency injection and dependency inversion differ?

**Answer:** Dependency inversion is a design principle about the direction of dependencies. Dependency injection is a way to provide an object’s dependencies from outside, often through a constructor. A dependency-injection container such as Spring helps create and connect objects, but using Spring annotations alone does not guarantee good dependency direction.

### Q6. Why is constructor injection usually a good default in Spring?

**Answer:** It makes required dependencies visible, supports immutable fields, and lets tests construct the object without starting the container. It also exposes a class that has too many dependencies. Use setter or optional injection only when a dependency is genuinely optional or must be changed after construction.

### Q7. What is the difference between a layered architecture and a ports-and-adapters architecture?

**Answer:** A layered architecture groups code by technical role, such as web, service, and persistence. Ports and adapters organizes around application policy: the core defines ports, and external technologies implement adapters. Either can work. A layered design can leak database or framework details inward; ports and adapters can add unnecessary interfaces if the application has no meaningful boundary to protect.

### Q8. How would you organize a growing Spring Boot codebase?

**Answer:** Start with a clear application entry point and group related code by feature or domain responsibility, such as `orders`, `customers`, and `billing`. Keep web, application, and persistence details inside those areas where useful. Avoid a single huge `service` or `util` package. Enforce package boundaries with architecture tests when the codebase and team need them.

### Q9. When should you choose a modular monolith instead of many deployable services?

**Answer:** Choose a modular monolith when the team benefits from one deployment but needs clear internal ownership and boundaries. It is easier to debug, test, and change consistently than a collection of services. Split a module into a separate deployment only when an actual need—such as independent release, scaling, or ownership—outweighs the extra operational and communication cost.

### Q10. How do you decide where a business rule belongs?

**Answer:** Put the rule near the data and concepts it governs, but keep it independent of transport and storage mechanics. A rule such as “a cancelled order cannot be shipped” should not live only in a controller or a database repository. Ensure all relevant entry points use the same rule, and use domain objects or focused application services when they make the rule clearer.

### Q11. What is the purpose of a repository abstraction?

**Answer:** A repository gives application code a domain-oriented way to load and save data while hiding query mechanics. It is useful when it clarifies persistence boundaries or enables tests at the right level. A generic repository interface that merely renames every ORM method can add indirection without hiding any meaningful detail.

### Q12. How should you design interfaces in Java?

**Answer:** Design an interface around a stable responsibility and the needs of its callers. Keep the surface small, name methods in the caller’s language, and avoid exposing implementation-specific types. Do not add an interface just because a class exists; add one when substitution, a boundary, or a separately evolving contract justifies it.

### Q13. What makes an API change backward-compatible?

**Answer:** Existing callers continue to compile and behave as before. Adding a required constructor parameter, changing a method’s meaning, narrowing accepted input, or changing exception behavior may break callers even if source code still compiles. Prefer additive changes, document deprecations, and use compatibility tests or consumer feedback before removing old behavior.

### Q14. When are Java records a good fit?

**Answer:** Records work well for transparent data carriers whose state is fixed after construction, such as request-independent value objects or query results. Their components define the data shape and generate accessors, equality, and other methods. A record is only shallowly immutable: a mutable object held by a component can still change. Avoid records when identity, mutable lifecycle, or a specialized inheritance model is central.

### Q15. What is the difference between immutability and an unmodifiable view?

**Answer:** An immutable object cannot have its observable state changed after construction. An unmodifiable collection view blocks mutation through that view, but the original collection or its elements may still change. For defensive immutability, copy mutable inputs and avoid exposing mutable internals.

### Q16. How do you choose between `Optional<T>` and `null`?

**Answer:** `Optional<T>` can make an absent return value explicit, especially for a method result. It is usually not a good field, parameter, or collection element type; use a clear validation rule or an empty collection there instead. Do not return `null` from a method declared to return `Optional`, and do not use `Optional` to hide unexpected failures.

### Q17. How should exceptions be handled across application layers?

**Answer:** Throw an exception when an operation cannot complete normally, preserve the original cause when translating it, and convert failures to user-facing responses at a deliberate boundary. Use meaningful exception types and avoid catching broad exceptions only to log and continue. Do not expose stack traces, SQL, or internal class names to external callers.

### Q18. When should you create a custom exception?

**Answer:** Create one when it communicates a distinct, actionable failure that callers need to handle differently. A custom type for every method adds noise. Include useful context without sensitive data, and preserve the original cause when wrapping a lower-level failure.

### Q19. What is the difference between validation and business-rule enforcement?

**Answer:** Validation checks whether input is well-formed and within basic constraints, such as a required field or valid email syntax. A business rule checks whether an operation is allowed in the current domain state, such as whether an account may be closed. Both must be enforced at a trusted application boundary; client-side validation is only a usability aid.

### Q20. Where should a Spring transaction boundary usually go?

**Answer:** Put it around a complete application operation that must succeed or fail as a unit, often at a service or application-service method. Keep the transaction no broader than necessary, and avoid slow external work inside it. Know Spring’s proxy behavior: self-invocation may bypass proxy-based transaction advice, and rollback rules depend on exception type and configuration.

### Q21. What are common pitfalls with `@Transactional`?

**Answer:** Common pitfalls include calling a transactional method from another method on the same object, assuming checked exceptions always trigger rollback, using the wrong transaction manager, and doing long-running remote or user-interactive work while holding a database transaction. Confirm the effective proxy, propagation, isolation, and rollback settings rather than assuming the annotation alone guarantees the intended behavior.

### Q22. How do you keep framework details from spreading through business logic?

**Answer:** Keep framework annotations and HTTP or persistence types near their integration boundaries. Pass simple, deliberate application inputs inward and return results that represent application outcomes. Do not abstract every library away: contain framework coupling where it makes changes and tests easier, rather than wrapping stable APIs without a reason.

### Q23. How do you decide whether to use inheritance or composition?

**Answer:** Prefer composition when one object needs to use another object’s behavior; it avoids inheriting accidental methods and makes dependencies explicit. Use inheritance when the subtype truly satisfies the parent’s contract and callers can safely substitute it. Avoid base classes that expose protected implementation details or require fragile override ordering.

### Q24. How do you apply SOLID without turning a small application into a framework?

**Answer:** Treat SOLID as prompts for examining change boundaries, substitution, and dependency direction—not as a mandate to create an interface or class for every concept. Refactor when a concrete change reveals a problem. Keep the simplest design that has clear responsibilities and testable seams.

### Q25. A service class has 18 injected dependencies and many `if` branches. How would you approach it?

**Answer:** First identify the behaviors and reasons it changes; do not immediately split by line count. Group cohesive operations, move policy to the appropriate domain or application component, and use a strategy or polymorphism only when the alternatives represent real variation. Add focused tests before restructuring, then verify behavior and reduce the dependency count incrementally.

### Q26. A team wants to replace a working in-house library with a popular framework. What do you evaluate?

**Answer:** Compare required capabilities, security and maintenance status, compatibility, operational impact, learning cost, migration risk, and the ongoing cost of owning the existing solution. Prototype the riskiest integration and define measurable acceptance criteria. Popularity alone does not prove the change is beneficial.

### Q27. A shared package change breaks several teams’ builds. What would you improve?

**Answer:** Clarify ownership and supported API, add compatibility or consumer tests for important contracts, and publish deprecation and migration guidance. Avoid exposing internal classes as public contracts. Use versioning and release notes that communicate behavior changes, and make the package’s compatibility promises realistic.

### Q28. A code review contains a large refactor mixed with a feature. Why is that risky, and what would you do?

**Answer:** The reviewer cannot easily distinguish behavior changes from movement or formatting, so regressions are harder to spot and rollback is less precise. Separate mechanical refactoring from behavior changes when practical, keep each change small, and add tests that demonstrate the intended behavior. If separation is impossible, explain the sequence and risk explicitly.

## Sources

### Official and technical references

- [Spring Boot: Structuring Your Code](https://docs.spring.io/spring-boot/reference/using/structuring-your-code.html) — package and application layout patterns.
- [Spring Framework: The IoC Container](https://docs.spring.io/spring-framework/reference/core/beans.html) — dependency injection and bean configuration.
- [Spring Framework: Declarative Transaction Management](https://docs.spring.io/spring-framework/reference/data-access/transaction/declarative.html) — proxy-based transaction behavior and configuration.
- [Java Language Specification, Java SE 21](https://docs.oracle.com/javase/specs/jls/se21/html/index.html) — language and type-system rules.
- [Java Records](https://docs.oracle.com/en/java/javase/21/language/records.html) — record semantics and usage.
- [Google Java Style Guide](https://google.github.io/styleguide/javaguide.html) — consistent Java style.

### Interview-topic pattern evidence only

- [Baeldung: Java Interview Questions](https://www.baeldung.com/java-interview-questions) — recurring interview topic patterns; not used as answer text.
- [Baeldung: Spring Interview Questions](https://www.baeldung.com/spring-boot-interview-questions) — recurring Spring topic patterns; not used as answer text.
