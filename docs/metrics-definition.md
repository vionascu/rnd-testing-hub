# Testing Metrics Definitions

Standardized KPIs for testing health and continuous improvement.

## Core Metrics

### 1. Pass Rate (%)

**Definition:** Percentage of tests that passed in a given time window.

$$\text{Pass Rate} = \frac{\text{Passed Tests}}{\text{Total Tests}} \times 100\%$$

**Interpretation:**
- 95-100%: Excellent
- 90-94%: Good
- 80-89%: Acceptable
- <80%: At risk

**When to investigate:** Sudden drops or trending downward.

**Remediation:**
- Review failed tests
- Check environment stability
- Identify flaky tests
- Improve test coverage

---

### 2. Failure Rate (%)

**Definition:** Percentage of tests that failed.

$$\text{Failure Rate} = \frac{\text{Failed Tests}}{\text{Total Tests}} \times 100\%$$

**Complement:** Failure rate + Pass rate + Skip rate = 100%

**Action Items:**
- < 5%: Monitor
- 5-10%: Investigate root causes
- > 10%: Block releases, fix critical issues

---

### 3. Flaky Rate (%)

**Definition (MVP):** Percentage of unique test names that show mixed pass/fail results across multiple runs.

$$\text{Flaky Rate} = \frac{\text{Tests with mixed outcomes}}{\text{Unique test names}} \times 100\%$$

**Detection Logic:**
- Collect all runs for each test by name
- If test appears as PASSED in one run AND FAILED in another → Flaky
- Otherwise → Stable

**Impact:** Flaky tests erode confidence and hide real failures.

**Action Items:**
- Quarantine flaky tests from critical path
- Root cause analysis (timing, state, environment)
- Fix in parallel to unblock team
- Target: 0% flakiness

---

### 4. Time-to-Feedback (TTF) - MVP Definition

**MVP TTF = Parse + Ingest Time**

Current definition: Time from when a test report is uploaded to when metrics are available.

$$\text{TTF (MVP)} = \text{Ingest Timestamp} + \text{Parse Duration}$$

**Example:**
- Report uploaded at 14:00:00
- Parsing completes at 14:00:05
- TTF = 5 seconds

**Future TTF (CI/CD):**
Once integrated with CI:
- Time from code push to first test result
- Includes: build + test execution + reporting
- Target: < 10 minutes for unit tests

**Note:** MVP definition is a placeholder. Real CI TTF depends on your pipeline.

---

### 5. API Endpoint Coverage (%)

**Definition:** Percentage of API endpoints with automated test coverage.

$$\text{Coverage} = \frac{\text{Endpoints Tested}}{\text{Total Endpoints}} \times 100\%$$

**MVP Heuristic for "tested":**
An endpoint is marked as tested if a test case name contains the pattern: `METHOD /path`

Examples:
- Test name contains "GET /users" → Covers GET /users
- Test name contains "POST /users" → Covers POST /users

**Naming Convention for Tests:**
- Happy path: `"GET /api/users"` or `"test_GET_api_users"`
- Negative case: `"GET /api/users_invalid_param"` (still matches "GET /api/users")

**Goals:**
- 80%+: Critical paths
- 60%+: All endpoints
- Target: Aspirational 100% for documented APIs

---

## Time Window Periods

| Period | Days | Use Case |
|--------|------|----------|
| 7d     | 7    | Daily standup, spot check |
| 30d    | 30   | Weekly review (default) |
| 90d    | 90   | Monthly retrospective |

## Metric Trends

### Viewing Trends

```bash
curl "http://localhost:8080/api/metrics/trends?metric=passRate&period=30d"
```

**Response:**
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

### Trending Analysis

- **Trending Up:** Good sign; improvements working
- **Trending Down:** Red flag; investigate causes
- **Stable:** Maintain current practices

## Quality Gate Recommendations

### Development Phase

| Metric | Threshold | Action |
|--------|-----------|--------|
| Pass Rate | < 90% | Fix failing tests |
| Flaky Rate | > 5% | Quarantine, investigate |
| Coverage | < 60% | Add tests for gaps |

### Pre-Release

| Metric | Threshold | Action |
|--------|-----------|--------|
| Pass Rate | 100% | Cannot release |
| Flaky Rate | 0% | Cannot release |
| Coverage | > 80% | Required for new code |

## Metrics API

### Summary (30-day window)
```bash
GET /api/metrics/summary?days=30
```

Response includes:
- pass_rate, failure_rate, flaky_rate
- total_tests_executed
- total_passed, total_failed, total_skipped

### Trends
```bash
GET /api/metrics/trends?metric={passRate|failureRate}&period={7d|30d|90d}
```

### API Coverage
```bash
GET /api/metrics/api-coverage/{specId}
```

## Anti-Patterns to Avoid

1. **Chasing 100%:** Perfect metrics may hide real issues
2. **Ignoring Flakiness:** Flaky tests are worse than no tests
3. **High Coverage, Low Quality:** Metrics don't measure test effectiveness
4. **Not Acting:** Metrics without action are noise

## Continuous Improvement

### Monthly Retrospective

1. Review metrics trends from past 90 days
2. Identify patterns (improving/declining)
3. Root cause analysis for outliers
4. Action items for next sprint
5. Adjust targets based on improvements

### Alerts (Future)

- Pass rate drops > 10% in one day
- Flaky rate increases > 5%
- TTF increases > 20%
- Coverage drops > 5%
