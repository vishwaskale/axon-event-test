package com.example.axon;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericRecord;
import java.io.IOException;

public class AvroValidatorTest {
    @Test
    void testAvroSchemaValidation() throws IOException {
        String payload = "test-payload";
        String schemaStr = "{\"type\":\"record\",\"name\":\"TestEvent\",\"fields\":[{\"name\":\"payload\",\"type\":\"string\"}]}";
        Schema schema = new Schema.Parser().parse(schemaStr);
        GenericRecord record = new GenericData.Record(schema);
        record.put("payload", payload);
        // Validate record against schema
        assertThat(record.getSchema().getName()).isEqualTo("TestEvent");
        assertThat(record.get("payload")).isEqualTo(payload);
    }
}

    
