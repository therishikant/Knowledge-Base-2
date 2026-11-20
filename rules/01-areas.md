# 1. Areas & Permissions

> Part of the Knowledge Base 2 rules. See [rules.md](rules.md) for the index and precedence.

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
- The `mistakes/` area mirrors this same structure (see §9.5 in
  [05-interview.md](05-interview.md)).
