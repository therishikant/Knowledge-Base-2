# 6. Session Flow

> Part of the Knowledge Base 2 rules. See [rules.md](rules.md) for the index and precedence.

---

## 10. Session Flow

Every working session follows this order:

1. **Surface what's due.** On session start, the LLM scans every
   `questions.json`, finds questions whose `nextAsk <= today` (per §9.4 in
   [05-interview.md](05-interview.md)), and shows me the **due revision list** first.
2. **Revision first.** All due revisions are cleared **before** any new
   learning begins or continues. Revision always takes priority.
3. **Then learn / continue.** Once nothing is due, the LLM works on **new
   material**, and it must **finish the in-progress topic first** — it stays on
   the current main topic (and its subtopics, in order) until that main topic is
   **cleared** (§9.2.1 in [05-interview.md](05-interview.md)) before starting a new one.
4. **Update progress.** After each lesson or interview, the LLM updates the
   progress file (§10.1) and the relevant `questions.json`.

### 10.1 Progress file

A dedicated file tracks where I am:

- **Location:** `learn/progress.json`
- **Purpose:** current position + what has been cleared, so any session can
  resume without re-inferring state.

```json
{
  "currentTopic": "react",
  "currentSubtopic": "components",
  "topics": {
    "react": {
      "status": "in-progress",
      "clearedSubtopics": ["jsx"],
      "pendingSubtopics": ["components", "props", "state"]
    }
  },
  "updatedAt": "2026-07-07"
}
```

| Field                       | Meaning                                                       |
| --------------------------- | ------------------------------------------------------------- |
| `currentTopic`              | Main topic currently in progress.                             |
| `currentSubtopic`           | Subtopic currently being learned.                             |
| `topics.<name>.status`      | `not-started` \| `in-progress` \| `cleared`.                  |
| `topics.<name>.clearedSubtopics` | Subtopics whose interviews are cleared.                  |
| `topics.<name>.pendingSubtopics` | Remaining subtopics, in learning order.                  |
| `updatedAt`                 | ISO date of the last update.                                  |

The LLM keeps `progress.json` in sync after every lesson and interview.
