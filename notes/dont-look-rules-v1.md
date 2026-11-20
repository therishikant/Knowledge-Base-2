# Knowledge Base 2 — Repository Rules

These rules govern how the LLM and I work together in this repository.
They are binding: the LLM must follow them at all times.

---

## 1. Purpose

This repo is a personal learning system. I use it to **learn new concepts**,
**revise old ones**, and **prove my understanding** through interviews.
The guiding principle: I learn best when I understand a topic *as a whole*,
from first principles, connected to real problems — not memorized in fragments.

---

## 2. The Three Areas

| Area   | Path      | Purpose                                              |
| ------ | --------- | ---------------------------------------------------- |
| Plan   | `plan/`   | Everything I intend to learn about.                  |
| Learn  | `learn/`  | Where the LLM teaches me and runs revision/interviews. |
| Notes  | `notes/`  | My own notes, written by me only.                    |

---

## 3. Permissions (Hard Rules)

- **Plan** — the LLM may **freely** create and modify files here.
- **Notes** — read-only for the LLM. It may read, verify, and suggest, never modify.
- **Learn** — the LLM may **freely** create and modify files here.
- Enforcement is intended to be hard (technically prevented), not just convention.

**Why:** the read-only **Notes** rule keeps me engaged — writing my own notes
maps the knowledge into my brain and forces real understanding.

---

## 4. The Learn Area

The `learn/` area has three sub-areas:

```
learn/
  learning/      # where I learn new topics
  interviewing/  # where I get revised / quizzed (revision mode)
  mistakes/      # where my mistakes are logged per topic
```

### 4.1 Folder structure

- Each new topic gets its own folder under `learning/`, named after the topic.
  - Example: `learn/learning/react/`
- Subtopics get nested folders inside the topic.
  - Example: `learn/learning/react/components/`
- The `interviewing/` area mirrors this per-topic structure for revision.
- The `mistakes/` area mirrors this same structure (see Section 9.4).

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
  (Plan is fully writable per Section 3).
- **Subtopics do NOT live here.** They are fetched and reordered into
  `learn/learning/<topic>/overview.md` (Section 6.2).

---

## 6. How Each Topic Is Taught

Every lesson must follow this approach:

1. **Bird's-eye view first.** Give the full overview of the whole topic before
   any depth. I need the complete picture before the details.
2. **First principles.** Explain the *why, how, and when*:
   - Why does this exist? What problem does it solve?
   - Why are the alternatives not feasible?
   - When and where is it used?
3. **Connect to reality.** Tie concepts to real software-development problems
   and real-world analogies.
4. **Bird's-eye → core.** Stay high-level on the overview; go deep only on the
   **core** subtopics.
5. **Diagrams are mandatory.** Every topic must be explained with diagrams.
6. **Map to existing knowledge.** Cross-reference new concepts with related
   notes in `notes/` to reinforce connections and similarity.
7. **Transferable knowledge.** Teach concepts in a connected ("web") manner so
   they carry across languages/tools — e.g. a "function" means the same thing
   in Java or Python, so learn the concept, not just one syntax.
8. **Ground in credible sources.** Fetch material from high-trust websites
   (official docs, reputable references) and **cite them inline** in the lesson.
   Never rely on the LLM's memory alone; verify claims against real sources.

### 6.1 Before starting a topic

- The LLM must **ask what I already know** about the area, to gauge prior
  knowledge before beginning.
- **Capture the mission.** Ask *why* I want to learn this topic (the real goal —
  e.g. clear interviews, build a project) and record it at the top of the
  topic's `overview.html`. Every lesson stays grounded in this mission.
- **Aim at my zone of proximal development.** Use `progress.json` and my logged
  `mistakes/` to pitch each lesson "just hard enough" — never trivially easy,
  never overwhelming.

### 6.2 Lesson file format

Lessons are authored as **self-contained HTML files** — beautiful,
print-friendly, Tufte-style typography — so they're pleasant to revisit. Two
kinds of files live under `learning/`:

- **Topic overview** — `learn/learning/<topic>/overview.html`
  The bird's-eye view of the whole topic, written **before** any subtopic. It
  frames the big picture, why the topic exists, how its subtopics fit together,
  the intuitive order they'll be learned in (per Section 5), and **the topic's
  mission** at the very top (per Section 6.1).
- **Subtopic lesson** — `learn/learning/<topic>/<subtopic>/lesson.html`
  One deep-dive lesson per subtopic.

Every **subtopic lesson** must follow this fixed section order:

1. **Summary** — a 1–3 line TL;DR at the very top (what it is + why it matters).
2. **Where it fits** — bird's-eye placement within the parent topic.
3. **Why it exists** — the problem it solves; why alternatives fall short.
4. **How it works** — core mechanics from first principles. **A diagram is
   mandatory here.**
5. **When to use** — real use cases tied to real software-development scenarios.
6. **Explain to a 12-year-old** — Feynman pass that answers the naive
   "why do we even need this?" questions.
7. **Connections** — links to related `notes/` and other topics, emphasizing
   transferable knowledge.
8. **In practice** — a code example or a small exercise.
9. **Pitfalls** — common gotchas and mistakes.
10. **Recap** — bullet takeaways designed for active recall.

- **Reference & glossary stay inside the lesson.** Cheat-sheet material and term
  definitions are embedded in the relevant `lesson.html` / `overview.html` (a
  reference block and a glossary section) — no separate reference or glossary
  files.
- **Cite credible sources.** Claims link inline to the high-trust sources they
  were fetched from (Section 6, item 8) — never teach from memory alone.

Depth rule (from Section 6): the `overview.html` stays high-level; the deep
detail belongs in the **core** subtopic lessons.

### 6.3 Code dissection (language / framework / tool topics)

When the topic is a language, framework, library, or tool, the **In practice**
section must include a **code dissection**, not a bare code dump:

- Show **idiomatic, real-world code**, verified against credible sources.
- **Dissect it chunk-by-chunk** — annotate each meaningful line/block with
  *what* it does and *why* it's written that way, flagging **idioms**,
  **gotchas**, and **alternatives** (feeds the `trade-offs` area, §9.1).
- **Suggest code to try** — offer a small variation or a "predict the output"
  challenge, tied to **Practice mode** (§9.6).
- Where useful, **run the code** to show real output and verify behavior —
  never rely on remembered behavior (§6 item 8).

---

## 7. Learning Techniques (Always Applied)

- **Feynman technique** — teach it as if to a 12-year-old; anticipate the
  naive "why do we even need this?" questions and answer them.
- **Active recall.**
- **Spaced repetition.**

**Goal:** not just to pass interviews, but to understand deeply enough to
solve novel problems and build further knowledge on top. Clearing interviews
is a *byproduct* of real understanding.

---

## 8. Notes Workflow

- After learning a topic, **I** write the note. The LLM never writes notes.
- Each note has a **summary at the top**, followed by full detail.
- After each lesson, the LLM should **gently suggest I keep writing my own
  notes** — a nudge, never nagging, and never writing them for me.
- The LLM **verifies my note only when I ask** (Section 8.1) — it does **not**
  auto-check after every lesson, and it never edits the note; I make all
  changes myself.

### 8.1 Verification

- **When:** on demand, right after I write or update a note (I ask for it).
- **Where the feedback goes:** **in chat only.** The LLM writes nothing to disk
  for a note review — `notes/` is read-only and no review files are created.
- **What it checks:**
  - **Factual accuracy** — errors and misconceptions.
  - **Completeness** — key points from the lesson that are missing.
  - **Clarity** — whether it's explained well enough to teach a 12-year-old.
  - **Missing diagram or example.**
- The LLM reports issues and suggestions; **I** decide what to change and edit
  the note myself.

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
**fail**. `result` drives the doubling schedule in Section 9.4; `score` and
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

- **Q&A** (default) — one question at a time, scored per Section 9.1.
- **Type mode** — for complex/multi-step topics, I type a full explanation from
  memory and the LLM grades the **gaps** (what I nailed, what was vague, what I
  missed entirely). Stronger encoding than recognition.
- **Practice mode** — for practical topics, the LLM has me **write code,
  predict output, or spot the bug** in real code — exercising the `practical`
  interview area (Section 9.1), not just theory recall.

---

## 10. Session Flow

Every working session follows this order:

1. **Surface what's due.** On session start, the LLM scans every
   `questions.json`, finds questions whose `nextAsk <= today` (per Section 9.4),
   and shows me the **due revision list** first.
2. **Revision first.** All due revisions are cleared **before** any new
   learning begins or continues. Revision always takes priority.
3. **Then learn / continue.** Once nothing is due, the LLM works on **new
   material**, and it must **finish the in-progress topic first** — it stays on
   the current main topic (and its subtopics, in order) until that main topic is
   **cleared** (Section 9.2.1) before starting a new one.
4. **Update progress.** After each lesson or interview, the LLM updates the
   progress file (Section 10.1) and the relevant `questions.json`.

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

---

## 11. Format

- **Lessons and overviews are HTML** (Section 6.2), beautiful and
  print-friendly. Other docs are plain **Markdown**; trackers are **JSON**
  (Sections 9.3 and 10.1).
- Standard **Git** file structure; the repo will be pushed to GitHub later.
- More tooling/format will be added when needed.