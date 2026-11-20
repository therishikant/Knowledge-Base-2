# Knowledge Base 2

A personal, AI-assisted learning system for mastering software engineering topics from first principles, writing personal notes, and proving understanding through spaced-repetition interviews.

---

## Core Philosophy

- **First Principles Over Memorization:** Learn *why* a concept exists and what problem it solves before diving into syntax or mechanics.
- **Active Recall:** Personal notes in `notes/` are strictly human-written (read-only for the AI) to enforce genuine understanding.
- **Structured Verification:** Understanding is tested through multi-area interviews and tracked with spaced repetition.

---

## Repository Structure

```text
Knowledge-Base-2/
├── plan/                 # Topics roadmap and learning levels (topics.md)
├── learn/                # AI-driven lessons, interviews, and progress tracking
│   ├── learning/         # Structured HTML/Markdown lessons per topic
│   ├── interviewing/     # Interview sessions and revision logs
│   ├── questions/        # Question banks and spaced-repetition tracking
│   └── progress.json     # Session state and overall progress tracker
├── notes/                # Personal notes (strictly human-written; read-only for AI)
└── rules/                # Binding rules governing how the AI teaches and interviews
    ├── rules.md          # Master index and rule precedence
    ├── 01-areas.md       # Area definitions and permission rules
    ├── 02-planning.md    # Topic planning and roadmap structure
    ├── 03-teaching.md    # First-principles teaching methodology
    ├── 04-notes.md       # Note verification workflow
    ├── 05-interview.md   # Interview mode, scoring, and spaced repetition
    ├── 06-session.md     # Session flow and progress tracking
    ├── 07-format.md      # File formatting standards
    └── topic-styles/     # Specialized rules per topic type (language, framework, DSA, HLD, LLD)
```

---

## Permissions & Area Rules

| Area | Path | AI Permission | Purpose |
| :--- | :--- | :--- | :--- |
| **Plan** | `plan/` | Read / Write | Track topics to learn and current proficiency levels |
| **Learn** | `learn/` | Read / Write | Generate lessons, run interviews, and track progress |
| **Notes** | `notes/` | **Read-Only** | Personal notes written in my own words (AI can review and verify, never edit) |
| **Rules** | `rules/` | Reference | System rules and teaching/interview guidelines |

---

## Workflow

1. **Plan:** Define or update topics and current levels (`none`, `beginner`, `intermediate`, `interview`) in [`plan/topics.md`](plan/topics.md).
2. **Learn:** Study interactive lessons generated in `learn/learning/<topic>/` following the rules in [`rules/03-teaching.md`](rules/03-teaching.md).
3. **Note:** Summarize concepts in `notes/` in your own words and have the AI review them for accuracy and gaps.
4. **Interview & Revise:** Run quiz/interview sessions tracked in `learn/interviewing/` and `learn/progress.json` to reinforce retention.
