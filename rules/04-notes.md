# 4. Notes Workflow

> Part of the Knowledge Base 2 rules. See [rules.md](rules.md) for the index and precedence.

---

## 8. Notes Workflow

- After learning a topic, **I** write the note. The LLM never writes notes.
- Each note has a **summary at the top**, followed by full detail.
- After each lesson, the LLM should **gently suggest I keep writing my own
  notes** — a nudge, never nagging, and never writing them for me.
- The LLM **verifies my note only when I ask** (§8.1) — it does **not**
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
