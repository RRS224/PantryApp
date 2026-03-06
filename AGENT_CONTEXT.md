# AGENT_CONTEXT.md — PantryCheck Shared Context

This file contains stable project context that rarely changes.

Agents should treat this as persistent background knowledge.

Do not repeat these details in prompts unless required.

---

## Project

PantryCheck

Android application for household pantry inventory tracking.

Design goals:
- extremely fast interaction
- large touch targets
- minimal friction during cooking
- offline-first operation

---

## Technology stack

Language: Kotlin

UI: Jetpack Compose (Material3)

Architecture:
MVVM

Persistence:
Room (SQLite)

Async:
Coroutines + Flow

Minimum Android version:
API 26

---

## Architecture principles

Domain layer
- business models
- repository interfaces

Data layer
- Room entities
- DAO interfaces
- database
- repository implementations

UI layer
- Compose screens
- ViewModels

Rules:
- UI must never access DAOs or Room entities directly
- UI talks only to ViewModels
- ViewModels call repository interfaces

---

## Data rules

- Inventory quantities must never become negative.
- Every inventory change must create a usage_history entry.
- Pantry items belong to locations.
- Locations belong to households.

---

## UI design philosophy

The app is intended for **fast one-handed kitchen use**.

Priorities:
- large buttons
- minimal typing
- clear visual state
- fast updates

UI should prefer **simple, fast interaction** over visual complexity.

---

## Development workflow

Stages are controlled by:

CURRENT_STAGE.md

Task checklists are in:

tasks.md

Agents must never implement work beyond the current stage.

---

## Agent workflow

Supervisor
→ coordinates work

Builder
→ writes code

Reviewer
→ audits code

User
→ verifies in Android Studio