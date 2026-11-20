# 2. Planning — Choosing & Organizing Topics

> Part of the Knowledge Base 2 rules. See [rules.md](rules.md) for the index and precedence.

---

## 5. How a Topic Is Chosen & Organized

1. The LLM generates a list of lessons/topics and records them in `plan/topics.md`.
2. It picks **one topic** at a time.
3. It **fetches all subtopics** needed to learn that topic.
4. It **analyzes and re-orders** the subtopics into the most intuitive
   learning sequence — not the raw fetched order.
5. It creates the topic folder and works through subtopics in that order.

### 5.1 `plan/topics.md` format

`plan/topics.md` lists **main topics only**, one per line, each tagged with my
**current proficiency**:

```
<topic> - <proficiency>
```

Example:

```
java - interview
spring boot - beginner
react - none
```

- **Proficiency** reflects how well I *already* know the topic (not a target):
  - `none` — brand new; haven't started.
  - `beginner` — some exposure / basics only.
  - `interview` — confident and interview-ready.
  - The scale is extensible; add levels between these as needed.
- The LLM may **freely** add, update, or reorganize entries in this file
  (Plan is fully writable per §3 in [01-areas.md](01-areas.md)).
- **Subtopics do NOT live here.** They are fetched and reordered into
  `learn/learning/<topic>/overview.html` (§6.2 in [03-teaching.md](03-teaching.md)).
