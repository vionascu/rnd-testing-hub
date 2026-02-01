package com.rnd.testinghub.adapters.persistence;

import com.rnd.testinghub.domain.TestSuiteRun;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TestSuiteRunRepository extends JpaRepository<TestSuiteRun, Long> {
    List<TestSuiteRun> findByTimestampAfter(LocalDateTime timestamp);
    @Query("SELECT t FROM TestSuiteRun t WHERE t.timestamp >= :from AND t.timestamp <= :to")
    List<TestSuiteRun> findByTimestampBetween(@Param("from") LocalDateTime from, @Param("to") LocalDateTime to);
}
