package com.rnd.testinghub.adapters.web;

import com.rnd.testinghub.application.OpenApiIngestionService;
import com.rnd.testinghub.domain.ApiEndpoint;
import com.rnd.testinghub.domain.ApiSpec;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/openapi")
public class OpenApiController {

    private final OpenApiIngestionService openApiIngestionService;

    public OpenApiController(OpenApiIngestionService openApiIngestionService) {
        this.openApiIngestionService = openApiIngestionService;
    }

    @PostMapping("/upload")
    public ResponseEntity<?> uploadOpenApi(@RequestParam("file") MultipartFile file) {
        try {
            String specContent = new String(file.getBytes(), StandardCharsets.UTF_8);
            String specFormat = file.getOriginalFilename().endsWith(".yaml") || file.getOriginalFilename().endsWith(".yml")
                    ? "yaml"
                    : "json";

            Long specId = openApiIngestionService.ingestOpenApiSpec(specContent, specFormat);

            return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "OpenAPI spec uploaded and parsed",
                "specId", specId
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                "status", "error",
                "message", e.getMessage()
            ));
        }
    }

    @GetMapping("/{specId}")
    public ResponseEntity<?> getSpec(@PathVariable Long specId) {
        ApiSpec spec = openApiIngestionService.getSpec(specId);
        if (spec == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(spec);
    }

    @GetMapping("/{specId}/endpoints")
    public ResponseEntity<?> getEndpoints(@PathVariable Long specId) {
        List<ApiEndpoint> endpoints = openApiIngestionService.getEndpointsForSpec(specId);
        return ResponseEntity.ok(Map.of(
            "specId", specId,
            "endpoints", endpoints,
            "total", endpoints.size()
        ));
    }
}
