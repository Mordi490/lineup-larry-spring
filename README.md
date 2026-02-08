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

## Important

Generate jOOQ sources before running dev, tests, or builds:

```bash
mise run generate-sources
```

Maven equivalent:

```bash
./mvnw generate-sources
```

## Run

Set datasource env vars (example):

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

## Tasks

```bash
mise run dev
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
