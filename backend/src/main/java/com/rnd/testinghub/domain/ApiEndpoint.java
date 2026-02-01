package com.rnd.testinghub.domain;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.LocalDateTime;

@Entity
@Table(name = "api_endpoint")
public class ApiEndpoint {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "spec_id", nullable = false)
    @JsonIgnore
    private ApiSpec apiSpec;

    @Column(nullable = false)
    private String method;

    @Column(nullable = false)
    private String path;

    private String summary;
    private String description;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    // Constructors
    public ApiEndpoint() {
    }

    public ApiEndpoint(ApiSpec apiSpec, String method, String path, String summary, String description) {
        this.apiSpec = apiSpec;
        this.method = method;
        this.path = path;
        this.summary = summary;
        this.description = description;
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public ApiSpec getApiSpec() {
        return apiSpec;
    }

    public void setApiSpec(ApiSpec apiSpec) {
        this.apiSpec = apiSpec;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public Long getSpecId() {
        return apiSpec != null ? apiSpec.getId() : null;
    }
}
