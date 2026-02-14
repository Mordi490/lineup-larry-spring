# Repository Guidelines

## Project Structure & Module Organization
This is a Spring Boot backend (Java 21) with feature-first packages under `src/main/java/dev/mordi/lineuplarry/lineup_larry_backend/`:
- `user/`, `lineup/`, `like/`: controllers, services, repositories, models
- `shared/`: cross-cutting concerns (global exceptions, dev config)
- `config/`: framework config (for example OpenAPI)
- `enums/`, `logging/`: shared domain/support code

Configuration and schema live in:
- `src/main/resources/application.properties`
- `src/main/resources/db/migration/` (Flyway SQL migrations)

Tests mirror app packages in `src/test/java/...`, with SQL fixtures in `src/test/resources/test-data.sql`. Generated jOOQ code is placed in `target/generated-sources/jooq`.

## Build, Test, and Development Commands
Use `mise` tasks (preferred):
- `mise run dev-tc`: run app with Testcontainers PostgreSQL (recommended local mode)
- `mise run dev`: run app against local PostgreSQL
- `mise run test`: run test suite
- `mise run clean-test`: clean + run tests
- `mise run build`: clean verify build
- `mise run ci`: CI-style `clean generate-sources verify`
- `mise run generate-sources`: regenerate jOOQ sources only

Maven equivalents are available via `./mvnw ...`.

## Coding Style & Naming Conventions
- Follow existing Java style (Google Java Format-like output, 2-space indentation, concise methods).
- Keep packages lowercase; class/record names `PascalCase`; methods/fields `lowerCamelCase`.
- Preserve suffix patterns: `*Controller`, `*Service`, `*Repository`, `*IntegrationTest`, `*RepositoryTest`.
- Prefer clear, domain-oriented naming (`LineupWithAuthorDTO`, `InvalidUserException`, etc.).

## Testing Guidelines
- Stack: JUnit 5, Spring Boot Test, Testcontainers.
- Add/adjust tests for any behavior change, including validation and error paths.
- Keep test scope explicit: controller/service/repository/integration.
- Run `mise run test` before opening a PR; use `mise run ci` for pre-merge confidence.

## Commit & Pull Request Guidelines
Current history favors short, imperative commit messages (for example: `update spring boot`, `added mise`). No strict conventional-commit format is enforced.

For new work, keep commits focused and descriptive:
- Suggested format: `<area>: <imperative summary>` (example: `lineup: validate title length`)

PRs should include:
- what changed and why
- linked issue/task (if any)
- commands run locally (for example `mise run test`)
- notes for API/config changes (update `README.md` when relevant)

## Security & Configuration Tips
- Do not commit secrets; prefer env vars for datasource credentials.
- Treat `dev` profile settings as local-only defaults.
- For production deployments, disable Swagger UI/docs unless intentionally exposed.
