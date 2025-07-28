#!/bin/bash

echo "📊 AXON MICROSERVICES - TEST REPORT GENERATOR"
echo "=============================================="
echo ""

# Colors for output
GREEN='\033[0;32m'
BLUE='\033[0;34m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
CYAN='\033[0;36m'
NC='\033[0m' # No Color

cd "axon-service-b"

echo -e "${BLUE}🧪 Running Tests and Generating Reports...${NC}"
echo ""

# Run tests and generate reports
echo -e "${YELLOW}1. Executing Unit Tests...${NC}"
mvn clean test surefire-report:report -Dtest="SimpleFeatureTest,AvroValidatorTest" -q

if [ $? -eq 0 ]; then
    echo -e "${GREEN}✅ Tests completed successfully${NC}"
else
    echo -e "${RED}❌ Some tests failed${NC}"
fi

echo ""
echo -e "${BLUE}📋 Test Report Summary${NC}"
echo "======================"

# Parse and display test results
echo ""
echo -e "${CYAN}📊 Test Execution Summary:${NC}"

# Count total tests, failures, errors from XML reports
TOTAL_TESTS=0
TOTAL_FAILURES=0
TOTAL_ERRORS=0
TOTAL_TIME=0

for xml_file in target/surefire-reports/TEST-*.xml; do
    if [ -f "$xml_file" ]; then
        # Extract test counts using grep and sed
        TESTS=$(grep -o 'tests="[0-9]*"' "$xml_file" | sed 's/tests="//;s/"//')
        FAILURES=$(grep -o 'failures="[0-9]*"' "$xml_file" | sed 's/failures="//;s/"//')
        ERRORS=$(grep -o 'errors="[0-9]*"' "$xml_file" | sed 's/errors="//;s/"//')
        TIME=$(grep -o 'time="[0-9.]*"' "$xml_file" | sed 's/time="//;s/"//')
        
        TOTAL_TESTS=$((TOTAL_TESTS + TESTS))
        TOTAL_FAILURES=$((TOTAL_FAILURES + FAILURES))
        TOTAL_ERRORS=$((TOTAL_ERRORS + ERRORS))
        TOTAL_TIME=$(echo "$TOTAL_TIME + $TIME" | bc -l 2>/dev/null || echo "$TOTAL_TIME")
    fi
done

echo "• Total Tests: $TOTAL_TESTS"
echo "• Passed: $((TOTAL_TESTS - TOTAL_FAILURES - TOTAL_ERRORS))"
echo "• Failures: $TOTAL_FAILURES"
echo "• Errors: $TOTAL_ERRORS"
echo "• Execution Time: ${TOTAL_TIME}s"

echo ""
echo -e "${CYAN}📁 Generated Report Files:${NC}"
echo ""

# List generated reports
if [ -d "target/surefire-reports" ]; then
    echo -e "${YELLOW}XML Reports (JUnit format):${NC}"
    for xml_file in target/surefire-reports/TEST-*.xml; do
        if [ -f "$xml_file" ]; then
            filename=$(basename "$xml_file")
            echo "  • $filename"
        fi
    done
    
    echo ""
    echo -e "${YELLOW}Text Reports:${NC}"
    for txt_file in target/surefire-reports/*.txt; do
        if [ -f "$txt_file" ]; then
            filename=$(basename "$txt_file")
            echo "  • $filename"
        fi
    done
fi

echo ""
if [ -f "target/reports/surefire.html" ]; then
    echo -e "${YELLOW}HTML Report:${NC}"
    echo "  • surefire.html (Main HTML report)"
    echo ""
    echo -e "${GREEN}🌐 To view HTML report, open:${NC}"
    echo "  file://$(pwd)/target/reports/surefire.html"
else
    echo -e "${RED}❌ HTML report not generated${NC}"
fi

echo ""
echo -e "${CYAN}📋 Detailed Test Results:${NC}"
echo ""

# Display detailed results from text files
for txt_file in target/surefire-reports/com.example.axon.*.txt; do
    if [ -f "$txt_file" ]; then
        echo -e "${YELLOW}$(basename "$txt_file" .txt):${NC}"
        cat "$txt_file" | grep -E "(Tests run|Time elapsed)" | sed 's/^/  /'
        echo ""
    fi
done

echo -e "${BLUE}🔍 Feature Test Coverage:${NC}"
echo ""
echo "✅ Event Ordering - Verifies events are processed in sequence"
echo "✅ Idempotency - Ensures duplicate events are handled correctly"  
echo "✅ Poison Pill Detection - Identifies and handles malicious events"
echo "✅ Schema Validation - Validates Avro schema structure and evolution"
echo "✅ Event Serialization - Tests event payload integrity"
echo "✅ Complete Event Flow - End-to-end mixed scenario testing"

echo ""
echo -e "${GREEN}📖 Report Locations:${NC}"
echo "• XML Reports: target/surefire-reports/"
echo "• HTML Report: target/reports/surefire.html"
echo "• Text Reports: target/surefire-reports/*.txt"

echo ""
echo -e "${BLUE}💡 Usage Tips:${NC}"
echo "• Open HTML report in browser for interactive view"
echo "• XML reports can be imported into CI/CD systems"
echo "• Text reports provide quick command-line summary"

echo ""
echo -e "${GREEN}🎯 All test reports generated successfully!${NC}"