package com.rnd.testinghub.adapters.persistence;

import com.rnd.testinghub.domain.TestCaseRun;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TestCaseRunRepository extends JpaRepository<TestCaseRun, Long> {
    @Query("SELECT t FROM TestCaseRun t WHERE t.suiteRun.id = :suiteRunId")
    List<TestCaseRun> findBySuiteRunId(@Param("suiteRunId") Long suiteRunId);

    List<TestCaseRun> findByTestName(String testName);

    @Query(value = "SELECT t FROM TestCaseRun t WHERE t.testName = :testName ORDER BY t.timestamp DESC")
    List<TestCaseRun> findRecentRunsForTest(@Param("testName") String testName, @Param("limit") int limit);
}
