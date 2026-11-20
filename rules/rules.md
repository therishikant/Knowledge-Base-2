# Knowledge Base 2 — Repository Rules

These rules govern how the LLM and I work together in this repository.
They are **binding**: the LLM must follow them at all times. Read this index
first, then the relevant area file(s).

---

## Purpose

This repo is a personal learning system. I use it to **learn new concepts**,
**revise old ones**, and **prove my understanding** through interviews.
The guiding principle: I learn best when I understand a topic *as a whole*,
from first principles, connected to real problems — not memorized in fragments.

---

## How the rules are organized

The rules are split by area. Each file keeps its own section numbers (e.g.
"§9.4" always lives in `05-interview.md`).

| File | Covers |
| ---- | ------ |
| [01-areas.md](01-areas.md) | The three areas, permissions, `learn/` folder structure |
| [02-planning.md](02-planning.md) | Choosing/organizing topics, `plan/topics.md` format |
| [03-teaching.md](03-teaching.md) | Teaching approach, lesson format, code dissection, learning techniques |
| [04-notes.md](04-notes.md) | Notes workflow and verification |
| [05-interview.md](05-interview.md) | Interview/revision mode, scoring, question JSON, spaced repetition |
| [06-session.md](06-session.md) | Session flow and `progress.json` |
| [07-format.md](07-format.md) | File formats and tooling |
| [topic-styles/](topic-styles/) | Per-topic-type lesson & interview styles (language, framework, DSA, HLD, LLD) |

---

## Precedence

1. The general rules (files `01`–`07`) apply to **everything**.
2. A `topic-styles/<type>.md` file **adds to or overrides** the general
   teaching (`03`) and interview (`05`) rules **for that topic type only**.
   Example: `topic-styles/language.md` mandates code dissection;
   `topic-styles/dsa.md` mandates complexity analysis.
3. When teaching or interviewing, the LLM must **infer the topic type** (from
   the topic itself / `plan/topics.md`) and load the matching topic-style file
   in addition to the general rules. If no topic-style file matches, the general
   rules alone apply.
4. To add a new topic type, copy [topic-styles/_template.md](topic-styles/_template.md).