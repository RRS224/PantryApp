# PantryCheck — Task Tracker (V1)
Version: 1.0
Status: Active


## Stage Completion Rule

A stage is considered complete only when all of the following are true:

1. All checklist items for the stage are completed.
2. Builder stage report has been produced.
3. Reviewer verdict is either:
   - APPROVED
   - APPROVED WITH MINOR CHANGES
4. Local Android Studio verification has succeeded.
5. CURRENT_STAGE.md has been updated.

Only after these conditions are met may the stage be committed and pushed.

## How to use
- The developer/AI must update this file as work progresses.
- Mark each item as: [ ] Not started, [~] In progress, [x] Done
- If something is out of V1 scope, mark it under “Future Hooks” and do NOT implement.

---

## Stage 0 — Preflight
- [ ] Read PantryApp/PantryApp_Specification.md end-to-end
- [ ] Confirm V1 scope boundaries (no future hooks implemented)
- [ ] Confirm tech stack: Kotlin + Compose Material3 + MVVM + Room + Coroutines/Flow + SpeechRecognizer
- [ ] Decide package name (default): com.pantrycheck.app

---

## Stage 1 — Android Studio Project Setup
- [x] Create Android Studio project (Compose + Material3)
- [x] Configure Gradle (Kotlin, Room, Coroutines, Navigation, Serialization/JSON)
- [x] App theme (light + dark)
- [x] Navigation scaffold (NavHost + routes)
- [x] Define base module/package structure (data, repository, ui, viewmodel, voice, utils)
- [x] Add required permissions placeholders (microphone) + runtime permission flow plan

Deliverable:
- [ ] Project opens in Android Studio and builds successfully

---

## Stage 2 — Database Layer (Room)

Goal
Implement the Room database foundation for PantryCheck while enforcing clean architecture boundaries so that Room types never leak outside the data layer.

Architecture Rules

Room must be isolated inside the data layer.
No @Entity, @Dao, RoomDatabase, or other Room types may appear in domain or UI layers.

Domain models must be plain Kotlin classes with no Room annotations.

Repository must be defined as an interface in the domain layer.

The Room implementation must live in the data layer and implement the repository interface.

All conversions between Room entities and domain models must use mapper functions.

DAO query results should return Flow where appropriate.

Required Package Structure (inside PantryCheck module)

domain/model

PantryItem (pure Kotlin model)

domain/repository

PantryRepository (interface)

data/local/room

PantryItemEntity (@Entity)

PantryDao (@Dao)

PantryDatabase (RoomDatabase)

data/mapper

PantryItemMapper (Entity ↔ Domain conversion)

data/repository

RoomPantryRepository : PantryRepository

di

Simple provider that exposes PantryRepository backed by RoomPantryRepository
(Hilt is not required unless already specified in the project.)

Repository Interface Contract (minimum)

observeAllItems(): Flow<List>

upsertItem(item: PantryItem)

deleteItem(item: PantryItem)

Optional methods if required by SPEC.md

observeExpiringSoon(thresholdDays: Int): Flow<List>

observeLowStock(): Flow<List>

Definition of Done

• Room database builds successfully
• Domain models contain no Room annotations
• Repository interface exists and Room implementation conforms to it
• Mapper functions convert between Entity and Domain
• Project compiles and runs

Out of Scope for Stage 2

• No Compose UI changes
• No business logic beyond persistence plumbing
• No inventory screens yet

### Entities (must match spec)
- [x] households
- [x] locations
- [x] pantry_items
- [x] usage_history
- [x] meal_ideas
- [x] meal_ingredients
- [x] community_outbox
- [x] shopping_list_items (if included in V1)

### DAOs + Database
- [x] DAO: HouseholdsDao
- [x] DAO: LocationsDao
- [x] DAO: PantryItemsDao
- [x] DAO: UsageHistoryDao
- [x] DAO: MealIdeasDao
- [x] DAO: MealIngredientsDao
- [x] DAO: CommunityOutboxDao
- [x] DAO: ShoppingListDao (if included)

- [x] PantryDatabase.kt created and migrations strategy defined (versioned)
- [x] Repository layer implemented (single repo or split repos)

### Seed data
- [x] Seed default household �Home�
- [x] Seed default locations per household: Pantry / Fridge / Freezer / Other
- [x] Seed system Meal Suggestions dataset (V1 can start small, structure must support 80�150)

Deliverable:
- [ ] App launches and DB seeds correctly on first run

---

## Stage 3 — Inventory Core (UI + Logic)
### Home Screen
- [x] Search bar + live filtering (under 200ms typical)
- [x] Tabs/filters: All / Pantry / Fridge / Freezer / Use Soon (as per spec)
- [x] Item list with inline quick actions:
  - [x] COUNT: [-] [+]
  - [x] LEVEL: “Level” button
  - [x] FRESH: “Use” + “Discard” (or on detail if not on list)
- [x] Add Item button
- [x] Entry card/button to Meal Suggestions

### Add/Edit Item
- [x] Add Item screen with:
  - [x] Item name (voice-ready)
  - [x] Variant (optional)
  - [x] Location (including custom locations)
  - [x] Tracking mode: COUNT / LEVEL / FRESH (default rules)
  - [x] COUNT fields: quantity + unit + optional size-per-unit
  - [x] LEVEL fields: level selector (0–5 labels)
  - [x] FRESH fields: added date + use-soon days + optional quantity
- [x] Edit flow reuses screen and updates updated_at
- [x] Naming normalization (base name + variant separation rules)

### Item Detail Screen
- [x] COUNT:
  - [x] [+1] [-1]
  - [x] Enter Current Stock (numeric keypad + Done)
  - [x] Undo snackbar
- [x] LEVEL:
  - [x] Set Level selector
  - [x] Undo snackbar
- [x] FRESH:
  - [x] Mark Used
  - [x] Discard
  - [x] Shows age and “Use soon” state

### Logging
- [x] All changes write usage_history entries (ADD/REMOVE/SET/ADJUST/LEVEL_SET/FRESH_USED/FRESH_DISCARDED)

Deliverable:
- [ ] User can fully manage inventory and see it persist after app restart

---

## Stage 4 — Recount Pantry (Workflow)
- [ ] Entry from Settings → Recount Pantry
- [ ] Recount Step 1: list existing items first (per household)
  - [ ] COUNT: Enter current stock / mark empty / skip
  - [ ] LEVEL: set level / mark empty / skip
  - [ ] FRESH: mark used/discard (optional for recount)
- [ ] Recount Step 2: “Add items not listed”
- [ ] Finish screen + recount timestamp stored (household-level)

Deliverable:
- [ ] Recount flow completes without data loss and updates updated_at properly

---

## Stage 5 — Meal Suggestions (Not Recipes)
### Screens
- [ ] Meal Suggestions main screen
- [ ] People selector: adults + kids
- [ ] Toggle: hide meals that can’t feed entered people (default ON)
- [ ] Filters: meal type + diet + kids-friendly
- [ ] Segments:
  - [ ] Daily: Breakfast / Lunch / Dinner
  - [ ] Special: Quick / Use-soon / Kids / Lazy day / Desserts
  - [ ] Event: Guest mode

### Matching engine (V1)
- [ ] Required-ingredient matching
- [ ] Near-match logic (missing 1 key ingredient) controlled by toggle or guest mode
- [ ] “You have” + “Missing” display
- [ ] Serving estimate display rules (best-effort; honest)

### Meal detail
- [ ] Shows ingredients list + tags + missing items + serving estimate
- [ ] Optional: add missing items to Shopping List (if shopping list in V1)

Deliverable:
- [ ] Suggestions appear and change based on inventory + people count

---

## Stage 6 — User Meal Ideas (Tight V1)
- [ ] Add Meal Idea screen:
  - [ ] Name
  - [ ] Meal type
  - [ ] Diet type
  - [ ] Kids-friendly toggle
  - [ ] Must-have ingredients list (max 10, autocomplete + voice-ready)
- [ ] Save user meal to DB
- [ ] Ensure user meals participate in suggestions
- [ ] Submit to Community:
  - [ ] Create outbox entry (community_outbox) with payload snapshot
  - [ ] Mark status = PENDING (no backend calls)

Deliverable:
- [ ] User can add meal ideas and see them appear in suggestions

---

## Stage 7 — Voice Input (Everywhere)
### Voice input in fields
- [ ] Mic on search bar
- [ ] Mic on Add/Edit Item fields
- [ ] Mic on Add Meal Idea fields
- [ ] Runtime permission handling (microphone)

### Voice command parser
- [ ] Parse and execute:
  - [ ] add item (“add milk two cartons fridge”)
  - [ ] set stock (“set eggs to twelve”)
  - [ ] plus/minus (“milk plus one”)
  - [ ] level updates (“oil half”)
  - [ ] fresh actions (“mark tomatoes used” / “discard chicken”)
  - [ ] meal queries (“suggest lunch for two adults and two kids”)
- [ ] Post-action snackbar summary + Undo (no modal confirmations)

Deliverable:
- [ ] Voice works reliably enough for basic flows; failures fall back to keyboard

---

## Stage 8 — Settings + Backup
- [ ] Units selector: Metric / Imperial (display + picker defaults; no forced conversions)
- [ ] Household selection (local only)
  - [ ] Create household (local)
  - [ ] Switch household
- [ ] Recount Pantry shortcut
- [ ] Export JSON (full household dataset)
- [ ] Import JSON (restore into DB; handle schema version)
- [ ] About/version

Deliverable:
- [ ] Export/import round trip works without corruption

---

## Stage 9 — Final QA Checklist
- [ ] No future hooks implemented (delivery ordering, community backend, etc.)
- [ ] Performance: search responsive, list smooth
- [ ] Offline-first: airplane mode test passes
- [ ] Data integrity: quantities never negative, undo works
- [ ] Seed data loads once, not duplicated
- [ ] Rotation / process death doesn’t break critical screens (basic state handling)
- [ ] RUN.md included with steps + permissions

---

## Future Hooks (DO NOT implement in V1)
- [ ] Delivery provider link-out + post-order “Add purchased items” workflow
- [ ] Community backend upload + moderation + gamification
- [ ] Cloud sync / multi-device accounts / real household sharing
- [ ] Barcode scanning + product DB
- [ ] Full cooking instructions / nutrition / videos





