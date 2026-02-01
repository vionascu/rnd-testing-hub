# Deployment Guide

How to deploy rnd-testing-hub to the cloud and make it publicly accessible.

## CI/CD Pipeline

GitHub Actions workflow automatically:
1. ✅ Validates setup (Java 21, Gradle)
2. ✅ Builds JAR artifact
3. ✅ Runs all 14 tests
4. ✅ Checks code quality
5. ✅ Builds Docker image
6. ✅ Pushes to GitHub Container Registry
7. ℹ️ (Optional) Deploys to cloud platform

**Workflow file:** `.github/workflows/ci-cd.yml`

**Status:** Visible at `https://github.com/YOUR-ORG/rnd-testing-hub/actions`

## Docker Image

### Build Locally

```bash
docker build -t rnd-testing-hub:latest .
```

### GitHub Container Registry

Image automatically pushed on main branch:

```bash
docker pull ghcr.io/YOUR-ORG/rnd-testing-hub:latest
```

### Run Locally

```bash
# Start with local PostgreSQL
docker-compose up -d

# Or run single container with external DB
docker run -p 8080:8080 \
  -e DATABASE_URL=jdbc:postgresql://postgres-host:5432/rnd_testing_hub \
  -e DATABASE_USER=postgres \
  -e DATABASE_PASSWORD=password \
  ghcr.io/YOUR-ORG/rnd-testing-hub:latest
```

## Cloud Deployment Options

### Option 1: Render.com (Recommended - Free Tier)

**Setup:**

1. Go to https://render.com
2. Sign up with GitHub
3. Create new **Web Service**
4. Connect GitHub repo (`rnd-testing-hub`)
5. Configure:
   - **Build Command:** `gradle build -x test`
   - **Start Command:** `java -jar backend/build/libs/rnd-testing-hub-0.1.0.jar`
6. Add environment variables:
   ```
   DATABASE_URL = postgresql://...
   DATABASE_USER = postgres
   DATABASE_PASSWORD = ****
   SPRING_JPA_HIBERNATE_DDL_AUTO = validate
   ```
7. Add PostgreSQL database service:
   - Click **New +** → **PostgreSQL**
   - Free tier = 256 MB RAM, 1 GB storage
8. Deploy

**Result:** Public URL like `https://rnd-testing-hub.onrender.com`

### Option 2: Railway.app

**Setup:**

1. Go to https://railway.app
2. Connect GitHub account
3. Import project
4. Add PostgreSQL service
5. Deploy

**Cost:** Free tier includes $5/month

**Result:** Public URL like `https://rnd-testing-hub-production.up.railway.app`

### Option 3: Fly.io

**Setup:**

1. Install Fly CLI: `brew install flyctl`
2. Initialize: `flyctl auth login`
3. Deploy:
   ```bash
   flyctl launch --image ghcr.io/YOUR-ORG/rnd-testing-hub:latest
   ```
4. Add PostgreSQL:
   ```bash
   flyctl postgres create
   ```

**Result:** Global deployment, free tier available

### Option 4: AWS (Elastic Beanstalk)

**Setup:**

1. Create EB environment
2. Upload JAR: `backend/build/libs/rnd-testing-hub-0.1.0.jar`
3. Configure RDS PostgreSQL
4. Deploy

**Cost:** $0 in free tier first 12 months

### Option 5: Docker Compose on VPS

**Setup on Ubuntu VPS:**

```bash
# SSH to VPS
ssh user@your-vps.com

# Install Docker
curl -fsSL https://get.docker.com -o get-docker.sh
sh get-docker.sh

# Clone repo
git clone https://github.com/YOUR-ORG/rnd-testing-hub.git
cd rnd-testing-hub

# Set environment
export DATABASE_PASSWORD=secure_random_password
export DATABASE_USER=postgres

# Deploy
docker-compose -f docker-compose.prod.yml up -d
```

**Access:** `http://YOUR-VPS-IP:8080`

## GitHub Actions Secrets (for Auto-Deployment)

To enable automatic deployment from CI/CD pipeline:

1. Go to **Settings → Secrets and variables → Actions**
2. Add secret: `RENDER_DEPLOY_HOOK_URL`
3. Value: Copy from Render deploy hook
4. Commits to `main` branch now trigger deployment

## Domain & HTTPS

### Map Custom Domain (Render)

1. Render dashboard → Environment settings
2. Add custom domain: `api.example.com`
3. Add DNS record (CNAME):
   ```
   api.example.com CNAME rnd-testing-hub.onrender.com
   ```
4. Render auto-provisions SSL certificate

### Use HTTPS

All major platforms provide free SSL certificates automatically.

## Monitoring Deployments

### GitHub Actions

```bash
# View pipeline
https://github.com/YOUR-ORG/rnd-testing-hub/actions

# View latest run
https://github.com/YOUR-ORG/rnd-testing-hub/actions/workflows/ci-cd.yml
```

### Application Logs

**Render:**
```bash
# Via dashboard
Dashboard → Logs
```

**Railway:**
```bash
# Via CLI
railway logs -f
```

**Fly.io:**
```bash
flyctl logs -a rnd-testing-hub
```

## Verify Deployment

```bash
# Health check
curl https://your-deployed-app/health

# Metrics
curl https://your-deployed-app/api/metrics/summary

# Practices
curl https://your-deployed-app/api/practices
```

## Database Backup

### Render PostgreSQL

1. Settings → Backups
2. Create manual backup
3. Or enable automated backups

### Railway PostgreSQL

1. Data → Backups
2. Create snapshot

### VPS PostgreSQL

```bash
# Backup
docker exec rnd-testing-hub-postgres-prod pg_dump -U postgres rnd_testing_hub > backup.sql

# Restore
docker exec -i rnd-testing-hub-postgres-prod psql -U postgres rnd_testing_hub < backup.sql
```

## Scaling & Performance

### For Production Scale

1. **Multi-instance:** Deploy multiple replicas with load balancer
2. **Caching:** Add Redis for metrics caching
3. **Async:** Implement job queue (RabbitMQ)
4. **CDN:** CloudFlare for static assets
5. **Monitoring:** Prometheus + Grafana

### Environment Variables for Production

```bash
# Database
DATABASE_URL=jdbc:postgresql://db-host:5432/prod_db
DATABASE_USER=postgres
DATABASE_PASSWORD=secure_password_2024

# Spring
SPRING_JPA_HIBERNATE_DDL_AUTO=validate
SPRING_PROFILES_ACTIVE=production

# Server
SERVER_PORT=8080
SERVER_COMPRESSION_ENABLED=true
SERVER_COMPRESSION_MIN_RESPONSE_SIZE=1024
```

## Rollback

### Render

1. Dashboard → Environment
2. Select previous deployment
3. Click "Redeploy"

### Railway

```bash
railway rollback
```

### GitHub Actions

Rerun previous workflow from Actions tab

## Cost Estimation (Monthly)

| Platform | Free | Paid |
|----------|------|------|
| Render | 0 (limited) | $7-12 |
| Railway | $0-5 | $5+ |
| Fly.io | 3 shared-cpu-1x | $3+ |
| AWS | $0-12 months | $10+ |
| DigitalOcean | - | $4-6 |

## Support & Troubleshooting

### Deployment Failed

Check logs:
```bash
# Render
Dashboard → Logs → View error

# Railway
railway logs -f

# Fly.io
flyctl logs -a rnd-testing-hub
```

### Database Connection Error

1. Verify DATABASE_URL format
2. Check credentials in environment
3. Ensure database service is running
4. Check firewall/network rules

### 502 Bad Gateway

1. App may be starting (wait 30s)
2. Check application logs
3. Verify environment variables set
4. Restart service

## Next Steps

1. Choose deployment platform above
2. Follow setup instructions
3. Set GitHub Actions secrets for auto-deploy
4. Monitor deployment status
5. Share public URL with team

**Example:** `https://rnd-testing-hub.onrender.com`

Once deployed, team can:
- ✅ Access metrics dashboard
- ✅ Upload test reports
- ✅ View best practices
- ✅ Generate tests
- ✅ All from public URL!
