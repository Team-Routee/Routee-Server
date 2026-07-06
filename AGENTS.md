# Agent Guide for routee

This document is the **project constitution** for AI coding agents working in the `routee` repository.
Design principles here take precedence over implementation convenience.

---

## 1. Overview

| Area                  | Value                                                                     |
|-----------------------|---------------------------------------------------------------------------|
| Project name          | `routee`                                                                  |
| Base package          | `org.sopt.routee`                                                         |
| Java                  | JDK 25                                                                    |
| Framework             | Spring Boot 4.0.7                                                         |
| Spring Modulith       | 2.0.7                                                                     |
| Build                 | Gradle (Groovy DSL)                                                       |
| Dependency management | Spring Boot BOM + Spring Modulith BOM                                     |
| Lombok                | Available in all modules                                                  |
| OpenAPI               | Springdoc OpenAPI 3.0.2 (in `routee-app`)                                 |
| Git workflow          | GitFlow — `develop` as PR target, `main` for release                      |
| Branch naming         | `{type}/{issue-number}/{description}` (e.g. `feature/1/common-resources`) |
| Commit format         | `type: 설명` in Korean (e.g. `feat: 공통 응답 레코드 구현`)                          |
| PR title              | `type: subject`                                                           |

---

## 2. Architecture Principles

### Spring Modulith

This project is a **modular monolith** built on Spring Modulith.

- Prefer Domain Events for state propagation between modules over direct calls.
- Module-level event handlers should remain replaceable by external messaging infrastructure
  (Kafka, RabbitMQ, etc.) without changing the publishing side.
- Do not expose implementation classes across module boundaries.
- Keep each module independently testable — no shared state, no cross-module internal imports.

### Module Boundary

The `internal` package is the **private implementation area** of a module.

- No other module may import any type from another module's `internal` package — ever.
- Modules interact only through another module's **`api` package**.
- Only the `api` package is exported through Spring Modulith Named Interfaces.
- Direct dependency on another module's implementation class is forbidden.

### API Package

Everything intended for other modules must reside under the `api` package.
The types a module may expose to the outside world are:

- **UseCase** — interface for triggering the module's behavior.
  A UseCase represents a business capability, not an application service.
- **Command** — input for a state-changing UseCase method, when another module is the caller
- **Query** — input for a read-only UseCase method, when another module is the caller
- **Result** — output returned from a UseCase
- **Domain Event** — signals that something has happened inside the module
- **Type** — shared enums/value types referenced by a UseCase signature (e.g. `MemberRole`)

A UseCase method that only the owning module's own controller ever calls does not need a
dedicated Command/Query type — it may take primitives, enums, or `api.type` values directly.
Promote a parameter to `api.command` / `api.query` only once another module actually needs
to construct it. DTOs that merely hand a request from a controller to its own module's
service stay under `internal.service.dto`.

Everything else belongs to `internal`.

### Package Structure

```
org.sopt.routee.{module}
├── api/                                   ← published interface — @NamedInterface("api")
│   ├── package-info.java
│   ├── usecase/                               ← every module's cross-module entry point
│   │   ├── package-info.java                     ← @NamedInterface("api")
│   │   └── {Module}UseCase.java                  ← public
│   ├── result/
│   │   ├── package-info.java                     ← @NamedInterface("api")
│   │   └── {Entity}Result.java                   ← public
│   ├── type/                                  ← shared enums/value types, when needed
│   │   ├── package-info.java                     ← @NamedInterface("api")
│   │   └── {Entity}Role.java                     ← public
│   └── command/ · query/ · event/ · port/     ← add only when a use case needs it (see below)
└── internal/                              ← private — never import from outside
    ├── controller/
    │   └── dto/                               ← request DTOs; mapped to a Command before
    │                                              reaching the service
    ├── service/
    │   └── dto/command/                       ← Commands consumed only within this module
    ├── repository/
    ├── entity/
    ├── mapper/
    ├── listener/
    ├── config/
    ├── exception/
    └── code/
```

`command`, `query`, and `event` are added under `api` only once another module needs to
construct that input or react to that event. `routee-external` uses `api.port` instead of
`api.usecase` — its published contract is a set of integration ports, not a business UseCase.

All sub-packages of `api` are considered part of the published API.

A module may also declare additional Named Interfaces for infrastructure that must be
exposed only to the Composition Root (e.g. `security/` for types that `routee-app`'s
security configuration must reference). These live outside `api`, are exceptions,
and should be minimized.

### Named Interface

Every module exposes exactly one primary published interface, spread across `api` and its
sub-packages:

```java
// api/package-info.java
@org.springframework.modulith.NamedInterface("api")
package org.sopt.routee.member.api;

// api/usecase/package-info.java
@org.springframework.modulith.NamedInterface("api")
package org.sopt.routee.member.api.usecase;
```

- A package-level `@NamedInterface` covers only the types declared directly in that
  package — it does **not** propagate to sub-packages. Every sub-package of `api` that
  contains public types must declare its own `package-info.java`.
- Give every one of these `package-info.java` files the **same name** (`"api"`). Spring
  Modulith merges named interfaces that share a name into a single logical published
  interface, so every sub-package of `api` in use (`usecase`, `result`, `type`, `port`,
  `command`, `query`, `event`) resolves to one `api` interface from another module's
  point of view.
- Only the `api` package (and its sub-packages) may be referenced from other modules.
- Never annotate an `internal` package with `@NamedInterface`.
- Verify with `ApplicationModules.of(RouteeApplication.class).verify()`
  (`routee-app`'s `ModularityTests`) after adding or moving packages — a missing
  `package-info.java` fails loudly as a boundary violation, not a silent leak.

### Composition Root

`routee-app` is the **Composition Root**.

- Contains: `@SpringBootApplication`, `SecurityFilterChain`, global configuration.
- Must not contain: business logic, domain entities, repositories, services.
- May reference only another module's `api` package — never `internal`.

### Inter-Module Communication

**Synchronous** — when the caller needs an immediate result, call the target module's UseCase.

```java
// Good
authUseCase.login(command)

// Bad — direct impl dependency
authService.

login(command)
```

**Asynchronous** — when a state change must propagate to other modules, publish a Domain Event.
The publishing module does not know who listens.

```java
// Good
applicationEventPublisher.publishEvent(new MemberRegistered(memberId))

	// Bad — cross-module impl call
	activityService.

initializeFor(memberId)
```

**Queries never use events.** Read operations always go through a UseCase.

### Domain Events

- Events describe something that **already happened**. Use past tense.
    - Good: `MemberRegistered`, `CourseCompleted`, `ActivityDeleted`
    - Bad: `InsertMemberEvent`, `UpdateCourseEvent`
- Events are **immutable**. Use `record`.
- Event listeners **orchestrate only**: invoke public UseCases or publish additional events.
  Listeners must not contain business rules or access repositories directly.
- `ApplicationModuleListener` classes belong in `internal.listener`.
- Design listeners so they can be replaced by a message broker (Kafka, etc.) without
  changing the publishing side.

### Entity Relationships

- JPA entity associations (`@OneToOne`, `@OneToMany`, `@ManyToOne`) are allowed only within the same module.
- Never create JPA entity associations across module boundaries.
- Cross-module references must use identifiers (e.g. `memberId`, `courseId`) instead of entity references.
- When additional data from another module is required for command processing, invoke the target module through its
  public UseCase.
- Read operations may use dedicated read models or read-only DTO projections instead of exposing domain entities.
- In a shared database environment, cross-module SQL joins are allowed only for read models. Such queries must return
  DTO projections and must not introduce compile-time dependencies on another module's entity classes.
- Never expose another module's entities outside its module boundary.
- Avoid bidirectional associations unless they are truly necessary to maintain aggregate consistency within the same
  module.
- `@ManyToMany` associations are forbidden. Always model many-to-many relationships using an explicit junction entity (
  association table) so additional attributes and lifecycle can be managed explicitly.

---

## 3. Module Roles

### routee-app

Composition Root and Spring Boot entry point.

- Bean registration and auto-configuration.
- Security (`SecurityFilterChain`, filter registration, exception delegation).
- Application bootstrap (`@SpringBootApplication`).
- Module wiring — connects domain modules without owning domain logic.
- Must not contain business logic, entities, repositories, or services.

### routee-common

Shared infrastructure used by all modules.

- Contains cross-cutting concerns: response format, result codes, base exception,
  global exception handler, validation support, logging abstractions.
- May contain a technical `BaseEntity` (`@MappedSuperclass`) providing auditing fields
  (`createdAt`, `updatedAt`) for domain modules to extend. This is infrastructure, not a
  domain entity — it has no table, no identifier, and no relationships.
- Must not contain: domain entities, repositories, domain services, business logic.
- Has no dependency on any domain module.

### routee-member

Member domain — owns member identity and lifecycle.

- Exposes `MemberUseCase` for the read operations other modules need (currently: resolving
  token claims by OAuth identity for login). Registration is handled through this module's
  own controller/service and does not need to be part of the cross-module contract.
- Exposes `MemberRole` as a shared type under `api.type`.
- Owns the `Member` JPA entity and `MemberRepository` (both internal).
- `routee-auth` declares a Gradle dependency on this module to resolve member identity during
  login. Other domain modules may do the same, provided the dependency stays directed and
  non-circular.

### Domain Modules

`routee-auth`, `routee-member`, `routee-activity`, `routee-course`

- Each module owns its domain completely.
- Exposes behavior through UseCase interfaces.
- Publishes Domain Events for state changes relevant to other modules.
- Must not import `internal` types from other domain modules.

### routee-external

Owns all integrations with external systems (third-party APIs, social login providers, etc.).
Uses a **port-adapter pattern**: public port interfaces define what an integration does; adapter implementations are private in `internal`.

- Exposes ports under `api.port` (e.g. `OidcVerifyPort`) instead of a `UseCase` —
  this module's public contract is "what can be verified/fetched", not a business capability.
- Exposes `OAuthProvider` as a shared type under `api.type`, used by any module that needs
  to identify a social login provider.
- Domain modules depend only on port interfaces — never on adapter implementations.
- Adapters translate third-party exceptions into typed exceptions that extend `BaseException`,
  so `GlobalExceptionHandler` handles them without any caller-side catch blocks.
- Must not contain business logic.
- Depended on by `routee-member` (OIDC subject verification) and `routee-auth` (token issuance
  needs the provider type and, currently, the same verification port).

---

## 4. Dependency Rules

```
routee-app                    → api of every module (composition root)
routee-common                 ← depended on by every other module
routee-external               ← depended on by routee-member, routee-auth
routee-member                 ← depended on by routee-auth
routee-auth, routee-activity,
routee-course                 ← no other domain module depends on these (yet)
```

A domain module depends directly on another domain module's `api` only when it has a real,
current need for a synchronous result — this is the exception, not the default shape. Prefer
Domain Events when an immediate return value isn't required.

**Allowed**

- Any module → `routee-common`
- `routee-app` → the `api` package of any domain module
- A module → its own `internal` packages
- Domain module → another domain module's **`api` package** only, when a synchronous result is required
  and the dependency is directed and non-circular

**Forbidden**

- `routee-common` → any domain module
- Any module → another module's `internal` package
- `routee-app` → another module's `internal` package
- Circular Gradle dependencies between modules

When a domain module depends on another domain module via Gradle,
it must use only the target module's `api` package — never its `internal` types.
Prefer Domain Events over direct UseCase calls whenever an immediate return value is not required.

Do not introduce circular Gradle dependencies between modules.

Note: Gradle `implementation` scope does not expose transitive dependencies.
Declare every dependency a module actually uses explicitly.

---

## 5. Security

- Security configuration (`SecurityFilterChain`, filter registration) belongs only in `routee-app`.
- JWT issuance and validation belong only in `routee-auth`.
- OIDC token decoding (Apple, Kakao JWKS) belongs in `routee-external`. Callers use the `OidcTokenVerifier` port interface.
- Other domain modules must not depend on Spring Security directly.
- Spring Security exceptions must be delegated to the MVC exception handling mechanism
  (`HandlerExceptionResolver` → `@RestControllerAdvice`) rather than handled inside filters.
- Endpoints that do not require authentication are managed through a centralized whitelist.

---

## 6. Naming Conventions

**UseCase**

- Names should describe a business capability, not an application operation.
    - Good: `LoginUseCase`, `CreateCourseUseCase`, `CompleteActivityUseCase`
    - Bad: `AuthService`, `MemberManager`, `CourseProcessor`

**Domain Events**

- Names should describe a completed business action. Use past tense.
    - Good: `MemberRegistered`, `CourseCreated`, `ActivityCompleted`
    - Bad: `CreateMember`, `CreatingMember`, `MemberCreate`, `InsertMemberEvent`

**Packages**

- The published API of a module resides under the `api` package.
  Business concepts are represented by sub-packages of `api`
  (e.g. `api.command`, `api.query`, `api.event`).
- Avoid `manager`, `helper`, `processor`, `util`, `misc` outside of `internal`.
- Inside `internal`, technical layer names (`controller`, `service`, `repository`) are acceptable.

---

## 7. Layer Rules

### UseCase

A UseCase is always an interface located under `api.usecase`.
Its implementation belongs to `internal.service`.
It is the module's **cross-module contract** — add a method here only for behavior another
module actually needs to call. Other modules depend only on the interface, never the
implementation.

### Controllers

Controllers are implementation details. They always belong to `internal.controller`
and are never exposed outside the module.

Controllers should remain thin.

- For endpoints local to the module, depend on the module's own `internal.service` directly —
  a UseCase indirection isn't needed until another module has to call the same behavior.
- For behavior owned by another module, depend on that module's UseCase interface, never its
  service implementation.
- Contain no business logic.
- Never access repositories directly.
- Map request objects to Command/Query objects and pass them to the service or UseCase.
  Do not pass controller request DTOs directly as-is.
- Build responses exclusively using the common response factory (`ApiResponse`).

**Swagger Documentation Separation**

Every controller must implement a `{Name}ControllerDocs` interface that lives in the same
`internal.controller` package. All Swagger annotations (`@Tag`, `@Operation`, `@ApiResponses`)
belong exclusively on the interface. The controller class contains only Spring MVC annotations
and implementation logic — no Swagger annotations.

```
internal/controller/
├── AuthControllerDocs.java   ← @Tag, @Operation, @ApiResponses
└── AuthController.java       ← @RestController, implements AuthControllerDocs
```

### Services

Services should be cohesive — one service should represent one business capability.

- Services always belong to `internal.service`.
- Contain all business logic for the module.
- Never depend on types in the controller layer.
- Never communicate with another module's service directly — use a UseCase instead.
- Call repositories for persistence.
- Publish Domain Events when state changes require cross-module propagation.

### Repositories

- Repositories always belong to `internal.repository`.
- Are never exposed outside the module.
- Are accessed only by services.
- Contain persistence logic only — no business logic.

### Mappers

Conversion between `api` contracts (Command, Query, Result) and `internal` entities is done
by a dedicated Mapper, not inline in the service or via static factory methods on the entity.

- Mappers always belong to `internal.mapper`.
- One Mapper per aggregate (e.g. `MemberMapper` converts `RegisterCommand` ↔ `Member`).
- Are Spring components injected into services via constructor injection, consistent with
  the rest of the codebase's DI convention.
- Contain only field-to-field translation — no persistence calls, no cross-module lookups,
  no validation beyond what the target type's constructor already enforces.
- Never imported outside the module; only the owning module's services depend on them.

### DTOs

- Are immutable. Use `record` unless mutability is required.
- Contain no business logic.
- Request DTOs live in `internal.controller.dto`. Do not reuse them as Commands —
  always map to a Command object before passing it to the service.
- A Command/Query consumed only by its own module's service stays under
  `internal.service.dto.command`. Move it to `api.command` / `api.query` only once another
  module needs to construct it.
- A Result returned across a module boundary belongs under `api.result`.

### Package Visibility

Only packages under `api` may contain public contracts intended for other modules.

- Implementation classes should remain package-private whenever possible.
- Avoid declaring public classes under `internal` unless required by Spring or JPA.

---

## 8. Coding Conventions

**Dependency Injection**

- Use constructor injection. Field injection (`@Autowired`) is forbidden.
- Prefer `@RequiredArgsConstructor` with `final` fields.

**Java 25 Features**

- Prefer `record` for immutable data carriers (DTOs, Commands, Results, Events).
- Use `sealed interface` to model closed type hierarchies.
- Use switch expressions instead of switch statements.
- Consider virtual threads for I/O-bound operations where appropriate.

**Optional**

- Use `Optional` only as a method return type.
- Never use `Optional` as a field type or method parameter.

**Exceptions**

- All application exceptions extend the common base exception class.
- Never `throw new RuntimeException(...)` directly.
- Business validation failures should throw domain exceptions — do not return `null`
  or `boolean` flags to signal failure.
- Translate third-party exceptions (JWT, HTTP client, etc.) into domain exceptions
  immediately at the boundary.
- Never expose third-party exception types beyond the module boundary.

**Logging**

- Use SLF4J `Logger`. `System.out.println` is forbidden.

**Transactions**

- Keep transaction scope as small as possible.
- Use `@Transactional(readOnly = true)` for read-only operations.

**JPA**

- Review every association for N+1 risk before merging.
- Do not use `EAGER` fetch without a documented reason.

**Configuration Classes**

- Configuration classes that are purely internal to a module belong in that module's `internal.config` package.
- Cross-module configuration (Security, global beans) belongs in `routee-app`.
- Avoid exposing implementation beans as public APIs. Inject UseCase interfaces wherever possible.

**BOM and Versions**

- Omit versions for dependencies managed by the Spring Boot BOM or Spring Modulith BOM.
- Always specify a version for dependencies not in the BOM.

---

## 9. Testing

- Prefer testing UseCases over controllers whenever possible.
- Test public APIs, not implementation details.
  Do not test private methods or `internal` types directly from outside the module.
- Mock external systems (third-party APIs, message brokers) at module boundaries.
- Keep module tests independent — a test for `routee-auth` must not require
  `routee-activity` to be running.
- Integration tests that cross module boundaries should go in `routee-app`.

---

## 10. Future Architecture Direction

This project is designed to evolve without breaking module boundaries:

- **UseCase interfaces** are transportable — a UseCase can be exposed as REST, gRPC,
  or any other protocol by changing only the adapter layer.
- **Domain Events** are migratable — the event bus can be replaced with Kafka or
  RabbitMQ without changing the domain logic or the publishing side.
- **Module boundaries** are maintained with MSA extraction in mind —
  each module could become an independent service if needed.

**Adapters may change. Domain must not.**

Keep module coupling loose. Never introduce shortcuts that violate module boundaries.

---

## 11. Current Development Phase

The common infrastructure is complete. `routee-auth`, `routee-member`, and `routee-external` are
actively implemented, each with a full `api`/`internal` split.
`routee-activity` has its JPA entities and repositories in place but no `service`, `api.usecase`,
or `controller` layer yet — treat it as schema-first, not yet behavior-complete.
`routee-course` is a pure placeholder (`package-info.java` only).

**Do not**

- Add domain logic to modules that have not started implementation.
- Add dependencies not in the BOM without explicit confirmation.
- Commit secrets or environment-specific values in `application-*.yml`.
- Duplicate abstractions that already exist in `routee-common`.

**Conventions**

- Human-facing commit messages and comments: Korean.
- AI-facing architecture guidance: English.

---

## 12. AI Agent Expectations

When modifying existing code:

- Prefer extending existing abstractions over introducing new ones.
- Follow existing package and naming conventions in the module you are working in.
- Preserve module boundaries — do not add imports that cross `internal` boundaries.
- Avoid unnecessary refactoring outside the scope of the requested task.
- Keep changes focused — modify less rather than more when in doubt.

When generating new code:

- Place types in the correct layer and package according to these principles.
- Do not create new modules or cross-cutting abstractions without explicit instruction.
- Use existing common infrastructure (`ApiResponse`, `BaseException`, etc.) rather than
  creating alternatives.

---

## 13. Architecture Decision Priority

When implementation convenience conflicts with architecture principles,
**always choose architecture**.

Never violate module boundaries for the sake of shorter or simpler code.
A clean boundary that requires slightly more code is always preferable to a shortcut
that couples modules in ways that are hard to undo.

When uncertain, prefer consistency with existing modules over introducing a new pattern.

When in doubt, prefer:

- **clarity** over cleverness
- **loose coupling** over shared state
- **explicit boundaries** over implicit convenience

---

## 14. Architecture Philosophy

The project prioritizes, in order:

1. Explicit module boundaries
2. Loose coupling
3. Replaceable infrastructure
4. Business-oriented APIs
5. Long-term maintainability

Short-term implementation convenience should never compromise these goals.