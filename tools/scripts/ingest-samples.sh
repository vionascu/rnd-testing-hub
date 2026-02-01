#!/bin/bash

# Ingest sample data into rnd-testing-hub

set -e

API_URL="${API_URL:-http://localhost:8080}"
echo "Using API URL: $API_URL"

# Check if API is running
if ! curl -s "$API_URL/health" > /dev/null; then
    echo "ERROR: API is not running at $API_URL"
    echo "Start it with: gradle bootRun"
    exit 1
fi

echo "API is healthy. Proceeding with ingestion..."

# Upload OpenAPI spec
echo ""
echo "Uploading OpenAPI spec..."
OPENAPI_RESPONSE=$(curl -s -X POST -F "file=@tools/sample-openapi/openapi.yaml" "$API_URL/api/openapi/upload")
echo "$OPENAPI_RESPONSE" | jq .
SPEC_ID=$(echo "$OPENAPI_RESPONSE" | jq -r '.specId')
echo "✓ OpenAPI spec uploaded with ID: $SPEC_ID"

# Upload JUnit report
echo ""
echo "Uploading JUnit report..."
JUNIT_RESPONSE=$(curl -s -X POST -F "file=@tools/sample-reports/junit.xml" "$API_URL/api/junit/upload")
echo "$JUNIT_RESPONSE" | jq .
SUITE_IDS=($(echo "$JUNIT_RESPONSE" | jq -r '.suiteIds[]'))
echo "✓ JUnit report uploaded with suite IDs: ${SUITE_IDS[@]}"

# Display metrics
echo ""
echo "=== METRICS SUMMARY ==="
METRICS=$(curl -s "$API_URL/api/metrics/summary?days=30")
echo "$METRICS" | jq .

# Display practices
echo ""
echo "=== BEST PRACTICES ==="
PRACTICES=$(curl -s "$API_URL/api/practices")
echo "Found $(echo "$PRACTICES" | jq '.total') practices"
echo "$PRACTICES" | jq '.practices[] | {title, slug}'

# Display endpoints
echo ""
echo "=== API ENDPOINTS ==="
ENDPOINTS=$(curl -s "$API_URL/api/openapi/$SPEC_ID/endpoints")
echo "Found $(echo "$ENDPOINTS" | jq '.total') endpoints"
echo "$ENDPOINTS" | jq '.endpoints[] | {method, path, summary}'

echo ""
echo "✓ Sample ingestion complete!"
echo ""
echo "Next steps:"
echo "1. View full metrics: curl $API_URL/api/metrics/summary"
echo "2. Preview test generation: curl -X POST -H 'Content-Type: application/json' -d '{\"baseUrl\": \"http://localhost:8081\", \"openApiSpecId\": $SPEC_ID, \"options\": {}}' $API_URL/api/generator/preview"
echo "3. Generate tests: curl -X POST -H 'Content-Type: application/json' -d '{\"baseUrl\": \"http://localhost:8081\", \"openApiSpecId\": $SPEC_ID, \"options\": {}}' $API_URL/api/generator/restassured > tests.zip"
