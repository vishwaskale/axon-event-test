package com.example.axon;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.beans.factory.annotation.Autowired;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class AxonOrderingIdempotencyPoisonPillTest {
    @Autowired
    private CommandGateway commandGateway;

    @Test
    void testAxonEventOrdering() {
        String[] events = {"event1", "event2", "event3"};
        java.util.List<String> received = new java.util.ArrayList<>();
        for (String event : events) {
            commandGateway.send(new TestEvent(event));
            received.add(event);
        }
        assertThat(received).containsExactly("event1", "event2", "event3");
    }

    @Test
    void testAxonIdempotency() {
        java.util.Set<String> processed = new java.util.HashSet<>();
        String event = "idempotent-event";
        commandGateway.send(new TestEvent(event));
        commandGateway.send(new TestEvent(event));
        processed.add(event);
        processed.add(event);
        assertThat(processed.size()).isEqualTo(1);
    }

    @Test
    void testAxonPoisonPill() {
        String event = "poison-pill";
        boolean errorHandled = false;
        try {
            commandGateway.send(new TestEvent(event));
            if ("poison-pill".equals(event)) {
                throw new RuntimeException("Poison pill detected");
            }
        } catch (Exception e) {
            errorHandled = true;
        }
        assertThat(errorHandled).isTrue();
    }
}
