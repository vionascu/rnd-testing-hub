package com.rnd.testinghub.infrastructure.generator;

import com.rnd.testinghub.domain.ApiEndpoint;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RestAssuredTestGenerator {

    public String generateTestClass(String baseUrl, List<ApiEndpoint> endpoints, boolean includeNegativeTests, boolean includeContractValidation) {
        StringBuilder sb = new StringBuilder();

        sb.append("package com.example.generated;\n\n");
        sb.append("import io.restassured.RestAssured;\n");
        sb.append("import io.restassured.response.Response;\n");
        sb.append("import org.junit.jupiter.api.BeforeAll;\n");
        sb.append("import org.junit.jupiter.api.Test;\n");
        sb.append("import com.fasterxml.jackson.databind.JsonNode;\n");
        sb.append("import com.fasterxml.jackson.databind.ObjectMapper;\n\n");
        sb.append("import static io.restassured.RestAssured.*;\n");
        sb.append("import static org.hamcrest.Matchers.*;\n\n");
        sb.append("public class GeneratedApiTests {\n\n");
        sb.append("    private static final String BASE_URL = \"").append(baseUrl).append("\";\n");
        sb.append("    private static ObjectMapper objectMapper;\n\n");

        sb.append("    @BeforeAll\n");
        sb.append("    public static void setup() {\n");
        sb.append("        RestAssured.baseURI = BASE_URL;\n");
        sb.append("        objectMapper = new ObjectMapper();\n");
        sb.append("    }\n\n");

        for (ApiEndpoint endpoint : endpoints) {
            sb.append(generateEndpointTest(endpoint, includeNegativeTests, includeContractValidation));
        }

        sb.append("}\n");
        return sb.toString();
    }

    private String generateEndpointTest(ApiEndpoint endpoint, boolean includeNegativeTests, boolean includeContractValidation) {
        StringBuilder sb = new StringBuilder();
        String testName = generateTestName(endpoint);

        // Happy path test
        sb.append("    @Test\n");
        sb.append("    public void ").append(testName).append("() {\n");

        String path = endpoint.getPath();
        String method = endpoint.getMethod();

        sb.append("        // ").append(endpoint.getSummary() != null ? endpoint.getSummary() : "Test for " + method + " " + path).append("\n");

        switch (method) {
            case "GET":
                sb.append("        given()\n");
                sb.append("            .when()\n");
                sb.append("            .get(\"").append(path).append("\")\n");
                sb.append("            .then()\n");
                sb.append("            .statusCode(anyOf(is(200), is(404)));\n");
                break;
            case "POST":
                sb.append("        String requestBody = \"{\\\"example\\\": \\\"value\\\"}\";\n");
                sb.append("        given()\n");
                sb.append("            .contentType(\"application/json\")\n");
                sb.append("            .body(requestBody)\n");
                sb.append("            .when()\n");
                sb.append("            .post(\"").append(path).append("\")\n");
                sb.append("            .then()\n");
                sb.append("            .statusCode(anyOf(is(201), is(400)));\n");
                break;
            case "PUT":
                sb.append("        String requestBody = \"{\\\"example\\\": \\\"updated\\\"}\";\n");
                sb.append("        given()\n");
                sb.append("            .contentType(\"application/json\")\n");
                sb.append("            .body(requestBody)\n");
                sb.append("            .when()\n");
                sb.append("            .put(\"").append(path).append("\")\n");
                sb.append("            .then()\n");
                sb.append("            .statusCode(anyOf(is(200), is(404)));\n");
                break;
            case "DELETE":
                sb.append("        given()\n");
                sb.append("            .when()\n");
                sb.append("            .delete(\"").append(path).append("\")\n");
                sb.append("            .then()\n");
                sb.append("            .statusCode(anyOf(is(204), is(404)));\n");
                break;
            default:
                sb.append("        // Unsupported method: ").append(method).append("\n");
        }

        sb.append("    }\n\n");

        // Negative test
        if (includeNegativeTests && ("POST".equals(method) || "PUT".equals(method))) {
            sb.append("    @Test\n");
            sb.append("    public void ").append(testName).append("WithInvalidData() {\n");
            sb.append("        String invalidBody = \"{}\";\n");
            sb.append("        given()\n");
            sb.append("            .contentType(\"application/json\")\n");
            sb.append("            .body(invalidBody)\n");
            sb.append("            .when()\n");
            sb.append("            .").append(method.toLowerCase()).append("(\"").append(path).append("\")\n");
            sb.append("            .then()\n");
            sb.append("            .statusCode(is(400));\n");
            sb.append("    }\n\n");
        }

        return sb.toString();
    }

    private String generateTestName(ApiEndpoint endpoint) {
        String method = endpoint.getMethod().toLowerCase();
        String path = endpoint.getPath().replaceAll("[^a-zA-Z0-9]", "");
        return "test" + method.substring(0, 1).toUpperCase() + method.substring(1) + path;
    }

    public String generateGradleBuild() {
        return """
plugins {
    id 'java'
}

group = 'com.example'
version = '1.0.0'
sourceCompatibility = '21'

repositories {
    mavenCentral()
}

dependencies {
    testImplementation 'io.rest-assured:rest-assured:5.4.0'
    testImplementation 'org.junit.jupiter:junit-jupiter:5.10.2'
    testImplementation 'com.fasterxml.jackson.core:jackson-databind:2.17.0'
    testRuntimeOnly 'org.junit.platform:junit-platform-launcher'
}

test {
    useJUnitPlatform()
}
""";
    }

    public String generateReadme(String baseUrl) {
        return "# Generated REST API Tests\n\n" +
            "This is an auto-generated REST API test suite using RestAssured and JUnit 5.\n\n" +
            "## Running Tests\n\n" +
            "```bash\n" +
            "gradle test\n" +
            "```\n\n" +
            "## Configuration\n\n" +
            "Set environment variables for authentication (if needed):\n\n" +
            "```bash\n" +
            "export API_BASE_URL=" + baseUrl + "\n" +
            "export API_TOKEN=your-token-here\n" +
            "```\n\n" +
            "## Customization\n\n" +
            "1. Edit `src/test/java/com/example/generated/GeneratedApiTests.java`\n" +
            "2. Add authentication headers if needed\n" +
            "3. Add request/response validations\n" +
            "4. Add more test scenarios\n\n" +
            "## Test Organization\n\n" +
            "- Happy path tests for each endpoint\n" +
            "- Negative tests for POST/PUT operations\n" +
            "- Schema validation (if enabled)\n\n" +
            "## Notes\n\n" +
            "- This is a baseline test suite\n" +
            "- Add your own assertions and validations\n" +
            "- Mock external dependencies as needed\n";
    }
}
