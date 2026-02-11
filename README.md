# Lineup Larry Backend

REST API built with Java + Spring Boot + jOOQ.

## Stack

- Java 21
- Spring Boot
- jOOQ
- Flyway
- PostgreSQL
- Testcontainers
- Maven (`./mvnw`)
- `mise` tasks

## Prerequisites

- `git`
- [`mise`](https://mise.jdx.dev/) installed
- Docker installed and running
- PostgreSQL (only if using local DB mode via `mise run dev`)

## Quick Start

```bash
git clone https://codeberg.org/Mordi/lineup-larry-spring.git
cd lineup-larry-spring
mise install
```

Then choose one run mode:

### Containerized DB (`dev-tc`) (recommended)

No datasource env vars are needed. Docker is required.

```bash
mise run dev-tc
```

### Local DB (`dev`)

Create a local Postgres database named `lineup_larry`, then set datasource env vars:

```bash
export SPRING_DATASOURCE_URL='jdbc:postgresql://localhost:5432/lineup_larry'
export SPRING_DATASOURCE_USERNAME='postgres'
export SPRING_DATASOURCE_PASSWORD='postgres'
```

Start app:

```bash
mise run dev
```

App runs on `http://localhost:9090`.

## Important

Most Maven tasks already trigger `generate-sources` through the Maven lifecycle. Run this only when you want to regenerate jOOQ classes directly:

```bash
mise run generate-sources
```

Maven equivalent:

```bash
./mvnw generate-sources
```

## Tasks

```bash
mise run dev
mise run dev-tc
mise run test
mise run clean-test
mise run clean
mise run build
mise run package
mise run install
mise run generate-sources
mise run ci
```

## API Docs

- Swagger UI: `http://localhost:9090/swagger-ui/index.html`
- OpenAPI JSON: `http://localhost:9090/v3/api-docs`
