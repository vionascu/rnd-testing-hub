# Architecture

## Overview

rnd-testing-hub is a modular monolith designed for R&D teams to aggregate testing practices and generate test code.

## Layered Architecture

```
┌─────────────────────────────────────┐
│        REST Controllers              │
│   (web adapters, request handling)   │
├─────────────────────────────────────┤
│     Application Services             │
│   (business logic, use cases)        │
├─────────────────────────────────────┤
│      Domain Entities                 │
│   (TestSuiteRun, ApiSpec, etc)      │
├─────────────────────────────────────┤
│  Adapters & Infrastructure           │
│  (repos, parsers, generators)       │
├─────────────────────────────────────┤
│      Database (PostgreSQL)           │
│        & Persistence                 │
└─────────────────────────────────────┘
```

## Module Organization

### `domain/`
Core business entities:
- `TestSuiteRun` - JUnit test execution result
- `TestCaseRun` - Individual test result
- `ApiSpec` - OpenAPI specification
- `ApiEndpoint` - Parsed REST endpoint
- `Practice` - Best practice article

**Pattern:** Domain models have no dependencies on external libraries (clean domain).

### `application/`
Business services implementing use cases:
- `JunitIngestionService` - Parse and persist JUnit XML
- `OpenApiIngestionService` - Parse and persist OpenAPI specs
- `MetricsService` - Calculate testing KPIs
- `PracticeService` - Manage practice articles

**Pattern:** Services coordinate domain logic and repository access.

### `adapters/`

#### `adapters/web/`
REST controllers exposing APIs:
- `JunitController` - JUnit upload & query
- `OpenApiController` - OpenAPI upload & query
- `MetricsController` - Metrics endpoints
- `GeneratorController` - Test generation
- `PracticeController` - Practices endpoint
- `HealthController` - Health check

**Pattern:** Controllers translate HTTP requests to service calls; minimal logic.

#### `adapters/persistence/`
JPA repositories for data access:
- `TestSuiteRunRepository`
- `TestCaseRunRepository`
- `ApiSpecRepository`
- `ApiEndpointRepository`
- `PracticeRepository`

**Pattern:** Repositories abstract database interactions.

### `infrastructure/`
Technical utilities:
- `JunitXmlParser` - Parse JUnit XML files
- `OpenApiParser` - Parse OpenAPI specs (uses Swagger Parser)
- `RestAssuredTestGenerator` - Generate Java test code
- `Initializer` - Load default practices on startup

**Pattern:** Infrastructure components are frameworks and parsers.

## Data Model

```sql
-- Test Execution
test_suite_run (id, suite_name, status, passed, failed, skipped, duration_ms, timestamp)
test_case_run (id, suite_run_id, test_name, status, duration_ms, error_message, timestamp)

-- API Specifications
api_spec (id, name, title, version, spec_content, spec_format)
api_endpoint (id, spec_id, method, path, summary)

-- Knowledge Base
practice (id, slug, title, content, tags)
```

## Key Flows

### 1. JUnit Report Ingestion

```
User uploads JUnit XML
  ↓
JunitController.uploadJunitReport()
  ↓
JunitIngestionService.ingestJunitReport()
  ↓
JunitXmlParser.parse()
  ↓
Save TestSuiteRun + TestCaseRun entities
  ↓
Return suite IDs
```

### 2. Metrics Calculation

```
Request for metrics (past 30 days)
  ↓
MetricsService.calculateSummaryMetrics()
  ↓
Query TestSuiteRun(timestamp >= 30d ago)
  ↓
Calculate pass_rate, failure_rate, flaky_rate
  ↓
Return metrics map as JSON
```

### 3. Test Code Generation

```
User requests test generation for OpenAPI spec
  ↓
GeneratorController receives request
  ↓
OpenApiIngestionService.getEndpointsForSpec()
  ↓
RestAssuredTestGenerator.generateTestClass()
  ↓
Generate .java, build.gradle.kts, README
  ↓
Package as ZIP file
  ↓
Return ZIP for download
```

## Deployment

### Local Development

```bash
# Terminal 1: Database
docker-compose up

# Terminal 2: Application
gradle bootRun

# Application runs at http://localhost:8080
```

### Production (Stub)

Environment variables:
- `DATABASE_URL` - PostgreSQL connection string
- `DATABASE_USER` - DB user
- `DATABASE_PASSWORD` - DB password

Build and run:
```bash
gradle build
java -jar backend/build/libs/rnd-testing-hub-0.1.0.jar
```

## Testing Strategy

### Unit Tests
- Test parsers (JunitXmlParser, OpenApiParser)
- Test metrics calculations
- Located in `src/test/java`

### Integration Tests
- Test full flows (ingest → persist → query)
- Use H2 in-memory database
- Use `@DirtiesContext` for isolation
- Test database interactions via repositories

## Scalability Considerations

### Current Limitations (MVP)
- Single-threaded API processing
- All metrics computed on-demand
- No caching
- No async job queue

### Future Improvements
- Background async jobs for large imports
- Cache metrics with TTL
- Archive old test results
- Full-text search on practices
- Multi-tenant support
- API rate limiting

## Design Principles

1. **Modularity**: Clear separation by package; easy to understand each part
2. **Testability**: Interfaces and dependency injection; easy to test
3. **Clarity**: Simple code over clever code; explicit over implicit
4. **Minimalism**: Only features needed for MVP
5. **No Magic**: Explicit configurations; no hidden behaviors
