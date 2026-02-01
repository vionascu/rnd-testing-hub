package com.rnd.testinghub.application;

import com.rnd.testinghub.adapters.persistence.PracticeRepository;
import com.rnd.testinghub.domain.Practice;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PracticeService {

    private final PracticeRepository practiceRepository;

    public PracticeService(PracticeRepository practiceRepository) {
        this.practiceRepository = practiceRepository;
    }

    public void initializePractices() {
        if (practiceRepository.count() > 0) return;

        practiceRepository.save(new Practice(
            "functional-rest-testing",
            "Functional Testing for REST APIs",
            """
# Functional Testing for REST APIs

Functional testing validates that REST API endpoints behave correctly according to specifications.

## Key Testing Scenarios

### Happy Path Testing
- Test normal operation with valid inputs
- Verify correct response status codes (200, 201, etc.)
- Validate response data structure and values

### Negative Testing
- Test with missing required fields
- Test with invalid data types
- Test with boundary values (very large numbers, empty strings)
- Verify error response codes (400, 404, 500, etc.)

### Authentication & Authorization
- Test with valid tokens/credentials
- Test with expired tokens
- Test with insufficient permissions (403)
- Test without authentication (401)

## Best Practices

1. Use fixtures and test data builders for consistency
2. Organize tests by endpoint and operation
3. Test both happy path and error cases
4. Validate response headers and timestamps
5. Clean up test data after each test

## Tools
- RestAssured for Java
- Cucumber for BDD
- Postman for manual validation""",
            "functional,rest,testing"
        ));

        practiceRepository.save(new Practice(
            "contract-testing",
            "Contract Testing for APIs",
            """
# Contract Testing for APIs

Contract testing ensures that API consumers and providers agree on request/response contracts.

## What is Contract Testing?

Contract testing validates:
- Request format and required fields
- Response schema and data types
- HTTP status codes
- Error response formats

## Consumer-Driven Contracts

1. Consumer defines what it expects from provider
2. Provider verifies it can fulfill those expectations
3. Both sides stay synchronized during development

## OpenAPI Schema Validation

- Validate requests against OpenAPI spec
- Validate responses against OpenAPI spec
- Catch breaking changes early
- Enable CI/CD quality gates

## Best Practices

1. Define OpenAPI spec before implementation
2. Run contract tests in CI/CD pipeline
3. Version your APIs
4. Document breaking changes
5. Maintain backward compatibility where possible""",
            "contract,testing,openapi"
        ));

        practiceRepository.save(new Practice(
            "flaky-tests-playbook",
            "Identifying and Fixing Flaky Tests",
            """
# Flaky Tests Playbook

Flaky tests fail intermittently without code changes, reducing confidence in test suites.

## Common Causes

### Timing Issues
- Tests rely on specific timing
- Threads execute in unpredictable order
- Async operations not properly awaited

### Environment Dependencies
- Tests depend on external services
- Database state varies between runs
- Test isolation issues

### Resource Contention
- Tests compete for ports or files
- Insufficient cleanup between tests
- Resource limits hit intermittently

## Detection Strategy

1. Run tests multiple times (20-50x)
2. Track test results in metrics system
3. Flag tests that fail < 100% of the time
4. Quarantine flaky tests from critical path

## Fixing Flaky Tests

1. **Add explicit waits** instead of fixed sleeps
2. **Isolate test state** - don't share resources
3. **Use test containers** for dependencies
4. **Fix race conditions** with proper synchronization
5. **Mock external services** to eliminate variability

## Prevention

- Review test code in pull requests
- Run new tests 10x before merging
- Monitor test reliability metrics
- Quarantine section for investigatng""",
            "flaky,testing,quality"
        ));

        practiceRepository.save(new Practice(
            "ci-quality-gates",
            "CI/CD Quality Gates for Testing",
            """
# CI/CD Quality Gates for Testing

Quality gates enforce minimum testing standards before code reaches production.

## Essential Metrics

### Code Coverage
- Minimum 80% coverage for new code
- 60% overall project coverage
- Critical paths at 100% coverage

### Test Pass Rate
- All tests must pass
- No flaky tests in gates
- Automated retry for transient failures

### Performance Thresholds
- API response time < 500ms (p99)
- Test execution time limit
- Build time monitoring

## Gate Implementation

```yaml
quality_gates:
  - name: unit-tests
    condition: pass_rate == 100%
    timeout: 10m
  - name: coverage
    condition: coverage >= 80%
    fail_on: new_code
  - name: mutation-testing
    condition: survived >= 75%
  - name: contract-tests
    condition: pass_rate == 100%
```

## Deployment Safety

1. Run gates for every pull request
2. Block merges on gate failures
3. Log all gate violations
4. Require manual approval for exceptions
5. Review exceptions in retrospectives""",
            "ci,quality,gates"
        ));
    }

    public List<Practice> getAllPractices() {
        return practiceRepository.findAll();
    }

    public Practice getPracticeBySlug(String slug) {
        return practiceRepository.findBySlug(slug).orElse(null);
    }

    public List<Practice> searchByTag(String tag) {
        return practiceRepository.findByTag(tag);
    }

    public List<Practice> search(String query) {
        return practiceRepository.search(query);
    }
}
