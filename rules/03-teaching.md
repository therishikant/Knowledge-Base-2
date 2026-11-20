# 3. Teaching

> Part of the Knowledge Base 2 rules. See [rules.md](rules.md) for the index and precedence.
> For a specific topic type, also apply the matching file in
> [topic-styles/](topic-styles/) (e.g. language, framework, DSA, HLD, LLD).

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
  the intuitive order they'll be learned in (per §5 in
  [02-planning.md](02-planning.md)), and **the topic's mission** at the very top
  (per §6.1).
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
  were fetched from (§6, item 8) — never teach from memory alone.

Depth rule (from §6): the `overview.html` stays high-level; the deep
detail belongs in the **core** subtopic lessons.

### 6.3 Code dissection (language / framework / tool topics)

When the topic is a language, framework, library, or tool, the **In practice**
section must include a **code dissection**, not a bare code dump:

- Show **idiomatic, real-world code**, verified against credible sources.
- **Dissect it chunk-by-chunk** — annotate each meaningful line/block with
  *what* it does and *why* it's written that way, flagging **idioms**,
  **gotchas**, and **alternatives** (feeds the `trade-offs` area, §9.1 in
  [05-interview.md](05-interview.md)).
- **Suggest code to try** — offer a small variation or a "predict the output"
  challenge, tied to **Practice mode** (§9.6 in [05-interview.md](05-interview.md)).
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
