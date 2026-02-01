package com.rnd.testinghub;

import com.rnd.testinghub.adapters.persistence.TestCaseRunRepository;
import com.rnd.testinghub.adapters.persistence.TestSuiteRunRepository;
import com.rnd.testinghub.application.JunitIngestionService;
import com.rnd.testinghub.domain.TestCaseRun;
import com.rnd.testinghub.domain.TestSuiteRun;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:testdb",
    "spring.datasource.driver-class-name=org.h2.Driver",
    "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
    "spring.jpa.hibernate.ddl-auto=create-drop"
})
class JunitIngestionTest {

    @Autowired
    private JunitIngestionService junitIngestionService;

    @Autowired
    private TestSuiteRunRepository testSuiteRunRepository;

    @Autowired
    private TestCaseRunRepository testCaseRunRepository;

    private String sampleJunitXml;

    @BeforeEach
    void setUp() {
        sampleJunitXml = """
            <?xml version="1.0" encoding="UTF-8"?>
            <testsuite name="SampleTests" tests="3" failures="1" skipped="1" time="1.5">
                <testcase classname="com.example.SampleTest" name="testPass" time="0.5"/>
                <testcase classname="com.example.SampleTest" name="testFail" time="0.8">
                    <failure message="Assertion failed">
                        Test failed with error
                    </failure>
                </testcase>
                <testcase classname="com.example.SampleTest" name="testSkip" time="0.2">
                    <skipped message="Not ready"/>
                </testcase>
            </testsuite>
            """;
    }

    @Test
    void testIngestJunitReport() throws Exception {
        List<Long> suiteIds = junitIngestionService.ingestJunitReport(sampleJunitXml);

        assertThat(suiteIds).hasSize(1);
        Long suiteId = suiteIds.get(0);

        TestSuiteRun suite = testSuiteRunRepository.findById(suiteId).orElse(null);
        assertThat(suite).isNotNull();
        assertThat(suite.getSuiteName()).isEqualTo("SampleTests");
        assertThat(suite.getTotalTests()).isEqualTo(3);
        assertThat(suite.getFailedTests()).isEqualTo(1);
        assertThat(suite.getSkippedTests()).isEqualTo(1);
    }

    @Test
    void testTestCasesArePersisted() throws Exception {
        List<Long> suiteIds = junitIngestionService.ingestJunitReport(sampleJunitXml);
        Long suiteId = suiteIds.get(0);

        List<TestCaseRun> cases = junitIngestionService.getTestCasesForSuite(suiteId);

        assertThat(cases).hasSize(3);

        assertThat(cases)
            .filteredOn(c -> c.getTestName().equals("com.example.SampleTest.testPass"))
            .hasSize(1)
            .allMatch(c -> c.getStatus().equals("passed"));

        assertThat(cases)
            .filteredOn(c -> c.getTestName().equals("com.example.SampleTest.testFail"))
            .hasSize(1)
            .allMatch(c -> c.getStatus().equals("failed"));

        assertThat(cases)
            .filteredOn(c -> c.getTestName().equals("com.example.SampleTest.testSkip"))
            .hasSize(1)
            .allMatch(c -> c.getStatus().equals("skipped"));
    }

    @Test
    void testCalculatedStats() throws Exception {
        List<Long> suiteIds = junitIngestionService.ingestJunitReport(sampleJunitXml);
        Long suiteId = suiteIds.get(0);

        TestSuiteRun suite = testSuiteRunRepository.findById(suiteId).orElse(null);

        assertThat(suite.getTotalTests()).isEqualTo(3);
        assertThat(suite.getPassedTests()).isEqualTo(1);
        assertThat(suite.getFailedTests()).isEqualTo(1);
        assertThat(suite.getSkippedTests()).isEqualTo(1);
        assertThat(suite.getStatus()).isEqualTo("failed");
    }
}
