# Topic style: Language

> **Per-topic-type rules.** These **add to or override** the general teaching
> ([../03-teaching.md](../03-teaching.md)) and interview
> ([../05-interview.md](../05-interview.md)) rules for topics of this type.
> General rules still apply unless explicitly overridden here.
> See [../rules.md](../rules.md) for precedence.

## Applies to

- Programming languages and their core/standard features.
- **Language versions/feature sets count as their own entry** — e.g. `java-8`
  (streams, lambdas, `Optional`) is a separate topic from `java`.
- Examples: `java`, `java-8`, `python`, `javascript`, `typescript`, `go`.

## Guiding principle: contrast-driven learning

I already program, so a new language is **not** re-learning loops and functions.
It's learning **what this language does differently and why**.

- **Java is the contrast anchor.** Teach every concept against Java: *what's the
  same, what's different, and why did the designers choose differently?*
- Skim what's transferable (control flow, basic functions); **go deep only on the
  differentiators** (below).
- Never teach "translated Java." The goal is **idiomatic** code in the target
  language.

## What to go deep on (the differentiators)

These are where lessons spend their depth, not on shared basics:

1. **Philosophy & execution model** — why the language exists, what it optimizes
   for, compiled vs interpreted, its runtime/VM, how code actually runs.
2. **Type system** — static/dynamic, strong/weak, inference, generics, null handling.
3. **Memory model** — GC vs manual vs ownership; value vs reference semantics.
4. **Paradigm leanings** — OOP / functional / procedural, and what's idiomatic.
5. **Error handling** — exceptions vs result types vs error values.
6. **Concurrency model** — threads / async-await / goroutines / actors.
7. **Idioms — "the <language> way"** — first-class; the thing beginners get wrong.
8. **Tooling & ecosystem** — package manager, build, test, formatter, linter.

## Lesson style (adds to / overrides §6)

- **Code dissection is mandatory** (§6.3) — dissect *idiomatic, real* code, not
  toy translations.
- **Bridge table** — every subtopic lesson includes a small **Java → this
  language** mapping for the concept being taught (place it near "Where it fits"
  or in the reference block).
- **Run everything** — snippets are executed to show real output; never rely on
  remembered behavior.
- **Idiom callout** — each lesson explicitly names the idiomatic way and contrasts
  it with the non-idiomatic ("translated") way.

### Subtopic ordering (default; reorder per §5 as needed)

1. Philosophy & execution model (the mental model)
2. Types, variables & data
3. Control flow *(brief — mostly transferable)*
4. Functions & the language's function features
5. Collections / core data structures
6. Paradigm features (OOP/FP — whatever is core)
7. Error handling
8. Memory & value/reference semantics
9. Concurrency
10. Modules/packages & tooling
11. Idioms & standard library
12. Ecosystem & real-world usage

The `overview.html` maps these and flags each as **deep** (a differentiator) or
**skim** (transferable from Java).

## Learning techniques (language-specific)

- **Read before write** — dissect idiomatic code first; reading idioms is how you
  absorb them.
- **Predict-the-output drills** — target the language's signature gotchas
  (Java autoboxing/`equals` vs `==`, Python mutable defaults, JS `this`/coercion).
- **Idiom-refactor drill** — take non-idiomatic ("translated") code and rewrite it
  the idiomatic way.
- **Rosetta exercise** — implement one small program end-to-end to *feel* the idioms.

## Interview style (adds to / overrides §9)

Weight the interview areas (§9.1) like this:

- **theory** — type system, memory model, runtime/execution, "why does this
  language do X instead of Java's Y?"
- **practical** — write **idiomatic** code; **trace / predict output**; spot the
  bug (gotchas). Heavy use of **Practice mode** (§9.6).
- **trade-offs** — when to reach for this language; feature trade-offs
  (e.g. composition vs inheritance, checked vs unchecked errors).
- **diagram** — memory/execution model, concurrency model where visual.

### Clearing bar (overrides §9.2 for languages)

A language topic is **cleared** only when I can:

- Write **idiomatic** code from scratch — not Java translated into the language.
- Explain its **execution and memory model** clearly.
- Know and avoid the language's **signature gotchas** (predict tricky output).
- Reason about **when to use this language** and its key feature trade-offs.

Idiomatic code + gotcha mastery is the defining bar — theory alone never clears.

## Notes

- For a version/feature entry (e.g. `java-8`), the "contrast anchor" is the
  **previous version** (plain `java`): what the new features replace and why.
