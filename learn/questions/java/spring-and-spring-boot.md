# Spring and Spring Boot — Senior Java Question Bank

[Back to index](index.md)

**Audience:** Senior Java developer / tech lead.  
**Version frame:** Java 8 explains legacy systems; Java 17/21 is the main working context. Spring Boot 2.7 / Spring Framework 5.3 belong to the older Java 8 generation. Boot 3 / Framework 6 require Java 17 or later and use Jakarta APIs. Check the exact release's requirements before choosing Java 25 or a newer Spring line. This is not a recommendation to start new work on an unsupported release.

**Scope:** The Spring container, Boot configuration, web applications, security, framework transaction boundaries, testing, and production operation. SQL/JPA mechanics belong in the persistence bank; cross-service protocols belong in the distributed-systems bank.

**Research basis:** Public interview guides repeatedly cover dependency injection, bean lifecycle, auto-configuration, proxies, transactions, security, test slices, and production debugging. The questions and answers below are original synthesis, not copied interview answers. Official documentation supports technical claims; guide coverage is not a statistical frequency ranking. Sources are linked at the end.

## Container and dependency design

### Q1. What are Spring Framework and Spring Boot, and how are they different?
**Answer:** Spring Framework supplies the container, dependency injection, web framework, transaction support, and other application tools. Spring Boot builds on Spring with opinionated defaults, dependency management, auto-configuration, embedded-server support, and production features. Boot does not replace Spring or generate every object automatically. For a senior design decision, explain which defaults help the team and where explicit configuration is needed.

### Q2. What is inversion of control, and how does dependency injection implement it?
**Answer:** Inversion of control means a framework controls part of the application's construction or execution rather than application code doing everything itself. Dependency injection is one form: the container supplies an object's collaborators. A pricing service receives a tax policy instead of constructing it. This separates business behavior from wiring and makes replacement and testing easier, but poor interface boundaries remain poor even when Spring wires them.

### Q3. Why is constructor injection usually better than field injection?
**Answer:** A constructor makes required dependencies visible and allows a valid object to be created in an ordinary unit test. Fields can be final, and missing dependencies fail early. Field injection hides requirements and usually needs framework or reflection support in tests. Setter injection is useful for genuinely optional dependencies. A constructor with many arguments often signals too many responsibilities; switching to field injection only hides that design problem.

### Q4. How do you resolve multiple beans of the same type?
**Answer:** Use a semantic `@Qualifier` when the caller needs a specific implementation, or `@Primary` when one implementation is the normal default. Inject a collection when all implementations should participate, such as a validation chain. Avoid relying on accidental bean names or registration order. A qualifier like `regionalTaxPolicy` communicates intent better than naming beans after implementation classes that may later change.

### Q5. How do `@Component`, `@Service`, `@Repository`, and `@Bean` differ?
**Answer:** The first three are component stereotypes discovered through scanning. `@Service` communicates business-layer intent; `@Repository` also participates in persistence-exception translation when the relevant infrastructure is enabled. `@Bean` declares an object from a factory method and is useful for third-party classes or explicit construction. None of these annotations automatically supplies every concern, such as transactions, authorization, or thread safety.

### Q6. What happens when Spring creates a typical bean?
**Answer:** Broadly, Spring instantiates the object, supplies dependencies, invokes applicable awareness callbacks and initialization processing, and exposes the finished bean, sometimes through a proxy. Bean post-processors can modify or wrap it. Initialization includes configured lifecycle callbacks such as `@PostConstruct`. Exact extension ordering matters when writing infrastructure. Avoid remote calls or business transactions during construction because the context and proxy relationships may not yet be ready.

### Q7. How do `BeanFactoryPostProcessor` and `BeanPostProcessor` differ?
**Answer:** A `BeanFactoryPostProcessor` changes bean-definition metadata before ordinary beans are created, for example replacing configuration values. A `BeanPostProcessor` acts on bean instances during their creation and can return a wrapper. Auto-proxy creation uses the latter kind of extension. Use these hooks sparingly: creating application beans too early while configuring infrastructure can bypass later processing and produce subtle proxy or lifecycle problems.

### Q8. What does singleton scope mean in Spring?
**Answer:** It normally means one instance per bean definition per container, not one instance for the entire JVM or cluster. A singleton service may serve many concurrent requests. Keep request-specific mutable state outside its fields, or use carefully designed synchronization where shared state is intentional. Injecting a repository into a singleton does not mean every request shares one physical JDBC connection.

### Q9. How can a singleton use a request-scoped or prototype dependency correctly?
**Answer:** Directly injecting a prototype into a singleton normally resolves it once when the singleton is created. Use `ObjectProvider`, a scoped proxy, or an explicit factory when a fresh or current scoped instance is needed. A request-scoped proxy resolves the target for the active request. Calling it from a background worker without a request context fails; pass required request data explicitly instead.

### Q10. Why are circular dependencies a design warning?
**Answer:** They often mean two components mix responsibilities or share a missing abstraction. Constructor cycles cannot be satisfied through normal construction. Even cycles that older configurations resolve through early references can expose partially initialized objects. Prefer extracting a coordinator, separating read and write roles, or publishing an appropriate event. `@Lazy` may defer resolution, but it does not remove the conceptual cycle.

### Q11. What is the difference between full `@Configuration` processing and `proxyBeanMethods = false`?
**Answer:** With full configuration processing, calls between eligible `@Bean` methods are intercepted so container semantics are preserved. With `proxyBeanMethods = false`, a direct Java call to another factory method is just a method call and can create another object. Prefer parameter injection between independent bean methods when using the non-proxied mode. The choice should reflect wiring behavior, not an unexplained performance rule.

### Q12. Where should business invariants live in a Spring application?
**Answer:** Put domain rules in cohesive domain or application services, not scattered across controllers, filters, and repository callbacks. Controllers translate transport input; repositories persist data. Spring annotations can apply infrastructure around a use case, but they should not be the only explanation of its rules. A useful test is whether the rule can be tested using ordinary Java objects without starting the complete application.

## Boot startup and configuration

### Q13. What does `@SpringBootApplication` combine?
**Answer:** It combines Boot configuration, auto-configuration enablement, and component scanning. Its package becomes an important default search root. Put the application class above the application's packages, or configure scanning deliberately. A misplaced main class can cause missing controllers or accidental discovery of unrelated components. Entity and repository scanning also have their own rules; component scanning is not a universal discovery mechanism.

### Q14. How does auto-configuration decide what to create?
**Answer:** Auto-configuration classes use conditions based on available classes, properties, existing beans, and the application type. Many defaults back off when a user supplies the relevant bean. Adding a dependency can therefore change startup behavior. Diagnose this using the conditions evaluation report rather than guessing. A condition matching does not guarantee that the resulting external connection, credentials, or runtime behavior is correct.

### Q15. How would you debug an unexpected auto-configured bean?
**Answer:** Start with the bean's origin and the conditions report, available through debug startup logging and, when safely exposed, Actuator's conditions endpoint. Inspect dependencies, active profiles, configuration imports, and competing beans. Supply a supported override or exclude a specific auto-configuration only when necessary. Do not copy internal auto-configuration classes into application code; their implementation details are not stable extension APIs.

### Q16. What are starters and the Boot dependency BOM?
**Answer:** A starter groups dependencies for a use case, such as MVC or data access. The bill of materials, or BOM, supplies a tested set of dependency versions. A starter does not itself contain all the framework behavior. Override versions only for a clear reason and validate compatibility. Pinning one transitive library independently can break the framework even when compilation succeeds.

### Q17. When should you use `@ConfigurationProperties` instead of `@Value`?
**Answer:** Use `@ConfigurationProperties` for related, typed settings such as client timeouts, endpoints, and pool limits. It supports binding and validation as one coherent contract. `@Value` suits a small isolated value or expression. Prefer explicit duration and size types over unexplained integers. Validate required configuration during startup so a missing timeout or malformed endpoint does not become a production-only failure.

### Q18. How should you reason about configuration precedence?
**Answer:** Configuration comes from ordered property sources, including packaged files, external files, environment variables, command-line arguments, and test overrides. Later or higher-priority sources can override earlier ones according to Boot's documented order. Diagnose the resolved value and its origin, not just the checked-in YAML. Avoid memorizing an incomplete precedence list and assuming it covers configuration imports, profile-specific files, and test annotations.

### Q19. What are profiles good for, and what are they poor at?
**Answer:** Profiles select environment-specific groups of beans or settings, such as local development adapters. They are poor substitutes for runtime feature management or authorization. Too many profile combinations create an untested configuration matrix. Keep environment differences small, test the real deployment combinations, and prefer validated settings when the behavior is a parameter rather than a completely different component.

### Q20. How should an application handle secrets?
**Answer:** Load secrets from an approved secret store or protected deployment mechanism, grant minimal access, and plan rotation. Do not put credentials in source code, container images, example logs, or exception messages. Treat configuration endpoints and diagnostic bundles as sensitive. Sanitization helps but is not a security boundary. A rotation design must consider connection pools and clients that captured credentials only at startup.

### Q21. When should work run in `@PostConstruct`, an application runner, or an application event?
**Answer:** Use initialization callbacks for local bean setup, not long business workflows. `ApplicationRunner` and `CommandLineRunner` run after context creation and are useful for deliberate startup tasks; failure can prevent startup. Application events expose lifecycle stages. A listener is not automatically asynchronous. Large data migrations or shared startup jobs need explicit coordination and recovery, rather than every replica doing the same work.

### Q22. What should a team check when moving a Java 8 Boot application to a modern baseline?
**Answer:** Check the supported upgrade path, Java 17 compatibility, `javax` to `jakarta` changes for affected enterprise APIs, security configuration, ORM behavior, dependency versions, tests, and operational defaults. Not all `javax` packages changed: JDBC's `javax.sql` remains a Java SE API. Compile, integration-test, and load-test incrementally. Java 21 or 25 runtime adoption is separate from changing the framework and dependency generation.

## Proxies, transactions, and framework boundaries

### Q23. How does Spring AOP apply cross-cutting behavior?
**Answer:** In the usual mode, Spring exposes a proxy around a bean. Calls through the proxy run interceptors for concerns such as transactions, caching, or method authorization, then reach the target. This is different from editing every method's bytecode. Spring AOP targets method execution; arbitrary constructor and field interception requires other techniques. Always ask whether a call actually crosses the managed proxy boundary.

### Q24. How do JDK proxies and class-based proxies differ?
**Answer:** JDK proxies expose interfaces and intercept calls made through those interfaces. Class-based proxies subclass the target and cannot override final methods or subclass final classes. Private methods are not overridable either. The available advice points and injection types therefore differ. Boot and framework defaults can vary by feature and release, so verify the actual proxy rather than assuming every bean uses the same mechanism.

### Q25. Why can `this.someTransactionalMethod()` fail to start a transaction?
**Answer:** A call from one method to another on the same target object normally bypasses the Spring proxy. The transaction interceptor never sees it. Move the transactional use case to a separate collaborator and call that managed bean, or use a programmatic transaction where appropriate. Making the method public does not fix self-invocation. AspectJ weaving has different interception semantics and should not be confused with proxy mode.

### Q26. Where should `@Transactional` normally be placed?
**Answer:** Place it around a complete application use case that needs one local transaction, often a public service method. Repository-level transactions alone may leave a multi-step business operation split across commits. Keep the scope short and avoid slow remote calls while holding database resources. Method visibility rules vary with proxy type and Spring version; a public managed boundary is the clearest convention.

### Q27. Which exceptions cause rollback by default?
**Answer:** Under the usual Spring defaults, unchecked exceptions and `Error` trigger rollback; checked exceptions do not unless configured. Use `rollbackFor` or an intentional application-wide policy when needed. Catching and swallowing an exception can prevent the interceptor from applying the expected rule. Some persistence failures already mark a transaction rollback-only. Test the actual exception path, not only the successful operation.

### Q28. How do `REQUIRED`, `REQUIRES_NEW`, and `NESTED` differ?
**Answer:** `REQUIRED` joins an existing transaction or creates one. `REQUIRES_NEW` starts an independent transaction while suspending the outer one where supported; it can need another connection. `NESTED` commonly uses a savepoint within the same physical transaction and depends on transaction-manager support. It is not simply another name for an independent commit. Choose propagation based on business atomicity and available infrastructure.

### Q29. Why does `UnexpectedRollbackException` occur?
**Answer:** An inner operation can mark a shared transaction rollback-only. If outer code catches the exception and continues, its apparent success does not make the transaction committable again. The outer commit then reports that rollback happened instead. Fix the intended business boundary or error handling. Adding `REQUIRES_NEW` everywhere may hide the symptom while breaking atomicity and exhausting the connection pool.

### Q30. Is `@Transactional(readOnly = true)` a write-prevention security rule?
**Answer:** No. It is a transaction hint that can influence ORM flush behavior, JDBC settings, and database behavior, depending on the manager and provider. It does not universally reject writes. Use database permissions or explicit read-only database transactions where enforcement is required. Likewise, an isolation declaration is meaningful only if the underlying transaction and database actually support the requested semantics.

### Q31. Does `@Transactional` cover a new thread, an asynchronous method, or an HTTP call?
**Answer:** An ordinary imperative transaction is associated with its executing thread and does not automatically follow work into a new thread or another service. An `@Async` target can start its own transaction when the call crosses the relevant proxy, but it does not share the caller's transaction. Reactive transactions use reactive context and different managers. Cross-service atomicity requires a separate protocol, not another annotation.

### Q32. When would you use programmatic transaction management?
**Answer:** Use `TransactionTemplate` when transaction boundaries are dynamic or when a small transactional segment must be separated from slow non-transactional work. It also makes exception handling and retry placement explicit. Declarative transactions are usually clearer for stable use-case boundaries. Do not mix styles casually: document which operation owns commit and ensure a retry opens a new transaction rather than repeating work inside a failed one.

### Q33. Does `@TransactionalEventListener(AFTER_COMMIT)` guarantee message delivery?
**Answer:** No. It runs in relation to a local transaction's completion, but a crash after commit can prevent the listener or its external action from completing. It is useful for best-effort local follow-up, not a durable messaging guarantee. If database changes and an event must survive together, persist an outbox record in the transaction. Database writes from an after-commit callback need a deliberate new transaction.

## Web and API behavior

### Q34. What happens inside Spring MVC when a request arrives?
**Answer:** Servlet filters run around servlet processing. `DispatcherServlet` finds a handler through handler mappings, uses a handler adapter, resolves arguments, invokes controller logic, and writes a response using return-value handlers and message converters. Interceptors and exception resolvers participate at defined points. This explains why a security filter failure may never reach a controller advice and why transport parsing can fail before the controller method runs.

### Q35. When should you use a filter, an interceptor, or controller advice?
**Answer:** Use servlet filters for concerns at the servlet boundary, such as security or request wrapping. MVC interceptors know the selected handler and can support MVC-specific cross-cutting behavior. `@RestControllerAdvice` centralizes appropriate controller-layer exception handling and response shaping. Do not implement authorization only in an MVC interceptor when other endpoints or dispatch paths can bypass it. Each mechanism has a different scope.

### Q36. How should validation be split between DTOs and business services?
**Answer:** Validate input shape near the API boundary using Jakarta Bean Validation, for example required fields and numeric bounds. Validate business rules, such as account ownership or allowed order transitions, in the use case using authoritative state. Database constraints remain the final protection for stored invariants. Input validation alone cannot stop a race between two valid requests that compete for the same resource.

### Q37. How should a REST API report errors?
**Answer:** Return stable error categories, appropriate status codes, a safe message, and a correlation identifier. Modern Spring supports `ProblemDetail` for standardized problem responses. Do not expose stack traces, SQL, tokens, or internal class names. Separate malformed input, missing resources, authorization failure, business conflicts, and server failure. A global handler that converts every exception to HTTP 200 makes clients, monitoring, and retries unreliable.

### Q38. Why should controllers usually return DTOs instead of JPA entities?
**Answer:** DTOs keep the external contract separate from persistence structure. Entity serialization can trigger unexpected lazy loads, recursive relationships, or exposure of private fields. It also makes schema changes accidentally change the API. Map only the data the use case needs, inside an intentional data-access boundary. The mapping cost is usually smaller than the long-term coupling and query surprises avoided.

### Q39. When would you choose Spring MVC over WebFlux?
**Answer:** MVC is a good fit for a blocking stack with JDBC and ordinary request-response workflows. WebFlux supports non-blocking reactive pipelines and backpressure, useful when the end-to-end stack can cooperate. Blocking calls on a reactive event loop can stall many requests. Neither model is automatically faster. Choose based on dependency behavior, concurrency needs, team skill, and measured latency, not fashion.

### Q40. Do Java 21 virtual threads make WebFlux unnecessary?
**Answer:** Virtual threads make many blocking request handlers cheaper to schedule, but they do not supply reactive streams, backpressure, or unlimited downstream capacity. Supported Boot releases can enable them with `spring.threads.virtual.enabled=true` on a suitable JDK. Database connections and remote quotas still need bounds. Java 25 adoption should follow supported release combinations and measurements; changing the JDK does not remove those architecture limits.

### Q41. How should outbound HTTP clients be configured?
**Answer:** Set connection and response deadlines, bounded connection pools, connection acquisition limits, safe TLS verification, and clear error mapping. Choose a client style matching the application: modern Spring offers synchronous and reactive options, while older systems often use `RestTemplate`. A timeout that excludes DNS or pool waiting may not bound total latency. Avoid constructing a new heavyweight client per request or logging authorization headers.

### Q42. How do Spring cache annotations work, and where do they fail?
**Answer:** `@Cacheable` can return a cached value without calling the target method; `@CachePut` updates through method execution, and `@CacheEvict` removes entries. In proxy mode, self-invocation can bypass caching. Keys must include relevant tenant and security context. Define expiry and invalidation deliberately. An annotation is not a complete cache-coherence strategy, and caching a mutable object can expose shared-state bugs.

## Security

### Q43. What is the role of `SecurityFilterChain`?
**Answer:** It defines which security filters apply to matching servlet requests and how authentication and authorization work. `FilterChainProxy` selects a matching chain, so chain order and request matching matter. A broad earlier chain can hide a later one. Modern configuration uses explicit beans rather than relying on the removed `WebSecurityConfigurerAdapter`. Test protected, public, and unmatched routes rather than only the login path.

### Q44. What is the difference between authentication and authorization?
**Answer:** Authentication establishes who the caller is. Authorization decides what that identity may do to a specific resource. A valid token does not prove access to every account identifier in a URL. Combine route-level policy with service-level checks where the use case needs them. Use trusted identity information, not a caller-supplied user ID, to decide ownership and tenant boundaries.

### Q45. What must a resource server validate in a JWT?
**Answer:** Validate the signature using trusted keys, the allowed algorithm, expiration and other relevant time claims, trusted issuer, and the intended audience. Map claims to authorities using an explicit policy. A signed JWT is not encrypted, and decoding its payload is not validation. Plan key rotation, token lifetime, and revocation requirements; self-contained tokens do not magically become invalid at logout.

### Q46. How do OAuth 2.0 and OpenID Connect differ?
**Answer:** OAuth 2.0 defines delegated access authorization. OpenID Connect adds an identity layer and an ID token for client authentication use cases. An API normally accepts an access token meant for that resource, not an ID token meant for a client. State which actor is the client, authorization server, resource server, and user before choosing a flow; confusing these roles leads to unsafe token handling.

### Q47. How do CSRF and CORS differ?
**Answer:** CSRF exploits credentials that a browser sends automatically, such as session cookies, to cause an unwanted action. CORS controls which browser origins may read permitted cross-origin responses; it is not server-side authentication. Disabling CSRF can be appropriate for a genuinely stateless bearer-token API without automatic credential use, but not simply because the API returns JSON. Review cookies and browser behavior first.

### Q48. How should passwords and sessions be handled?
**Answer:** Store passwords using an adaptive, salted password encoder such as an appropriately configured bcrypt or Argon2 implementation, not reversible encryption or plain hashes. Tune cost to operational constraints. For sessions, use secure cookie settings, session fixation protection, and an intentional expiration policy. A cluster needs shared session handling or a deliberate alternative. Rate-limit credential attempts without revealing whether an account exists.

### Q49. Why use method security in addition to URL rules?
**Answer:** The same business operation can be reached through controllers, message handlers, or other services. Method security can protect a managed service boundary independently of one URL. Enable it explicitly and verify proxy coverage; self-invocation has the same general proxy problem. Roles are often too coarse by themselves. Check resource ownership and tenant membership where the domain operation has access to that information.

## Testing

### Q50. How do unit tests, slice tests, and full-context tests differ?
**Answer:** Unit tests construct a small object graph and test business logic quickly. Slice tests load selected framework infrastructure, such as MVC or JPA support. `@SpringBootTest` loads the application context and checks broader integration. Use each for a different risk. A suite containing only mocked unit tests can miss broken wiring, while starting the full context for every branch makes feedback unnecessarily slow.

### Q51. When do `@WebMvcTest` and `@SpringBootTest` give different confidence?
**Answer:** `@WebMvcTest` checks a selected web slice: request mapping, conversion, validation, advice, and relevant filters. Mock or supply collaborators intentionally. `@SpringBootTest` checks the broader application wiring; a real server needs an appropriate web environment such as `RANDOM_PORT`. Neither automatically proves real database or remote-provider compatibility. Name the risk being tested before selecting the annotation.

### Q52. How should database integration tests avoid false confidence?
**Answer:** Use the production database engine where dialect, constraints, isolation, or migration behavior matters, often through Testcontainers. Embedded substitutes can be useful but may accept SQL or concurrency behavior that production rejects. Apply real migrations, inspect generated SQL, and include concurrent tests for critical invariants. A repository test that rolls back every transaction may never exercise commit-time failures or actual post-commit behavior.

### Q53. Why can a transactional HTTP integration test leave data behind?
**Answer:** With a real server, the test client and request handler usually run on different threads. The test method's transaction does not wrap the server's transaction, so rolling back the test cannot undo committed server writes. Use explicit cleanup, isolated databases, or carefully designed test fixtures. Conversely, an all-in-one-thread test can hide lazy-loading bugs by keeping a transaction open longer than production does.

### Q54. What should be mocked in a Spring test?
**Answer:** Mock a boundary when the test does not need its real behavior, such as a remote pricing provider in a controller test. Do not mock the repository when testing SQL behavior. Excessive bean replacement can make a full-context test validate a configuration that never runs in production. On Framework 6.2-based stacks, prefer supported bean-override tools such as `@MockitoBean`; check older or newer release compatibility.

### Q55. Why can Spring test suites become slow or flaky?
**Answer:** Different profiles, bean overrides, properties, and context definitions reduce test-context reuse. Excessive `@DirtiesContext` forces rebuilding. Shared mutable fixtures, fixed ports, time-based assertions, and real asynchronous work introduce interference. Standardize test configurations and use deterministic waits with time bounds. Measure context creation and dependency startup before optimizing assertions; parallelizing a suite with shared state can make it faster and less trustworthy.

## Production and case studies

### Q56. How do health, readiness, and liveness differ?
**Answer:** Health is a broad diagnostic view. Readiness says whether an instance should receive traffic; liveness says whether restarting it is appropriate. A failed shared database should not normally make every instance fail liveness and enter a restart storm. Readiness dependency checks also need judgment: removing all instances may be worse than serving partial functionality. Startup probes handle slow initialization separately.

### Q57. How should Actuator be exposed safely?
**Answer:** Expose only required endpoints, secure them, and preferably restrict their network access. Health detail, environment data, heap dumps, log-level changes, and management operations can reveal or alter sensitive state. A separate management port is not protection by itself. Test security for both the application and management endpoints. Use metrics for routine diagnosis instead of repeatedly collecting high-risk diagnostic dumps.

### Q58. What should you measure in a Spring service?
**Answer:** Track request rate, error rate, latency distributions, and saturation of threads, connections, queues, and other constrained resources. Connect metrics with traces and structured logs using safe correlation identifiers. Micrometer integrates these concerns in modern Boot stacks. Avoid unbounded metric labels such as user IDs or raw URLs. Averages alone can hide a small but important group of very slow requests.

### Q59. How do `@Async` and `@Scheduled` behave across replicas?
**Answer:** `@Async` submits work to an executor; it is not durable job storage. Configure bounded resources, rejection behavior, context propagation, and exception handling. `@Scheduled` runs in each application instance unless something coordinates it. A three-replica deployment can run the same job three times. Use an appropriate durable scheduler, queue, or lease and make the job safe to repeat.

### Q60. What is graceful shutdown meant to protect?
**Answer:** It lets an instance stop accepting new work while finishing or safely abandoning existing work within a deadline. Coordinate readiness removal, load-balancer draining, server shutdown, executor termination, and message-consumer behavior. Configure the platform's termination grace period to exceed the application's planned drain time. It does not guarantee completion after a hard crash, so durable jobs and external side effects still need recovery.

### Q61. A service times out after a deployment. How would you investigate?
**Answer:** Compare the change with the previous version and separate startup, request handling, and downstream time. Check resolved configuration, connection acquisition, new SQL, pool limits, DNS/TLS setup, and security calls. Use traces, thread samples, and saturation metrics. Roll back or reduce traffic if user impact is high. Increasing all timeouts first can merely keep more work waiting and worsen the incident.

### Q62. Transactions stopped rolling back after a refactor. What would you check?
**Answer:** Check whether the method moved into self-invocation, the object is created with `new`, a different bean or transaction manager is used, or an exception is now caught or checked. Verify that transaction support is enabled and that calls reach the managed proxy. Reproduce the failure with an integration test that checks committed state, not just whether the annotation is present.

### Q63. An endpoint issues hundreds of SQL statements. What is the Spring-side investigation?
**Answer:** Trace the request and count statements by phase. Check entity serialization, Open EntityManager in View, mapping code, and repeated repository calls. Prefer a DTO-oriented use case with an explicit fetch plan and a bounded query budget. Turning off Open EntityManager in View can expose hidden access, but it does not itself provide the right query. Detailed N+1 remedies belong in the persistence bank.

### Q64. When would you consider AOT processing and a native image?
**Answer:** Consider them when startup time and memory footprint materially affect the workload. Build-time analysis can require hints for reflection, resources, serialization, and dynamic proxies; dynamic runtime assumptions may fail. Compare native-image and JVM behavior under representative traffic and operational tooling. Faster startup does not guarantee faster steady-state throughput. AOT-generated configuration must be built and tested for the intended application features.

### Q65. What should a tech lead require before approving a reusable Spring starter?
**Answer:** Require a narrow purpose, documented configuration, safe defaults, supported version ranges, and clear override behavior. Use conditional auto-configuration and test both matching and non-matching conditions, often with `ApplicationContextRunner`. Avoid broad component scanning and surprising global beans. Include security and observability implications, failure behavior, and migration notes. A starter should reduce repeated decisions without secretly taking ownership of every application's architecture.

## Sources and further reading

Official references support the answers; interview guides below were used only to identify recurring question themes.

- [Spring Framework: container and beans](https://docs.spring.io/spring-framework/reference/core/beans.html) — Q1–Q12.
- [Spring Boot 3.5: auto-configuration](https://docs.spring.io/spring-boot/3.5/reference/using/auto-configuration.html) and [externalized configuration](https://docs.spring.io/spring-boot/3.5/reference/features/external-config.html) — Q13–Q22.
- [Spring Boot 3.5: system requirements](https://docs.spring.io/spring-boot/3.5/system-requirements.html) — release-specific Java baseline; do not infer every release's compatibility from one page.
- [Spring Framework: proxying mechanisms](https://docs.spring.io/spring-framework/reference/core/aop/proxying.html) — Q23–Q25.
- [Spring Framework: declarative transaction annotations](https://docs.spring.io/spring-framework/reference/data-access/transaction/declarative/annotations.html) and [propagation](https://docs.spring.io/spring-framework/reference/data-access/transaction/declarative/tx-propagation.html) — Q26–Q33, Q62.
- [Spring Framework: web MVC](https://docs.spring.io/spring-framework/reference/web/webmvc.html) and [WebFlux](https://docs.spring.io/spring-framework/reference/web/webflux.html) — Q34–Q42.
- [Spring Security: servlet architecture](https://docs.spring.io/spring-security/reference/servlet/architecture.html), [JWT resource server](https://docs.spring.io/spring-security/reference/servlet/oauth2/resource-server/jwt.html), and [CSRF](https://docs.spring.io/spring-security/reference/servlet/exploits/csrf.html) — Q43–Q49.
- [Spring Boot 3.5: testing applications](https://docs.spring.io/spring-boot/3.5/reference/testing/spring-boot-applications.html) — Q50–Q55.
- [Spring Boot 3.5: production-ready features](https://docs.spring.io/spring-boot/3.5/reference/actuator/index.html) and [native images](https://docs.spring.io/spring-boot/3.5/reference/packaging/native-image/index.html) — Q56–Q64.
- [Interview topic guide: Spring Boot internals](https://cpbrucemeena.github.io/interview-prep/java-interview/SPRING_BOOT_NOTES/) and [Event Looped Spring Boot interview guide](https://www.eventlooped.com/interview-prep/spring-boot) — topic discovery only; version and behavior claims are checked against official references.
