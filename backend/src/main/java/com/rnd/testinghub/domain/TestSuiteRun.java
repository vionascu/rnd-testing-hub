package com.rnd.testinghub.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "test_suite_run")
public class TestSuiteRun {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String suiteName;

    @Column(nullable = false)
    private String status;

    @Column(nullable = false)
    private Integer totalTests;

    @Column(nullable = false)
    private Integer passedTests;

    @Column(nullable = false)
    private Integer failedTests;

    @Column(nullable = false)
    private Integer skippedTests;

    @Column(nullable = false)
    private Long durationMs;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    private String uploadSourceId;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Constructors
    public TestSuiteRun() {
    }

    public TestSuiteRun(String suiteName, String status, Integer totalTests, Integer passedTests,
                        Integer failedTests, Integer skippedTests, Long durationMs, LocalDateTime timestamp, String uploadSourceId) {
        this.suiteName = suiteName;
        this.status = status;
        this.totalTests = totalTests;
        this.passedTests = passedTests;
        this.failedTests = failedTests;
        this.skippedTests = skippedTests;
        this.durationMs = durationMs;
        this.timestamp = timestamp;
        this.uploadSourceId = uploadSourceId;
    }

    // Getters and setters
    public Long getId() { return id; }
    public String getSuiteName() { return suiteName; }
    public void setSuiteName(String suiteName) { this.suiteName = suiteName; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Integer getTotalTests() { return totalTests; }
    public void setTotalTests(Integer totalTests) { this.totalTests = totalTests; }
    public Integer getPassedTests() { return passedTests; }
    public void setPassedTests(Integer passedTests) { this.passedTests = passedTests; }
    public Integer getFailedTests() { return failedTests; }
    public void setFailedTests(Integer failedTests) { this.failedTests = failedTests; }
    public Integer getSkippedTests() { return skippedTests; }
    public void setSkippedTests(Integer skippedTests) { this.skippedTests = skippedTests; }
    public Long getDurationMs() { return durationMs; }
    public void setDurationMs(Long durationMs) { this.durationMs = durationMs; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    public String getUploadSourceId() { return uploadSourceId; }
    public void setUploadSourceId(String uploadSourceId) { this.uploadSourceId = uploadSourceId; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
