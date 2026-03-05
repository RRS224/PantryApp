# PantryCheck (Repo: PantryApp)

PantryCheck is an offline-first Android pantry inventory and meal suggestion app built with Kotlin, Jetpack Compose (Material3), Room, and Coroutines/Flow.

This repository is developed using an AI-assisted, staged workflow.

## Source of Truth
Read these files before making any changes:
- `SPEC.md` — complete product specification (V1 + future hooks)
- `build_prompt.md` — staged build flow + guardrails
- `tasks.md` — task tracker for V1
- `AGENTS.md` — repo conventions and non-negotiable working rules

## V1 Scope (What we are building now)
- Pantry inventory (COUNT / LEVEL / FRESH)
- Fast stock updates + Enter Current Stock + Undo + history logging
- Recount Pantry workflow
- Meal Suggestions (not recipes) + guest mode + kids + desserts
- Ingredient intelligence: aliases + substitutes + staples (as per SPEC.md)
- User-added meal ideas (tight V1) + Community outbox (PENDING only)
- Voice input for fields + command-style voice actions
- Settings + JSON export/import backup
- Multi-household ready data model (local only in V1)

## Non-Goals (Not in V1)
- Barcode scanning / external product databases
- Payments / in-app ordering
- Community backend, stats, gamification, sync
- Cloud login / real multi-device sync
- Full recipe steps / nutrition

## Tech Stack
- Kotlin
- Jetpack Compose (Material3)
- MVVM + Repository
- Room (SQLite)
- Coroutines + Flow
- Android SpeechRecognizer (voice)

Min SDK: API 26

## Repo Layout
- Repo root: specs and AI workflow documents
- Android Studio project will live in: `PantryCheck/`

## How to Build / Run (once code exists)
- Open `PantryCheck/` in Android Studio
- Sync Gradle
- Run on emulator or device
- Grant microphone permission for voice features

## AI Workflow Rules
- Work stage-by-stage (Stage 1 → stop → wait for approval)
- No future hooks implemented during V1
- Prefer the simplest implementation that satisfies SPEC.md
- Full files with paths; avoid partial snippets for core files