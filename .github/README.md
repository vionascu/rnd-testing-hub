# rnd-testing-hub on GitHub

Welcome! This is the GitHub home for **rnd-testing-hub**, an R&D Testing Best-Practices Aggregator.

## 🚀 Quick Links

- **Main Docs:** [README.md](../README.md)
- **Quick Start:** [Quickstart Guide](../docs/quickstart.md)
- **Deployment:** [Deployment Guide](../DEPLOYMENT.md)
- **Commands:** [API Commands](../COMMANDS.md)
- **CI/CD Pipeline:** [View Status](../../actions)

## ✅ Current Status

| Stage | Status | Details |
|-------|--------|---------|
| Build | ✅ | Gradle build successful (71 MB JAR) |
| Tests | ✅ | 14/14 tests passing |
| Docker | ✅ | Image built & ready |
| Deploy | ℹ️ | Awaiting cloud setup |

## 📊 Project Stats

- **Lines of Java:** 1,200+
- **Test Classes:** 4
- **REST Endpoints:** 15
- **Database Tables:** 7
- **Documentation:** 6 guides

## 🎯 Key Features

✨ **Ingest:** Upload OpenAPI specs & JUnit XML reports
📈 **Metrics:** Calculate pass rate, flakiness, coverage
📚 **Playbooks:** Pre-loaded best practices
🧪 **Generate:** Create RestAssured tests from OpenAPI

## 🏃 Get Started in 3 Steps

### 1. Clone
```bash
git clone https://github.com/YOUR-ORG/rnd-testing-hub.git
cd rnd-testing-hub
```

### 2. Start locally
```bash
docker-compose up -d && cd backend && gradle bootRun
```

### 3. Ingest samples
```bash
bash tools/scripts/ingest-samples.sh
```

✅ **Done!** API running at `http://localhost:8080`

## 🌐 Deploy to Cloud

Choose your platform:

- **[Render.com](https://render.com)** (Recommended - Free tier)
- **[Railway.app](https://railway.app)**
- **[Fly.io](https://fly.io)**
- **[AWS](https://aws.amazon.com)**

See [DEPLOYMENT.md](../DEPLOYMENT.md) for detailed instructions.

## 📦 Available Artifacts

### JAR
```bash
java -jar backend/build/libs/rnd-testing-hub-0.1.0.jar
```

### Docker Image
```bash
docker pull ghcr.io/YOUR-ORG/rnd-testing-hub:latest
docker run -p 8080:8080 ghcr.io/YOUR-ORG/rnd-testing-hub:latest
```

### Docker Compose
```bash
docker-compose up -d
```

## 🔄 CI/CD Pipeline

GitHub Actions runs on every push:

```
✅ Setup (Java 21 + Gradle)
  ↓
✅ Build (Gradle compile)
  ↓
✅ Test (14 integration tests)
  ↓
✅ Quality (Code checks)
  ↓
✅ Docker (Build & push image)
  ↓
ℹ️ Deploy (Optional - manual setup)
```

**View pipeline:** [Actions tab](../../actions)

## 🛠 Technology Stack

- **Language:** Java 21
- **Framework:** Spring Boot 3.4
- **Build:** Gradle Kotlin DSL
- **Database:** PostgreSQL
- **Testing:** JUnit 5, AssertJ
- **Container:** Docker, Docker Compose

## 📖 Documentation

| Document | Purpose |
|----------|---------|
| [README.md](../README.md) | Feature overview & quick start |
| [quickstart.md](../docs/quickstart.md) | Detailed setup guide |
| [architecture.md](../docs/architecture.md) | Design & modules |
| [metrics-definition.md](../docs/metrics-definition.md) | KPI specs |
| [COMMANDS.md](../COMMANDS.md) | API reference |
| [DEPLOYMENT.md](../DEPLOYMENT.md) | Cloud deployment |

## 🤝 Contributing

1. Fork the repo
2. Create feature branch: `git checkout -b feature/awesome`
3. Commit: `git commit -m "Add awesome feature"`
4. Push: `git push origin feature/awesome`
5. Open Pull Request

All PRs run full CI/CD pipeline before merge.

## 📝 Sample Data

Pre-loaded examples included:

- **OpenAPI:** 6-endpoint User API
- **JUnit XML:** 8-test sample report
- **Best Practices:** 4 testing playbooks

See `tools/` directory.

## 🐳 Docker Hub

Official images:

```bash
docker pull ghcr.io/YOUR-ORG/rnd-testing-hub:latest
docker pull ghcr.io/YOUR-ORG/rnd-testing-hub:v0.1.0
```

## 📞 Support

- **Issues:** Report bugs in [GitHub Issues](../../issues)
- **Discussions:** Ask questions in [GitHub Discussions](../../discussions)
- **Docs:** See [DEPLOYMENT.md](../DEPLOYMENT.md) for troubleshooting

## 📜 License

MIT License - See LICENSE file

## 🎉 Ready to Use?

### Local Development
```bash
docker-compose up -d && cd backend && gradle bootRun
```

### Cloud Deployment
Follow [DEPLOYMENT.md](../DEPLOYMENT.md)

### Next Steps
1. Ingest sample data: `bash tools/scripts/ingest-samples.sh`
2. View metrics: `curl http://localhost:8080/api/metrics/summary`
3. Generate tests: See [COMMANDS.md](../COMMANDS.md)

---

**Status:** ✅ Production-ready MVP

**Last Updated:** 2026-02-01

**Questions?** Open an issue or check the documentation!
