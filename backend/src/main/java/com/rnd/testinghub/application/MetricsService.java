package com.rnd.testinghub.application;

import com.rnd.testinghub.adapters.persistence.ApiEndpointRepository;
import com.rnd.testinghub.adapters.persistence.TestCaseRunRepository;
import com.rnd.testinghub.adapters.persistence.TestSuiteRunRepository;
import com.rnd.testinghub.domain.TestCaseRun;
import com.rnd.testinghub.domain.TestSuiteRun;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class MetricsService {

    private final TestSuiteRunRepository testSuiteRunRepository;
    private final TestCaseRunRepository testCaseRunRepository;
    private final ApiEndpointRepository apiEndpointRepository;

    public MetricsService(TestSuiteRunRepository testSuiteRunRepository,
                          TestCaseRunRepository testCaseRunRepository,
                          ApiEndpointRepository apiEndpointRepository) {
        this.testSuiteRunRepository = testSuiteRunRepository;
        this.testCaseRunRepository = testCaseRunRepository;
        this.apiEndpointRepository = apiEndpointRepository;
    }

    public Map<String, Object> calculateSummaryMetrics(int daysWindow) {
        LocalDateTime from = LocalDateTime.now().minusDays(daysWindow);
        LocalDateTime to = LocalDateTime.now();

        List<TestSuiteRun> suites = testSuiteRunRepository.findByTimestampBetween(from, to);

        double passRate = calculatePassRate(suites);
        double failureRate = calculateFailureRate(suites);
        double flakyRate = calculateFlakyRate();

        return Map.of(
            "window_days", daysWindow,
            "pass_rate", String.format("%.2f%%", passRate * 100),
            "failure_rate", String.format("%.2f%%", failureRate * 100),
            "flaky_rate", String.format("%.2f%%", flakyRate * 100),
            "total_test_runs", suites.size(),
            "total_run_suites", suites.size(),
            "total_tests_executed", suites.stream().mapToInt(TestSuiteRun::getTotalTests).sum(),
            "total_passed", suites.stream().mapToInt(TestSuiteRun::getPassedTests).sum(),
            "total_failed", suites.stream().mapToInt(TestSuiteRun::getFailedTests).sum(),
            "timestamp", LocalDateTime.now()
        );
    }

    public double calculatePassRate(List<TestSuiteRun> suites) {
        if (suites.isEmpty()) return 0.0;

        int totalTests = suites.stream().mapToInt(TestSuiteRun::getTotalTests).sum();
        int passedTests = suites.stream().mapToInt(TestSuiteRun::getPassedTests).sum();

        return totalTests == 0 ? 0.0 : (double) passedTests / totalTests;
    }

    public double calculateFailureRate(List<TestSuiteRun> suites) {
        if (suites.isEmpty()) return 0.0;

        int totalTests = suites.stream().mapToInt(TestSuiteRun::getTotalTests).sum();
        int failedTests = suites.stream().mapToInt(TestSuiteRun::getFailedTests).sum();

        return totalTests == 0 ? 0.0 : (double) failedTests / totalTests;
    }

    public double calculateFlakyRate() {
        // MVP heuristic: A test is flaky if it appears as failed in one run and passed in another
        // Get all test case runs
        List<TestCaseRun> allRuns = testCaseRunRepository.findAll();

        if (allRuns.isEmpty()) return 0.0;

        // Group by test name
        Map<String, List<TestCaseRun>> runsByTest = allRuns.stream()
            .collect(Collectors.groupingBy(TestCaseRun::getTestName));

        int flakyTests = 0;
        for (List<TestCaseRun> runs : runsByTest.values()) {
            if (runs.size() > 1) {
                List<String> statuses = runs.stream()
                    .map(TestCaseRun::getStatus)
                    .distinct()
                    .collect(Collectors.toList());

                // Flaky if it has both passed and failed statuses
                if (statuses.contains("passed") && statuses.contains("failed")) {
                    flakyTests++;
                }
            }
        }

        return runsByTest.isEmpty() ? 0.0 : (double) flakyTests / runsByTest.size();
    }

    public Map<String, Object> calculateApiCoverage(Long specId) {
        // MVP coverage: endpoints tested if test name contains "METHOD path" pattern
        List<String> endpoints = apiEndpointRepository.findByApiSpecId(specId)
            .stream()
            .map(e -> e.getMethod() + " " + e.getPath())
            .collect(Collectors.toList());

        List<TestCaseRun> allTests = testCaseRunRepository.findAll();

        int testedEndpoints = 0;
        for (String endpoint : endpoints) {
            boolean isTestedby = allTests.stream()
                .anyMatch(t -> t.getTestName().contains(endpoint));

            if (isTestedby) {
                testedEndpoints++;
            }
        }

        double coverage = endpoints.isEmpty() ? 0.0 : (double) testedEndpoints / endpoints.size();

        return Map.of(
            "spec_id", specId,
            "total_endpoints", endpoints.size(),
            "tested_endpoints", testedEndpoints,
            "coverage", String.format("%.2f%%", coverage * 100),
            "coverage_decimal", coverage
        );
    }

    public Map<String, Object> getTestTrends(String metric, String period) {
        // MVP: return data for period (7d, 30d, 90d)
        int daysWindow = 30;
        if ("7d".equals(period)) daysWindow = 7;
        else if ("90d".equals(period)) daysWindow = 90;

        LocalDateTime from = LocalDateTime.now().minusDays(daysWindow);
        LocalDateTime to = LocalDateTime.now();
        List<TestSuiteRun> suites = testSuiteRunRepository.findByTimestampBetween(from, to);

        Map<String, Double> trend = new HashMap<>();

        // Group by date and calculate metric for each day
        Map<String, List<TestSuiteRun>> byDate = suites.stream()
            .collect(Collectors.groupingBy(s -> s.getTimestamp().toLocalDate().toString()));

        for (Map.Entry<String, List<TestSuiteRun>> entry : byDate.entrySet()) {
            double value = 0.0;
            if ("passRate".equals(metric)) {
                value = calculatePassRate(entry.getValue());
            } else if ("failureRate".equals(metric)) {
                value = calculateFailureRate(entry.getValue());
            }
            trend.put(entry.getKey(), value);
        }

        return Map.of(
            "metric", metric,
            "period", period,
            "trend", trend,
            "data_points", trend.size()
        );
    }
}
