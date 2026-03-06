# build_prompt.md — PantryCheck (Staged AI-Assisted Development)

This repository is built in strict stages. Each run must implement ONE stage only.

Primary sources of truth:
0) AGENT_CONTEXT.md (stable project context)
1) CURRENT_STAGE.md
2) SPEC.md
3) tasks.md
4) PROJECT_STATE.md
5) DB_SCHEMA_ROOM.md
6) ARCHITECTURE.md
7) UI_ARCHITECTURE.md
8) AGENTS.md

Workflow coordination:
If a Supervisor agent is present, Builder must follow instructions from SUPERVISOR_AGENT.md as long as they do not conflict with SPEC.md, CURRENT_STAGE.md, tasks.md, or AGENTS.md.

If any documents conflict:
- SPEC.md has highest priority
- CURRENT_STAGE.md defines the active stage
- tasks.md defines the work within that stage
- AGENTS.md defines behavior rules

----------------------------------------------------------------------
## Non-Negotiable Stage Lock

You MUST implement ONLY the single stage explicitly requested by the user.
Always confirm the current stage by reading CURRENT_STAGE.md.

If the requested stage is Stage N:
- Do not implement Stage N+1 or later.
- Do not add UI/screens/navigation for later stages.
- Do not add “nice to have” architecture or dependencies.

If you notice work that belongs to a later stage:
- leave a TODO comment in the most relevant file
- mention it in the final stage report
- STOP

----------------------------------------------------------------------
## Allowed Scope by Stage

Stage 1 — Android scaffold
- project setup, build stabilization, minimal app launch

Stage 2 — Data layer (Room foundation)
- Room schema (entities/DAOs/database/migrations)
- repository interface + Room implementation
- seed logic
- DI wiring (simple provider only; no Hilt unless SPEC explicitly requires)
- NO UI, no new screens

Stage 3+ — UI and features
- do not touch during Stage 2 unless required for compilation

----------------------------------------------------------------------
## Standard Execution Protocol (for every stage)

When asked to implement a stage, do this sequence:

### Step 0 — Confirm Stage + Inputs
State:
- which stage you are implementing
- which files you are using as sources of truth
- what is out of scope

### Step 1 — Plan (before writing code)
Provide:
- a short checklist of tasks you will complete for this stage
- an explicit list of files you will create/modify (with paths)

If your file list includes UI/screens during Stage 2:
- revise the plan to remove them.

### Step 2 — Implement
Create/modify only what is required for the stage.
Follow AGENTS.md rules.

### Step 3 — Verify
At minimum:
- Gradle sync succeeds
- project compiles
- app launches (if applicable to stage)
- Stage 2: seed runs without crash and tables are accessible

### Step 4 — Stage Report (mandatory)
Output in this exact format:

A) Summary (3–6 bullets)
B) Folder tree (only the new/changed parts)
C) Files changed (paths only)
D) How to verify (exact steps/commands)
E) tasks.md checkbox updates you made
F) Assumptions / Questions (if any)

Then STOP and wait for explicit approval.

----------------------------------------------------------------------
## Stage 2 Specific Requirements (Room)

Stage 2 must implement the database exactly as defined in:
- tasks.md Stage 2 checklist
- DB_SCHEMA_ROOM.md for canonical schema

Architecture constraints (must follow):
- Domain models have NO Room annotations
- Room entities are separate from domain models
- Repository is an interface in domain layer
- Room repository implementation lives in data layer
- Mappers convert Entity ↔ Domain
- DAO queries should return Flow where appropriate

Seeding requirements:
- Seed household “Home”
- Seed locations: Pantry / Fridge / Freezer / Other
- Seed system Meal Suggestions dataset (V1 can start small; structure supports 80–150)

Migration requirements:
- Create a clear strategy (start at version 1)
- Prefer additive migrations; avoid destructive changes

Out of scope for Stage 2:
- new UI screens
- Compose changes (unless required to fix compilation)
- feature logic beyond persistence plumbing

----------------------------------------------------------------------
## Drift Detection (self-check)

Before finalizing your work, ask yourself:
- Did I touch any UI file unnecessarily?
- Did I add any dependency not explicitly required?
- Did I implement anything beyond Stage N?

If yes:
- revert those changes
- keep only Stage N deliverables

----------------------------------------------------------------------
## Final Rule

After completing the stage report:
STOP. Do not continue to the next stage until the user explicitly asks.