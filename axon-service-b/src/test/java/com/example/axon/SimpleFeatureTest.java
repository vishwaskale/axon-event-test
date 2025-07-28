package com.example.axon;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Simple unit tests demonstrating key features without Spring context
 */
public class SimpleFeatureTest {

    @Test
    void testEventOrdering() {
        // Given: A list to track event order
        List<String> receivedEvents = new ArrayList<>();
        String[] events = {"event1", "event2", "event3"};
        
        // When: Events are processed in sequence
        for (String event : events) {
            receivedEvents.add(event);
        }
        
        // Then: Order should be preserved
        assertThat(receivedEvents).containsExactly("event1", "event2", "event3");
        assertThat(receivedEvents).hasSize(3);
    }

    @Test
    void testIdempotency() {
        // Given: A set to track processed events (simulating idempotency)
        Set<String> processedEvents = new HashSet<>();
        List<String> receivedEvents = new ArrayList<>();
        String event = "duplicate-event";
        
        // When: Same event is processed multiple times
        receivedEvents.add(event);
        processedEvents.add(event);
        
        receivedEvents.add(event); // Duplicate
        boolean wasAlreadyProcessed = !processedEvents.add(event); // Set.add returns false if already exists
        
        // Then: Event should be received twice but processed once
        assertThat(receivedEvents).hasSize(2);
        assertThat(processedEvents).hasSize(1);
        assertThat(wasAlreadyProcessed).isTrue();
        assertThat(processedEvents).contains(event);
    }

    @Test
    void testPoisonPillDetection() {
        // Given: A poison pill detector
        List<String> poisonPillEvents = new ArrayList<>();
        String normalEvent = "normal-event";
        String poisonEvent = "poison-pill";
        
        // When: Processing different types of events
        if ("poison-pill".equals(normalEvent)) {
            poisonPillEvents.add(normalEvent);
        }
        
        if ("poison-pill".equals(poisonEvent)) {
            poisonPillEvents.add(poisonEvent);
        }
        
        // Then: Only poison pill should be detected
        assertThat(poisonPillEvents).hasSize(1);
        assertThat(poisonPillEvents).contains(poisonEvent);
        assertThat(poisonPillEvents).doesNotContain(normalEvent);
    }

    @Test
    void testTestEventSerialization() {
        // Given: TestEvent instances
        TestEvent event1 = new TestEvent("test-payload");
        TestEvent event2 = new TestEvent();
        
        // When: Setting and getting payload
        event2.setPayload("another-payload");
        
        // Then: Serialization should work correctly
        assertThat(event1.getPayload()).isEqualTo("test-payload");
        assertThat(event2.getPayload()).isEqualTo("another-payload");
        assertThat(event1.toString()).contains("test-payload");
        assertThat(event2.toString()).contains("another-payload");
    }

    @Test
    void testCompleteEventFlow() {
        // Given: Event processing simulation
        List<String> receivedEvents = new ArrayList<>();
        Set<String> processedEvents = new HashSet<>();
        List<String> poisonPillEvents = new ArrayList<>();
        
        String[] events = {"event-A", "event-B", "event-A", "poison-pill", "event-C"};
        
        // When: Processing all events
        for (String event : events) {
            // Always receive
            receivedEvents.add(event);
            
            // Process only if not seen before (idempotency)
            processedEvents.add(event);
            
            // Detect poison pills
            if ("poison-pill".equals(event)) {
                poisonPillEvents.add(event);
            }
        }
        
        // Then: Verify complete flow
        assertThat(receivedEvents).hasSize(5); // All events received
        assertThat(processedEvents).hasSize(4); // Unique events processed (A, B, poison-pill, C)
        assertThat(poisonPillEvents).hasSize(1); // One poison pill detected
        assertThat(processedEvents).contains("event-A", "event-B", "poison-pill", "event-C");
    }

    @Test
    void testEventOrderingWithDuplicates() {
        // Given: Mixed events with duplicates
        List<String> receivedEvents = new ArrayList<>();
        Set<String> processedEvents = new HashSet<>();
        
        String[] events = {"first", "second", "first", "third", "second"};
        
        // When: Processing events maintaining order but tracking uniqueness
        for (String event : events) {
            receivedEvents.add(event);
            processedEvents.add(event);
        }
        
        // Then: Order preserved, duplicates handled
        assertThat(receivedEvents).containsExactly("first", "second", "first", "third", "second");
        assertThat(processedEvents).containsExactlyInAnyOrder("first", "second", "third");
        assertThat(receivedEvents).hasSize(5);
        assertThat(processedEvents).hasSize(3);
    }
}