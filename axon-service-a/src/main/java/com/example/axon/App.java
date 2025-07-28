package com.example.axon;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.*;
import org.axonframework.eventhandling.gateway.EventGateway;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Hello world!
 *
 */
@SpringBootApplication
@RestController
public class App 
{
    @Autowired
    private EventGateway eventGateway;

    public static void main( String[] args )
    {
        SpringApplication.run(App.class, args);
    }

    @PostMapping("/emit-event")
    public String emitEvent(@RequestBody String payload) {
        // Send event to Axon via EventGateway
        eventGateway.publish(new TestEvent(payload));
        return "Event emitted: " + payload;
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
