package com.rnd.testinghub.adapters.web;

import com.rnd.testinghub.application.MetricsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/metrics")
public class MetricsController {

    private final MetricsService metricsService;

    public MetricsController(MetricsService metricsService) {
        this.metricsService = metricsService;
    }

    @GetMapping("/summary")
    public ResponseEntity<?> getSummary(@RequestParam(defaultValue = "30") int days) {
        Map<String, Object> metrics = metricsService.calculateSummaryMetrics(days);
        return ResponseEntity.ok(metrics);
    }

    @GetMapping("/trends")
    public ResponseEntity<?> getTrends(
        @RequestParam(defaultValue = "passRate") String metric,
        @RequestParam(defaultValue = "30d") String period) {

        Map<String, Object> trends = metricsService.getTestTrends(metric, period);
        return ResponseEntity.ok(trends);
    }

    @GetMapping("/api-coverage/{specId}")
    public ResponseEntity<?> getApiCoverage(@PathVariable Long specId) {
        Map<String, Object> coverage = metricsService.calculateApiCoverage(specId);
        return ResponseEntity.ok(coverage);
    }
}
