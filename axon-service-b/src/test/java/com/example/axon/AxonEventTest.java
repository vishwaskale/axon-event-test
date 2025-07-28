package com.example.axon;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.context.SpringBootTest;
import org.axonframework.eventhandling.gateway.EventGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestPropertySource;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@TestPropertySource(properties = {
    "axon.axonserver.enabled=false",
    "axon.eventhandling.processors.kafka.mode=subscribing"
})
public class AxonEventTest {
    
    @Autowired
    private EventGateway eventGateway;
    
    @Autowired
    private TestEventHandler testEventHandler;

    @BeforeEach
    void setUp() {
        testEventHandler.clearState();
    }

    @Test
    void testEventEmissionAndHandling() throws InterruptedException {
        // Given: A test payload
        String payload = "axon-payload";
        
        // When: Event is published
        eventGateway.publish(new TestEvent(payload));
        
        // Wait for async processing
        Thread.sleep(50);
        
        // Then: Event should be received and processed
        assertThat(testEventHandler.getReceivedEvents()).contains(payload);
        assertThat(testEventHandler.getProcessedEvents()).contains(payload);
    }

    @Test
    void testEventReplayOrdering() throws InterruptedException {
        // Given: Multiple events in sequence
        String[] events = {"axon-event1", "axon-event2", "axon-event3"};
        
        // When: Events are published in order
        for (String event : events) {
            eventGateway.publish(new TestEvent(event));
        }
        
        // Wait for async processing
        Thread.sleep(100);
        
        // Then: Events should be received in the same order
        assertThat(testEventHandler.getReceivedEvents())
            .containsExactly("axon-event1", "axon-event2", "axon-event3");
    }
    
    @Test
    void testEventSerialization() {
        // Given: TestEvent with various payloads
        TestEvent simpleEvent = new TestEvent("simple");
        TestEvent jsonEvent = new TestEvent("{\"key\":\"value\"}");
        TestEvent specialCharsEvent = new TestEvent("special-chars: !@#$%^&*()");
        
        // When/Then: Events should maintain their payload integrity
        assertThat(simpleEvent.getPayload()).isEqualTo("simple");
        assertThat(jsonEvent.getPayload()).isEqualTo("{\"key\":\"value\"}");
        assertThat(specialCharsEvent.getPayload()).isEqualTo("special-chars: !@#$%^&*()");
        
        // Test toString method
        assertThat(simpleEvent.toString()).contains("simple");
        assertThat(jsonEvent.toString()).contains("{\"key\":\"value\"}");
    }
    
    @Test
    void testEventHandlerStateManagement() throws InterruptedException {
        // Given: Initial clean state
        assertThat(testEventHandler.getReceivedEvents()).isEmpty();
        assertThat(testEventHandler.getProcessedEvents()).isEmpty();
        
        // When: Publishing events
        eventGateway.publish(new TestEvent("event1"));
        eventGateway.publish(new TestEvent("event2"));
        
        Thread.sleep(50);
        
        // Then: State should be updated
        assertThat(testEventHandler.getReceivedEvents()).hasSize(2);
        assertThat(testEventHandler.getProcessedEvents()).hasSize(2);
        
        // When: Clearing state
        testEventHandler.clearState();
        
        // Then: State should be empty
        assertThat(testEventHandler.getReceivedEvents()).isEmpty();
        assertThat(testEventHandler.getProcessedEvents()).isEmpty();
    }
}
