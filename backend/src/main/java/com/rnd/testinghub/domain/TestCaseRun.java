package com.rnd.testinghub.domain;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.LocalDateTime;

@Entity
@Table(name = "test_case_run")
public class TestCaseRun {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "suite_run_id", nullable = false)
    @JsonIgnore
    private TestSuiteRun suiteRun;

    @Column(nullable = false)
    private String testName;

    @Column(nullable = false)
    private String status;

    @Column(nullable = false)
    private Long durationMs;

    @Column(columnDefinition = "TEXT")
    private String errorMessage;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    // Constructors
    public TestCaseRun() {
    }

    public TestCaseRun(TestSuiteRun suiteRun, String testName, String status, Long durationMs, String errorMessage, LocalDateTime timestamp) {
        this.suiteRun = suiteRun;
        this.testName = testName;
        this.status = status;
        this.durationMs = durationMs;
        this.errorMessage = errorMessage;
        this.timestamp = timestamp;
    }

    // Getters and setters
    public Long getId() { return id; }
    public TestSuiteRun getSuiteRun() { return suiteRun; }
    public void setSuiteRun(TestSuiteRun suiteRun) { this.suiteRun = suiteRun; }
    public String getTestName() { return testName; }
    public void setTestName(String testName) { this.testName = testName; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Long getDurationMs() { return durationMs; }
    public void setDurationMs(Long durationMs) { this.durationMs = durationMs; }
    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    public Long getSuiteRunId() {
        return suiteRun != null ? suiteRun.getId() : null;
    }
}
