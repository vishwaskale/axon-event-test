package com.example.axon;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.context.SpringBootTest;
import org.axonframework.eventhandling.gateway.EventGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestPropertySource;
import static org.assertj.core.api.Assertions.assertThat;
import java.util.concurrent.TimeUnit;

@SpringBootTest
@TestPropertySource(properties = {
    "axon.axonserver.enabled=false",
    "axon.eventhandling.processors.kafka.mode=subscribing"
})
public class AxonOrderingIdempotencyPoisonPillTest {
    
    @Autowired
    private EventGateway eventGateway;
    
    @Autowired
    private TestEventHandler testEventHandler;

    @BeforeEach
    void setUp() {
        // Clear any previous state
        testEventHandler.clearState();
    }

    @Test
    void testEventOrdering() throws InterruptedException {
        // Given: Multiple events in sequence
        String[] events = {"event1", "event2", "event3"};
        
        // When: Events are published
        for (String event : events) {
            eventGateway.publish(new TestEvent(event));
        }
        
        // Wait for async processing
        Thread.sleep(100);
        
        // Then: Events should be received in order
        assertThat(testEventHandler.getReceivedEvents())
            .containsExactly("event1", "event2", "event3");
    }

    @Test
    void testIdempotency() throws InterruptedException {
        // Given: Same event sent multiple times
        String event = "idempotent-event";
        
        // When: Same event is published twice
        eventGateway.publish(new TestEvent(event));
        eventGateway.publish(new TestEvent(event));
        
        // Wait for async processing
        Thread.sleep(100);
        
        // Then: Event should be processed only once
        assertThat(testEventHandler.getProcessedEvents()).hasSize(1);
        assertThat(testEventHandler.getProcessedEvents()).contains(event);
        assertThat(testEventHandler.getReceivedEvents()).hasSize(2); // Both received
    }

    @Test
    void testPoisonPillHandling() throws InterruptedException {
        // Given: A poison pill event
        String poisonEvent = "poison-pill";
        String normalEvent = "normal-event";
        
        // When: Poison pill and normal events are published
        eventGateway.publish(new TestEvent(poisonEvent));
        eventGateway.publish(new TestEvent(normalEvent));
        
        // Wait for async processing
        Thread.sleep(100);
        
        // Then: Both events should be received, poison pill should be logged as error
        assertThat(testEventHandler.getReceivedEvents()).contains(poisonEvent, normalEvent);
        assertThat(testEventHandler.getPoisonPillEvents()).contains(poisonEvent);
        assertThat(testEventHandler.getPoisonPillEvents()).doesNotContain(normalEvent);
    }

    @Test
    void testEventProcessingFlow() throws InterruptedException {
        // Given: Mix of normal and duplicate events
        eventGateway.publish(new TestEvent("event-A"));
        eventGateway.publish(new TestEvent("event-B"));
        eventGateway.publish(new TestEvent("event-A")); // Duplicate
        eventGateway.publish(new TestEvent("poison-pill"));
        
        // Wait for async processing
        Thread.sleep(100);
        
        // Then: Verify complete flow
        assertThat(testEventHandler.getReceivedEvents()).hasSize(4);
        assertThat(testEventHandler.getProcessedEvents()).hasSize(3); // A, B, poison-pill (only unique)
        assertThat(testEventHandler.getPoisonPillEvents()).hasSize(1);
    }
}
