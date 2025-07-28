# AXON Topic Test Microservices - User Guide

## Introduction
This guide explains how to use, run, and test the AXON Topic Test microservices project. The project demonstrates event-driven communication between two Spring Boot microservices using Axon, with Avro/JSON schema validation and robust event testing.

## Prerequisites
- Java 17 or newer
- Maven

## Setup Steps
### 1. Build Microservices
- From the project root, run:
  ```zsh
  mvn clean install -f axon-service-a/pom.xml
  mvn clean install -f axon-service-b/pom.xml
  ```

### 2. Run Microservices
- In separate terminals, start each service:
  ```zsh
  mvn spring-boot:run -f axon-service-a/pom.xml
  mvn spring-boot:run -f axon-service-b/pom.xml
  ```

### 3. Emit Events
- To emit a simple event from Service A (now running on port 9090):
  ```zsh
  curl -X POST http://localhost:9090/emit-event -H "Content-Type: application/json" -d '"sample-payload"'
  ```
  - This will send a string payload to Service A, which emits an Axon event (`TestEvent`).

- To emit a logical event with a small sample payload:
  ```zsh
  curl -X POST http://localhost:9090/emit-logical-event
  ```
  - This will emit a logical event with type `SIMPLE_TYPE` and data `sample-data`.

- Service B will consume and process these events using Axon event handlers (default port 8081, or your configured port).

### 4. Run Tests
- To run all tests for a service:
  ```zsh
  cd axon-service-a && mvn test
  cd ../axon-service-b && mvn test
  ```
- To run only Axon event flow tests:
  ```zsh
  mvn -Dtest=AxonOrderingIdempotencyPoisonPillTest test -f axon-service-a/pom.xml
  mvn -Dtest=AxonOrderingIdempotencyPoisonPillTest test -f axon-service-b/pom.xml
  ```
- To run only Axon event emission/replay tests:
  ```zsh
  mvn -Dtest=AxonEventTest test -f axon-service-a/pom.xml
  mvn -Dtest=AxonEventTest test -f axon-service-b/pom.xml
  ```
- To run only Avro validator tests:
  ```zsh
  mvn -Dtest=AvroValidatorTest test -f axon-service-a/pom.xml
  mvn -Dtest=AvroValidatorTest test -f axon-service-b/pom.xml
  ```

> **Note:** Service A runs on port 9090, Service B runs on port 8081 (or another free port you configure). Update your curl commands and integration points accordingly.

## Event Handler Logic (Service B)
- Maintains a list of received events for ordering.
- Processes each event only once (idempotency).
- Detects and handles poison pill events with error logging and custom error handling.

## Advanced Usage
- Extend event types and handlers as needed.
- Implement additional test logic for custom event scenarios.
- Integrate with a real Schema Registry for production Avro validation.

## Troubleshooting
- Check Maven and Java versions if build issues occur.
- Review logs for event flow and error handling.
- If ports conflict, update `application.properties` in each service to use different server ports.

---
For more details, see the main `README.md` and `.github/copilot-instructions.md` for workspace-specific instructions.
