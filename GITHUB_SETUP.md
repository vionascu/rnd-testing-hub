# GitHub Setup & Public Deployment

Complete guide to push rnd-testing-hub to GitHub and make it publicly accessible.

## Step 1: Create GitHub Repository

### Option A: Create on GitHub UI

1. Go to https://github.com/new
2. **Repository name:** `rnd-testing-hub`
3. **Description:** "R&D Testing Best-Practices Aggregator & Test Generator"
4. **Public** (for public accessibility)
5. Click **Create repository**

### Option B: Use GitHub CLI

```bash
gh repo create rnd-testing-hub \
  --public \
  --description "R&D Testing Best-Practices Aggregator & Test Generator" \
  --source=. \
  --remote=origin \
  --push
```

## Step 2: Initialize Git & Push Code

```bash
cd /Users/viionascu/Projects/rnd-testing-hub

# Initialize git repo
git init

# Add all files
git add .

# Commit
git commit -m "Initial commit: rnd-testing-hub MVP

- OpenAPI ingestion & parsing
- JUnit XML ingestion & parsing
- Metrics engine (5 KPIs)
- Best practices knowledge base
- REST test generator (RestAssured)
- GitHub Actions CI/CD pipeline
- Docker support
- 14/14 tests passing"

# Add remote
git remote add origin https://github.com/YOUR-USERNAME/rnd-testing-hub.git

# Push to main branch
git branch -M main
git push -u origin main
```

## Step 3: Enable GitHub Actions

GitHub Actions is automatically enabled for public repos.

**Verify:**
1. Go to https://github.com/YOUR-USERNAME/rnd-testing-hub
2. Click **Actions** tab
3. Should see workflow running: `CI/CD Pipeline`

## Step 4: Enable Container Registry

1. Go to repo **Settings → Code and automation → Actions → General**
2. Under "Workflow permissions":
   - ✅ Read and write permissions
   - ✅ Allow GitHub Actions to create and approve pull requests
3. Save

This allows the workflow to push Docker images.

## Step 5: Create GitHub Secrets (for Deployment)

If using Render auto-deployment:

1. **Settings → Secrets and variables → Actions**
2. Click **New repository secret**
3. Add: `RENDER_DEPLOY_HOOK_URL`
4. Value: (Get from Render deploy settings)

## Step 6: Configure GitHub Pages (Optional - for Documentation)

1. **Settings → Pages**
2. **Source:** Deploy from a branch
3. **Branch:** `main`
4. **Folder:** `/docs` (if using GitHub Pages)
5. Save

Documentation will be available at: `https://YOUR-USERNAME.github.io/rnd-testing-hub`

## Step 7: Public Accessibility - Choose Deployment Option

### Option A: GitHub Container Registry + Render

**Best for:** Easy cloud deployment with GitHub Actions integration

1. **Push Docker image to GitHub Container Registry:**
   - Automatic via GitHub Actions on push to `main`
   - Image: `ghcr.io/YOUR-USERNAME/rnd-testing-hub:latest`

2. **Deploy on Render:**
   - Go to https://render.com
   - New Web Service
   - Connect GitHub repo
   - Deploy from Docker image: `ghcr.io/YOUR-USERNAME/rnd-testing-hub:latest`
   - **Result:** Public URL like `https://rnd-testing-hub.onrender.com`

### Option B: Direct Docker Compose on VPS

1. SSH to VPS
2. Clone repo:
   ```bash
   git clone https://github.com/YOUR-USERNAME/rnd-testing-hub.git
   cd rnd-testing-hub
   ```
3. Deploy:
   ```bash
   docker-compose up -d
   ```
4. Access: `http://YOUR-VPS-IP:8080`

### Option C: GitHub Codespaces (Development Only)

Open in browser:
```
https://github.com/codespaces/new?repo=YOUR-USERNAME/rnd-testing-hub
```

Then:
```bash
docker-compose up -d
cd backend && gradle bootRun
```

Codespace provides a preview URL (public for 30 days).

## Step 8: Monitor CI/CD Pipeline

### View Pipeline Runs

```
https://github.com/YOUR-USERNAME/rnd-testing-hub/actions
```

### Pipeline Stages (automatically run on each push):

1. **Setup & Validate** - Java 21, Gradle check
2. **Build** - Compile & JAR artifact
3. **Test** - Run 14 integration tests
4. **Code Quality** - Architecture checks
5. **Docker** - Build & push image
6. **Deploy** - Trigger cloud deployment (if configured)
7. **Summary** - Report pipeline status

### Check Build Status

Add badge to README:

```markdown
[![CI/CD Pipeline](https://github.com/YOUR-USERNAME/rnd-testing-hub/actions/workflows/ci-cd.yml/badge.svg)](https://github.com/YOUR-USERNAME/rnd-testing-hub/actions/workflows/ci-cd.yml)
```

## Step 9: Make It Discoverable

### Add Topics

1. **Settings → About**
2. Add topics (comma-separated):
   ```
   testing, java, spring-boot, rest-api, test-generation, metrics
   ```

### Add Links

1. **Settings → About**
2. **Website:** URL of deployed app (e.g., https://rnd-testing-hub.onrender.com)
3. **Docs:** Link to documentation

## Step 10: Create Releases

Tag and release:

```bash
# Create tag
git tag -a v0.1.0 -m "Release v0.1.0 - MVP Complete"

# Push tag
git push origin v0.1.0
```

GitHub will:
1. Create Release page
2. Attach JAR as release asset
3. Show release notes

Access at: `https://github.com/YOUR-USERNAME/rnd-testing-hub/releases`

## Public URL Examples

### After Deployment

**Render:**
```
https://rnd-testing-hub.onrender.com
```

**Railway:**
```
https://rnd-testing-hub-production.up.railway.app
```

**Fly.io:**
```
https://rnd-testing-hub.fly.dev
```

**GitHub Pages (docs):**
```
https://YOUR-USERNAME.github.io/rnd-testing-hub
```

## Share Application

Once deployed, share the public URL:

```
🚀 rnd-testing-hub is live!

Repository: https://github.com/YOUR-USERNAME/rnd-testing-hub
Application: https://rnd-testing-hub.onrender.com

Features:
✅ Upload OpenAPI specs & JUnit reports
✅ View testing metrics & trends
✅ Access best practices playbooks
✅ Generate automated REST tests

Try it now!
```

## CI/CD Pipeline Flow

```
Code Push to main
    ↓
GitHub Actions Triggered
    ↓
├─ Setup & Validate
├─ Build JAR
├─ Run 14 Tests
├─ Quality Checks
├─ Build Docker Image
├─ Push to ghcr.io
└─ Deploy (if configured)
    ↓
✅ Application Updated

Visit: https://rnd-testing-hub.onrender.com
```

## Troubleshooting

### GitHub Actions Not Running

1. Repo must be **Public** (for Actions to run without authentication)
2. Workflow file in `.github/workflows/ci-cd.yml`
3. Check **Actions** tab for error logs

### Docker Push Failed

1. GitHub Container Registry login may be needed
2. Token permissions: ensure GITHUB_TOKEN has `packages: write`
3. Check in `Actions → All workflows → Latest run`

### Deployment Didn't Trigger

1. Push must be to `main` branch (see workflow condition)
2. Render webhook must be configured (see DEPLOYMENT.md)
3. Check deployment logs on Render dashboard

## Verification Checklist

- [ ] Code pushed to GitHub
- [ ] Repository is **Public**
- [ ] GitHub Actions running (Actions tab)
- [ ] All 14 tests passing
- [ ] Docker image built
- [ ] Cloud deployment configured
- [ ] Public URL accessible
- [ ] Health check returns 200: `curl https://YOUR-URL/health`
- [ ] Sample data ingested
- [ ] Metrics endpoint working

## Quick Reference

### URLs

| Page | URL |
|------|-----|
| Repository | `https://github.com/YOUR-USERNAME/rnd-testing-hub` |
| Actions Pipeline | `https://github.com/YOUR-USERNAME/rnd-testing-hub/actions` |
| Releases | `https://github.com/YOUR-USERNAME/rnd-testing-hub/releases` |
| Issues | `https://github.com/YOUR-USERNAME/rnd-testing-hub/issues` |
| Docker Image | `ghcr.io/YOUR-USERNAME/rnd-testing-hub:latest` |
| Live App | `https://rnd-testing-hub.onrender.com` (after deploy) |

### Commands

```bash
# Clone
git clone https://github.com/YOUR-USERNAME/rnd-testing-hub.git

# View CI/CD logs
gh run view

# View deployment
gh release view v0.1.0

# Check repo health
gh repo view YOUR-USERNAME/rnd-testing-hub
```

## Next Steps

1. ✅ Create GitHub repo (this guide)
2. ✅ Push code
3. ✅ Enable Actions
4. ✅ Deploy to cloud (DEPLOYMENT.md)
5. ✅ Share public URL
6. 🎯 Team starts using it!

---

**Status:** GitHub setup complete & ready for deployment

**Questions?** See [DEPLOYMENT.md](DEPLOYMENT.md) or GitHub documentation
