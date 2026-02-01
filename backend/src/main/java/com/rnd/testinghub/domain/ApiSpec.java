package com.rnd.testinghub.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "api_spec")
public class ApiSpec {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String title;
    private String version;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String specContent;

    @Column(nullable = false)
    private String specFormat; // yaml or json

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
    public ApiSpec() {
    }

    public ApiSpec(String name, String title, String version, String specContent, String specFormat, String uploadSourceId) {
        this.name = name;
        this.title = title;
        this.version = version;
        this.specContent = specContent;
        this.specFormat = specFormat;
        this.uploadSourceId = uploadSourceId;
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getSpecContent() {
        return specContent;
    }

    public void setSpecContent(String specContent) {
        this.specContent = specContent;
    }

    public String getSpecFormat() {
        return specFormat;
    }

    public void setSpecFormat(String specFormat) {
        this.specFormat = specFormat;
    }

    public String getUploadSourceId() {
        return uploadSourceId;
    }

    public void setUploadSourceId(String uploadSourceId) {
        this.uploadSourceId = uploadSourceId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
