# GitHub Deployment Guide

Make rnd-testing-hub publicly accessible via GitHub.

## What This Includes

✅ **GitHub Repository** - Public code hosting
✅ **GitHub Actions** - Automated CI/CD pipeline
✅ **GitHub Container Registry** - Docker image storage
✅ **GitHub Pages** - Documentation hosting
✅ **GitHub Releases** - Downloadable artifacts

## Step 1: Push to GitHub

### Initialize Git

```bash
cd /Users/viionascu/Projects/rnd-testing-hub

git init
git add .
git commit -m "Initial commit: rnd-testing-hub MVP"
```

### Create Repository

Go to https://github.com/new and create:
- **Repository name:** `rnd-testing-hub`
- **Public** (important!)
- Click **Create repository**

### Push Code

```bash
git remote add origin https://github.com/YOUR-USERNAME/rnd-testing-hub.git
git branch -M main
git push -u origin main
```

## Step 2: Enable GitHub Actions

GitHub Actions is **automatically enabled** for public repos.

**Verify:** Go to repo → **Actions** tab

The workflow in `.github/workflows/ci-cd.yml` will run on every push:

```
✅ Stage 1: Setup & Validate (Java 21, Gradle)
✅ Stage 2: Build (JAR artifact)
✅ Stage 3: Test (14 integration tests)
✅ Stage 4: Code Quality (architecture checks)
✅ Stage 5: Docker (build & push image)
```

## Step 3: Access Publicly

### JAR Download

After first push, artifact available at:

```
https://github.com/YOUR-USERNAME/rnd-testing-hub/actions
→ Latest workflow run → Artifacts → rnd-testing-hub-jar
```

**Download & Run:**

```bash
# Extract JAR
unzip rnd-testing-hub-jar.zip

# Start PostgreSQL
docker-compose up -d

# Run JAR
java -jar rnd-testing-hub-0.1.0.jar
```

### Docker Image

After first push, image pushed to GitHub Container Registry:

```bash
# Pull image
docker pull ghcr.io/YOUR-USERNAME/rnd-testing-hub:latest

# Run container
docker run -p 8080:8080 \
  -e DATABASE_URL=jdbc:postgresql://host:5432/db \
  -e DATABASE_USER=postgres \
  -e DATABASE_PASSWORD=pass \
  ghcr.io/YOUR-USERNAME/rnd-testing-hub:latest
```

### Test Reports

After each run:

```
https://github.com/YOUR-USERNAME/rnd-testing-hub/actions
→ Latest workflow run → Artifacts → test-results
```

## Step 4: Enable GitHub Pages

Documentation hosting (optional):

1. Settings → Pages
2. **Source:** Deploy from branch
3. **Branch:** `main`
4. **Folder:** `/ (root)` or `/docs`

**Your docs site:** `https://YOUR-USERNAME.github.io/rnd-testing-hub`

## Step 5: Create Release

Tag a version:

```bash
git tag -a v0.1.0 -m "MVP - Production Ready"
git push origin v0.1.0
```

GitHub automatically:
- Creates Release page
- Attaches JAR artifact
- Shows release notes

**Access:** `https://github.com/YOUR-USERNAME/rnd-testing-hub/releases`

## What's Publicly Accessible

### Repository
```
https://github.com/YOUR-USERNAME/rnd-testing-hub

Includes:
- Source code (25 Java files)
- Configuration files
- Documentation (6 guides)
- Sample data
- GitHub Actions workflow
```

### CI/CD Pipeline Status
```
https://github.com/YOUR-USERNAME/rnd-testing-hub/actions

Shows:
- Build logs
- Test results
- Artifacts
- Deployment status
```

### Documentation
```
https://YOUR-USERNAME.github.io/rnd-testing-hub

Includes:
- README
- Quick Start
- Architecture
- Metrics Definition
- API Reference
```

### Docker Image
```
ghcr.io/YOUR-USERNAME/rnd-testing-hub:latest

Publically available for:
- Pull
- Run locally
- Deploy to cloud
```

### Releases
```
https://github.com/YOUR-USERNAME/rnd-testing-hub/releases

Downloadable:
- JAR artifact
- Release notes
- Source code zip
```

## Run Locally from GitHub

**Option 1: Clone & Build**

```bash
git clone https://github.com/YOUR-USERNAME/rnd-testing-hub.git
cd rnd-testing-hub
docker-compose up -d
cd backend && gradle bootRun
```

**Option 2: Use Docker Image**

```bash
docker pull ghcr.io/YOUR-USERNAME/rnd-testing-hub:latest
docker-compose -f docker-compose.prod.yml up -d
```

**Option 3: Download JAR from Releases**

1. Go to Releases
2. Download `rnd-testing-hub-0.1.0.jar`
3. `java -jar rnd-testing-hub-0.1.0.jar`

## Monitor Pipeline

### View Pipeline Runs

```
https://github.com/YOUR-USERNAME/rnd-testing-hub/actions/workflows/ci-cd.yml
```

### Key Information

- **Build status:** ✅ or ❌
- **Test results:** 14/14 passed
- **Build time:** ~7 seconds
- **Artifacts:** JAR, Docker image, test reports

### Add Status Badge to README

```markdown
[![CI/CD](https://github.com/YOUR-USERNAME/rnd-testing-hub/actions/workflows/ci-cd.yml/badge.svg)](https://github.com/YOUR-USERNAME/rnd-testing-hub/actions/workflows/ci-cd.yml)
```

## Visibility & Discoverability

### Add Repository Topics

Settings → About → Topics:
```
testing, java, spring-boot, rest-api, test-generation, metrics, ci-cd
```

### Update Description

Settings → About → Description:
```
R&D Testing Best-Practices Aggregator - Generate tests, track metrics, access playbooks
```

### Add Links

Settings → About:
- **Website:** Link to deployed app
- **Discussions:** Enable for Q&A

## Team Access

### Share Repository Link

```
https://github.com/YOUR-USERNAME/rnd-testing-hub
```

Team members can:
- ✅ View code
- ✅ Review workflow status
- ✅ Download artifacts
- ✅ Pull Docker image
- ✅ Fork & contribute
- ✅ Open issues/discussions

### Collaborate

```bash
# Team member setup
git clone https://github.com/YOUR-USERNAME/rnd-testing-hub.git
cd rnd-testing-hub
docker-compose up -d
cd backend && gradle bootRun
```

## GitHub Container Registry (Docker Image)

### Make Image Public

1. Go to: `https://github.com/YOUR-USERNAME/settings/packages`
2. Select `rnd-testing-hub` image
3. Click **Change visibility**
4. Select **Public**
5. **Save**

### Access Image

```bash
# Anyone can now pull
docker pull ghcr.io/YOUR-USERNAME/rnd-testing-hub:latest
```

## Usage for Team

### View Tests & Metrics

```
https://github.com/YOUR-USERNAME/rnd-testing-hub/actions
→ Latest run → Test results artifact
```

### Get JAR

```
https://github.com/YOUR-USERNAME/rnd-testing-hub/releases/latest
→ Download rnd-testing-hub-0.1.0.jar
```

### Use Docker Image

```bash
docker pull ghcr.io/YOUR-USERNAME/rnd-testing-hub:latest
docker run -p 8080:8080 ghcr.io/YOUR-USERNAME/rnd-testing-hub:latest
```

### View Documentation

```
https://github.com/YOUR-USERNAME/rnd-testing-hub/blob/main/README.md
https://YOUR-USERNAME.github.io/rnd-testing-hub
```

## Update Workflow

Push changes and pipeline runs automatically:

```bash
git add .
git commit -m "Add new feature"
git push origin main

# GitHub Actions automatically:
# 1. Builds
# 2. Tests
# 3. Uploads artifacts
# 4. Builds Docker image
# 5. Pushes to registry
```

## Checklist

- [ ] Code pushed to GitHub
- [ ] Repository is **Public**
- [ ] GitHub Actions running
- [ ] All 14 tests passing
- [ ] Docker image built
- [ ] JAR artifact available
- [ ] GitHub Pages enabled (optional)
- [ ] Release created
- [ ] Repository topics added
- [ ] Description updated

## Verification

### Check Pipeline Status

```bash
# All stages passed
https://github.com/YOUR-USERNAME/rnd-testing-hub/actions
```

### Test from GitHub

```bash
# Clone & run
git clone https://github.com/YOUR-USERNAME/rnd-testing-hub.git
cd rnd-testing-hub
docker-compose up -d
cd backend && gradle bootRun

# Visit http://localhost:8080/health
```

### Verify Docker Image

```bash
docker pull ghcr.io/YOUR-USERNAME/rnd-testing-hub:latest
docker run -it ghcr.io/YOUR-USERNAME/rnd-testing-hub:latest
```

## Public Access Points

| Resource | URL |
|----------|-----|
| Repository | `https://github.com/YOUR-USERNAME/rnd-testing-hub` |
| Pipeline | `https://github.com/YOUR-USERNAME/rnd-testing-hub/actions` |
| Releases | `https://github.com/YOUR-USERNAME/rnd-testing-hub/releases` |
| Docker Image | `ghcr.io/YOUR-USERNAME/rnd-testing-hub:latest` |
| Documentation | `https://YOUR-USERNAME.github.io/rnd-testing-hub` |
| Issues | `https://github.com/YOUR-USERNAME/rnd-testing-hub/issues` |

## Share with Team

```
🚀 rnd-testing-hub is now on GitHub!

Repository: https://github.com/YOUR-USERNAME/rnd-testing-hub

Access:
📦 JAR: Download from Releases
🐳 Docker: ghcr.io/YOUR-USERNAME/rnd-testing-hub:latest
📚 Docs: README + guides
📈 Pipeline: https://github.com/.../actions

Quick Start:
git clone https://github.com/YOUR-USERNAME/rnd-testing-hub.git
docker-compose up -d && cd backend && gradle bootRun

Visit http://localhost:8080/health
```

---

**Everything is publicly accessible via GitHub!**

✅ Code visible
✅ CI/CD pipeline transparent
✅ Artifacts downloadable
✅ Docker image available
✅ Documentation hosted
