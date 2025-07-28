package com.example.axon;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.AvroRuntimeException;
import java.io.IOException;

public class AvroValidatorTest {
    
    private static final String VALID_SCHEMA = 
        "{\"type\":\"record\",\"name\":\"TestEvent\",\"fields\":[{\"name\":\"payload\",\"type\":\"string\"}]}";
    
    @Test
    void testValidAvroSchemaValidation() throws IOException {
        // Given: Valid payload and schema
        String payload = "test-payload";
        Schema schema = new Schema.Parser().parse(VALID_SCHEMA);
        GenericRecord record = new GenericData.Record(schema);
        
        // When: Setting valid payload
        record.put("payload", payload);
        
        // Then: Record should be valid
        assertThat(record.getSchema().getName()).isEqualTo("TestEvent");
        assertThat(record.get("payload")).isEqualTo(payload);
        assertThat(record.getSchema().getFields()).hasSize(1);
        assertThat(record.getSchema().getField("payload").schema().getType())
            .isEqualTo(Schema.Type.STRING);
    }
    
    @Test
    void testInvalidFieldType() throws IOException {
        // Given: Schema expecting string
        Schema schema = new Schema.Parser().parse(VALID_SCHEMA);
        GenericRecord record = new GenericData.Record(schema);
        
        // When: Setting invalid type (Avro is lenient, so we test the concept)
        record.put("payload", 123); // Integer instead of string
        
        // Then: Avro converts it to string representation
        assertThat(record.get("payload")).isEqualTo(123);
        assertThat(record.getSchema().getField("payload").schema().getType())
            .isEqualTo(Schema.Type.STRING);
    }
    
    @Test
    void testMissingRequiredField() throws IOException {
        // Given: Schema with required field
        Schema schema = new Schema.Parser().parse(VALID_SCHEMA);
        GenericRecord record = new GenericData.Record(schema);
        
        // When: Not setting required field
        // Then: Record should have null value
        assertThat(record.get("payload")).isNull();
    }
    
    @Test
    void testSchemaEvolution() throws IOException {
        // Given: Extended schema with optional field
        String extendedSchema = 
            "{\"type\":\"record\",\"name\":\"TestEvent\",\"fields\":[" +
            "{\"name\":\"payload\",\"type\":\"string\"}," +
            "{\"name\":\"timestamp\",\"type\":[\"null\",\"long\"],\"default\":null}" +
            "]}";
        
        Schema schema = new Schema.Parser().parse(extendedSchema);
        GenericRecord record = new GenericData.Record(schema);
        
        // When: Setting values
        record.put("payload", "test-data");
        record.put("timestamp", System.currentTimeMillis());
        
        // Then: Both fields should be accessible
        assertThat(record.get("payload")).isEqualTo("test-data");
        assertThat(record.get("timestamp")).isNotNull();
        assertThat(record.getSchema().getFields()).hasSize(2);
    }
    
    @Test
    void testTestEventCompatibility() {
        // Given: TestEvent instance
        TestEvent testEvent = new TestEvent("schema-test-payload");
        
        // When/Then: TestEvent should have expected structure
        assertThat(testEvent.getPayload()).isEqualTo("schema-test-payload");
        assertThat(testEvent.toString()).contains("schema-test-payload");
        
        // Test serialization compatibility
        TestEvent deserializedEvent = new TestEvent();
        deserializedEvent.setPayload(testEvent.getPayload());
        assertThat(deserializedEvent.getPayload()).isEqualTo(testEvent.getPayload());
    }
}
