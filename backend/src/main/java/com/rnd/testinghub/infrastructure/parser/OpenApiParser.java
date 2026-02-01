package com.rnd.testinghub.infrastructure.parser;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.parser.OpenAPIV3Parser;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class OpenApiParser {

    public static class EndpointInfo {
        public final String method;
        public final String path;
        public final String summary;
        public final String description;

        public EndpointInfo(String method, String path, String summary, String description) {
            this.method = method;
            this.path = path;
            this.summary = summary;
            this.description = description;
        }
    }

    public static class ApiInfo {
        public final String title;
        public final String version;
        public final List<EndpointInfo> endpoints;

        public ApiInfo(String title, String version, List<EndpointInfo> endpoints) {
            this.title = title;
            this.version = version;
            this.endpoints = endpoints;
        }
    }

    public ApiInfo parse(String specContent) throws Exception {
        OpenAPIV3Parser parser = new OpenAPIV3Parser();
        OpenAPI openAPI = parser.readContents(specContent).getOpenAPI();

        if (openAPI == null) {
            throw new IllegalArgumentException("Failed to parse OpenAPI spec");
        }

        String title = openAPI.getInfo().getTitle();
        String version = openAPI.getInfo().getVersion();
        List<EndpointInfo> endpoints = extractEndpoints(openAPI);

        return new ApiInfo(title, version, endpoints);
    }

    private List<EndpointInfo> extractEndpoints(OpenAPI openAPI) {
        List<EndpointInfo> endpoints = new ArrayList<>();

        if (openAPI.getPaths() == null) {
            return endpoints;
        }

        for (Map.Entry<String, PathItem> pathEntry : openAPI.getPaths().entrySet()) {
            String path = pathEntry.getKey();
            PathItem pathItem = pathEntry.getValue();

            // Extract all HTTP methods for this path
            if (pathItem.getGet() != null) {
                endpoints.add(new EndpointInfo("GET", path, pathItem.getGet().getSummary(), pathItem.getGet().getDescription()));
            }
            if (pathItem.getPost() != null) {
                endpoints.add(new EndpointInfo("POST", path, pathItem.getPost().getSummary(), pathItem.getPost().getDescription()));
            }
            if (pathItem.getPut() != null) {
                endpoints.add(new EndpointInfo("PUT", path, pathItem.getPut().getSummary(), pathItem.getPut().getDescription()));
            }
            if (pathItem.getDelete() != null) {
                endpoints.add(new EndpointInfo("DELETE", path, pathItem.getDelete().getSummary(), pathItem.getDelete().getDescription()));
            }
            if (pathItem.getPatch() != null) {
                endpoints.add(new EndpointInfo("PATCH", path, pathItem.getPatch().getSummary(), pathItem.getPatch().getDescription()));
            }
            if (pathItem.getHead() != null) {
                endpoints.add(new EndpointInfo("HEAD", path, pathItem.getHead().getSummary(), pathItem.getHead().getDescription()));
            }
        }

        return endpoints;
    }
}
