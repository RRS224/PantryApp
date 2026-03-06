# UI_ARCHITECTURE.md — PantryCheck Compose UI Rules

This document defines the UI architecture used by the project.

Agents must follow these patterns when implementing UI.

---

## UI framework

Jetpack Compose (Material3)

Architecture pattern:
MVVM

Screen logic must be handled by ViewModels.

---

## Screen structure

Each feature screen should follow this pattern:

ViewModel
- state flows
- business logic
- repository calls

Composable screen
- renders state
- sends user actions to ViewModel

UI must not directly access repositories or DAOs.

---

## Navigation

Navigation uses:

Navigation Compose

Routes must be defined in a single navigation graph.

Screens should not navigate directly without using the NavController.

---

## Layout principles

UI must prioritize:

- large touch targets
- simple layouts
- quick single-hand usage

Buttons may be larger than typical Material defaults because the app is designed for kitchen use.

---

## State management

Use:

StateFlow or Flow exposed by ViewModels.

Composable screens collect state via:

collectAsState()

---

## Error handling

User-visible operations should not fail silently.

Use:

Snackbar  
or  
Material feedback components

---

## Future rule

UI must remain thin.

Business logic must stay in ViewModels or repositories.