package com.rnd.testinghub.application;

import com.rnd.testinghub.adapters.persistence.ApiEndpointRepository;
import com.rnd.testinghub.adapters.persistence.ApiSpecRepository;
import com.rnd.testinghub.domain.ApiEndpoint;
import com.rnd.testinghub.domain.ApiSpec;
import com.rnd.testinghub.infrastructure.parser.OpenApiParser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class OpenApiIngestionService {

    private final ApiSpecRepository apiSpecRepository;
    private final ApiEndpointRepository apiEndpointRepository;
    private final OpenApiParser openApiParser;

    public OpenApiIngestionService(ApiSpecRepository apiSpecRepository,
                                   ApiEndpointRepository apiEndpointRepository,
                                   OpenApiParser openApiParser) {
        this.apiSpecRepository = apiSpecRepository;
        this.apiEndpointRepository = apiEndpointRepository;
        this.openApiParser = openApiParser;
    }

    @Transactional
    public Long ingestOpenApiSpec(String specContent, String specFormat) throws Exception {
        // Parse the OpenAPI spec
        OpenApiParser.ApiInfo apiInfo = openApiParser.parse(specContent);

        // Generate upload source ID
        String uploadSourceId = UUID.randomUUID().toString();

        // Save API spec
        ApiSpec apiSpec = new ApiSpec(
            apiInfo.title,
            apiInfo.title,
            apiInfo.version,
            specContent,
            specFormat,
            uploadSourceId
        );
        apiSpec = apiSpecRepository.save(apiSpec);

        // Save endpoints
        for (OpenApiParser.EndpointInfo endpointInfo : apiInfo.endpoints) {
            ApiEndpoint endpoint = new ApiEndpoint(
                apiSpec,
                endpointInfo.method,
                endpointInfo.path,
                endpointInfo.summary,
                endpointInfo.description
            );
            apiEndpointRepository.save(endpoint);
        }

        return apiSpec.getId();
    }

    public List<ApiEndpoint> getEndpointsForSpec(Long specId) {
        return apiEndpointRepository.findByApiSpecId(specId);
    }

    public ApiSpec getSpec(Long specId) {
        return apiSpecRepository.findById(specId).orElse(null);
    }
}
