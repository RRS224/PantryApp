# AGENTS.md — PantryCheck (PantryApp)

## Role
You are a disciplined senior Android engineer working in this repository.

Your job is to build the Android app described in:
- SPEC.md (source of truth)
- build_prompt.md (staged build plan + guardrails)
- tasks.md (task checklist)

Follow these documents exactly.

Agents must read CURRENT_STAGE.md to determine which stage is active before performing any work.

Codex reads AGENTS.md to understand repo conventions and working agreements.

---

## Stage-Lock Rules (Agent Guardrails)

1) Implement ONLY the explicitly requested stage; if any task implies Stage 3+ (UI/Compose/ViewModels/navigation), STOP and leave TODOs instead.  
2) Allowed changes are restricted to the stage’s target folders/packages; do not modify UI files unless needed to fix compilation caused by Stage 2 wiring.  
3) Do not add new dependencies, libraries, or architecture patterns (e.g., Hilt) unless SPEC.md or tasks.md explicitly demands it.  
4) Before coding, list the exact files you will create/modify; if that list includes UI/screens, treat it as drift and revise.  
5) If requirements are unclear, do not guess: add a short “Question / Assumption” note in PROJECT_STATE.md and implement only the unambiguous parts.
6) Do not introduce new architectural frameworks (DI frameworks, networking stacks, paging libraries, etc.) unless SPEC.md explicitly requires them.


## Working agreements (non-negotiable)
1) Implement ONLY V1 scope. Do not implement items marked “future hooks” or “not in V1”.
2) Work strictly stage-by-stage as defined in build_prompt.md:
   - Complete Stage N
   - Stop
   - Wait for explicit confirmation before Stage N+1
3) No architectural rewrites mid-stream. Prefer the simplest approach that satisfies SPEC.md.
4) No new dependencies unless essential. If you add any dependency, explain why and list alternatives.
5) Provide full files with paths. No partial snippets for core files.
6) Do not change filenames or folder layout unless required by Android Studio conventions.
7) Always keep the app offline-first for V1.

---

## Project conventions
### Package name
Default package: `com.pantrycheck.app`
If a different package is required, ask before changing.

### Stack (must use)
- Kotlin
- Jetpack Compose (Material3)
- MVVM
- Room (SQLite, Flow-based queries)
- Coroutines + Flow
- Android SpeechRecognizer for voice input

### Minimum Android version
API 26

---

## Repo structure expectation
Repository root contains:
- SPEC.md
- build_prompt.md
- tasks.md
- AGENTS.md
- (generated Android Studio project folder) PantryCheck/

Database schema is defined in DB_SCHEMA_ROOM.md.
Agents must follow this schema when implementing Room entities.

Place all Android code inside PantryCheck/.

---

## Build & run expectations
- The project must open in Android Studio and Gradle sync without errors.
- Do not introduce custom build steps.
- Prefer stable, commonly used AndroidX libraries only.

---

## Stage completion deliverables
At the end of each stage:
1) Provide a brief folder tree summary
2) List files added/changed (paths only)
3) State how to run/verify that stage
4) Update tasks.md checkboxes for items completed in that stage

Stop and wait.

---

## Testing & verification (lightweight for V1)
- Ensure the app launches without crashing.
- Validate navigation routes compile.
- Ensure database operations are not on the main thread.
- Ensure microphone permission flow exists before invoking SpeechRecognizer.

---

## Data rules (do not violate)
- Respect household_id rules from SPEC.md.
- Log inventory changes to usage_history per SPEC.md.
- Quantity must never go negative.
- Avoid silent failures; show snackbars for user-facing actions.

---

## Important: ingredient matching intelligence
Implement alias normalization + substitutes + staples exactly as described in SPEC.md.
Do not invent additional matching rules beyond the spec.

---

## If anything is ambiguous
- Ask a single targeted question OR choose the simplest reasonable default and clearly state the assumption.