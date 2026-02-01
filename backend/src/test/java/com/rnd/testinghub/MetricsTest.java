package com.rnd.testinghub;

import com.rnd.testinghub.adapters.persistence.TestCaseRunRepository;
import com.rnd.testinghub.adapters.persistence.TestSuiteRunRepository;
import com.rnd.testinghub.application.JunitIngestionService;
import com.rnd.testinghub.application.MetricsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:testdb",
    "spring.datasource.driver-class-name=org.h2.Driver",
    "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
    "spring.jpa.hibernate.ddl-auto=create-drop"
})
class MetricsTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MetricsService metricsService;

    @Autowired
    private JunitIngestionService junitIngestionService;

    @Autowired
    private TestSuiteRunRepository testSuiteRunRepository;

    private String sampleJunitXml;

    @BeforeEach
    void setUp() {
        sampleJunitXml = """
            <?xml version="1.0" encoding="UTF-8"?>
            <testsuite name="SampleTests" tests="4" failures="1" skipped="0" time="1.5">
                <testcase classname="com.example.SampleTest" name="testPass1" time="0.5"/>
                <testcase classname="com.example.SampleTest" name="testPass2" time="0.4"/>
                <testcase classname="com.example.SampleTest" name="testFail" time="0.8">
                    <failure message="Assertion failed">
                        Test failed with error
                    </failure>
                </testcase>
                <testcase classname="com.example.SampleTest" name="testPass3" time="0.2"/>
            </testsuite>
            """;
    }

    @Test
    void testPassRateCalculation() throws Exception {
        junitIngestionService.ingestJunitReport(sampleJunitXml);

        var metrics = metricsService.calculateSummaryMetrics(30);

        // 3 passed out of 4 = 75%
        assertThat(metrics)
            .containsKey("pass_rate")
            .containsKey("failure_rate")
            .containsKey("total_tests_executed");
        assertThat(metrics.get("total_tests_executed")).isEqualTo(4);
        assertThat(metrics.get("total_passed")).isEqualTo(3);
        assertThat(metrics.get("total_failed")).isEqualTo(1);
    }

    @Test
    void testMetricsEndpoint() throws Exception {
        junitIngestionService.ingestJunitReport(sampleJunitXml);

        mockMvc.perform(get("/api/metrics/summary?days=30"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.total_tests_executed").value(4))
            .andExpect(jsonPath("$.total_passed").value(3))
            .andExpect(jsonPath("$.total_failed").value(1));
    }

    @Test
    void testTrendsEndpoint() throws Exception {
        junitIngestionService.ingestJunitReport(sampleJunitXml);

        mockMvc.perform(get("/api/metrics/trends?metric=passRate&period=30d"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.metric").value("passRate"))
            .andExpect(jsonPath("$.period").value("30d"));
    }

    @Test
    void testFlakyRateCalculationNoFlakiness() throws Exception {
        junitIngestionService.ingestJunitReport(sampleJunitXml);

        double flakyRate = metricsService.calculateFlakyRate();

        // No flakiness since all tests have consistent status
        assertThat(flakyRate).isEqualTo(0.0);
    }

    @Test
    void testEmptyMetricsBeforeIngestion() {
        var metrics = metricsService.calculateSummaryMetrics(30);

        // Before ingesting any data, should return empty/zero metrics
        assertThat(metrics).containsKey("pass_rate");
        assertThat(metrics.get("total_tests_executed")).isEqualTo(0);
        assertThat(metrics.get("total_run_suites")).isEqualTo(0);
    }
}
