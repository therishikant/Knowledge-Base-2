# Java Testing and Code Quality

[Back to index](index.md)

**Audience:** Senior Java developers and tech leads  
**Scope:** Test design, build quality, static analysis, maintainability, and safe change.

Every question is followed immediately by an answer. Questions are original; interview guides below were used only to identify recurring topic patterns.

## Questions and answers

### Q1. What makes a test useful?

**Answer:** A useful test checks an observable behavior that matters, fails clearly when that behavior breaks, and is repeatable. It should be fast enough to run at the right point in development. A test that passes while the wrong behavior occurs, or that requires a developer to inspect output manually, provides little protection.

### Q2. Explain the test pyramid and its limitations.

**Answer:** The traditional pyramid suggests many fast unit tests, fewer integration tests, and a small number of end-to-end tests because slower broad tests cost more to run and diagnose. It is a useful starting point, not a fixed ratio. Choose the smallest test level that gives confidence in a behavior; a well-designed integration test can be more valuable than many brittle mocks.

### Q3. How do unit, integration, and end-to-end tests differ?

**Answer:** A unit test checks a small unit in isolation, often a method or class. An integration test checks that components work together, such as a service with a real database engine. An end-to-end test exercises a deployed-like path through several boundaries. Their main difference is the scope and dependencies being verified, not the test framework or test name.

### Q4. When should you use a real dependency instead of a mock?

**Answer:** Use a real dependency when its behavior is deterministic, cheap, and important to the interaction being tested—for example, a value object or an embedded test database. Use a test double when a dependency is slow, nondeterministic, costly, or needs a failure scenario that is otherwise hard to produce. Verify actual integration behavior in a test that uses the real technology.

### Q5. What is the difference between a stub, a mock, and a fake?

**Answer:** A stub returns prearranged values. A mock records or verifies expected interactions. A fake is a working but simplified implementation, such as an in-memory repository. The labels can vary by team. Prefer the least complex test double that makes the test clear, and avoid verifying incidental call order.

### Q6. How do you avoid over-mocking?

**Answer:** Mock only boundaries whose behavior the test does not need to verify. Construct the class under test and use real immutable values or simple collaborators where practical. Too many mocks make tests mirror the implementation, so harmless refactoring breaks them while integration defects remain undetected.

### Q7. What is Arrange–Act–Assert?

**Answer:** It is a test structure: arrange the initial state, act by invoking the behavior, and assert the observable result. Clear sections make a test easier to read and diagnose. Avoid excessive setup and multiple unrelated actions in one test.

### Q8. How should you name tests?

**Answer:** Name a test for the behavior and relevant condition, such as `rejectsExpiredToken` or `returnsEmptyWhenNoMatchingOrders`. A good name explains the failure without requiring a reader to decode a test number. Avoid names that merely repeat the method under test.

### Q9. What are parameterized tests useful for?

**Answer:** They exercise the same rule with several input and expected-output pairs without duplicating test logic. Use them for boundary values, parsing cases, and similar examples. Keep each case understandable; do not turn a parameter table into an opaque mini-language.

### Q10. How should tests cover edge cases?

**Answer:** Identify meaningful boundaries from the contract: empty input, minimum and maximum values, missing data, invalid state, and failure responses. Include cases that distinguish the desired behavior from likely mistakes. Do not create large collections of arbitrary values without a reason.

### Q11. How do you test code that uses the current time?

**Answer:** Inject a `Clock` or another time provider and use a fixed clock in tests. This makes time-dependent behavior repeatable and lets tests cover boundaries such as expiry. Avoid sleeping in tests; it is slow and still leaves timing races.

### Q12. How do you test random or generated values?

**Answer:** Make the generator or source of randomness replaceable, then supply a deterministic source in tests. For probabilistic properties, use a well-defined statistical test only when the expected distribution and tolerances are justified. A flaky test caused by unpredictable randomness should not gate normal builds.

### Q13. What does a good integration test for a Spring repository verify?

**Answer:** It verifies relevant mapping and query behavior against a database engine compatible with production, including constraints, transactions, and edge cases that an in-memory substitute may not reproduce. Keep fixtures focused and ensure each test controls its data. Use Testcontainers or an equivalent managed test database when fidelity matters.

### Q14. What are the benefits and costs of Testcontainers?

**Answer:** Testcontainers starts disposable instances of real services, improving confidence in database and broker integration behavior. The costs include startup time, container-runtime requirements, resource use, and test environment complexity. Use it for tests where real service behavior matters; do not start a container for every pure unit test.

### Q15. How do you keep tests independent?

**Answer:** Each test should establish its own inputs and leave no state that changes another test’s outcome. Avoid shared mutable fixtures, reliance on execution order, and external services with uncontrolled state. Cleanup is useful, but disposable environments and transaction rollback are often safer than manual cleanup code.

### Q16. What causes flaky tests, and how do you respond?

**Answer:** Common causes are timing assumptions, shared state, random input, order dependence, network instability, and incomplete cleanup. Reproduce the failure, preserve diagnostics, identify the source, and fix it rather than repeatedly rerunning or disabling the test. Quarantine may be a short-term containment step with an owner and deadline, not a permanent solution.

### Q17. What does code coverage measure, and what does it not measure?

**Answer:** Coverage tools report which lines, branches, or other code elements ran during selected tests. Coverage does not prove that assertions are correct, that outputs are checked, or that important requirements are covered. Use it to find untested code and gaps, not as a substitute for test quality or as a universal target.

### Q18. What is mutation testing?

**Answer:** A mutation tool makes small changes to production code, such as changing a comparison or return value, and checks whether tests fail. A surviving mutation can reveal a missing assertion or a behavior the tests do not distinguish. Mutation testing costs more time than ordinary coverage analysis, so apply it to critical or complex logic rather than blindly to every line.

### Q19. How do static analysis tools complement tests?

**Answer:** Static analysis can identify suspicious patterns, likely defects, style drift, and dependency problems without executing the program. Tests verify selected runtime behavior. Neither replaces the other. Configure rules to match the codebase, fix high-value findings, and avoid noisy policies that developers learn to ignore.

### Q20. How should a team use code formatting and style rules?

**Answer:** Automate stable formatting and basic style rules so reviews focus on behavior and design. Adopt a consistent formatter and commit its configuration. Avoid spending review time on subjective formatting when a tool can decide it; reserve judgment for readability, naming, and meaningful structure.

### Q21. What should a Java pull-request quality gate include?

**Answer:** At minimum, compile the code, run focused unit tests, and apply agreed static checks. Add integration tests and packaging or compatibility checks where the change requires them. Keep feedback fast and actionable, make failures reproducible, and put slower broad checks later in the pipeline without allowing them to disappear from release validation.

### Q22. A test suite takes 40 minutes and developers skip it. What do you do?

**Answer:** Measure where the time goes, then separate fast feedback from slower verification. Remove redundant setup, parallelize safe independent tests, use appropriate test scopes, and replace unnecessary full-environment tests with focused ones. Preserve tests that protect important behavior; do not simply delete slow tests without understanding their value.

### Q23. How can you test code that depends on a difficult-to-create legacy class?

**Answer:** First test observable behavior around the existing seam. Introduce a small adapter or wrapper where it reduces coupling, then characterize current behavior before refactoring. Avoid a large rewrite just to make a unit test possible; add seams incrementally and preserve compatibility.

### Q24. What is a characterization test?

**Answer:** It records what legacy code actually does, including behavior that may be surprising, so a refactor can preserve it. The test does not claim the behavior is ideal. After the system is understood, replace or refine tests to express the intended contract.

### Q25. What should a code review focus on?

**Answer:** Review correctness, edge cases, security, failure handling, compatibility, test quality, and whether the change fits existing boundaries. Ask concrete questions and distinguish blocking defects from preferences. Keep changes small enough to review, and do not rely on a reviewer to execute the code mentally instead of running tests.

### Q26. How do you decide whether a test belongs in a unit or integration suite?

**Answer:** Put it at the smallest level that can verify the behavior reliably. If correctness depends on framework wiring, SQL semantics, serialization, or an external integration, use an integration test for that part. A unit test can still cover business logic quickly; both may be warranted when they answer different questions.

### Q27. A developer raises the global coverage threshold after a coverage report. Why might that fail to improve quality?

**Answer:** A threshold can incentivize low-value tests that execute code without meaningful assertions. It can also penalize generated code or make unrelated work harder. Use coverage trends and risk-based targets, review uncovered critical behavior, and measure whether tests catch regressions rather than optimizing a single percentage.

### Q28. How do you test a bug fix so it does not return?

**Answer:** Add a test that demonstrates the incorrect behavior before the fix and passes after it, at the narrowest reliable level. Include relevant boundary or regression cases. Then run the focused test and the appropriate broader suite; a test that only repeats the implementation detail may not protect the user-visible defect.

## Sources

### Official and technical references

- [JUnit 5 User Guide](https://junit.org/junit5/docs/current/user-guide/) — test lifecycle, assertions, parameterized tests, and extensions.
- [Mockito documentation](https://javadoc.io/doc/org.mockito/mockito-core/latest/org/mockito/Mockito.html) — mocks, stubs, and verification.
- [Testcontainers for Java](https://java.testcontainers.org/) — disposable integration-test dependencies.
- [JaCoCo documentation](https://www.jacoco.org/jacoco/trunk/doc/) — code coverage and report interpretation.
- [PIT Mutation Testing](https://pitest.org/) — mutation testing concepts and Java tooling.
- [Maven Build Lifecycle](https://maven.apache.org/guides/introduction/introduction-to-the-lifecycle.html) — standard build phases.
- [ArchUnit](https://www.archunit.org/) — tests for Java architecture and package rules.
- [Google Testing Blog](https://testing.googleblog.com/) — practical testing strategy and test reliability.

### Interview-topic pattern evidence only

- [Baeldung: Java Unit Testing Best Practices](https://www.baeldung.com/java-unit-testing-best-practices) — recurring testing topic patterns; not used as answer text.
- [Baeldung: Java Interview Questions](https://www.baeldung.com/java-interview-questions) — recurring Java interview topic patterns; not used as answer text.
