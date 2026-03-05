# DB_SCHEMA_ROOM.md — PantryCheck Database Schema

This document defines the **canonical Room database schema** for PantryCheck.

All agents implementing Stage 2 must follow this schema exactly.

If SPEC.md conflicts with this file, **SPEC.md takes priority**.

---

# Design Goals

The schema is designed to support:

• multiple households
• multiple storage locations per household
• inventory items that move between locations
• consumption and waste analytics
• future community features
• offline-first operation
• minimal migration pain in later versions

Key design decisions:

• Use **TEXT UUID primary keys** (better for offline + sync later)
• Prefer **archiving** over deletion for core tables
• Maintain **append-only usage history**
• Keep pantry_items as the **source of truth for item location**

---

# Tables Overview

Stage 2 implements these tables:

Core inventory tables

```
households
locations
pantry_items
usage_history
```

Supporting tables

```
meal_ideas
meal_ingredients
community_outbox
shopping_list_items (optional V1)
```

---

# households

Represents a household or home.

Columns

```
id TEXT PRIMARY KEY
name TEXT NOT NULL
is_archived INTEGER NOT NULL DEFAULT 0
created_at INTEGER NOT NULL
updated_at INTEGER NOT NULL
```

---

# locations

Represents storage places inside a household.

Examples:

Pantry
Fridge
Freezer
Other

Columns

```
id TEXT PRIMARY KEY
household_id TEXT NOT NULL
name TEXT NOT NULL
kind TEXT NOT NULL
sort_order INTEGER NOT NULL DEFAULT 0
is_archived INTEGER NOT NULL DEFAULT 0
created_at INTEGER NOT NULL
updated_at INTEGER NOT NULL
```

Foreign keys

```
household_id → households.id
```

Indexes

```
household_id
(household_id, name)
```

Notes

Locations should be **archived, not deleted**, to avoid orphaned items.

---

# pantry_items

Represents an item stored in a location.

Columns

```
id TEXT PRIMARY KEY
household_id TEXT NOT NULL
location_id TEXT NOT NULL

name TEXT NOT NULL
brand TEXT
category TEXT
barcode TEXT

quantity REAL NOT NULL DEFAULT 1
unit TEXT NOT NULL DEFAULT 'unit'
min_quantity REAL

expiry_date INTEGER
purchase_date INTEGER
opened_date INTEGER

notes TEXT
photo_uri TEXT

is_archived INTEGER NOT NULL DEFAULT 0
created_at INTEGER NOT NULL
updated_at INTEGER NOT NULL
```

Foreign keys

```
household_id → households.id
location_id → locations.id
```

Indexes

```
(household_id, location_id)
(household_id, name)
expiry_date
barcode
```

---

# usage_history

Append-only history of inventory activity.

Examples

consume
restock
move
expired
adjust

Columns

```
id TEXT PRIMARY KEY
household_id TEXT NOT NULL
pantry_item_id TEXT NOT NULL

event_type TEXT NOT NULL
delta_quantity REAL
from_location_id TEXT
to_location_id TEXT
note TEXT

event_at INTEGER NOT NULL
created_at INTEGER NOT NULL
```

Foreign keys

```
household_id → households.id
pantry_item_id → pantry_items.id
from_location_id → locations.id
to_location_id → locations.id
```

Indexes

```
(pantry_item_id, event_at DESC)
(household_id, event_at DESC)
```

---

# meal_ideas

Represents meal suggestions.

Columns

```
id TEXT PRIMARY KEY
title TEXT NOT NULL
description TEXT
cuisine TEXT
tags_json TEXT
source_type TEXT NOT NULL
is_archived INTEGER NOT NULL DEFAULT 0
created_at INTEGER NOT NULL
updated_at INTEGER NOT NULL
```

---

# meal_ingredients

Ingredient rows belonging to a meal.

Columns

```
id TEXT PRIMARY KEY
meal_id TEXT NOT NULL

ingredient_name TEXT NOT NULL
pantry_item_id TEXT

quantity REAL
unit TEXT

is_optional INTEGER NOT NULL DEFAULT 0
sort_order INTEGER NOT NULL DEFAULT 0
```

Foreign keys

```
meal_id → meal_ideas.id
pantry_item_id → pantry_items.id
```

---

# community_outbox

Queue for future community sharing / sync.

Columns

```
id TEXT PRIMARY KEY
entity_type TEXT NOT NULL
entity_id TEXT NOT NULL
op_type TEXT NOT NULL
payload_json TEXT NOT NULL
status TEXT NOT NULL
attempt_count INTEGER NOT NULL DEFAULT 0
last_attempt_at INTEGER
created_at INTEGER NOT NULL
```

---

# shopping_list_items (optional)

Shopping list entries.

Columns

```
id TEXT PRIMARY KEY
household_id TEXT NOT NULL
name TEXT NOT NULL
pantry_item_id TEXT
target_quantity REAL
unit TEXT
status TEXT NOT NULL
created_at INTEGER NOT NULL
updated_at INTEGER NOT NULL
```

Foreign keys

```
household_id → households.id
pantry_item_id → pantry_items.id
```

---

# Seeding Rules

On first database creation:

Create default household

```
Home
```

Create default locations

```
Pantry
Fridge
Freezer
Other
```

Seed system meal suggestions dataset.

Initial V1 dataset can be small but schema must support **80-150 entries**.

---

# Location Relationship Model

The **current location of an item** is stored directly in:

```
pantry_items.location_id
```

When an item is moved:

1. Update `pantry_items.location_id`
2. Log a `MOVE` event in `usage_history`

This ensures:

• fast current-state queries
• complete historical tracking
• future analytics support
