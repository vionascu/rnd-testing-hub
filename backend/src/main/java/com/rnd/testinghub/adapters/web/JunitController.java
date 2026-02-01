package com.rnd.testinghub.adapters.web;

import com.rnd.testinghub.application.JunitIngestionService;
import com.rnd.testinghub.domain.TestCaseRun;
import com.rnd.testinghub.domain.TestSuiteRun;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/junit")
public class JunitController {

    private final JunitIngestionService junitIngestionService;

    public JunitController(JunitIngestionService junitIngestionService) {
        this.junitIngestionService = junitIngestionService;
    }

    @PostMapping("/upload")
    public ResponseEntity<?> uploadJunitReport(@RequestParam("file") MultipartFile file) {
        try {
            String xmlContent = new String(file.getBytes(), StandardCharsets.UTF_8);
            List<Long> suiteIds = junitIngestionService.ingestJunitReport(xmlContent);

            return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "JUnit report uploaded and parsed",
                "suiteIds", suiteIds,
                "suites", suiteIds.size()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                "status", "error",
                "message", e.getMessage()
            ));
        }
    }

    @GetMapping("/{suiteId}")
    public ResponseEntity<?> getTestSuite(@PathVariable Long suiteId) {
        TestSuiteRun suite = junitIngestionService.getTestSuiteRun(suiteId);
        if (suite == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(suite);
    }

    @GetMapping("/{suiteId}/cases")
    public ResponseEntity<?> getTestCases(@PathVariable Long suiteId) {
        List<TestCaseRun> cases = junitIngestionService.getTestCasesForSuite(suiteId);
        return ResponseEntity.ok(Map.of(
            "suiteId", suiteId,
            "cases", cases,
            "total", cases.size()
        ));
    }
}
