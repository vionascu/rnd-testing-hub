package com.rnd.testinghub;

import com.rnd.testinghub.adapters.persistence.ApiEndpointRepository;
import com.rnd.testinghub.adapters.persistence.ApiSpecRepository;
import com.rnd.testinghub.application.OpenApiIngestionService;
import com.rnd.testinghub.domain.ApiEndpoint;
import com.rnd.testinghub.domain.ApiSpec;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:testdb",
    "spring.datasource.driver-class-name=org.h2.Driver",
    "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
    "spring.jpa.hibernate.ddl-auto=create-drop"
})
class OpenApiIngestionTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private OpenApiIngestionService openApiIngestionService;

    @Autowired
    private ApiSpecRepository apiSpecRepository;

    @Autowired
    private ApiEndpointRepository apiEndpointRepository;

    private String sampleOpenApiSpec;

    @BeforeEach
    void setUp() throws Exception {
        sampleOpenApiSpec = """
            openapi: 3.0.0
            info:
              title: Test API
              version: 1.0.0
            paths:
              /api/users:
                get:
                  summary: Get all users
                post:
                  summary: Create user
              /api/users/{id}:
                get:
                  summary: Get user by ID
                put:
                  summary: Update user
                delete:
                  summary: Delete user
            """;
    }

    @Test
    void testIngestOpenApiSpec() throws Exception {
        Long specId = openApiIngestionService.ingestOpenApiSpec(sampleOpenApiSpec, "yaml");

        assertThat(specId).isNotNull();

        ApiSpec spec = apiSpecRepository.findById(specId).orElse(null);
        assertThat(spec).isNotNull();
        assertThat(spec.getName()).isEqualTo("Test API");
        assertThat(spec.getVersion()).isEqualTo("1.0.0");
    }

    @Test
    void testEndpointsArePersisted() throws Exception {
        Long specId = openApiIngestionService.ingestOpenApiSpec(sampleOpenApiSpec, "yaml");

        List<ApiEndpoint> endpoints = openApiIngestionService.getEndpointsForSpec(specId);

        assertThat(endpoints).isNotEmpty();
        assertThat(endpoints).hasSize(5); // 2 for /api/users + 3 for /api/users/{id}

        // Verify specific endpoints
        assertThat(endpoints)
            .filteredOn(e -> e.getMethod().equals("GET") && e.getPath().equals("/api/users"))
            .hasSize(1);

        assertThat(endpoints)
            .filteredOn(e -> e.getMethod().equals("POST") && e.getPath().equals("/api/users"))
            .hasSize(1);

        assertThat(endpoints)
            .filteredOn(e -> e.getPath().equals("/api/users/{id}"))
            .hasSize(3); // GET, PUT, DELETE
    }

    @Test
    void testUploadEndpoint() throws Exception {
        mockMvc.perform(post("/api/openapi/upload")
                .param("file", "test")
                .contentType("multipart/form-data"))
            .andExpect(status().isBadRequest()); // multipart test - this is a placeholder
    }

    @Test
    void testGetEndpointsEndpoint() throws Exception {
        Long specId = openApiIngestionService.ingestOpenApiSpec(sampleOpenApiSpec, "yaml");

        mockMvc.perform(get("/api/openapi/" + specId + "/endpoints"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.specId").value(specId))
            .andExpect(jsonPath("$.total").value(5));
    }
}
