# Security for Java Backend Applications

[Back to index](index.md)

**Audience:** Senior Java developers and tech leads  
**Scope:** Secure implementation and operation of Java backend applications. This is defensive guidance, not an exploitation guide.

Every question is followed immediately by an answer. Questions are original; interview guides below were used only to identify recurring topic patterns.

## Questions and answers

### Q1. How do authentication and authorization differ?

**Answer:** Authentication establishes who or what is making a request. Authorization decides whether that identity may perform a specific action on a specific resource. A valid login does not imply access to every record or operation. Enforce authorization on the server for each relevant action and resource.

### Q2. What is the difference between role-based and attribute-based access control?

**Answer:** Role-based access control grants permissions through named roles, such as `ADMIN` or `SUPPORT`. Attribute-based access control evaluates properties of the user, resource, action, or context, such as ownership or tenant. Roles are easy to understand for broad permissions; attributes handle fine-grained rules but can become difficult to audit if policies are scattered.

### Q3. Where should authorization checks be placed in a Spring application?

**Answer:** Enforce checks at a trusted server-side boundary and, for important operations, near the business operation itself. Spring Security supports request-level and method-level authorization. Avoid relying only on hidden UI controls or a single controller check when other code paths can invoke the same operation.

### Q4. What is the purpose of Spring Security’s filter chain?

**Answer:** The filter chain applies security behavior to incoming web requests, including authentication, authorization, security headers, and exploit protections. The order and matchers matter. Review the effective chain and its configuration; a rule that is never matched or is shadowed by a broad earlier rule may not protect the intended endpoint.

### Q5. What is CSRF, and when is CSRF protection important?

**Answer:** Cross-site request forgery tricks a user’s browser into sending an authenticated request that the user did not intend. It is especially relevant when browsers automatically attach credentials such as session cookies. CSRF defenses use tokens or equivalent mechanisms and appropriate cookie settings. Disabling protection solely because an API is “REST” is not a sufficient analysis.

### Q6. How is CORS different from authentication or authorization?

**Answer:** Cross-Origin Resource Sharing is a browser policy that controls whether web pages from another origin may read responses. It does not authenticate callers or grant access by itself. Configure allowed origins, methods, headers, and credential behavior narrowly; never treat permissive CORS as an access-control system.

### Q7. How do prepared statements prevent SQL injection?

**Answer:** Prepared statements keep query structure separate from values, so user input is treated as data rather than executable SQL syntax. Use parameter binding for values and allowlists for dynamic identifiers such as column names, which generally cannot be bound as ordinary values. ORM use alone is not protection if queries are assembled unsafely.

### Q8. Can JPA or Hibernate applications still have injection flaws?

**Answer:** Yes. Dynamically assembled JPQL, HQL, or native SQL can still interpret attacker-controlled text as query syntax. Bind values through the query API and constrain dynamic query structure with a fixed mapping or allowlist. Inspect query construction, not just the choice of ORM.

### Q9. What is the difference between input validation and output encoding?

**Answer:** Input validation decides whether data fits the application’s expected format and rules. Output encoding makes data safe for a particular output context, such as HTML or JavaScript. Validation is not a replacement for contextual encoding; the same string may be valid data but unsafe to insert into a page without correct encoding.

### Q10. What makes a password-storage scheme safe?

**Answer:** Store passwords with a purpose-built, slow password-hashing algorithm such as Argon2id, bcrypt, or scrypt, using a unique salt and current recommended parameters. Never store plaintext or use a fast general hash such as plain SHA-256. Use a vetted library and plan to update parameters as hardware and guidance change.

### Q11. How should password reset tokens be designed?

**Answer:** Generate unpredictable, single-use, time-limited tokens; bind them to the intended account and purpose; store them safely, preferably as a verifier rather than recoverable plaintext; and invalidate them after use or password change. Avoid revealing whether an account exists in responses, and apply rate limits to reset requests.

### Q12. How should a backend manage secrets?

**Answer:** Keep credentials and signing keys out of source code, logs, images, and ordinary configuration committed to version control. Use an approved secret manager or workload identity, restrict access to the minimum needed, rotate credentials, and audit access. If a secret is exposed, revoke or rotate it; deleting the line from the latest commit is not enough.

### Q13. What is the principle of least privilege?

**Answer:** Give a user, service, process, or cloud identity only the permissions and duration required for its task. Separate runtime and deployment privileges, avoid shared administrator credentials, and review permissions as responsibilities change. Least privilege limits the damage from mistakes or compromised credentials.

### Q14. How should session cookies be configured?

**Answer:** Use HTTPS and set `Secure`, `HttpOnly`, and an appropriate `SameSite` policy. Use strong, unpredictable session identifiers, rotate them after authentication or privilege changes, and expire or invalidate them appropriately. Cookie flags support security but do not replace authorization or CSRF defenses.

### Q15. When is a JWT appropriate, and what are its risks?

**Answer:** A signed JWT can carry claims that a recipient verifies without consulting a central session store on every request. It is not automatically safer or stateless in every useful sense. Validate signature, allowed algorithms, issuer, audience, expiry, and intended use; limit sensitive claims and lifetime; and plan how to handle revocation and key rotation.

### Q16. Why should a service not trust a decoded JWT without verifying its signature?

**Answer:** Decoding only reveals the claims; it does not prove that a trusted issuer created them or that they were not altered. Use a maintained library, validate the signature and relevant claims, and reject unexpected algorithms and key sources. Treat every unverified claim as attacker-controlled input.

### Q17. What is unsafe deserialization?

**Answer:** It is turning untrusted serialized data into object graphs in a way that can trigger dangerous behavior or abuse application classes. Avoid native Java serialization for untrusted input. Prefer narrow, schema-driven formats, validate allowed types and sizes, and keep libraries patched.

### Q18. How do you reduce server-side request forgery risk?

**Answer:** Do not fetch arbitrary user-supplied URLs. If fetching is required, use strict destination allowlists, validate resolved addresses and redirects, block access to internal and metadata networks, and apply egress network controls. DNS and redirect behavior can defeat naive string checks, so enforce policy at multiple layers.

### Q19. What are common file-upload security controls?

**Answer:** Enforce size and count limits, validate content rather than trusting the supplied filename or MIME type, generate a safe storage name, and store uploads outside executable web roots. Scan or process files in isolation where appropriate, restrict download permissions, and defend against path traversal and archive expansion.

### Q20. How should an API limit resource-exhaustion risks?

**Answer:** Bound request sizes, collection counts, execution time, concurrency, and expensive operations. Apply rate limits where appropriate and reject malformed input early. Limits must be coordinated with legitimate workload needs and should produce observable, safe failures rather than exhausting the process.

### Q21. How should a backend protect sensitive data in logs?

**Answer:** Do not log passwords, access tokens, session IDs, private keys, payment data, or unnecessary personal data. Redact or tokenize sensitive fields, restrict log access and retention, and avoid returning raw exception details to clients. Security logging should capture useful events without creating a second sensitive-data store.

### Q22. What information should a security audit log record?

**Answer:** Record relevant security events such as authentication outcomes, privilege changes, and sensitive administrative actions with reliable timestamps and actor/resource context. Protect integrity and access, avoid secrets and excessive personal data, and make the records useful for detection and investigation. Audit events do not replace application logs or monitoring metrics.

### Q23. Why are dependency updates part of application security?

**Answer:** Third-party libraries execute with the application’s privileges and can contain known vulnerabilities or risky behavior. Maintain an inventory, monitor advisories, scan dependencies, update deliberately, and test compatibility. A scanner result should be assessed for affected versions and reachable use, but uncertain findings should be investigated rather than ignored.

### Q24. What does a secure software supply chain include?

**Answer:** It includes trusted dependency sources, reviewed and reproducible builds, protected release credentials, artifact integrity, and controlled deployment. Pin and verify dependencies where practical, protect CI permissions, and know which source revision produced a deployed artifact. Build provenance helps investigation but does not prove the application is vulnerability-free.

### Q25. What security headers are useful for a backend?

**Answer:** Headers such as Content Security Policy, frame protections, MIME-sniffing protections, and transport security can reduce browser-side risks when the service serves browser content. Choose headers for the actual response and deployment model; some browser policies are not meaningful for a pure machine-to-machine API. Test them without assuming headers fix server-side authorization flaws.

### Q26. What does secure error handling look like?

**Answer:** Return a stable, minimal error response with a correlation identifier where useful. Keep stack traces, SQL, internal paths, and secrets in protected diagnostics, not the client response. Log enough context to investigate while avoiding sensitive fields; ensure error handling itself does not swallow failures or misreport success.

### Q27. How do TLS and application authorization solve different problems?

**Answer:** TLS protects data in transit and helps establish the server identity to the client. Authorization decides which authenticated principal may do what. TLS does not prevent an authenticated but unauthorized user from accessing another customer’s record, so both transport protection and resource-level checks are needed.

### Q28. How should security requirements be included in code review?

**Answer:** Check trust boundaries, authorization on each action and resource, query construction, input/output handling, secret exposure, failure paths, dependency changes, and data logging. Review the threat assumptions and tests, not only the happy path. Use security specialists for high-risk changes rather than treating a checklist as a complete assessment.

### Q29. A user can change an order ID in a URL and view another customer’s order. What class of flaw is this?

**Answer:** This is a broken object-level authorization flaw: the application accepts an object identifier without verifying that the current principal may access that object. Enforce authorization on the server for every requested object, ideally using the authenticated identity and resource ownership in the application operation. Add regression tests for both allowed and denied access.

### Q30. A production log accidentally contains bearer tokens. What should the team do?

**Answer:** Treat the tokens as exposed: revoke or rotate them, restrict access to the affected logs, assess retention and copies, and follow the incident process. Remove or mask the logging path, add a regression test, and prevent recurrence with scanning or structured redaction. Deleting a visible log entry alone does not invalidate a credential.

## Sources

### Official and technical references

- [OWASP Java Security Cheat Sheet](https://cheatsheetseries.owasp.org/cheatsheets/Java_Security_Cheat_Sheet.html) — Java-specific security concerns.
- [OWASP SQL Injection Prevention Cheat Sheet](https://cheatsheetseries.owasp.org/cheatsheets/SQL_Injection_Prevention_Cheat_Sheet.html) — parameterized queries and safe query construction.
- [OWASP Password Storage Cheat Sheet](https://cheatsheetseries.owasp.org/cheatsheets/Password_Storage_Cheat_Sheet.html) — password hashing guidance.
- [OWASP Authentication Cheat Sheet](https://cheatsheetseries.owasp.org/cheatsheets/Authentication_Cheat_Sheet.html) — authentication controls.
- [OWASP Secrets Management Cheat Sheet](https://cheatsheetseries.owasp.org/cheatsheets/Secrets_Management_Cheat_Sheet.html) — secret lifecycle and access controls.
- [OWASP Cross-Site Request Forgery Prevention Cheat Sheet](https://cheatsheetseries.owasp.org/cheatsheets/Cross-Site_Request_Forgery_Prevention_Cheat_Sheet.html) — CSRF defenses.
- [OWASP Server-Side Request Forgery Prevention Cheat Sheet](https://cheatsheetseries.owasp.org/cheatsheets/Server_Side_Request_Forgery_Prevention_Cheat_Sheet.html) — outbound request controls.
- [OWASP Deserialization Cheat Sheet](https://cheatsheetseries.owasp.org/cheatsheets/Deserialization_Cheat_Sheet.html) — safe handling of serialized input.
- [OWASP Application Security Verification Standard](https://owasp.org/www-project-application-security-verification-standard/) — security requirements and verification.
- [Spring Security Reference](https://docs.spring.io/spring-security/reference/) — web security, authorization, CSRF, and integration.

### Interview-topic pattern evidence only

- [Baeldung: Spring Security Interview Questions](https://www.baeldung.com/spring-security-interview-questions) — recurring security interview topic patterns; not used as answer text.
- [Baeldung: Java Interview Questions](https://www.baeldung.com/java-interview-questions) — recurring Java interview topic patterns; not used as answer text.
