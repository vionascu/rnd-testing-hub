package com.rnd.testinghub.adapters.web;

import com.rnd.testinghub.application.OpenApiIngestionService;
import com.rnd.testinghub.domain.ApiEndpoint;
import com.rnd.testinghub.infrastructure.generator.RestAssuredTestGenerator;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@RestController
@RequestMapping("/api/generator")
public class GeneratorController {

    private final RestAssuredTestGenerator testGenerator;
    private final OpenApiIngestionService openApiIngestionService;

    public GeneratorController(RestAssuredTestGenerator testGenerator,
                             OpenApiIngestionService openApiIngestionService) {
        this.testGenerator = testGenerator;
        this.openApiIngestionService = openApiIngestionService;
    }

    @PostMapping("/restassured")
    public ResponseEntity<Resource> generateTests(@RequestBody GeneratorRequest request) {
        try {
            // Get endpoints for the spec
            List<ApiEndpoint> endpoints = openApiIngestionService.getEndpointsForSpec(request.openApiSpecId);

            // Generate test code
            String testCode = testGenerator.generateTestClass(
                request.baseUrl,
                endpoints,
                request.options.includeNegativeTests,
                request.options.includeContractValidation
            );

            // Generate gradle build
            String gradleBuild = testGenerator.generateGradleBuild();

            // Generate README
            String readme = testGenerator.generateReadme(request.baseUrl);

            // Create ZIP file
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            try (ZipOutputStream zos = new ZipOutputStream(baos)) {
                // Add test class
                addFileToZip(zos, "src/test/java/com/example/generated/GeneratedApiTests.java", testCode);

                // Add gradle build
                addFileToZip(zos, "build.gradle.kts", gradleBuild);

                // Add README
                addFileToZip(zos, "README.md", readme);

                // Add settings.gradle
                addFileToZip(zos, "settings.gradle.kts", "rootProject.name = \"generated-api-tests\"");
            }

            Resource resource = new ByteArrayResource(baos.toByteArray());

            return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"rest-api-tests.zip\"")
                .contentType(MediaType.parseMediaType("application/zip"))
                .body(resource);

        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    private void addFileToZip(ZipOutputStream zos, String fileName, String content) throws IOException {
        ZipEntry entry = new ZipEntry(fileName);
        zos.putNextEntry(entry);
        zos.write(content.getBytes(StandardCharsets.UTF_8));
        zos.closeEntry();
    }

    @PostMapping("/preview")
    public ResponseEntity<?> previewGeneratedCode(@RequestBody GeneratorRequest request) {
        try {
            List<ApiEndpoint> endpoints = openApiIngestionService.getEndpointsForSpec(request.openApiSpecId);
            String testCode = testGenerator.generateTestClass(
                request.baseUrl,
                endpoints,
                request.options.includeNegativeTests,
                request.options.includeContractValidation
            );

            return ResponseEntity.ok(Map.of(
                "status", "success",
                "code", testCode,
                "lines", testCode.split("\n").length
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "status", "error",
                "message", e.getMessage()
            ));
        }
    }

    public static class GeneratorRequest {
        public String baseUrl;
        public Long openApiSpecId;
        public Auth auth = new Auth();
        public Options options = new Options();

        public static class Auth {
            public String type = "NONE";
            public String token;
            public String username;
            public String password;
        }

        public static class Options {
            public boolean includeNegativeTests = true;
            public boolean includeContractValidation = false;
        }
    }
}
