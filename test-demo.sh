#!/bin/bash

echo "🧪 AXON MICROSERVICES TESTING DEMO"
echo "=================================="
echo ""

# Colors for output
GREEN='\033[0;32m'
BLUE='\033[0;34m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

echo -e "${BLUE}📋 Running Unit Tests...${NC}"
echo ""

echo -e "${YELLOW}1. Testing Event Ordering, Idempotency & Poison Pill Detection${NC}"
cd "axon-service-b"
mvn test -Dtest=SimpleFeatureTest -q
if [ $? -eq 0 ]; then
    echo -e "${GREEN}✅ Core functionality tests PASSED${NC}"
else
    echo -e "${RED}❌ Core functionality tests FAILED${NC}"
fi
echo ""

echo -e "${YELLOW}2. Testing Schema Validation${NC}"
mvn test -Dtest=AvroValidatorTest -q
if [ $? -eq 0 ]; then
    echo -e "${GREEN}✅ Schema validation tests PASSED${NC}"
else
    echo -e "${RED}❌ Schema validation tests FAILED${NC}"
fi
echo ""

echo -e "${BLUE}🚀 Manual Testing with Live Services${NC}"
echo ""
echo "To test with live services, run these commands in separate terminals:"
echo ""
echo -e "${YELLOW}Terminal 1 - Start Service B:${NC}"
echo "cd axon-service-b && mvn spring-boot:run"
echo ""
echo -e "${YELLOW}Terminal 2 - Start Service A:${NC}"
echo "cd axon-service-a && mvn spring-boot:run"
echo ""
echo -e "${YELLOW}Terminal 3 - Test Commands:${NC}"
echo ""
echo "# Test Event Ordering:"
echo 'curl -X POST http://localhost:9090/emit-event -H "Content-Type: application/json" -d '"'"'"event1"'"'"
echo 'curl -X POST http://localhost:9090/emit-event -H "Content-Type: application/json" -d '"'"'"event2"'"'"
echo 'curl -X POST http://localhost:9090/emit-event -H "Content-Type: application/json" -d '"'"'"event3"'"'"
echo ""
echo "# Test Idempotency:"
echo 'curl -X POST http://localhost:9090/emit-event -H "Content-Type: application/json" -d '"'"'"duplicate-test"'"'"
echo 'curl -X POST http://localhost:9090/emit-event -H "Content-Type: application/json" -d '"'"'"duplicate-test"'"'"
echo ""
echo "# Test Poison Pill:"
echo 'curl -X POST http://localhost:9090/emit-event -H "Content-Type: application/json" -d '"'"'"poison-pill"'"'"
echo ""
echo -e "${GREEN}📊 Check Service B logs for:${NC}"
echo "- INFO: Received event: [payload]"
echo "- INFO: Processed event: [payload]"
echo "- WARN: Duplicate event ignored: [payload]"
echo "- ERROR: Poison pill detected! Event: poison-pill"
echo ""
echo -e "${BLUE}📖 For detailed testing guide, see: TEST_GUIDE.md${NC}"