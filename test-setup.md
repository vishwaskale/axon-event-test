# Testing the Kafka Event Flow

## Summary of Changes Made

### Issues Fixed:
1. **Service A** - Added proper Kafka configuration for event publishing
2. **Service B** - Fixed Kafka configuration for event consumption
3. **Both Services** - Updated application properties for proper Kafka integration
4. **Event Classes** - Made TestEvent serializable with proper constructors

### Key Configuration Changes:

#### Service A (Publisher):
- Enabled Kafka event publishing with `axon.kafka.publisher.enabled=true`
- Configured Kafka producer properties
- Added proper serializers for Kafka messages

#### Service B (Consumer):
- Configured tracking event processor to use Kafka message source
- Set up proper Kafka consumer properties
- Configured consumer group and offset management

## Testing Steps:

1. **Start Kafka** (make sure Kafka is running on localhost:9092)

2. **Start Service B** (Consumer):
   ```bash
   cd "/Users/e070464/Desktop/AXON Topic Test/axon-service-b"
   mvn spring-boot:run
   ```

3. **Start Service A** (Publisher):
   ```bash
   cd "/Users/e070464/Desktop/AXON Topic Test/axon-service-a"
   mvn spring-boot:run
   ```

4. **Send a test event**:
   ```bash
   curl -X POST http://localhost:9090/emit-event \
        -H "Content-Type: application/json" \
        -d "Hello from Service A"
   ```

5. **Check Service B logs** - You should see:
   - Kafka consumer connecting and subscribing to "Axon.Events" topic
   - Event received and processed by TestEventHandler
   - Log messages: "Received event: Hello from Service A" and "Processed event: Hello from Service A"

## Expected Behavior:
- Service A publishes events to Kafka topic "Axon.Events"
- Service B consumes events from the same topic
- Events are properly deserialized and handled by the TestEventHandler
- Logs show the complete flow from publishing to consumption

## Troubleshooting:
- Check Kafka is running and accessible
- Verify both services start without errors
- Check logs for any Kafka connection issues
- Ensure topic "Axon.Events" is created (auto-created by default)