# Databases and Persistence — Senior Java Question Bank

[Back to index](index.md)

**Audience:** Senior Java developer / tech lead.  
**Version frame:** Java 8-era JPA commonly uses `javax.persistence`; modern Jakarta Persistence uses `jakarta.persistence`. Java 17/21 is the main working context. Jakarta Persistence 3.2 requires Java 17 and adds features not available in every earlier provider. Check the actual ORM and database version; a Java 25 runtime alone does not change SQL or persistence guarantees.

**Scope:** Data modeling, SQL, query execution, database transactions, JDBC, JPA/Hibernate, migrations, and data recovery. Spring proxy behavior is in the Spring bank; cross-service consistency protocols are in the distributed-systems bank; complete product architectures are in the system-design bank.

**Research basis:** Interview guides repeatedly emphasize indexes, execution plans, isolation, locking, N+1 queries, entity state, and safe schema changes. The original questions below explain those patterns using official vendor and persistence references. Guide coverage indicates recurring themes, not measured interview frequency. PostgreSQL and MySQL differences are stated rather than treated as universal SQL behavior.

## Question index

1. [When is a relational database a good default?](#q1-when-is-a-relational-database-a-good-default)
2. [What do normalization and denormalization trade off?](#q2-what-do-normalization-and-denormalization-trade-off)
3. [How do you choose a primary key?](#q3-how-do-you-choose-a-primary-key)
4. [Why should constraints exist even when Java code validates input?](#q4-why-should-constraints-exist-even-when-java-code-validates-input)
5. [What is the difference between `WHERE` and `HAVING`?](#q5-what-is-the-difference-between-where-and-having)
6. [How can a `LEFT JOIN` accidentally behave like an inner join?](#q6-how-can-a-left-join-accidentally-behave-like-an-inner-join)
7. [How do `EXISTS`, joins, and `IN` differ?](#q7-how-do-exists-joins-and-in-differ)
8. [What SQL mistakes commonly cause duplicate or missing results?](#q8-what-sql-mistakes-commonly-cause-duplicate-or-missing-results)
9. [How would you find the latest order for each customer?](#q9-how-would-you-find-the-latest-order-for-each-customer)
10. [How should Java and SQL represent money and time?](#q10-how-should-java-and-sql-represent-money-and-time)
11. [What does an index improve, and what does it cost?](#q11-what-does-an-index-improve-and-what-does-it-cost)
12. [How would you order columns in a composite B-tree index?](#q12-how-would-you-order-columns-in-a-composite-b-tree-index)
13. [Why might a database ignore an index?](#q13-why-might-a-database-ignore-an-index)
14. [What does a covering or index-only query mean?](#q14-what-does-a-covering-or-index-only-query-mean)
15. [What is a sargable predicate?](#q15-what-is-a-sargable-predicate)
16. [How do you read an execution plan?](#q16-how-do-you-read-an-execution-plan)
17. [How do nested-loop, hash, and merge joins differ?](#q17-how-do-nested-loop-hash-and-merge-joins-differ)
18. [How does keyset pagination differ from offset pagination?](#q18-how-does-keyset-pagination-differ-from-offset-pagination)
19. [When does table partitioning help, and when does it not?](#q19-when-does-table-partitioning-help-and-when-does-it-not)
20. [What does ACID mean in practical terms?](#q20-what-does-acid-mean-in-practical-terms)
21. [How does MVCC work?](#q21-how-does-mvcc-work)
22. [What anomalies do isolation levels address?](#q22-what-anomalies-do-isolation-levels-address)
23. [How do PostgreSQL and MySQL defaults differ?](#q23-how-do-postgresql-and-mysql-defaults-differ)
24. [What is a lost update, and how can you prevent it?](#q24-what-is-a-lost-update-and-how-can-you-prevent-it)
25. [What is write skew?](#q25-what-is-write-skew)
26. [How do optimistic and pessimistic locking differ?](#q26-how-do-optimistic-and-pessimistic-locking-differ)
27. [Why do deadlocks happen?](#q27-why-do-deadlocks-happen)
28. [What is the difference between a lock timeout and a deadlock?](#q28-what-is-the-difference-between-a-lock-timeout-and-a-deadlock)
29. [How should a serializable transaction failure be retried?](#q29-how-should-a-serializable-transaction-failure-be-retried)
30. [Does a successful commit mean data survives every failure?](#q30-does-a-successful-commit-mean-data-survives-every-failure)
31. [How should JDBC resources and prepared statements be used?](#q31-how-should-jdbc-resources-and-prepared-statements-be-used)
32. [How should a JDBC connection pool be sized?](#q32-how-should-a-jdbc-connection-pool-be-sized)
33. [What causes connection leaks and apparent pool exhaustion?](#q33-what-causes-connection-leaks-and-apparent-pool-exhaustion)
34. [What is the relationship between JPA and Hibernate?](#q34-what-is-the-relationship-between-jpa-and-hibernate)
35. [What are the main entity states?](#q35-what-are-the-main-entity-states)
36. [What is the persistence context?](#q36-what-is-the-persistence-context)
37. [How do `persist` and `merge` differ?](#q37-how-do-persist-and-merge-differ)
38. [What is dirty checking, and how do flush and commit differ?](#q38-what-is-dirty-checking-and-how-do-flush-and-commit-differ)
39. [Why can entity `equals`, `hashCode`, and `toString` be dangerous?](#q39-why-can-entity-equals-hashcode-and-tostring-be-dangerous)
40. [What is the owning side of a JPA relationship?](#q40-what-is-the-owning-side-of-a-jpa-relationship)
41. [How do cascades and orphan removal differ?](#q41-how-do-cascades-and-orphan-removal-differ)
42. [What is the N+1 query problem?](#q42-what-is-the-n1-query-problem)
43. [When should you use a fetch join, an entity graph, batch fetching, or a projection?](#q43-when-should-you-use-a-fetch-join-an-entity-graph-batch-fetching-or-a-projection)
44. [Is `LAZY` guaranteed to prevent all association loading?](#q44-is-lazy-guaranteed-to-prevent-all-association-loading)
45. [What causes `LazyInitializationException`, and what is the right response?](#q45-what-causes-lazyinitializationexception-and-what-is-the-right-response)
46. [Why are collection fetch joins risky with pagination?](#q46-why-are-collection-fetch-joins-risky-with-pagination)
47. [How does `@Version` implement optimistic locking?](#q47-how-does-version-implement-optimistic-locking)
48. [What is the difference between first-level and second-level caches?](#q48-what-is-the-difference-between-first-level-and-second-level-caches)
49. [How do bulk JPQL updates differ from changing managed entities?](#q49-how-do-bulk-jpql-updates-differ-from-changing-managed-entities)
50. [How do JPQL, Criteria, and native SQL compare?](#q50-how-do-jpql-criteria-and-native-sql-compare)
51. [Can Java records be used as entities?](#q51-can-java-records-be-used-as-entities)
52. [When is a document database a better fit than relational storage?](#q52-when-is-a-document-database-a-better-fit-than-relational-storage)
53. [How do a cache, a search index, and a database differ?](#q53-how-do-a-cache-a-search-index-and-a-database-differ)
54. [What are soft-delete trade-offs?](#q54-what-are-soft-delete-trade-offs)
55. [How do read replicas affect correctness?](#q55-how-do-read-replicas-affect-correctness)
56. [What are backups, point-in-time recovery, RPO, and RTO?](#q56-what-are-backups-point-in-time-recovery-rpo-and-rto)
57. [A query became slow without a code change. What would you investigate?](#q57-a-query-became-slow-without-a-code-change-what-would-you-investigate)
58. [How would you prevent inventory from becoming negative?](#q58-how-would-you-prevent-inventory-from-becoming-negative)
59. [How should a large import be implemented?](#q59-how-should-a-large-import-be-implemented)
60. [How would you perform a backward-compatible schema change?](#q60-how-would-you-perform-a-backward-compatible-schema-change)
61. [How should a unique constraint be added to a large dirty table?](#q61-how-should-a-unique-constraint-be-added-to-a-large-dirty-table)
62. [A database CPU graph is normal, but requests wait. What next?](#q62-a-database-cpu-graph-is-normal-but-requests-wait-what-next)
63. [Users report that saved changes disappear. How would you investigate?](#q63-users-report-that-saved-changes-disappear-how-would-you-investigate)
64. [Two operators edit the same customer record. How should the API handle it?](#q64-two-operators-edit-the-same-customer-record-how-should-the-api-handle-it)
65. [What should a tech lead include in a persistence review?](#q65-what-should-a-tech-lead-include-in-a-persistence-review)

## Modeling and SQL

### Q1. When is a relational database a good default?
**Answer:** It is a good default when the data has relationships, integrity rules, transactional updates, and several query shapes. Constraints and SQL make many business invariants easier to express. A document or key-value store may fit a dominant access pattern better, but “NoSQL scales” is not a complete reason. Compare required guarantees, query flexibility, operational cost, and growth using a realistic workload.

### Q2. What do normalization and denormalization trade off?
**Answer:** Normalization separates facts to reduce duplication and update anomalies. Denormalization stores derived or repeated data to make important reads cheaper. The cost is keeping copies consistent and defining which copy is authoritative. Start with business invariants and query measurements. A precomputed order summary can be useful; duplicating customer contact data everywhere without a correction strategy usually creates a maintenance problem.

### Q3. How do you choose a primary key?
**Answer:** Choose a stable, unique identifier with suitable size and generation behavior. Numeric sequences are compact and index-friendly but may need coordination across independent writers. UUIDs support decentralized generation but have larger storage and potentially poorer insertion locality depending on the format. A business identifier can change or expose sensitive information. Keep natural uniqueness constraints even when the primary key is synthetic.

### Q4. Why should constraints exist even when Java code validates input?
**Answer:** Other code paths, concurrent requests, scripts, and future services can bypass one validation method. Unique, foreign-key, check, and not-null constraints protect stored state at the authoritative boundary. Java validation improves messages and catches errors early; it does not replace database enforcement. Translate constraint failures into useful domain outcomes, and account for vendor-specific treatment of nulls and deferred constraints.

### Q5. What is the difference between `WHERE` and `HAVING`?
**Answer:** `WHERE` filters input rows before grouping; `HAVING` filters groups after aggregation. To find customers with more than three paid orders, first filter paid rows with `WHERE`, then group by customer and apply `HAVING COUNT(*) > 3`. Moving a condition changes meaning when it refers to an aggregate. Inspect the plan for performance, but never rewrite a query in a way that changes its result.

### Q6. How can a `LEFT JOIN` accidentally behave like an inner join?
**Answer:** A predicate in `WHERE` that requires a non-null value from the right table removes unmatched rows. For example, `LEFT JOIN payment p ... WHERE p.status = 'PAID'` excludes orders with no payment. Put the status condition in the join condition when unmatched orders must remain. Explain the desired result with a small example containing no match, one match, and multiple matches.

### Q7. How do `EXISTS`, joins, and `IN` differ?
**Answer:** `EXISTS` expresses whether a qualifying row exists without requiring its columns. A join combines rows and can multiply the result. `IN` tests membership, but nulls are especially important for `NOT IN`, which can produce unknown rather than true. Do not claim one form is always fastest; optimizers can transform equivalent expressions. Choose correct semantics, then inspect the engine's actual plan.

### Q8. What SQL mistakes commonly cause duplicate or missing results?
**Answer:** Joining two one-to-many relationships can multiply rows. Adding `DISTINCT` may hide the symptom without fixing wrong aggregates. Missing tie-breakers can make pagination unstable, and null-sensitive comparisons can omit rows. Establish the intended result grain, such as one row per order, before writing joins. Aggregate children separately or use existence checks when the question is not asking for every child combination.

### Q9. How would you find the latest order for each customer?
**Answer:** A window function makes the rule explicit:

```sql
SELECT customer_id, order_id, created_at
FROM (
    SELECT customer_id, order_id, created_at,
           ROW_NUMBER() OVER (
               PARTITION BY customer_id
               ORDER BY created_at DESC, order_id DESC
           ) AS rn
    FROM orders
) ranked
WHERE rn = 1;
```

The second sort key breaks timestamp ties deterministically. This returns customers with orders; include a customer table if customers without orders must appear.

### Q10. How should Java and SQL represent money and time?
**Answer:** Use a suitable exact decimal representation for monetary values, typically `BigDecimal` with explicit scale and rounding rules, or an integer minor-unit representation when the currency model permits it. Avoid binary floating point for exact balances. For time, distinguish an instant, a local date-time, and a business date. Define zone rules and database mappings; a column called `timestamp` does not mean identical behavior across vendors.

## Indexes and execution plans

### Q11. What does an index improve, and what does it cost?
**Answer:** An index provides an alternate structure for finding or ordering rows. It can reduce scanned data but consumes storage, memory, and write work. Every insert, update, and delete may need index maintenance. Too many indexes can make a write-heavy service slower. Design indexes around important predicates, joins, ordering, and result sizes, then validate them with real data distributions rather than indexing every column.

### Q12. How would you order columns in a composite B-tree index?
**Answer:** Consider the complete query pattern. Equality filters often make useful leading columns, followed by a range or ordering key. For `tenant_id = ? ORDER BY created_at, id`, an index beginning with those columns can be effective. Leading columns usually matter strongly, but “later columns can never be used” is too absolute; engines can use other strategies, including version-dependent skip scans. Verify the plan.

### Q13. Why might a database ignore an index?
**Answer:** A sequential scan can be cheaper when the query needs much of the table. Other reasons include stale statistics, incompatible types or collations, a function on the indexed expression, poor selectivity, or a costly sort and heap access pattern. The optimizer chooses estimated total cost, not “index good, scan bad.” Compare estimates with actual rows and I/O before forcing a plan.

### Q14. What does a covering or index-only query mean?
**Answer:** It means the index contains the values needed for a query so some table access can be avoided. Details are engine-specific: PostgreSQL index-only scans still depend on visibility information and may visit heap pages. Included columns can help but enlarge the index. Do not promise zero table reads merely because all selected columns appear in the index definition.

### Q15. What is a sargable predicate?
**Answer:** It is a predicate the engine can use effectively as an index search condition. Applying a function to a column may prevent a normal index from supporting the desired lookup. Instead of extracting the year from every timestamp, a range covering that year may work better. Expression indexes can also help. Preserve timezone and boundary semantics when rewriting; faster but wrong time filtering is not an optimization.

### Q16. How do you read an execution plan?
**Answer:** Follow scans, joins, sorts, and aggregates, checking estimated versus actual rows, loops, time, and buffer activity. Large estimate errors often explain a poor join strategy. Look for spilled sorts, repeated expensive inner operations, and excessive data read before filtering. Start with the costly part of the actual workload. In PostgreSQL, `EXPLAIN ANALYZE` executes the statement, so use care with writes and expensive production queries.

### Q17. How do nested-loop, hash, and merge joins differ?
**Answer:** A nested loop can work well when the outer input is small and the inner lookup is indexed. A hash join builds a hash table and can fit larger equality joins, with memory and spill considerations. A merge join combines ordered inputs and can benefit from existing ordering. No strategy is always best; cardinality, available indexes, memory, and data distribution drive the choice.

### Q18. How does keyset pagination differ from offset pagination?
**Answer:** Offset pagination skips a number of result rows, so deep pages can become expensive and concurrent changes can shift positions. Keyset pagination resumes after a stable ordered key, such as `(created_at, id)`, and usually scales better for sequential browsing. It needs a deterministic order, suitable index, and a cursor contract. It does not naturally support arbitrary page-number jumps or guarantee a frozen dataset.

### Q19. When does table partitioning help, and when does it not?
**Answer:** Partitioning can help prune irrelevant data, manage retention, and maintain large datasets in smaller pieces. It is not automatically horizontal sharding across machines. Queries need predicates that allow pruning, and too many partitions increase planning and maintenance costs. Constraints and uniqueness rules can have restrictions. Choose a partition key based on workload and lifecycle, not only the table's row count.

## Transactions and concurrency

### Q20. What does ACID mean in practical terms?
**Answer:** Atomicity means a transaction's changes commit together or do not become committed state. Consistency means declared invariants remain valid when the application and database enforce them. Isolation controls interactions between concurrent transactions. Durability describes survival of acknowledged commits under the configured failure model. ACID does not mean every business rule is automatic or that default isolation makes all concurrent workflows safe.

### Q21. How does MVCC work?
**Answer:** Multiversion concurrency control keeps row versions so reads can use a consistent visibility view while writes occur. It reduces some read-write blocking but does not eliminate locks, write conflicts, or cleanup work. Snapshot rules depend on isolation level and engine. Long transactions can hold old versions relevant for too long, increasing storage or cleanup pressure. “MVCC means lock-free database” is incorrect.

### Q22. What anomalies do isolation levels address?
**Answer:** Dirty reads observe uncommitted changes. Nonrepeatable reads observe a changed row across reads. Phantom reads observe changed matching row sets. Serialization anomalies include outcomes that cannot match any serial execution. The SQL standard defines minimum protections; engines may offer stronger behavior. For example, PostgreSQL maps Read Uncommitted to Read Committed and its Repeatable Read prevents phantoms but can still allow serialization anomalies.

### Q23. How do PostgreSQL and MySQL defaults differ?
**Answer:** PostgreSQL normally defaults to Read Committed; MySQL InnoDB normally defaults to Repeatable Read. Their snapshot and locking behavior differs, especially between ordinary consistent reads and locking operations. InnoDB can use next-key or gap locks in relevant operations. State the engine and transaction shape before predicting behavior. A Spring isolation setting named `DEFAULT` means the database's configured default, not one universal level.

### Q24. What is a lost update, and how can you prevent it?
**Answer:** Two callers read the same old value and later overwrite each other's changes. An atomic statement such as `SET quantity = quantity - 1` can avoid the read-modify-write gap for a counter. Version checks or suitable locking can protect more complex changes. Select the method according to the invariant. A surrounding transaction alone does not guarantee protection at every isolation level.

### Q25. What is write skew?
**Answer:** Two transactions read a shared condition but update different rows, together breaking an invariant. For example, two on-call doctors each see another doctor available and both go off duty. Per-row version checks may not conflict because different rows changed. Use serializable isolation with retry, a shared locked coordination row, or another representation that makes the invariant enforceable. First identify every row involved in the rule.

### Q26. How do optimistic and pessimistic locking differ?
**Answer:** Optimistic locking detects conflicting changes, commonly using a version value, and rejects one writer. Pessimistic locking obtains database locks before completing the decision, making others wait or fail. Optimistic approaches fit relatively rare conflicts and user editing; pessimistic approaches can fit short, highly contested updates. Both need handling for failure and timeouts. Neither should hold database resources while waiting for a person.

### Q27. Why do deadlocks happen?
**Answer:** Transactions acquire resources in conflicting orders and form a wait cycle. The database detects a cycle and aborts a participant. Keep transactions short, access shared resources in a consistent order, index relevant lookups, and retry a safely repeatable whole transaction when appropriate. Capture the database's deadlock report. Increasing lock timeouts does not remove a true deadlock and may prolong other contention.

### Q28. What is the difference between a lock timeout and a deadlock?
**Answer:** A lock timeout means waiting exceeded a configured limit, even if no cycle exists. A deadlock is a cycle that prevents participants from making progress. They can require different operational investigation but both may invalidate a transaction. Use short bounded waits for interactive operations, record useful diagnostics, and determine whether a complete retry is safe. Blindly retrying a permanently hot resource increases load.

### Q29. How should a serializable transaction failure be retried?
**Answer:** Start a fresh transaction, reread the necessary state, and repeat the complete decision, with a bounded retry policy and jitter if contention warrants it. Repeating only the final SQL statement can reuse an invalid earlier decision. Keep external effects outside the retry unless they have a safe idempotency protocol. Serializable isolation prevents unsafe committed combinations by sometimes requiring an operation to run again.

### Q30. Does a successful commit mean data survives every failure?
**Answer:** No. Durability depends on logging, flush settings, replication acknowledgment, storage, and the promised failure model. Asynchronous replicas may lag behind acknowledged primary writes. Some configurations trade possible recent data loss for lower latency. Define which failures must be survived and test recovery. A process crash, machine loss, region loss, and accidental deletion are different failure cases.

## JDBC, pooling, and JPA fundamentals

### Q31. How should JDBC resources and prepared statements be used?
**Answer:** Use prepared statements to bind data values and prevent SQL injection through those values. Identifiers and sort directions usually need an allowlist because they cannot be bound as ordinary values. Close connections, statements, and result sets using structured resource handling such as try-with-resources. Closing a pooled connection normally returns it to the pool. Do not concatenate user input into SQL.

### Q32. How should a JDBC connection pool be sized?
**Answer:** Size it against database capacity and measured concurrent database work, not the number of HTTP threads. Account for all application replicas, admin tools, and background jobs. Set bounded acquisition waits and monitor active, idle, waiting, and timed-out connections. Too large a pool can increase database contention. Java 21 virtual threads can increase waiting concurrency, but they do not increase the database's safe connection count.

### Q33. What causes connection leaks and apparent pool exhaustion?
**Answer:** Unclosed resources are one cause, but long transactions, slow queries, lock waits, and remote calls inside a transaction can keep every connection busy without a literal leak. Measure checkout duration and database wait states. Leak-detection logs identify suspiciously long ownership, not always forgotten closes. Increasing pool size without understanding the hold time can move the failure into the database.

### Q34. What is the relationship between JPA and Hibernate?
**Answer:** JPA, now Jakarta Persistence, defines a standard API and mapping contract. Hibernate ORM is a provider that implements the contract and offers additional features. Standard annotations do not guarantee identical SQL or every runtime detail across providers. Clearly distinguish portable behavior from a Hibernate-specific optimization. Changing only the API dependency is not enough to upgrade the provider or obtain support for a new specification feature.

### Q35. What are the main entity states?
**Answer:** A new entity has not become managed. A managed entity belongs to a persistence context, where changes may be detected and synchronized. A detached entity no longer belongs to that context. A removed entity is scheduled for deletion. State controls which operations work and when SQL occurs. A Java object containing an ID is not necessarily managed, and closing the context does not erase the object's field values.

### Q36. What is the persistence context?
**Answer:** It tracks managed entities and their identity within its scope, providing the first-level cache and change tracking. Repeated loading of the same identity can return the same managed instance. This is not a global cache and not proof that the database has not changed elsewhere. A normal `EntityManager` is not meant for concurrent application-thread sharing. Bound its lifetime and avoid accumulating millions of managed objects.

### Q37. How do `persist` and `merge` differ?
**Answer:** `persist` makes a new entity managed and schedules its insertion, subject to identifier strategy and flush behavior. `merge` copies state into a managed instance and returns that instance; the argument does not automatically become managed. Ignoring the returned object can cause confusion. Blindly merging client-supplied graphs can overwrite fields or trigger large cascades. Prefer explicit loading and intentional updates for sensitive use cases.

### Q38. What is dirty checking, and how do flush and commit differ?
**Answer:** Dirty checking detects changes to managed state so the provider can issue updates. Flush synchronizes pending changes with the database inside the transaction; commit completes the transaction. A flushed change can still roll back. Automatic flush may occur before relevant queries, so SQL can execute earlier than expected. Constraint failures may appear at flush or commit rather than at the setter or repository call.

### Q39. Why can entity `equals`, `hashCode`, and `toString` be dangerous?
**Answer:** A generated identifier can change after insertion, breaking hash-based collection membership if hashing depends on it. Lazy relationships can trigger SQL or recursion during equality, logging, or string conversion. Proxies complicate exact-class comparisons. Choose an identity strategy deliberately, keep behavior stable where collections require it, and avoid traversing relationship graphs. Automatically generated methods need review just like handwritten entity code.

### Q40. What is the owning side of a JPA relationship?
**Answer:** It is the side whose mapping controls the relationship's database update. In a common bidirectional one-to-many association, the child's many-to-one owns the foreign key, and the parent's collection uses `mappedBy`. Keep both Java sides consistent using helper methods. Updating only the inverse collection can leave the foreign key unchanged. Ownership is a persistence concept, not a statement about which domain object is more important.

### Q41. How do cascades and orphan removal differ?
**Answer:** Cascades propagate selected entity-manager operations, such as persist or remove, along an association. Orphan removal schedules deletion when a privately owned child is removed from the relationship according to the mapping rules. They are not database foreign-key cascade settings. Use them according to lifecycle ownership. Cascading remove across a many-to-many relationship can delete shared objects that another aggregate still needs.

## Fetching and persistence performance

### Q42. What is the N+1 query problem?
**Answer:** A query loads N parent rows, then additional queries load associated data for each parent. It commonly appears during loops or serialization. Detect it with statement counts, traces, and representative integration tests. A small development dataset may hide the cost. The remedy is a deliberate fetch plan, not blindly making every relationship eager, which can create different over-fetching or secondary-query problems.

### Q43. When should you use a fetch join, an entity graph, batch fetching, or a projection?
**Answer:** Use a fetch join or entity graph when a particular entity graph is needed. Batch fetching can reduce repeated association lookups without one enormous join. Use DTO projections when a read needs only selected columns and no managed graph. Compare SQL count, row multiplication, memory, and pagination behavior. Fetching fewer statements is not always faster if each statement produces a huge Cartesian expansion.

### Q44. Is `LAZY` guaranteed to prevent all association loading?
**Answer:** Not universally. In the specification, lazy fetching is generally a hint, while eager fetching requires the related state to be available. Provider capabilities, enhancement, relationship type, and access patterns affect execution. Eager loading does not guarantee one join either. Define and test the actual SQL for critical paths instead of inferring it from one annotation.

### Q45. What causes `LazyInitializationException`, and what is the right response?
**Answer:** In Hibernate, code tries to initialize lazy state when the required session is no longer available. Load the use case's required data inside its intended boundary, often into a DTO. Avoid fixing every occurrence by keeping a session open through rendering or making all associations eager. Those choices can hide database access in the web layer and produce unpredictable query counts.

### Q46. Why are collection fetch joins risky with pagination?
**Answer:** A parent with many children occupies many joined rows, while the API page usually counts parents. Applying a row limit directly can give incomplete or incorrect parent pages, and providers may instead paginate in memory or reject the combination. A common solution first pages stable parent IDs, then fetches the required graph for those IDs and restores order. Test generated SQL and provider warnings.

### Q47. How does `@Version` implement optimistic locking?
**Answer:** Updates include the expected version in their condition and advance the version on success. If no row matches, the provider detects a conflicting change. Handle the failure as a conflict or retry an appropriate complete operation. It does not automatically protect invariants across unrelated rows. Native SQL and bulk updates must follow a deliberate version policy or they can bypass the expected protection.

### Q48. What is the difference between first-level and second-level caches?
**Answer:** The first-level cache belongs to a persistence context and tracks managed identity. A second-level cache can share entity state across contexts when enabled and configured; query caching is another separate concern. Shared caches need correct invalidation, especially when other writers bypass the ORM. Cache hit ratio alone is insufficient: monitor stale-data risk, memory cost, and whether the database remains the main bottleneck.

### Q49. How do bulk JPQL updates differ from changing managed entities?
**Answer:** Bulk updates operate directly against database rows and do not normally synchronize every already-managed entity or run ordinary per-entity lifecycle behavior. The persistence context may contain stale state afterward. Flush relevant changes first, then clear or refresh intentionally. Verify optimistic-lock behavior separately. Mixing bulk operations and managed updates in one context without a plan can overwrite newer values or return inconsistent data.

### Q50. How do JPQL, Criteria, and native SQL compare?
**Answer:** JPQL expresses queries over entity mappings and is readable for many stable queries. Criteria builds query structure programmatically and can help compose dynamic predicates, though it can become verbose. Native SQL exposes vendor features and gives direct control, at the cost of portability and mapping work. Use the simplest tool meeting the workload. None removes the need to inspect actual execution plans.

### Q51. Can Java records be used as entities?
**Answer:** Do not assume a record is a portable JPA entity: records are final and do not fit standard entity construction and mutability requirements. They are useful for DTO projections. Jakarta Persistence 3.2 adds standard support for records as embeddables, which is different from entities and requires a supporting provider. Always name the specification and provider version when discussing modern record support.

## Specialized data stores, change, and recovery

### Q52. When is a document database a better fit than relational storage?
**Answer:** It can fit aggregate-shaped data commonly read and written together, with variable structure and limited cross-aggregate joins. Define document size, indexing, transaction scope, and schema evolution. “Schema-less” does not remove the application's schema contract. Embedding unbounded histories can become expensive, while excessive references recreate joins in application code. Choose from access patterns and integrity needs, not a general claim that documents are more flexible.

### Q53. How do a cache, a search index, and a database differ?
**Answer:** A cache accelerates access to data that usually has another authoritative source. A search index supports retrieval and ranking, often as a derived representation. A database maintains authoritative state under its documented transaction and durability guarantees. A product can provide several roles, but the roles should still be explicit. Define rebuild procedures and acceptable lag before treating a derived system as indispensable.

### Q54. What are soft-delete trade-offs?
**Answer:** Soft deletion preserves a record with a status or deletion timestamp, which can help undo and audit requirements. Every relevant query, uniqueness rule, relationship, and retention process must handle deleted rows correctly. It can increase index size and leak data when a filter is missed. Soft deletion is not necessarily legal erasure, and it does not replace backups or a full audit history.

### Q55. How do read replicas affect correctness?
**Answer:** Replicas can scale eligible reads, but asynchronous lag can cause stale results after a successful write. Route read-your-writes operations to the authoritative primary or use a documented position-based mechanism where available. Do not send lock-based decisions to a stale read replica. Replicas also need separate capacity and failure monitoring; adding them does not automatically improve primary write throughput.

### Q56. What are backups, point-in-time recovery, RPO, and RTO?
**Answer:** Backups capture recoverable data. Point-in-time recovery combines a suitable backup with retained transaction logs to recover near a selected time. Recovery point objective, RPO, is the tolerated data-loss interval; recovery time objective, RTO, is the tolerated restoration duration. A replica is not a replacement for backup because it can copy destructive changes. Verify objectives with actual restore drills.

## Scenario and case-study questions

### Q57. A query became slow without a code change. What would you investigate?
**Answer:** Check data growth and skew, statistics, plan changes, cache state, blocking, storage latency, vacuum or maintenance health, and changed parameter values. Compare actual plans and wait events with the healthy period. A growing tenant can make an old assumption invalid. Do not add a random index before checking whether the query is waiting for a lock rather than executing slowly.

### Q58. How would you prevent inventory from becoming negative?
**Answer:** For a simple stock counter, use a conditional atomic update:

```sql
UPDATE inventory
SET available = available - :amount
WHERE sku = :sku AND available >= :amount;
```

Require a positive amount and check the affected-row count. Add a nonnegative constraint where supported. Put related reservation records in the same transaction. Retries need a unique request identifier so the same reservation does not decrement stock twice.

### Q59. How should a large import be implemented?
**Answer:** Validate input, process bounded chunks, use suitable JDBC or ORM batching, and release managed state between chunks when using an ORM. Choose transaction size to balance atomicity, log volume, locks, memory, and retry cost. Identifier strategies can affect insert batching. Track progress with a restartable checkpoint and unique input identity. A single transaction holding every imported entity is usually a poor production default.

### Q60. How would you perform a backward-compatible schema change?
**Answer:** Use expand-and-contract: add compatible structures, deploy code that can tolerate both versions, backfill in bounded chunks, switch reads and writes deliberately, then remove old structures after old code is gone. Check locking, replication, and data consistency during backfill. “Online DDL” is engine- and operation-specific. A migration tool records ordering; it does not make an unsafe alteration safe automatically.

### Q61. How should a unique constraint be added to a large dirty table?
**Answer:** First identify and resolve duplicates according to an explicit business rule. Prevent new duplicates while cleanup runs, then build or validate the constraint using supported low-impact techniques. Check engine-specific transaction and locking restrictions for online index creation. Test on production-like volume and plan for a failed build. Assuming the migration is safe because it passed on ten test rows is not enough.

### Q62. A database CPU graph is normal, but requests wait. What next?
**Answer:** Inspect lock waits, connection acquisition, storage latency, network issues, transaction age, and per-query wait events. Low CPU does not mean the database is healthy; sessions may be blocked or waiting for I/O. Trace a representative request from pool checkout through commit. Separate application queuing from database execution before changing pool size, instance size, or query timeouts.

### Q63. Users report that saved changes disappear. How would you investigate?
**Answer:** Follow one operation using safe correlation identifiers and verify the actual commit outcome. Check swallowed exceptions, rollback-only transactions, detached-object handling, stale replica reads, concurrent overwrites, caches, and incorrect tenant routing. Read directly from the authoritative store with the correct key and context. Distinguish “never committed” from “committed but not visible” and “committed then overwritten” before selecting a fix.

### Q64. Two operators edit the same customer record. How should the API handle it?
**Answer:** Return a version with the representation and require it when updating, possibly through an HTTP precondition such as `If-Match`. Apply the update only when the stored version matches. Return a clear conflict or failed-precondition outcome and let the user reload or merge intentionally. Silent last-write-wins may be acceptable for some fields, but it should be a business decision rather than an accidental ORM behavior.

### Q65. What should a tech lead include in a persistence review?
**Answer:** Review invariants, constraints, transaction boundaries, concurrent updates, generated SQL, execution plans, index cost, connection budgets, migration safety, and recovery. Require realistic data volume and skew, not only repository unit tests. Include data retention, access control, and sensitive-data handling. Document provider-specific assumptions and expected query budgets so a future refactor cannot silently change correctness or create a large operational cost.

## Sources and further reading

Official references support technical behavior; interview guides were used only to discover recurring themes.

- [PostgreSQL: constraints](https://www.postgresql.org/docs/current/ddl-constraints.html) and [window functions](https://www.postgresql.org/docs/current/tutorial-window.html) — Q1–Q10.
- [PostgreSQL: multicolumn indexes](https://www.postgresql.org/docs/current/indexes-multicolumn.html), [index-only scans](https://www.postgresql.org/docs/current/indexes-index-only-scans.html), and [using EXPLAIN](https://www.postgresql.org/docs/current/using-explain.html) — Q11–Q19 and Q57.
- [PostgreSQL: transaction isolation](https://www.postgresql.org/docs/current/transaction-iso.html) and [explicit locking](https://www.postgresql.org/docs/current/explicit-locking.html) — Q20–Q30 and Q58.
- [MySQL 8.4: InnoDB transaction isolation](https://dev.mysql.com/doc/refman/8.4/en/innodb-transaction-isolation-levels.html) — engine-specific comparison in Q23; do not generalize its default to PostgreSQL.
- [Oracle Java 21: JDBC basics](https://docs.oracle.com/en/java/javase/21/docs/api/java.sql/java/sql/package-summary.html) — Q31–Q33 and Q59.
- [Jakarta Persistence 3.2 specification and release details](https://jakarta.ee/specifications/persistence/3.2/) and [specification PDF](https://jakarta.ee/specifications/persistence/3.2/jakarta-persistence-spec-3.2.pdf) — Q34–Q51 and Q59, including record embeddables.
- [Hibernate ORM 6.6 user guide](https://docs.jboss.org/hibernate/orm/6.6/userguide/html_single/Hibernate_User_Guide.html) — entity state, fetching, batching, caching, locking, and provider-specific details.
- [MongoDB: data modeling](https://www.mongodb.com/docs/manual/data-modeling/) — Q52.
- [PostgreSQL: CREATE INDEX](https://www.postgresql.org/docs/current/sql-createindex.html), [partitioning](https://www.postgresql.org/docs/current/ddl-partitioning.html), and [continuous archiving and recovery](https://www.postgresql.org/docs/current/continuous-archiving.html) — Q19, Q56, and Q60–Q61.
- [Interview topic guide: Dev Weekends database questions](https://resources.devweekends.com/resources/interview-questions/databases) and [SQL/JPA/Hibernate interview topic outline](https://github.com/namgaxilem/self-learning-docs/blob/main/content/sql-jpa-hibernate.md) — topic discovery only.
