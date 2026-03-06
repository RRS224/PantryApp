# TIME_TRACKING_RULES.md — PantryCheck Agent Time Tracking

This file defines how all agents track time spent on work.

## Purpose

Track:
- time spent by Builder
- time spent by Reviewer
- time spent by Supervisor
- cumulative time across all agents

## Rule

Each agent must include these fields at the top of every substantial work cycle:

- Agent name
- Stage
- Task / pass type
- Start time
- End time
- Elapsed time

## Format

Use this exact format:

Agent: <Builder|Reviewer|Supervisor>
Stage: <Stage number + title>
Task: <implementation|review|fix pass|re-review|orchestration>
Start: <YYYY-MM-DD HH:MM>
End: <YYYY-MM-DD HH:MM>
Elapsed: <Xm or Xh Ym>

## Logging rule

At the end of each completed cycle, append one row to TIME_LOG.md.

If exact end time is not available, estimate conservatively and label it:
Estimated elapsed: <Xm>

## Cumulative total

TIME_LOG.md must maintain:
- subtotal by agent
- subtotal by stage
- grand total across all agents

## Scope

Track only meaningful work cycles.
Do not log trivial acknowledgements, one-line confirmations, or pure routing messages unless they took substantial effort.

## Commit rule

Do not update TIME_LOG.md for a stage until that cycle is complete enough to report.