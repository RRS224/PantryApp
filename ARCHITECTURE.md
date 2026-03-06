# ARCHITECTURE.md — PantryCheck System Architecture

This document defines the permanent architecture rules of the project.

Agents must not change these rules unless the user explicitly approves.

---

## Architecture style

The application uses a simplified Clean Architecture structure:

Domain layer
- business models
- repository interfaces

Data layer
- Room entities
- DAO interfaces
- database
- repository implementations
- entity ↔ domain mappers

UI layer
- Jetpack Compose screens
- ViewModels

---

## Layer boundaries

Domain layer
- contains no Android or Room dependencies

Data layer
- may depend on Room and Android libraries
- implements domain repository interfaces

UI layer
- depends on domain models and repository interfaces
- must not depend directly on Room entities

---

## Dependency flow

Allowed:

UI → Domain  
UI → Repository interface  
Data → Domain  

Not allowed:

UI → Room entity  
UI → DAO  
Domain → Data layer

---

## Database

Room is the single source of truth for local persistence.

Schema definition is maintained in:

DB_SCHEMA_ROOM.md

Room entities must remain isolated in the data layer.

---

## Repository pattern

Repository interfaces live in the domain layer.

Room repository implementations live in the data layer.

UI interacts only with repository interfaces.

---

## Offline-first requirement

All core functionality must work without network access.

Future sync/community features must use an outbox pattern.