package com.rnd.testinghub.adapters.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

@Controller
public class WelcomeController {

    @GetMapping("/")
    public String index() {
        return "forward:/index.html";
    }

    @GetMapping("/dashboard")
    public String dashboard() {
        return "forward:/index.html";
    }
}

@RestController
@RequestMapping("/api/info")
class InfoController {

    @GetMapping
    public Map<String, Object> welcome() {
        Map<String, Object> response = new HashMap<>();
        response.put("application", "rnd-testing-hub");
        response.put("version", "0.1.0");
        response.put("status", "UP");
        response.put("description", "R&D Testing Best-Practices Aggregator");
        response.put("message", "Welcome! Use the endpoints below to interact with the application.");

        Map<String, String> endpoints = new HashMap<>();
        endpoints.put("Health Check", "GET /health");
        endpoints.put("Metrics Summary", "GET /api/metrics/summary");
        endpoints.put("Metrics Trends", "GET /api/metrics/trends");
        endpoints.put("API Coverage", "GET /api/metrics/api-coverage");
        endpoints.put("Best Practices", "GET /api/practices");
        endpoints.put("Test Cases", "GET /api/junit/{id}/cases");
        endpoints.put("API Endpoints", "GET /api/openapi/{id}/endpoints");
        endpoints.put("Upload JUnit Report", "POST /api/junit/upload");
        endpoints.put("Upload OpenAPI Spec", "POST /api/openapi/upload");
        endpoints.put("Generate Tests", "POST /api/generator/restassured");
        endpoints.put("Preview Tests", "POST /api/generator/preview");

        response.put("available_endpoints", endpoints);

        Map<String, String> quickStart = new HashMap<>();
        quickStart.put("1_health", "curl http://localhost:8080/health");
        quickStart.put("2_metrics", "curl http://localhost:8080/api/metrics/summary | jq");
        quickStart.put("3_practices", "curl http://localhost:8080/api/practices | jq");
        quickStart.put("4_endpoints", "curl http://localhost:8080/api/openapi/1/endpoints | jq");

        response.put("quick_start", quickStart);

        return response;
    }

    @GetMapping("/api")
    public Map<String, Object> api() {
        Map<String, Object> response = new HashMap<>();
        response.put("application", "rnd-testing-hub");
        response.put("version", "0.1.0");
        response.put("api_base_path", "/api");

        Map<String, Object> resources = new HashMap<>();

        Map<String, String> metrics = new HashMap<>();
        metrics.put("summary", "GET /api/metrics/summary");
        metrics.put("trends", "GET /api/metrics/trends");
        metrics.put("api_coverage", "GET /api/metrics/api-coverage");
        resources.put("metrics", metrics);

        Map<String, String> junit = new HashMap<>();
        junit.put("upload", "POST /api/junit/upload");
        junit.put("cases", "GET /api/junit/{id}/cases");
        resources.put("junit", junit);

        Map<String, String> openapi = new HashMap<>();
        openapi.put("upload", "POST /api/openapi/upload");
        openapi.put("endpoints", "GET /api/openapi/{id}/endpoints");
        resources.put("openapi", openapi);

        Map<String, String> practices = new HashMap<>();
        practices.put("list", "GET /api/practices");
        resources.put("practices", practices);

        Map<String, String> generator = new HashMap<>();
        generator.put("restassured", "POST /api/generator/restassured");
        generator.put("preview", "POST /api/generator/preview");
        resources.put("generator", generator);

        response.put("resources", resources);

        return response;
    }
}
