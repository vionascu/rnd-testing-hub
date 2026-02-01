# rnd-testing-hub

**R&D Testing Best-Practices Aggregator & Metrics Engine**

An MVP Java application that helps R&D teams and engineers:
- **Aggregate** testing best practices and playbooks
- **Compute** testing-focused KPIs (pass rate, flakiness, coverage, TTF)
- **Generate** production-ready REST API tests from OpenAPI specs
- **Track** testing health over time

*"What's our testing health and how do we improve it?"*

## Features

✅ **Ingest Testing Data**
- Upload JUnit XML reports (test results)
- Upload OpenAPI specs (REST endpoints)
- Parse and persist automatically

✅ **Calculate Testing Metrics**
- Pass rate, failure rate, flakiness detection
- API endpoint coverage calculation
- Time-to-feedback (MVP: ingest time)
- Trends over 7d/30d/90d

✅ **Best Practices Knowledge Base**
- 4 pre-loaded testing playbooks (functional, contract, flaky, CI gates)
- Search by tag or keyword
- Markdown-formatted guidance

✅ **Generate Automated Tests**
- Generate production-ready RestAssured + JUnit tests from OpenAPI
- Support for happy path + negative test cases
- Download as runnable Gradle project

## Quick Start

**5-minute setup:**

```bash
# 1. Start database
docker-compose up -d

# 2. Start app
gradle bootRun

# 3. Ingest samples
bash tools/scripts/ingest-samples.sh

# 4. View metrics
curl http://localhost:8080/api/metrics/summary?days=30

# 5. Download generated tests
curl -X POST -H 'Content-Type: application/json' \
  -d '{
    "baseUrl": "http://localhost:8081",
    "openApiSpecId": 1,
    "options": {"includeNegativeTests": true}
  }' \
  http://localhost:8080/api/generator/restassured -o tests.zip
```

See [Quickstart](./docs/quickstart.md) for detailed instructions.

## API Endpoints

### Ingestion
- `POST /api/openapi/upload` - Upload OpenAPI spec
- `POST /api/junit/upload` - Upload JUnit XML report

### Metrics
- `GET /api/metrics/summary?days=30` - Summary metrics
- `GET /api/metrics/trends?metric=passRate&period=30d` - Trends
- `GET /api/metrics/api-coverage/{specId}` - Coverage

### Best Practices
- `GET /api/practices` - List all
- `GET /api/practices?tag=functional` - Filter by tag
- `GET /api/practices?query=contract` - Search
- `GET /api/practices/{slug}` - Get single

### Test Generation
- `POST /api/generator/restassured` - Generate tests as ZIP
- `POST /api/generator/preview` - Preview code

## Documentation

- [Quickstart](./docs/quickstart.md) - Get started in 5 minutes
- [Architecture](./docs/architecture.md) - Design & module overview
- [Metrics](./docs/metrics-definition.md) - KPI definitions & trends

## Development

### Prerequisites
- Java 21+
- Gradle 9.x+
- Docker (PostgreSQL)

### Run Tests
```bash
gradle test
```

### Build
```bash
gradle build -x test
```

### Project Structure
```
rnd-testing-hub/
├── backend/
│   ├── src/main/java/com/rnd/testinghub/
│   │   ├── domain/              # Entities
│   │   ├── application/         # Services
│   │   ├── adapters/            # Controllers & repos
│   │   └── infrastructure/      # Parsers & generators
│   ├── src/test/java/
│   └── build.gradle.kts
├── tools/
│   ├── sample-openapi/openapi.yaml
│   ├── sample-reports/junit.xml
│   └── scripts/ingest-samples.sh
├── docs/
│   ├── quickstart.md
│   ├── architecture.md
│   └── metrics-definition.md
└── docker-compose.yml
```

## Technology Stack

- **Language:** Java 21
- **Framework:** Spring Boot 3.4
- **Build:** Gradle (Kotlin DSL)
- **Database:** PostgreSQL + Flyway migrations
- **Testing:** JUnit 5, AssertJ, Testcontainers
- **Parsing:** Swagger Parser (OpenAPI), DOM (JUnit XML)
- **Code Generation:** Custom RestAssured generator

## Sample Scenario

```bash
# 1. Upload OpenAPI spec with 6 endpoints
curl -F "file=@tools/sample-openapi/openapi.yaml" \
  http://localhost:8080/api/openapi/upload

# 2. Upload JUnit report (8 tests, 2 failed)
curl -F "file=@tools/sample-reports/junit.xml" \
  http://localhost:8080/api/junit/upload

# 3. Check metrics
curl http://localhost:8080/api/metrics/summary
# Response: pass_rate=75%, flaky_rate=0%, coverage=33% (2/6 endpoints)

# 4. Read best practice on fixing flaky tests
curl http://localhost:8080/api/practices/flaky-tests-playbook

# 5. Generate tests for untested endpoints
curl -X POST -H 'Content-Type: application/json' \
  -d '{"baseUrl":"http://api.local", "openApiSpecId":1, "options":{}}' \
  http://localhost:8080/api/generator/restassured > tests.zip

# 6. Unzip and run
unzip tests.zip && gradle test
```

## Status

**MVP Completion:** 85% (Step 0-6 complete, Step 7 in progress)

Implemented:
- ✅ Step 0: Skeleton + Docker + Flyway
- ✅ Step 1: OpenAPI ingestion
- ✅ Step 2: JUnit XML ingestion
- ✅ Step 3: Metrics engine
- ✅ Step 4: Best practices KB
- ✅ Step 5-6: Test generator + ZIP download
- 🔄 Step 7: Documentation & verification

## Known Limitations (MVP)

- Lab wizard UI not implemented
- JaCoCo coverage reporting disabled (Java 25 incompatibility)
- No async job processing (metrics computed on-demand)
- TTF metric is MVP placeholder (ingest time, not CI time)
- Flakiness detection is heuristic-based
- No authentication/authorization
- Single-process only

## Next Steps

Production readiness would require:
1. Add authentication (OAuth2, JWT)
2. Implement async job queue (RabbitMQ)
3. Add metrics caching and TTL
4. Pagination for large result sets
5. Full-text search on practices
6. Lab wizard UI (React/Vue frontend)
7. CI/CD integration templates (GitLab, GitHub Actions)
8. Monitoring & alerting
9. API rate limiting
