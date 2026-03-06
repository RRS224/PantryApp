# ORCHESTRATOR_AGENT.md
PantryCheck Development Orchestrator

This document defines the orchestration workflow used when AI agents work on this repository.

The orchestrator internally simulates three roles:

1. Supervisor
2. Builder
3. Reviewer

The user interacts only with the orchestrator.

---

# Core Rule

Every task must execute in four visible phases:

1. SUPERVISOR PHASE  
2. BUILDER PHASE  
3. REVIEWER PHASE  
4. SUPERVISOR FINAL GATE  

Each phase must appear in the response output.

Phases must never be skipped.

---

# Phase Responsibilities

## Supervisor Phase
Define:

- scope
- stage restrictions
- files likely affected
- success criteria

Supervisor must always read and obey:

CURRENT_STAGE.md

Supervisor blocks cross-stage implementation.

---

## Builder Phase

Builder performs implementation work.

Builder must obey:

- SPEC.md
- tasks.md
- AGENTS.md
- ARCHITECTURE.md
- UI_ARCHITECTURE.md
- PROJECT_STATE.md
- DB_SCHEMA_ROOM.md
- AGENT_CONTEXT.md

Builder must return a report in the format:

A) Summary  
B) Folder tree (changed/new only)  
C) Files changed (paths only)  
D) How to verify  
E) tasks.md checkbox updates  
F) Assumptions / Questions  

---

## Reviewer Phase

Reviewer validates:

- stage discipline
- architecture compliance
- UI architecture compliance
- data integrity
- compile/runtime risks
- scope compliance

Reviewer returns one verdict:

APPROVED  
APPROVED WITH MINOR CHANGES  
REJECTED  

Reviewer lists only critical issues.

---

## Supervisor Final Gate

If verdict is:

APPROVED  
APPROVED WITH MINOR CHANGES

Return Android Studio verification checklist.

If verdict is:

REJECTED

Run one narrow Builder fix pass followed by one Reviewer re-check.

Do not run more than one automatic fix cycle.

---

# Android Studio Failure Protocol

If the user provides a build or runtime error:

1. Identify the first meaningful error.
2. Run a narrow Builder fix.
3. Run Reviewer validation.
4. Return a verification checklist.

Avoid requesting full Gradle logs unless necessary.

---

# Architecture Rules

PantryCheck uses:

MVVM architecture

UI layer:
Compose screens and ViewModels only.

UI must never call:

Room
DAOs
database entities

ViewModels must access data only through repository interfaces.

---

# UI Design Rule

PantryCheck prioritizes:

Speed of action over data description.

Prefer direct manipulation lists and large touch targets.

Avoid unnecessary detail screens.

---

# Commit Rule

Commit or push must never be authorized until:

1. Android Studio build succeeds
2. App launches
3. Verification checklist passes
4. User confirms success

---

# Token Discipline

Keep responses compact.

Do not repeat repository documents.

Focus only on changes.

---

# Interaction Model

The user may issue commands such as:

Start Stage X  
Continue Stage X  
Fix Android Studio error  
Refine UI  
Proceed to next stage  

The orchestrator handles all internal phases automatically.

The user must never relay prompts between agents.