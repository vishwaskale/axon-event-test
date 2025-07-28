package com.example.axon;

import org.axonframework.extensions.kafka.eventhandling.consumer.streamable.StreamableKafkaMessageSource;
import org.axonframework.extensions.kafka.eventhandling.consumer.ConsumerFactory;
import org.axonframework.extensions.kafka.eventhandling.consumer.Fetcher;
import org.axonframework.serialization.Serializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.Arrays;

@Configuration
public class KafkaConfig {
    
    @Bean
    public StreamableKafkaMessageSource<String, byte[]> kafkaMessageSource(
            @Autowired ConsumerFactory<String, byte[]> consumerFactory,
            @Autowired Fetcher fetcher,
            @Autowired Serializer serializer) {
        return StreamableKafkaMessageSource.<String, byte[]>builder()
                .topics(Arrays.asList("Axon.Events"))
                .consumerFactory(consumerFactory)
                .fetcher(fetcher)
                .serializer(serializer)
                .build();
    }
}
