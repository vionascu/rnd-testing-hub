package com.rnd.testinghub.application;

import com.rnd.testinghub.adapters.persistence.TestCaseRunRepository;
import com.rnd.testinghub.adapters.persistence.TestSuiteRunRepository;
import com.rnd.testinghub.domain.TestCaseRun;
import com.rnd.testinghub.domain.TestSuiteRun;
import com.rnd.testinghub.infrastructure.parser.JunitXmlParser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class JunitIngestionService {

    private final TestSuiteRunRepository testSuiteRunRepository;
    private final TestCaseRunRepository testCaseRunRepository;
    private final JunitXmlParser junitXmlParser;

    public JunitIngestionService(TestSuiteRunRepository testSuiteRunRepository,
                                 TestCaseRunRepository testCaseRunRepository,
                                 JunitXmlParser junitXmlParser) {
        this.testSuiteRunRepository = testSuiteRunRepository;
        this.testCaseRunRepository = testCaseRunRepository;
        this.junitXmlParser = junitXmlParser;
    }

    @Transactional
    public List<Long> ingestJunitReport(String xmlContent) throws Exception {
        List<JunitXmlParser.TestSuiteInfo> suites = junitXmlParser.parse(xmlContent);
        String uploadSourceId = UUID.randomUUID().toString();
        List<Long> suiteIds = new java.util.ArrayList<>();

        LocalDateTime now = LocalDateTime.now();

        for (JunitXmlParser.TestSuiteInfo suiteInfo : suites) {
            String status = suiteInfo.failed > 0 ? "failed" : (suiteInfo.tests == suiteInfo.passed ? "passed" : "mixed");

            TestSuiteRun suiteRun = new TestSuiteRun(
                suiteInfo.name,
                status,
                suiteInfo.tests,
                suiteInfo.passed,
                suiteInfo.failed,
                suiteInfo.skipped,
                suiteInfo.durationMs,
                now,
                uploadSourceId
            );
            suiteRun = testSuiteRunRepository.save(suiteRun);
            suiteIds.add(suiteRun.getId());

            // Save test cases
            for (JunitXmlParser.TestCaseInfo caseInfo : suiteInfo.cases) {
                TestCaseRun caseRun = new TestCaseRun(
                    suiteRun,
                    caseInfo.name,
                    caseInfo.status,
                    caseInfo.durationMs,
                    caseInfo.errorMessage,
                    now
                );
                testCaseRunRepository.save(caseRun);
            }
        }

        return suiteIds;
    }

    public TestSuiteRun getTestSuiteRun(Long id) {
        return testSuiteRunRepository.findById(id).orElse(null);
    }

    public List<TestCaseRun> getTestCasesForSuite(Long suiteId) {
        return testCaseRunRepository.findBySuiteRunId(suiteId);
    }

    public List<TestSuiteRun> getTestSuiteRunsInTimeWindow(LocalDateTime from, LocalDateTime to) {
        return testSuiteRunRepository.findByTimestampBetween(from, to);
    }
}
