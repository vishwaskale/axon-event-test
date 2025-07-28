package com.example.axon;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.config.ProcessingGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SpringBootApplication
public class App {
    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }
}

@Component
@ProcessingGroup("kafka")
class TestEventHandler {
    private static final Logger logger = LoggerFactory.getLogger(TestEventHandler.class);
    private final java.util.List<String> receivedEvents = new java.util.ArrayList<>();
    private final java.util.Set<String> processedEvents = new java.util.HashSet<>();
    private final java.util.List<String> poisonPillEvents = new java.util.ArrayList<>();

    @EventHandler
    public void on(TestEvent event) {
        logger.info("Received event: {}", event.getPayload());
        
        // Ordering: maintain a list of received events
        synchronized (receivedEvents) {
            receivedEvents.add(event.getPayload());
        }
        
        // Idempotency: process only if not seen before
        synchronized (processedEvents) {
            if (!processedEvents.contains(event.getPayload())) {
                processedEvents.add(event.getPayload());
                logger.info("Processed event: {}", event.getPayload());
            } else {
                logger.warn("Duplicate event ignored: {}", event.getPayload());
            }
        }
        
        // Poison pill: handle error scenario
        if ("poison-pill".equals(event.getPayload())) {
            logger.error("Poison pill detected! Event: {}", event.getPayload());
            synchronized (poisonPillEvents) {
                poisonPillEvents.add(event.getPayload());
            }
            // Custom error handling logic here
        }
    }
    
    // Getter methods for testing
    public java.util.List<String> getReceivedEvents() {
        synchronized (receivedEvents) {
            return new java.util.ArrayList<>(receivedEvents);
        }
    }
    
    public java.util.Set<String> getProcessedEvents() {
        synchronized (processedEvents) {
            return new java.util.HashSet<>(processedEvents);
        }
    }
    
    public java.util.List<String> getPoisonPillEvents() {
        synchronized (poisonPillEvents) {
            return new java.util.ArrayList<>(poisonPillEvents);
        }
    }
    
    // Method to clear state for testing
    public void clearState() {
        synchronized (receivedEvents) {
            receivedEvents.clear();
        }
        synchronized (processedEvents) {
            processedEvents.clear();
        }
        synchronized (poisonPillEvents) {
            poisonPillEvents.clear();
        }
        logger.info("TestEventHandler state cleared");
    }
}

class TestEvent {
    private String payload;
    
    // Default constructor for serialization
    public TestEvent() {}
    
    public TestEvent(String payload) { 
        this.payload = payload; 
    }
    
    public String getPayload() { 
        return payload; 
    }
    
    public void setPayload(String payload) {
        this.payload = payload;
    }
    
    @Override
    public String toString() {
        return "TestEvent{payload='" + payload + "'}";
    }
}

@RestController
@RequestMapping("/test")
class TestController {
    
    @Autowired
    private TestEventHandler eventHandler;
    
    @GetMapping("/received-events")
    public java.util.List<String> getReceivedEvents() {
        return eventHandler.getReceivedEvents();
    }
    
    @GetMapping("/processed-events")
    public java.util.Set<String> getProcessedEvents() {
        return eventHandler.getProcessedEvents();
    }
    
    @GetMapping("/poison-pill-events")
    public java.util.List<String> getPoisonPillEvents() {
        return eventHandler.getPoisonPillEvents();
    }
    
    @PostMapping("/clear-state")
    public String clearState() {
        eventHandler.clearState();
        return "State cleared successfully";
    }
}

@RestController
class HealthController {
    
    @GetMapping("/actuator/health")
    public java.util.Map<String, String> health() {
        java.util.Map<String, String> health = new java.util.HashMap<>();
        health.put("status", "UP");
        return health;
    }
}
