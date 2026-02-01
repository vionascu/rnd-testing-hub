# Commands Reference

Quick reference for common operations on rnd-testing-hub.

## Development

### Build & Test
```bash
# Run tests
gradle test

# Build jar
gradle build

# Build without tests
gradle build -x test

# Run application
gradle bootRun

# Clean all
gradle clean
```

### Database

```bash
# Start PostgreSQL
docker-compose up -d

# Stop PostgreSQL
docker-compose down

# View logs
docker-compose logs postgres

# Recreate (reset data)
docker-compose down -v && docker-compose up -d
```

## API Usage

### Health Check
```bash
curl http://localhost:8080/health
```

### OpenAPI Operations

```bash
# Upload OpenAPI spec
curl -F "file=@tools/sample-openapi/openapi.yaml" \
  http://localhost:8080/api/openapi/upload

# List endpoints for a spec
curl http://localhost:8080/api/openapi/1/endpoints
```

### JUnit Operations

```bash
# Upload JUnit report
curl -F "file=@tools/sample-reports/junit.xml" \
  http://localhost:8080/api/junit/upload

# Get test cases for a suite
curl http://localhost:8080/api/junit/1/cases
```

### Metrics

```bash
# Get summary (last 30 days)
curl http://localhost:8080/api/metrics/summary?days=30

# Get summary (last 7 days)
curl http://localhost:8080/api/metrics/summary?days=7

# Get trends
curl "http://localhost:8080/api/metrics/trends?metric=passRate&period=30d"

# API coverage for spec
curl http://localhost:8080/api/metrics/api-coverage/1
```

### Best Practices

```bash
# List all practices
curl http://localhost:8080/api/practices

# Filter by tag
curl "http://localhost:8080/api/practices?tag=functional"
curl "http://localhost:8080/api/practices?tag=flaky"

# Search by keyword
curl "http://localhost:8080/api/practices?query=contract"
curl "http://localhost:8080/api/practices?query=ci"

# Get single practice
curl http://localhost:8080/api/practices/flaky-tests-playbook
```

### Test Generation

```bash
# Preview generated code
curl -X POST \
  -H "Content-Type: application/json" \
  -d '{
    "baseUrl": "http://localhost:8081",
    "openApiSpecId": 1,
    "options": {"includeNegativeTests": true}
  }' \
  http://localhost:8080/api/generator/preview | jq '.code'

# Download tests as ZIP
curl -X POST \
  -H "Content-Type: application/json" \
  -d '{
    "baseUrl": "http://localhost:8081",
    "openApiSpecId": 1,
    "options": {
      "includeNegativeTests": true,
      "includeContractValidation": false
    }
  }' \
  http://localhost:8080/api/generator/restassured \
  -o tests.zip

# Unzip and run
unzip tests.zip
cd /path/to/tests
gradle test
```

## Automated Ingestion

```bash
# Ingest sample data (all-in-one)
bash tools/scripts/ingest-samples.sh

# This script does:
# 1. Checks API health
# 2. Uploads OpenAPI spec
# 3. Uploads JUnit report
# 4. Displays metrics summary
# 5. Lists practices
# 6. Shows API endpoints
```

## Production Deployment

### Build JAR
```bash
gradle build
```

### Run JAR with PostgreSQL
```bash
java -jar backend/build/libs/rnd-testing-hub-0.1.0.jar \
  --spring.datasource.url=jdbc:postgresql://postgres-host:5432/rnd_testing_hub \
  --spring.datasource.username=postgres \
  --spring.datasource.password=secure_password \
  --server.port=8080
```

### Run with Docker (future)
```bash
# Build image
docker build -t rnd-testing-hub .

# Run container
docker run -p 8080:8080 \
  -e DATABASE_URL=jdbc:postgresql://postgres:5432/rnd_testing_hub \
  -e DATABASE_USER=postgres \
  -e DATABASE_PASSWORD=password \
  rnd-testing-hub
```

## Debugging

### View Logs
```bash
# Application logs (while running)
gradle bootRun

# See test output
gradle test --info
```

### Database Access

```bash
# Connect to PostgreSQL
psql -h localhost -U postgres -d rnd_testing_hub

# View tables
\dt

# Query test runs
SELECT * FROM test_suite_run;
SELECT * FROM test_case_run;
SELECT * FROM api_spec;
SELECT * FROM api_endpoint;
SELECT * FROM practice;
```

### API Response Examples

#### Metrics Summary
```json
{
  "window_days": 30,
  "pass_rate": "75.00%",
  "failure_rate": "25.00%",
  "flaky_rate": "0.00%",
  "total_test_runs": 2,
  "total_run_suites": 2,
  "total_tests_executed": 8,
  "total_passed": 6,
  "total_failed": 2,
  "timestamp": "2026-02-01T18:40:00"
}
```

#### API Coverage
```json
{
  "spec_id": 1,
  "total_endpoints": 6,
  "tested_endpoints": 2,
  "coverage": "33.33%",
  "coverage_decimal": 0.333
}
```

#### Trends
```json
{
  "metric": "passRate",
  "period": "30d",
  "trend": {
    "2026-02-01": 0.75,
    "2026-02-02": 0.80,
    "2026-02-03": 0.82
  },
  "data_points": 3
}
```

#### Practice
```json
{
  "id": 1,
  "slug": "functional-rest-testing",
  "title": "Functional Testing for REST APIs",
  "content": "# Functional Testing for REST APIs\n\n...",
  "tags": "functional,rest,testing",
  "createdAt": "2026-02-01T18:30:00",
  "updatedAt": "2026-02-01T18:30:00"
}
```

## Useful jq Filters

```bash
# Extract pass rate from metrics
curl http://localhost:8080/api/metrics/summary | jq '.pass_rate'

# List all practice titles
curl http://localhost:8080/api/practices | jq '.practices[].title'

# Get coverage percentage
curl http://localhost:8080/api/metrics/api-coverage/1 | jq '.coverage'

# Extract endpoint list
curl http://localhost:8080/api/openapi/1/endpoints | \
  jq '.endpoints[] | "\(.method) \(.path)"'

# Parse test results
curl http://localhost:8080/api/junit/1/cases | \
  jq '.cases[] | {name: .testName, status: .status}'
```

## Troubleshooting

### API not responding
```bash
# Check health
curl http://localhost:8080/health

# Check if running
pgrep -f "gradle bootRun"

# Check port in use
lsof -i :8080
```

### Database connection error
```bash
# Check PostgreSQL running
docker-compose ps

# Check logs
docker-compose logs postgres

# Restart DB
docker-compose restart postgres
```

### Gradle build failure
```bash
# Clean cache
rm -rf ~/.gradle
gradle clean

# Rebuild
gradle build
```

## Performance Tips

- **Metrics:** For large datasets, filter by shorter time window (7d instead of 90d)
- **Search:** Use specific tags instead of full-text search when possible
- **Generation:** For large OpenAPI specs (100+ endpoints), generation may take a few seconds
- **Tests:** H2 in-memory database is ~10x faster than PostgreSQL for testing

## Useful Links

- [OpenAPI Spec](http://localhost:8080/api/openapi) - Your uploaded specs
- [Metrics Summary](http://localhost:8080/api/metrics/summary?days=30) - Current health
- [Practices](http://localhost:8080/api/practices) - Best practices library
- [Health Check](http://localhost:8080/health) - API status
