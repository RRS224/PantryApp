# CURRENT_STAGE.md

This file defines the current development stage of the PantryCheck project.

Agents must read this file before starting any task.

---

Current stage

Stage 3 — Inventory CRUD UI

Status

In progress

---

Next stage

Stage 4 — Inventory intelligence and workflows

Not started.

---

Rules for agents

1. Never implement work beyond the current stage.
2. Do not automatically start the next stage.
3. Wait for explicit instruction from the user before moving stages.
4. Always verify the current stage against tasks.md.

---

Workflow mode

Supervisor-controlled multi-agent workflow may be used.

When a Supervisor agent is active:
- Builder and Reviewer must follow Supervisor instructions.
- Supervisor must still obey SPEC.md, CURRENT_STAGE.md, tasks.md, and AGENTS.md.
- Supervisor cannot authorize work beyond the current stage.
