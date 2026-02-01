# How to Access rnd-testing-hub

Your application is **now publicly accessible via GitHub**. Here are all the ways you and your team can access it:

---

## Option 1: Download JAR from GitHub Actions ✅ (Recommended - No Installation)

### Quick Access (No Docker or Java build needed)

1. Go to your repository: [https://github.com/vionascu/rnd-testing-hub](https://github.com/vionascu/rnd-testing-hub)
2. Click **Actions** tab
3. Click the latest successful workflow run
4. Scroll to **Artifacts** section
5. Download: `rnd-testing-hub-jar` (containing the built JAR)
6. Extract the ZIP file locally
7. **Run the JAR:**

```bash
# You need PostgreSQL running first
# Option A: If you have PostgreSQL installed locally:
postgresql-server -D /path/to/db

# Option B: Using Docker only for database:
docker run -d -p 5432:5432 \
  -e POSTGRES_PASSWORD=postgres \
  -e POSTGRES_DB=rnd_testing_hub \
  postgres:17-alpine

# Then run the application:
java -jar rnd-testing-hub-0.1.0.jar
```

8. **Access the application:**
   - Health check: http://localhost:8080/health
   - Swagger docs: http://localhost:8080/swagger-ui.html

---

## Option 2: Use Docker Image from GitHub Container Registry

### Pull the Pre-built Docker Image

```bash
# Login to GitHub Container Registry (optional for public image)
docker login ghcr.io

# Pull the image
docker pull ghcr.io/vionascu/rnd-testing-hub:latest

# Run with environment variables
docker run -d -p 8080:8080 \
  -e DATABASE_URL=jdbc:postgresql://host.docker.internal:5432/rnd_testing_hub \
  -e DATABASE_USER=postgres \
  -e DATABASE_PASSWORD=postgres \
  ghcr.io/vionascu/rnd-testing-hub:latest
```

---

## Option 3: Clone & Build Locally

### For Development / Contributing

```bash
# Clone repository
git clone https://github.com/vionascu/rnd-testing-hub.git
cd rnd-testing-hub

# Start PostgreSQL
docker-compose up -d

# Build and run
cd backend
./gradlew bootRun

# Application runs at: http://localhost:8080
```

---

## Option 4: Full Docker Compose (Recommended for Teams)

### Complete Stack with Database

```bash
# Clone repository
git clone https://github.com/vionascu/rnd-testing-hub.git
cd rnd-testing-hub

# For development
docker-compose up -d
cd backend && gradle bootRun

# For production
docker-compose -f docker-compose.prod.yml up -d
```

Access at: `http://localhost:8080`

---

## Access Points & Features

### REST API Endpoints (Available Immediately)

Once running, access these endpoints:

| Endpoint | Method | Description |
|----------|--------|-------------|
| `/health` | GET | Health check |
| `/api/junit/upload` | POST | Upload JUnit XML reports |
| `/api/junit/{id}/cases` | GET | Get test cases from report |
| `/api/openapi/upload` | POST | Upload OpenAPI specs |
| `/api/openapi/{id}/endpoints` | GET | Get parsed endpoints |
| `/api/metrics/summary` | GET | Testing metrics (pass rate, flakiness, coverage) |
| `/api/metrics/trends` | GET | Metrics over time |
| `/api/metrics/api-coverage` | GET | API coverage metrics |
| `/api/generator/restassured` | POST | Generate RestAssured tests |
| `/api/practices` | GET | Best practices knowledge base |
| `/swagger-ui.html` | GET | Interactive API documentation |

### Sample Data Included

Automatically loaded on first run:

- **4 Best Practices** - Testing playbooks
- **Sample OpenAPI** - User API specification
- **Sample JUnit Report** - Test results

Then generate tests and view metrics!

---

## GitHub CI/CD Pipeline Status

### View Pipeline Runs

Every push triggers the pipeline:
- ✅ Setup & Validate
- ✅ Build Application
- ✅ Run Tests (14/14 passing)
- ✅ Code Quality
- ✅ Build Docker Image
- ✅ Docker image pushed to ghcr.io

**View at:** [GitHub Actions](https://github.com/vionascu/rnd-testing-hub/actions)

### Build Artifacts Available

After each successful build (available for 30 days):

1. **JAR Artifact**: `rnd-testing-hub-0.1.0.jar` (71 MB)
2. **Test Results**: HTML report of all 14 tests
3. **Docker Image**: Published to `ghcr.io/vionascu/rnd-testing-hub:latest`

---

## Quickest Way to Get Started (< 5 minutes)

### If you have Docker installed:

```bash
# 1. Download JAR from GitHub Actions (or let Docker handle it)
# 2. Start database
docker run -d -p 5432:5432 \
  -e POSTGRES_PASSWORD=postgres \
  -e POSTGRES_DB=rnd_testing_hub \
  postgres:17-alpine

# 3. Run the application
java -jar rnd-testing-hub-0.1.0.jar
```

### Then:

```bash
# 4. Test it
curl http://localhost:8080/health

# 5. View Swagger docs
open http://localhost:8080/swagger-ui.html

# 6. Ingest sample data
bash tools/scripts/ingest-samples.sh

# 7. View metrics
curl http://localhost:8080/api/metrics/summary
```

---

## Sharing with Your Team

### Share This Link

```
https://github.com/vionascu/rnd-testing-hub
```

**Your team can:**
- ✅ View source code
- ✅ Check pipeline status (Actions tab)
- ✅ Download JAR artifact
- ✅ Pull Docker image
- ✅ Read documentation
- ✅ See test results
- ✅ Clone & run locally
- ✅ No authentication needed (public repo!)

---

## Requirements for Running

### Minimum Requirements

1. **Java 21** (for JAR execution)
   - Check: `java -version`
   - Download: [Oracle Java 21](https://www.oracle.com/java/technologies/downloads/#java21)

2. **PostgreSQL 17** or Docker
   - Install locally: [PostgreSQL.org](https://www.postgresql.org/download/)
   - Or use Docker: `docker run postgres:17-alpine`

### Optional

- **Docker** - For containerized execution
- **Gradle 9.3.1** - Only needed for building from source (provided in repo)
- **Git** - For cloning repository

---

## Troubleshooting

### "Cannot find JAR file"
- Go to GitHub Actions → Latest run → Artifacts → Download `rnd-testing-hub-jar`
- Extract the ZIP file
- JAR is inside

### "Connection refused to PostgreSQL"
- Ensure PostgreSQL is running on port 5432
- Check: `psql -U postgres -d rnd_testing_hub`
- If not installed, use Docker: `docker run -d -p 5432:5432 postgres:17-alpine`

### "Java command not found"
- Install Java 21
- Add to PATH
- Check: `java -version` (should show 21.x)

### "Port 8080 already in use"
- Kill the process: `lsof -ti:8080 | xargs kill -9`
- Or change port: `java -jar rnd-testing-hub-0.1.0.jar --server.port=8081`

---

## What's Next?

1. **Test the API:**
   ```bash
   curl http://localhost:8080/api/metrics/summary | jq
   ```

2. **Upload your own test data:**
   ```bash
   curl -X POST http://localhost:8080/api/junit/upload \
     -F "file=@your-junit-report.xml"
   ```

3. **Generate tests from OpenAPI:**
   ```bash
   curl -X POST http://localhost:8080/api/generator/restassured \
     -H "Content-Type: application/json" \
     -d '{"specId": 1, "packageName": "com.example"}'
   ```

4. **View full API documentation:**
   - Open: http://localhost:8080/swagger-ui.html

---

## Status

- **Repository**: [Public on GitHub](https://github.com/vionascu/rnd-testing-hub)
- **Code**: 25 Java files, fully typed
- **Tests**: 14/14 passing (100%)
- **Build**: Automated via GitHub Actions
- **Docker**: Published to GitHub Container Registry
- **Documentation**: Complete with guides and samples
- **Ready to use**: ✅ Yes!

---

**Any questions?** Check [GitHub Issues](https://github.com/vionascu/rnd-testing-hub/issues) or review the [README.md](README.md)
