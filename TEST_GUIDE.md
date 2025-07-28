# Unit Testing Guide for Axon Service B

This guide shows how to run unit tests to check idempotency, poison pill handling, schema validation, and other features.

## 🧪 Available Tests

### 1. **Schema Validation Tests** (`AvroValidatorTest`)
Tests Avro schema validation and compatibility:

```bash
# Run schema validation tests
cd axon-service-b
mvn test -Dtest=AvroValidatorTest

# Or run specific test methods
mvn test -Dtest=AvroValidatorTest#testValidAvroSchemaValidation
mvn test -Dtest=AvroValidatorTest#testSchemaEvolution
mvn test -Dtest=AvroValidatorTest#testTestEventCompatibility
```

**What it tests:**
- ✅ Valid Avro schema validation
- ✅ Schema evolution compatibility  
- ✅ TestEvent serialization/deserialization
- ✅ Field type validation
- ✅ Missing field handling

### 2. **Event Processing Tests** (`AxonEventTest`)
Tests basic event emission and handling:

```bash
# Run event processing tests
mvn test -Dtest=AxonEventTest

# Specific tests
mvn test -Dtest=AxonEventTest#testEventEmissionAndHandling
mvn test -Dtest=AxonEventTest#testEventReplayOrdering
mvn test -Dtest=AxonEventTest#testEventSerialization
```

**What it tests:**
- ✅ Event emission and handling
- ✅ Event ordering during replay
- ✅ Event serialization with special characters
- ✅ Event handler state management

### 3. **Advanced Features Tests** (`AxonOrderingIdempotencyPoisonPillTest`)
Tests ordering, idempotency, and poison pill handling:

```bash
# Run advanced feature tests
mvn test -Dtest=AxonOrderingIdempotencyPoisonPillTest

# Specific tests
mvn test -Dtest=AxonOrderingIdempotencyPoisonPillTest#testEventOrdering
mvn test -Dtest=AxonOrderingIdempotencyPoisonPillTest#testIdempotency
mvn test -Dtest=AxonOrderingIdempotencyPoisonPillTest#testPoisonPillHandling
```

**What it tests:**
- ✅ **Event Ordering**: Events are processed in the correct sequence
- ✅ **Idempotency**: Duplicate events are handled correctly (received but not reprocessed)
- ✅ **Poison Pill Detection**: Malicious events are detected and logged
- ✅ **Complete Event Flow**: Mixed scenarios with normal, duplicate, and poison events

## 🚀 Running All Tests

```bash
# Run all tests
cd axon-service-b
mvn test

# Run tests with detailed output
mvn test -X

# Run tests and generate reports
mvn test surefire-report:report
```

## 📊 Test Results Interpretation

### ✅ **Successful Test Output Example:**
```
[INFO] Tests run: 13, Failures: 0, Errors: 0, Skipped: 0
[INFO] 
[INFO] Results:
[INFO] 
[INFO] Tests run: 13, Failures: 0, Errors: 0, Skipped: 0
```

### 📋 **Test Coverage:**

| Feature | Test Method | Status | Description |
|---------|-------------|--------|-------------|
| **Schema Validation** | `testValidAvroSchemaValidation` | ✅ | Validates Avro schema structure |
| **Schema Evolution** | `testSchemaEvolution` | ✅ | Tests backward compatibility |
| **Event Serialization** | `testTestEventCompatibility` | ✅ | Tests TestEvent serialization |
| **Event Ordering** | `testEventOrdering` | ✅ | Verifies event sequence preservation |
| **Idempotency** | `testIdempotency` | ✅ | Ensures duplicate handling |
| **Poison Pill** | `testPoisonPillHandling` | ✅ | Tests malicious event detection |
| **Complete Flow** | `testEventProcessingFlow` | ✅ | End-to-end scenario testing |

## 🔧 Manual Testing with Live Services

You can also test the functionality with running services:

### 1. **Start Services:**
```bash
# Terminal 1: Start Service B
cd axon-service-b
mvn spring-boot:run

# Terminal 2: Start Service A  
cd axon-service-a
mvn spring-boot:run
```

### 2. **Test Event Ordering:**
```bash
curl -X POST http://localhost:9090/emit-event -H "Content-Type: application/json" -d '"event1"'
curl -X POST http://localhost:9090/emit-event -H "Content-Type: application/json" -d '"event2"'
curl -X POST http://localhost:9090/emit-event -H "Content-Type: application/json" -d '"event3"'
```

### 3. **Test Idempotency:**
```bash
curl -X POST http://localhost:9090/emit-event -H "Content-Type: application/json" -d '"duplicate-test"'
curl -X POST http://localhost:9090/emit-event -H "Content-Type: application/json" -d '"duplicate-test"'
```

### 4. **Test Poison Pill:**
```bash
curl -X POST http://localhost:9090/emit-event -H "Content-Type: application/json" -d '"poison-pill"'
```

### 5. **Check Service B Logs:**
Look for these log messages in Service B console:
- `INFO: Received event: [payload]` - Event received
- `INFO: Processed event: [payload]` - Event processed (first time)
- `WARN: Duplicate event ignored: [payload]` - Idempotency working
- `ERROR: Poison pill detected! Event: poison-pill` - Poison pill detected

## 📈 Test Reports

After running tests, check the reports:
```bash
# View test reports
open target/surefire-reports/index.html

# Or check individual test files
ls target/surefire-reports/
```

## 🐛 Troubleshooting

### Common Issues:

1. **Tests not running**: Ensure JUnit 5 dependencies are correct
2. **Spring context failures**: Check application.properties configuration
3. **Kafka connection issues**: Ensure Kafka is running on localhost:9092

### Debug Commands:
```bash
# Run with debug output
mvn test -X -Dtest=AvroValidatorTest

# Check dependencies
mvn dependency:tree

# Compile only
mvn compile test-compile
```

## 🎯 Key Testing Features Demonstrated

✅ **Schema Validation**: Avro schema compatibility and validation  
✅ **Event Ordering**: Sequential event processing verification  
✅ **Idempotency**: Duplicate event detection and handling  
✅ **Poison Pill Detection**: Malicious event identification  
✅ **Error Handling**: Graceful failure management  
✅ **State Management**: Event handler state tracking  
✅ **Serialization**: Event payload integrity  

This comprehensive testing suite ensures your Axon-based microservices handle all critical scenarios correctly!