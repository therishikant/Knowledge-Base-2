# 5. Interview / Revision Mode

> Part of the Knowledge Base 2 rules. See [rules.md](rules.md) for the index and precedence.
> For a specific topic type, also apply the matching file in
> [topic-styles/](topic-styles/) (e.g. language, framework, DSA, HLD, LLD).

---

## 9. Interview / Revision Mode

After completing a topic, the LLM interviews me thoroughly. This is the
**strictest reviewer** and acts as revision mode.

### 9.1 Rules

- Difficulty progresses **easy → medium → hard**.
- Each topic is split across these difficulty levels; my current level is tracked.
- The next topic unlocks only after I **clear** the current interview.
- Questions are fetched **per topic**, and **per subtopic** if subtopics exist,
  into a question pile.
- **Interview areas** — each question tests one or more of:
  - `theory` — explain the concept.
  - `practical` — write or read code.
  - `diagram` — draw or explain it visually.
  - `trade-offs` — why X over Y; real-world judgment.
- **Scoring** — each answer is scored **1–5** (1 worst, 5 best); a score of
  **≥ 4 is a pass** (drives the schedule in 9.4).

### 9.2 Clearing an interview

A topic's interview is **cleared** only when all of the following hold:

- I understand the topic **completely** and feel confident about it.
- I can **explain the theory** clearly (Feynman-style, to a 12-year-old).
- I can **code** the relevant part where the topic is practical.
- Where applicable, I can **draw/explain the diagrams**.
- I can reason about the **trade-offs** (why this over the alternatives).

Theory alone is never enough. Interviews are judged across multiple areas —
**theory + practical/coding + diagrams** — and clearing is assessed after each
**main topic** is completed. (The full set of interview areas will be refined
as we go.)

### 9.2.1 Clearing a main topic

A **main topic** (e.g. `react`) is cleared only when **both** hold:

- **All of its subtopics are cleared** (each subtopic interview passed per 9.2).
- **All `hard`-level questions of the topic are answered (passed)** — passing
  the hard tier is mandatory, not just a medium threshold.

Only after a main topic is cleared does the next main topic unlock.


### 9.3 Question tracking (JSON)

Questions are tracked in **JSON** (optimized for the LLM, not for human
readability — kept as JSON for now). One JSON file per topic/subtopic, stored
in the matching `interviewing/` folder (e.g.
`learn/interviewing/react/components/questions.json`).

**File shape:**

```json
{
  "topic": "react",
  "subtopic": "components",
  "questions": [
    {
      "number": 1,
      "question": "What is a component and why does React use them?",
      "level": "easy",
      "areas": ["theory", "practical", "diagram"],
      "intervalDays": null,
      "lastAsked": null,
      "nextAsk": null,
      "performance": {
        "lastResult": null,
        "lastScore": null,
        "streak": 0
      },
      "history": [
        {
          "date": "2026-07-06",
          "result": "pass",
          "score": 4,
          "note": "Explained well; slight gap on re-render trigger."
        }
      ]
    }
  ]
}
```

**Field reference:**

| Field                 | Type                | Meaning                                                        |
| --------------------- | ------------------- | -------------------------------------------------------------- |
| `number`              | int                 | Question ID within the topic/subtopic.                         |
| `question`            | string              | The actual question text.                                      |
| `level`               | `easy\|medium\|hard`| Difficulty tier.                                               |
| `areas`               | string[]            | Which areas it tests: `theory`, `practical`, `diagram`, `trade-offs`. |
| `intervalDays`        | int \| null         | Current spacing interval; `null` until first asked.            |
| `lastAsked`           | date \| null        | ISO date it was last asked; `null` initially.                  |
| `nextAsk`             | date \| null        | ISO date it should next be asked; `null` initially.            |
| `performance`         | object              | Rolling status (see below).                                    |
| `performance.lastResult` | `pass\|fail\|null` | Outcome of the most recent attempt.                           |
| `performance.lastScore`  | int(1–5) \| null | Granular quality of the last answer (1 worst, 5 best).        |
| `performance.streak`     | int              | Consecutive passes; resets to 0 on a fail.                     |
| `history`             | object[]            | Append-only log of every attempt.                              |
| `history[].date`      | date                | When the attempt happened.                                     |
| `history[].result`    | `pass\|fail`        | Pass/fail for that attempt (drives the schedule).              |
| `history[].score`     | int(1–5)            | Quality score for that attempt.                                |
| `history[].note`      | string              | Short note on what went right/wrong.                           |

**Pass/fail rule:** an attempt is a **pass** when `score >= 4`, otherwise a
**fail**. `result` drives the doubling schedule in 9.4; `score` and
`streak` are for tracking trends and deciding when a topic is cleared (9.2).

### 9.4 Spaced-repetition schedule (doubling)

`nextAsk` is derived from `intervalDays`, which doubles on a pass and resets
on a fail:

- **Fail** → `intervalDays` resets to **1** (next ask is tomorrow).
- **Pass** → `intervalDays` advances along this sequence (in days):
  `2 → 4 → 8 → 16 → 30`.
- **30 days is the maximum.** Once reached, the question keeps being revised
  at the 30-day interval, again and again.
- `nextAsk = lastAsked + intervalDays`.

### 9.5 On poor performance

- If I don't do well on a question, the LLM points me back to **revise the
  concept from my notes**, or corrects me.
- The mistake is logged in the **`learn/mistakes/` area** so I don't repeat it.
- The `mistakes/` area mirrors the `learning/` structure:
  - Each **core topic** is a folder.
  - A mistake for a core topic is a **file** inside that folder.
  - A **subtopic** becomes a nested folder, with its own mistake file inside.
- Records are kept in **both short and detailed** forms.

### 9.6 Revision formats

Revision isn't only Q&A. The LLM picks the format that best builds **storage
strength** (long-term retention), not just in-the-moment fluency:

- **Q&A** (default) — one question at a time, scored per 9.1.
- **Type mode** — for complex/multi-step topics, I type a full explanation from
  memory and the LLM grades the **gaps** (what I nailed, what was vague, what I
  missed entirely). Stronger encoding than recognition.
- **Practice mode** — for practical topics, the LLM has me **write code,
  predict output, or spot the bug** in real code — exercising the `practical`
  interview area (9.1), not just theory recall.
