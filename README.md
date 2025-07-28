# AXON Topic Test Microservices

## Overview
This project demonstrates event-driven communication between two Spring Boot microservices (`axon-service-a` and `axon-service-b`) using Axon. It includes Avro/JSON schema validation, event replay, ordering, idempotency, retries, and poison pill handling—all with and without Kafka.

## Microservices
- **axon-service-a**: Emits events via REST endpoints using Axon (port 9090).
- **axon-service-b**: Consumes and processes events using Axon event handlers (port 8081 or your configured port).

## How to Run
1. **Build the services:**
   ```zsh
   mvn clean install -f axon-service-a/pom.xml
   mvn clean install -f axon-service-b/pom.xml
   ```
2. **Start the services:**
   ```zsh
   mvn spring-boot:run -f axon-service-a/pom.xml
   mvn spring-boot:run -f axon-service-b/pom.xml
   ```
3. **Emit events:**
   - Emit a simple event:
     ```zsh
     curl -X POST http://localhost:9090/emit-event -H "Content-Type: application/json" -d '"sample-payload"'
     ```
   - Emit a logical event with a small payload:
     ```zsh
     curl -X POST http://localhost:9090/emit-logical-event
     ```

## Event Types
- `TestEvent`: Simple string payload event.
- `LogicalEvent`: Logical event with type and data fields (e.g., type: `SIMPLE_TYPE`, data: `sample-data`).

## Testing
- Run all tests for a service:
  ```zsh
  cd axon-service-a && mvn test
  cd ../axon-service-b && mvn test
  ```
- Run specific tests (ordering, idempotency, poison pill, Avro validation) using the provided Axon event test classes.

> **Note:** Service A runs on port 9090, Service B runs on port 8081 (or another free port you configure). Update your curl commands and integration points accordingly.

## Notes
- All event flow is handled by Axon; there is no Kafka dependency.
- Service B handles event ordering, idempotency, poison pill detection, and schema validation.

For detailed usage and troubleshooting, see `USER_GUIDE.md`.

## User Guide
- **Event Emission:**
  - Send a POST request to `/emit-event` on Service A to emit a test event.
- **Event Consumption:**
  - Service B automatically consumes and logs events via Axon event handler.
  - Event handler in Service B:
    - Maintains ordering of events.
    - Ensures idempotency (processes each event only once).
    - Detects and handles poison pill events with error logging.
- **Schema Validation:**
  - Avro schema validation is performed in `AvroValidatorTest.java` using MockSchemaRegistryClient.
- **Replay & Ordering:**
  - Replay and ordering tests are implemented in Axon test classes.
- **Idempotency & Retries:**
  - Duplicate event and retry logic is tested in Axon test classes.
- **Poison Pill Handling:**
  - Poison pill scenario is tested in Axon test classes to ensure error handling in event flow.

## How to Run Both Services and Enable Communication

### Prerequisites
- Java 17 or newer
- Maven

### Steps
1. **Build Both Services**
   - From the project root, run:
     ```zsh
     mvn clean install -f axon-service-a/pom.xml
     mvn clean install -f axon-service-b/pom.xml
     ```
2. **Run Both Services**
   - Open two terminal windows/tabs.
   - In the first terminal, start Service A:
     ```zsh
     cd axon-service-a
     mvn spring-boot:run
     ```
   - In the second terminal, start Service B:
     ```zsh
     cd ../axon-service-b
     mvn spring-boot:run
     ```
3. **Verify Communication**
   - Emit an event from Service A using curl or Postman:
     ```zsh
     curl -X POST http://localhost:9090/emit-event -H "Content-Type: application/json" -d '"your-payload"'
     ```
   - Service A will publish the event via Axon.
   - Service B will consume the event and log output (see terminal logs for Service B).

4. **Troubleshooting**
   - Check both service logs for event flow and error handling.
   - If ports conflict, update `application.properties` in each service to use different server ports.

---
For further customization, see `.github/copilot-instructions.md` for workspace-specific coding instructions.
