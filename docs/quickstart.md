# Quick Start Guide

Get rnd-testing-hub running in 5 minutes.

## Prerequisites

- Java 21+
- Docker (for PostgreSQL)
- curl or Postman (for API calls)

## 1. Start the Database

```bash
docker-compose up -d
```

Verify it's running:
```bash
docker-compose ps
```

## 2. Start the Application

```bash
gradle bootRun
```

Wait for: `Started RndTestingHubApplication in X seconds`

## 3. Verify Health

```bash
curl http://localhost:8080/health
```

Expected response:
```json
{
  "status": "UP",
  "application": "rnd-testing-hub",
  "version": "0.1.0"
}
```

## 4. Ingest Sample Data

```bash
bash tools/scripts/ingest-samples.sh
```

This will:
- Upload sample OpenAPI spec
- Upload sample JUnit report
- Display calculated metrics
- List best practices

## 5. View the Metrics

```bash
curl http://localhost:8080/api/metrics/summary?days=30
```

Example response:
```json
{
  "window_days": 30,
  "pass_rate": "75.00%",
  "failure_rate": "25.00%",
  "flaky_rate": "0.00%",
  "total_tests_executed": 8,
  "total_passed": 6,
  "total_failed": 2
}
```

## 6. View Best Practices

```bash
curl http://localhost:8080/api/practices
```

Search by tag:
```bash
curl "http://localhost:8080/api/practices?tag=functional"
```

Search by keyword:
```bash
curl "http://localhost:8080/api/practices?query=contract"
```

## 7. Generate REST Tests

Preview generated code:
```bash
curl -X POST \
  -H "Content-Type: application/json" \
  -d '{
    "baseUrl": "http://localhost:8081",
    "openApiSpecId": 1,
    "options": {"includeNegativeTests": true}
  }' \
  http://localhost:8080/api/generator/preview
```

Download runnable ZIP:
```bash
curl -X POST \
  -H "Content-Type: application/json" \
  -d '{
    "baseUrl": "http://localhost:8081",
    "openApiSpecId": 1,
    "options": {"includeNegativeTests": true}
  }' \
  http://localhost:8080/api/generator/restassured \
  -o tests.zip
```

Unzip and run:
```bash
unzip tests.zip
gradle test
```

## Next Steps

- Read [Architecture](./architecture.md) for design details
- Read [Metrics Definition](./metrics-definition.md) for KPI details
- Check [API Endpoints](#api-endpoints) below

## API Endpoints

### Health
- `GET /health` - Check API status

### OpenAPI Ingestion
- `POST /api/openapi/upload` - Upload OpenAPI spec
- `GET /api/openapi/{specId}` - Get spec details
- `GET /api/openapi/{specId}/endpoints` - List parsed endpoints

### JUnit Ingestion
- `POST /api/junit/upload` - Upload JUnit XML report
- `GET /api/junit/{suiteId}` - Get suite details
- `GET /api/junit/{suiteId}/cases` - List test cases

### Metrics
- `GET /api/metrics/summary?days=30` - Summary metrics
- `GET /api/metrics/trends?metric=passRate&period=30d` - Trends
- `GET /api/metrics/api-coverage/{specId}` - API coverage

### Best Practices
- `GET /api/practices` - List all practices
- `GET /api/practices?tag=functional` - Filter by tag
- `GET /api/practices?query=contract` - Search by keyword
- `GET /api/practices/{slug}` - Get single practice

### Test Generation
- `POST /api/generator/restassured` - Generate tests as ZIP
- `POST /api/generator/preview` - Preview generated code

## Troubleshooting

### Port 5432 already in use

Stop the existing container:
```bash
docker-compose down
docker-compose up -d
```

### Port 8080 already in use

Stop the application and try again, or use different port:
```bash
PORT=8081 gradle bootRun
```

### Database connection error

Ensure PostgreSQL is healthy:
```bash
docker-compose ps
docker-compose logs postgres
```

Recreate if needed:
```bash
docker-compose down -v
docker-compose up -d
```
