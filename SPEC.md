# PantryCheck — Complete Build Specification (V1 + Future Hooks)
Version: 1.0
Last Updated: 2026-03-05

## 0) Purpose of this document
This document is the single source of truth for building PantryCheck.
It is written for AI-assisted development. The build must follow this spec exactly and avoid assumptions.
If something is not explicitly in V1 scope, it must be treated as a future hook only.

---

## 1) Product Overview
### Product name
PantryCheck

### Platform
Android

### Core purpose
An offline-first pantry inventory app that helps households:
1) track what they have (practically, using containers/levels)
2) quickly update stock (including “Enter Current Stock”)
3) get “Meal Suggestions” (not recipes) based on current inventory
4) optionally add their own Meal Suggestions and submit them to a future community database

### Primary daily draw
“What should I cook today?” meal suggestions based on inventory (including desserts, kids-friendly options, and guest-mode menu suggestions).

---

## 2) Build scope

### V1 (must implement)
A) Pantry inventory:
- Add item, edit item, delete item
- Search and voice search
- Container-first quantity (e.g., 2 cartons, 6 cans)
- Two inventory modes:
  - COUNT mode: discrete quantities (cartons/cans/packs/etc.)
  - LEVEL mode: staple levels (Full/Half/Low etc.)
- Fresh/perishable items supported via lightweight “Fresh Mode” behavior (see section 6)

B) Fast stock updates:
- + / − quick adjustments
- Enter Current Stock (numeric keypad + Done)
- Undo snackbar after any stock change
- Usage history logging for adjustments

C) Recount Pantry:
- Show existing items first for quick correction
- Then allow adding new items that entered pantry since last use

D) Meal Suggestions (not recipe instructions):
- Daily: Breakfast/Lunch/Dinner
- Special: Quick (≤15 min), Use-soon, Kids meals, Lazy day meals, Desserts
- Event: Guest mode (“Suggest 4 dishes for 5 people with a dessert”)
- People selector (adults + kids option)
- Serving estimate behavior (best effort; see section 9)
- Display “missing key items” where applicable

E) User-added Meal Suggestions (tight V1):
- User can add a meal idea with must-have ingredients
- Can optionally tag as kids-friendly and mark meal type + diet type
- No cooking steps, no images, no optional ingredients in V1
- “Submit to Community” stores locally in an outbox for future backend syncing

F) Settings:
- Units: Metric / Imperial (affects unit pickers and display defaults)
- Household selection (single household in V1, but schema supports multiple)
- Recount Pantry entry point
- Export/Import (JSON) for backup (offline)

G) Voice input:
- Voice input option for all input areas (fields and search)
- Voice commands for add/update/set/level changes and meal queries (see section 12)

### Not in V1 (future hooks only; do not implement now)
- Barcode scanning + external product databases
- Payments/checkout inside PantryCheck
- Deep integration with delivery providers (cart, order status)
- Social feed, comments, DMs
- Cloud sync / multi-device login
- Community leaderboards/statistics (requires backend)
- Full recipe instructions/nutrition

---

## 3) Tech Architecture
- Language: Kotlin
- UI: Jetpack Compose
- Architecture: MVVM
- Local DB: Room (SQLite)
- Voice recognition: Android SpeechRecognizer (offline recognition if available; fall back to online recognition on device)
- Min Android: API 26 (Android 8.0)
- Offline-first: app must function without internet for all V1 features

Performance:
- Search results under 200ms for typical lists
- DB calls must not block UI thread
- Smooth list rendering for 500+ items

---

## 4) UI Wireframes (exact layout guidance)

### 4.1 Home Screen (Inventory)
- Top app bar: “PantryCheck”
- Search bar with microphone icon
- Optional “Segments” tabs or filter row: All / Pantry / Fridge / Freezer / Use Soon
- List items with quick controls

Wireframe:
------------------------------------------------
PantryCheck
[ Search items… ] (mic)

[ All ] [ Pantry ] [ Fridge ] [ Freezer ] [ Use Soon ]

Milk (Whole) — 1 carton (Fridge)     [-] [+]
Eggs — 12 pcs (Fridge)               [-] [+]
Pasta — 2 packs (Pantry)             [-] [+]
Cooking Oil — Half (Pantry)          [Level]
Tomatoes — Fresh (Added 3d ago)      [Use] [Discard]

(+) Add Item

Rules:
- COUNT items show [-] [+] inline
- LEVEL items show a “Level” button inline (opens level selector)
- Fresh items show “Use” and “Discard” quick actions inline (optional in list; mandatory in detail)

Home Screen must also include a prominent card/button:
- “Meal Suggestions” (entry point)

### 4.2 Add Item Screen
Fields with mic icons:
- Item name
- Variant (optional)
- Location (Pantry/Fridge/Freezer/Other + custom)
- Tracking mode: COUNT / LEVEL / FRESH (default chosen via rules)
If COUNT:
- Quantity value (numeric keypad)
- Inventory unit picker (carton/can/pack/bottle/bag/jar/etc.)
- Size per unit (optional): value + unit (e.g., 1 litre)
If LEVEL:
- Level selector: Full / 3/4 / Half / 1/4 / Low / Empty
If FRESH:
- Optional quantity (count-ish) + unit (e.g., 6 pcs) OR leave blank
- Added date (default today)
- Use-soon threshold (optional, default from category)

Buttons:
[Save] [Cancel]

### 4.3 Item Detail Screen
Shows item header + location + tracking block.

COUNT:
- Quantity display
- Buttons: [+1] [-1]
- Button: Enter Current Stock
- Button: Edit
- Button: Delete
- Last updated + Undo snackbar after change

LEVEL:
- Level display and selector
- Button: Set Level
- Button: Edit
- Button: Delete

FRESH:
- Status and age display (“Added 3 days ago”)
- Buttons: Mark Used, Discard
- Optional: Enter current stock if quantity is tracked

### 4.4 Meal Suggestions Entry Screen
Top:
- People selector:
  - Adults: stepper
  - Kids: stepper
  - Toggle: “Hide meals that clearly can’t feed this many” (default ON)
- Filters:
  - Meal type: Breakfast/Lunch/Dinner/Dessert
  - Diet: Veg/Egg/Non-veg
  - Kids-friendly toggle
- Segments list:
  - Daily Meals
  - Quick (≤15 min)
  - Use-soon
  - Kids meals
  - Lazy day meals
  - Desserts
  - Guest mode

Shows suggestion cards:
- Meal name
- Tags (icons/text)
- “You have:” short list
- “Missing:” short list (if any)
- “Serves approx: X” OR “Serves approx: X–Y” OR “Estimate unavailable”
- Button: View Details

### 4.5 Meal Suggestion Detail Screen
Displays:
- Meal name
- Tags
- Required (must-have) ingredients
- Optional: none in V1
- Inventory match status
- Serving estimate text
Actions:
- “Add missing items to Shopping List” (V1 can be local list)
- “Open Delivery Provider” (future hook UI can exist but disabled or hidden in V1)
- “Save as favorite” (optional V1; if included keep local only)

### 4.6 Add Meal Idea Screen (User-created)
Fields (with mic):
- Meal name
- Meal type (Breakfast/Lunch/Dinner/Dessert)
- Diet (Veg/Egg/Non-veg)
- Kids-friendly (toggle)
- Ingredients (Must-have list)
  - Add ingredient row: text + autocomplete
  - Max 10 ingredients
Buttons:
[Save] [Cancel]
After saving:
- Show “Submit to Community” (stores in outbox, future upload)

### 4.7 Settings Screen
- Units: Metric / Imperial
- Household: select household (V1 only one default household; allow creating additional locally)
- Recount Pantry
- Backup:
  - Export JSON
  - Import JSON
- About / Version

---

## 5) Inventory Model — Practical Handling

### 5.1 Packaging-first quantities
The app must support container-based tracking as first-class:
Examples:
- 2 cartons of milk
- 6 cans of juice
- 1 bag of rice
- 3 packs of noodles
- 1 jar of peanut butter
Optionally store size per unit:
- “carton (1 litre)”
- “bag (5 kg)”

### 5.2 Units
Global setting:
- Metric: g/kg/ml/litre
- Imperial: oz/lb/fl oz/gallon
Important rule:
- Do NOT auto-convert stored values. Preserve what user entered.
- The unit system affects default pickers and display formatting, not forced conversions.

### 5.3 Inventory Modes
- COUNT: quantity_value + inventory_unit (+ optional size_per_unit)
- LEVEL: level_value (0–5 mapped to labels)
- FRESH: lightweight perishable tracking (section 6)

### 5.4 Quick updates
COUNT items:
- inline [-] [+] on list
- detail screen [+1] [-1]
- Enter Current Stock (direct numeric)

LEVEL items:
- Set Level (Full/3/4/Half/1/4/Low/Empty)

---

## 6) Fresh / Perishable Handling (Vegetables, meats, fresh items)
Goal: practical tracking without forcing grams.

Fresh items have:
- Added date (default today)
- Use-soon threshold days (default from category; editable)
- Status: ACTIVE / USED / DISCARDED

Display:
- “Tomatoes — Fresh (Added 3 days ago)”
- “Chicken — Use soon (Added 2 days ago)”

Actions:
- Mark Used
- Discard

Frozen foods:
- If packaged (peas pack, nuggets bag): COUNT + location Freezer
- If bulk stash (loose): LEVEL + location Freezer

Meat:
- default COUNT unit “pack”
- optional size per unit (e.g., 500g)

---

## 7) Enter Current Stock (must implement)
On COUNT item detail:
- Button “Enter Current Stock”
- Opens numeric keypad input (integer by default)
- Done updates quantity_value directly
- Log change in UsageHistory (type ADJUST)
- Show Undo snackbar

Optional:
- Long-press quantity on list to open same editor

Rules:
- Quantity cannot go below 0
- If user enters blank, cancel
- If user enters huge number, show subtle confirmation (“This seems high. Continue?”) with a threshold per unit type

---

## 8) Recount Pantry (must implement)
Entry: Settings → Recount Pantry

Flow:
1) Show current inventory list first (only items currently in DB for selected household)
2) Each row provides:
   - Enter Current Stock (direct numeric) OR level selector for LEVEL items
   - Mark Empty (sets quantity 0 or level Empty)
   - Skip
3) After reviewing all items:
   - Screen: “Add items not listed”
   - Allows quick add of new items (Add Item flow)
4) Finish:
   - Record recount timestamp for household

---

## 9) Meal Suggestions System (V1)
Terminology:
- “Meal Suggestions” / “Food Suggestions”
- Not “recipe engine”
- No cooking steps in V1

### 9.1 Suggestion library (offline)
Ship with a base library (target 80–150 suggestions):
- includes Indian + global staples
- includes Desserts
- includes Kids-friendly tags
- includes Quick/Lazy day segments
- includes Guest-mode capable dishes

### 9.2 Segments
Daily:
- Breakfast / Lunch / Dinner
Special:
- Quick (≤15 min)
- Use-soon focus
- Kids meals
- Lazy day meals
- Desserts
Event:
- Guest mode menu builder

### 9.3 Filters
- Diet: Veg / Egg / Non-veg
- Kids-friendly toggle
- Meal type selection
- “Hide meals that clearly can’t feed this many” toggle

### 9.4 People input
- Adults count (default 2)
- Kids count (default 0)
Portion model:
- 1 adult = 1.0 portion unit
- 1 kid = 0.6 portion unit
Total portions = adults*1.0 + kids*0.6

### 9.5 Serving estimate behavior (honest best-effort)
- For COUNT items with known size per unit or typical servings, estimate servings
- For LEVEL staples (oil/salt/sugar/flour/rice etc.), treat as “available unless Low/Empty”
- For FRESH items, treat as available but estimate may be uncertain
UI output:
- If confident: “Serves approx: X”
- If partial: “Serves approx: X–Y”
- If not computable: “Serving estimate unavailable”
Additionally:
- For fresh-heavy dishes, optionally show “Stock likely for: X” rather than hiding, unless user chooses strict hide toggle.

## Pantry Staples Handling

### Purpose

Certain ingredients are considered **basic household staples** and should not normally block meal suggestions.

Examples include:

salt  
oil  
sugar  
flour  
rice  
basic spices  

Without this rule, the suggestion engine may frequently show meals as unavailable simply because the pantry inventory does not explicitly contain these items.

Example:

Meal: Vegetable Stir Fry  
Ingredients: vegetables, oil, salt  

If oil and salt are not explicitly tracked in the pantry inventory, the system may incorrectly mark the meal as missing ingredients.

To prevent this behavior, PantryCheck includes a **Staples System**.

---

## Default Pantry Staples

The app ships with a predefined list of common pantry staples.

Initial starter list:

salt  
sugar  
cooking oil  
butter  
ghee  
flour  
rice  
cornflour  
baking powder  
baking soda  
black pepper  
turmeric  
chilli powder  
cumin  
coriander powder  

These ingredients are assumed to be available **unless the user explicitly marks them as Low or Empty**.

---

## Staples Behavior

During ingredient matching:

1. If a required ingredient belongs to the staples list
2. And the user has **not explicitly marked it Low or Empty**

Then the system treats the ingredient as **available**.

It must not appear under "Missing".

---

## Optional Inventory Tracking

Users may still add staples to their inventory.

Example:

oil — Half  
salt — Full  
flour — Low  

If the user tracks the staple:

The **actual inventory status overrides the default assumption**.

Example:

If the user sets:

oil — Empty

Then meal suggestions that require oil must show:

Missing: oil

---

## Matching Priority Including Staples

Ingredient availability must be determined in this order:

1. Exact inventory match
2. Alias normalized match
3. Substitute match
4. Pantry staple assumed available
5. Missing ingredient

---

## UI Behavior

Staples should **not be highlighted as required inventory** unless the user explicitly tracks them.

Example meal suggestion card:

You have:  
chicken  
garlic  
onions  

Missing:  
soy sauce  

Staples such as oil or salt should not appear.

---

## Future Expansion

Future builds may allow:

• user-customizable staple lists  
• region-specific staple packs  
• automatic detection of staple ingredients

These features are **not part of V1**.

### 9.6 Matching logic (V1)
Each suggestion has:
- required ingredients list (must-have)
- tags (meal type, diet type, kids-friendly, dessert, quick, region)
Match:
- Required ingredients present → strong match
- Missing 1 required ingredient → near match (shown only if user enables “Show near matches” OR under Guest mode planning)
Display:
- “You have:” list (max 5)
- “Missing:” list (max 3; show “+N more” if needed)

---

## Ingredient Normalization and Substitution System

### Purpose
Meal Suggestions must correctly match pantry inventory with meal ingredients even when different names are used for the same ingredient.

Example:

Pantry item:  
capsicum

Meal ingredient:  
bell pepper

Without normalization the system would incorrectly mark the ingredient as missing.

This system ensures reliable matching across regional naming differences.

---

## Canonical Ingredient Model

All ingredients must be converted to a **canonical name** before matching.

Example conversions:

capsicum → bell pepper  
sooji → semolina  
curd → yogurt  
aubergine → eggplant  
coriander leaves → cilantro  

Matching always occurs using canonical names.

---

## Ingredient Aliases Table

Create a table or local JSON dataset called:

ingredient_aliases

Structure:

alias (TEXT)  
canonical (TEXT)

Example rows:

capsicum → bell pepper  
bell peppers → bell pepper  
sooji → semolina  
rava → semolina  
suji → semolina  
curd → yogurt  
dahi → yogurt  
aubergine → eggplant  
brinjal → eggplant  
coriander leaves → cilantro  
spring onion → scallion  
green onion → scallion  
maida → all purpose flour  
atta → whole wheat flour  

Normalization algorithm:

1. Convert ingredient to lowercase.
2. Trim whitespace.
3. Remove plural suffix where obvious.
4. Look up alias table.
5. Replace with canonical value if alias exists.
6. If no alias exists, use normalized input as canonical.

This process must run for:

• pantry item names  
• meal ingredient names

---

## Ingredient Substitution System (Optional but Recommended)

Certain ingredients can substitute for each other.

Example:

butter ↔ ghee  
milk ↔ plant milk  
cream ↔ milk (partial substitute)  

Create a dataset:

ingredient_substitutes

Structure:

canonical (TEXT)  
substitute_canonical (TEXT)

Example rows:

butter → ghee  
ghee → butter  
milk → almond milk  
milk → soy milk  
cream → milk  
lime → lemon  

Matching logic:

If required ingredient is not present:

1. Check if a substitute exists in pantry inventory.
2. If substitute is present:
   - mark ingredient as **substitutable** instead of missing.

Example UI output:

Missing: butter  
Substitute available: ghee

---

## Matching Priority

Ingredient matching priority order:

1. Exact canonical match
2. Alias normalized match
3. Substitute match
4. Missing ingredient

---

## UI Behavior

When substitutes exist:

Display:

Missing: butter  
Substitute available: ghee

If no substitutes exist:

Display:

Missing: butter

---

## Performance Requirement

Alias lookup must be O(1) using a hashmap or dictionary loaded at app startup.

The alias dataset should be lightweight (target: 100–200 entries).

---

## Initial Alias Dataset (Starter List)

The system must ship with a starter dataset including common international and Indian ingredient aliases.

Examples include:

capsicum  
bell pepper  

sooji  
rava  
semolina  

curd  
dahi  
yogurt  

aubergine  
eggplant  
brinjal  

coriander leaves  
cilantro  

spring onion  
green onion  
scallion  

maida  
all purpose flour  

atta  
whole wheat flour  

chilli  
chili  

mince  
ground meat  

ladyfinger  
okra  

arbi  
taro root  

These mappings can be expanded in future versions.

---

## Future Expansion

Future builds may:

• download regional alias packs  
• allow users to define their own aliases  
• crowdsource ingredient mappings through the community database

These features are **not part of V1**.

## 10) Guest Mode (V1)
Input:
- People (adults/kids)
- Diet preference (veg/egg/non-veg) optional
- Time constraint: quick/normal optional

Output:
- Suggest a menu set:
  - 2 mains
  - 1 side
  - 1 dessert
Optional:
  - starter/snack if suitable

Rules:
- Prefer dishes with high inventory match
- Show missing items per dish
- Show serving estimate per dish (best effort)

---

## 11) User-added Meal Ideas (V1 tight)
User can create a meal idea with:
- Meal name
- Meal type
- Diet tag
- Kids-friendly toggle
- Must-have ingredients list (max 10)
No optional ingredients in V1
No cooking steps
No photos

These user meal ideas must participate in meal suggestions matching like system meals.

---

## 12) Voice System (V1)
Voice input must be available for:
- Search
- Add item fields
- Add meal fields
- Quick updates via command-style voice

### 12.1 Voice input UI rule
Every text field has a mic icon.
Tap mic → SpeechRecognizer → fill field.
If speech fails → show “Could not recognize speech. Try again.” and keep keyboard available.

### 12.2 Voice command grammar (must support)
Inventory add:
- “add milk two cartons”
- “add almond milk one carton fridge”
- “add rice one bag pantry”
- “add tomatoes fresh”
Inventory set stock:
- “set milk to three cartons”
- “update eggs to twelve”
Increment/decrement:
- “milk plus one”
- “eggs minus six”
Level:
- “oil half”
- “sugar low”
Fresh actions:
- “mark tomatoes used”
- “discard chicken”
Search:
- “find milk”
- “do we have eggs”
Meal queries:
- “what can I cook today”
- “suggest lunch for two adults and two kids”
- “suggest dessert for five people”
- “guest menu for five people with dessert”

Parsing order:
1) detect action keywords (add/set/update/plus/minus/mark/discard/suggest)
2) extract quantities and units (carton/can/pack/etc.)
3) extract level keywords (full/half/low/empty)
4) detect location keywords (pantry/fridge/freezer)
5) remaining tokens form item name + variant if applicable

Voice confirmation:
- Do NOT use modal dialogs.
- After action, show snackbar summary with Undo.

---

## 13) Data Model (Room schema) — optimized for future analytics + multi-household
All tables must include `household_id` to support future sharing/sync.

### 13.1 Households
Table: households
- household_id (UUID, PK)
- name (TEXT)
- created_at (TS)

V1:
- create default household on first run: “Home”
- allow local creation of more households (no sync)

### 13.2 Pantry Items
Table: pantry_items
- item_id (UUID, PK)
- household_id (UUID, FK households)
- item_name (TEXT)               // base name normalized
- item_variant (TEXT nullable)   // e.g., Whole, Almond, Tropicana
- location_id (UUID nullable)    // FK to locations
- inventory_mode (TEXT)          // COUNT | LEVEL | FRESH
- quantity_value (REAL nullable) // for COUNT/FRESH optional
- inventory_unit (TEXT nullable) // carton/can/pack/pcs/bag/jar/etc.
- size_value (REAL nullable)     // per-unit size, optional
- size_unit (TEXT nullable)      // ml/litre/g/kg/fl oz etc.
- level_value (INT nullable)     // 0..5 for LEVEL
- fresh_added_date (TS nullable) // for FRESH
- fresh_use_soon_days (INT nullable)
- fresh_status (TEXT nullable)   // ACTIVE | USED | DISCARDED
- notes (TEXT nullable)
- created_at (TS)
- updated_at (TS)

Index:
- household_id
- item_name
- updated_at

### 13.3 Locations
Table: locations
- location_id (UUID, PK)
- household_id (UUID, FK)
- name (TEXT) // Pantry, Fridge, Freezer, Other, plus custom
- created_at (TS)

V1:
- seed Pantry/Fridge/Freezer/Other in each household

### 13.4 Usage History
Table: usage_history
- history_id (UUID, PK)
- household_id (UUID, FK)
- item_id (UUID, FK)
- action_type (TEXT) // ADD, REMOVE, SET, ADJUST, LEVEL_SET, FRESH_USED, FRESH_DISCARDED
- previous_value (TEXT nullable) // store as string for flexibility (e.g., “2 cartons”, “Half”)
- new_value (TEXT nullable)
- delta_value (REAL nullable)
- timestamp (TS)

### 13.5 Meal Ideas (system + user)
Table: meal_ideas
- meal_id (UUID, PK)
- household_id (UUID nullable)     // null for SYSTEM meals; set for USER meals
- title (TEXT)
- meal_type (TEXT)                 // breakfast/lunch/dinner/dessert
- diet_type (TEXT)                 // veg/egg/nonveg
- kids_friendly (BOOL)
- quick_tag (BOOL)
- lazy_tag (BOOL)
- use_soon_tag (BOOL)
- region_tag (TEXT nullable)       // e.g., indian, global
- created_by (TEXT)                // SYSTEM | USER | COMMUNITY
- author_display_name (TEXT nullable)
- community_id (TEXT nullable)     // future backend ID
- created_at (TS)
- updated_at (TS)

### 13.6 Meal Ingredients
Table: meal_ingredients
- meal_ingredient_id (UUID, PK)
- meal_id (UUID, FK meal_ideas)
- ingredient_name (TEXT)
- normalized_key (TEXT)            // for matching; normalized
- is_required (BOOL)               // V1: true for all
- created_at (TS)

### 13.7 Community Submissions Outbox (future backend)
Table: community_outbox
- submission_id (UUID, PK)
- meal_id (UUID, FK meal_ideas)
- status (TEXT)                    // PENDING, SUBMITTED, APPROVED, REJECTED
- payload_snapshot_json (TEXT)
- submitted_at (TS nullable)
- rejection_reason (TEXT nullable)

### 13.8 Shopping List (V1 optional but recommended)
Table: shopping_list_items
- shopping_item_id (UUID, PK)
- household_id (UUID, FK)
- name (TEXT)
- quantity_value (REAL nullable)
- unit (TEXT nullable)
- created_at (TS)
- checked (BOOL)

---

## 14) Naming normalization (must implement)
Goal: avoid duplicates like “whole milk”, “milk carton”, “milk”.

Rules:
- normalize base item_name:
  - lowercase
  - trim spaces
  - remove common filler words (“a”, “the”, “packet of”, etc.)
  - simple plural normalization for common cases
- keep variant separate where possible
Examples:
- “whole milk” → item_name=milk, variant=whole
- “tropicana orange juice” → item_name=orange juice, variant=tropicana

When adding:
- if same household_id + item_name + variant + location + mode exists:
  - COUNT: increase quantity by new quantity (or set if using SET command)
  - LEVEL: set level
  - FRESH: treat as a new entry OR refresh added_date (V1 choose refresh added_date)

---

## 15) Error handling rules
- Quantity cannot be negative; clamp at 0
- Voice failures must not block user
- Deleting item requires confirmation, but prefer soft-delete (optional V1.1)
- Any destructive change should allow Undo where feasible

---

## 16) Backup / Export / Import (V1)
- Export household data to JSON
- Import JSON restores into local DB
- Include schema version field for migration

---

## 17) Delivery Providers integration (future hook)
Goal: no payments, no checkout in PantryCheck.

Later behavior:
- show list of providers configured by region (FreshToHome, BigBasket, Amazon Fresh, Instamart, etc.)
- tapping provider opens provider website/app (deep link if available)
- PantryCheck can prepare a “missing items list”
- Post-order inventory update:
  - V1.1+: user confirms items bought and PantryCheck adds to inventory
  - Future: parse email order confirmation (optional)

Store provider definitions (future):
- provider_name
- open_url
- optional search_url_template with {query}

---

## 18) Community sharing + gamification (future hooks)
Not implemented in V1, but must be planned.

Future features:
- username creation at first community submission:
  - “Share anonymously” OR “Use a username”
  - “Accept suggested username” OR “Create my own”
- stats:
  - “X households tried your meal idea today”
  - weekly top meal / top dessert / top kids meal
- reactions (not comments initially):
  - Tried it
  - Family loved it
  - Saved
  - Quick and easy
Backend requirements:
- moderation pipeline
- anti-spam controls
- dedup tries per household per day per meal
- reporting and takedown

---

## 19) Acceptance Criteria (V1)
A) Inventory:
- add/edit/delete items
- COUNT/LEVEL/FRESH modes work as specified
- search + voice search works
- fast +/− updates work
- Enter Current Stock works with numeric keypad and Undo
- Recount Pantry workflow works (existing first, then add new)

B) Meal Suggestions:
- suggestions show from system library
- people selector affects hiding/serving text behavior
- segments include: Daily, Quick, Use-soon, Kids, Lazy day, Desserts, Guest mode
- “missing items” display works
- user can add a meal idea (tight) and it participates in suggestions

C) Data:
- Room schema matches section 13
- all actions log to usage_history
- export/import JSON works for backup

D) Offline:
- app works with no internet for all V1 functions

---

## 20) Implementation notes (for developer/AI)
- Prioritize speed of interaction over complex features.
- Do not implement full recipes, nutrition, barcode databases, or payment flows.
- Build with clean separation between:
  - inventory engine
  - suggestion engine
  - UI layer
- Ensure database schema supports multi-household in future (household_id everywhere).