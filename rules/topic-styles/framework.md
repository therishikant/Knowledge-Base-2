# Topic style: Framework

> **Per-topic-type rules.** These **add to or override** the general teaching
> ([../03-teaching.md](../03-teaching.md)) and interview
> ([../05-interview.md](../05-interview.md)) rules for topics of this type.
> General rules still apply unless explicitly overridden here.
> See [../rules.md](../rules.md) for precedence.

## Applies to

- Frameworks and libraries built on top of a language.
- Examples: `spring boot`, `react`, `angular`, `express`, `django`.

## Guiding principle: demystify the magic

A language teaches me *how to express logic*; a framework teaches me *how to work
within someone else's opinions and machinery*. The whole game is understanding
**what the framework does for me under the hood** — so I never cargo-cult
annotations/decorators.

- **Contrast anchor = the base language without the framework.** For every
  feature, answer: *"what would I have to write myself if this framework didn't
  exist?"* (Spring vs hand-rolled servlets + manual wiring; React vs raw DOM.)
- **Primary anchor is Spring Boot** (matches my Java), but stay
  framework-agnostic: use **mixed examples** (React, Express, etc.) where they
  illustrate a concept better.
- Skim the base language; **go deep on the framework's abstractions and magic**.

## What to go deep on (the differentiators)

1. **Problem & philosophy** — what pain it removes; what I'd write by hand without it.
2. **Core abstractions & mental model** — the handful of concepts everything
   builds on (beans / components / middleware / routes).
3. **The magic, demystified** — what actually happens behind an
   annotation/decorator (DI container, annotation processing, virtual
   DOM/reactivity, proxying). *The deep-learning goal and #1 interview differentiator.*
4. **Lifecycle & control flow** — bootstrap, request/component lifecycle; who calls
   my code and when (inversion of control).
5. **Conventions & configuration** — convention-over-configuration, project
   structure, "the framework way."
6. **Composition & extension points** — hooks, middleware, providers; how to plug
   in without fighting the framework.
7. **State / data flow** — how data moves through the app.
8. **Ecosystem & tooling** — CLI, common companion libraries, testing.

## Lesson style (adds to / overrides §6)

- **"Strip the magic" is mandatory** — for any abstraction, show what it does
  under the hood, ideally by contrasting with the hand-written base-language
  equivalent (via code dissection §6.3).
- **Learn by building** — each framework topic builds toward **one small running
  app** that grows as subtopics are added (grounds it in the mission, §6.1).
- **Lifecycle diagrams** — "who calls what, when" is diagrammed (this is how the
  mandatory §6 diagram earns its keep for frameworks).
- **Convention callout** — name the idiomatic/expected structure and warn against
  fighting the framework.

### Subtopic ordering (default; reorder per §5 as needed)

1. Problem & philosophy (what it removes; "without it you'd write X")
2. Setup, project structure & bootstrap
3. Core abstractions & mental model
4. The magic under the hood (how those abstractions actually work)
5. Lifecycle & control flow
6. Composition & extension points
7. State / data flow
8. Configuration & conventions
9. Common patterns / idioms
10. Testing
11. Ecosystem & real-world usage

The `overview.html` maps these, flags each as **deep** (framework-specific) or
**skim** (base language), and describes the **small app** that will grow across
the subtopics.

## Learning techniques (framework-specific)

- **Hand-roll then adopt** — write the naive base-language version first, feel the
  pain, then see the framework remove it.
- **Trace the lifecycle** — follow one request/render end-to-end through the
  framework's machinery.
- **Read framework code / generated output** — dissect what an annotation or
  decorator expands into.
- **Break-and-fix** — misconfigure on purpose (wrong bean scope, missing
  provider) to learn the failure modes.

## Interview style (adds to / overrides §9)

Weight the interview areas (§9.1) like this:

- **theory** — mental model, lifecycle, "what does this annotation/decorator
  actually do under the hood?"
- **practical** — wire/build a feature, read framework code, **debug a
  config/lifecycle issue** (heavy Practice mode, §9.6).
- **trade-offs** — this framework vs alternatives vs plain language; convention
  and abstraction trade-offs.
- **diagram** — lifecycle, architecture, data flow.

### Clearing bar (overrides §9.2 for frameworks)

A framework topic is **cleared** only when I can:

- **Build a small real feature idiomatically** — the framework's way, not fighting it.
- **Explain the magic under the hood** — no black boxes; I know what the
  abstractions actually do.
- Trace the **lifecycle / control flow** and name the key **extension points**.
- Reason about **when to use this framework** and its convention/abstraction trade-offs.

"Demystify the magic + build idiomatically" is the defining bar — memorized
annotations never clear.

## Notes

- Keep the growing "small app" under the topic's `learning/` folder so lessons and
  the running example stay together.

