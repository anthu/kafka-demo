package com.dynatrace.kafkademo.message;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Serializer;

import java.io.IOException;
import java.util.Map;

public class TracableMessageSerializer implements Serializer<TracableMessage> {
    private ObjectMapper objectMapper = new ObjectMapper();
    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
    }

    @Override
    public byte[] serialize(String topic, TracableMessage message)
    {
        if (message == null) {
            return null;
        }
        try {
            return objectMapper.writeValueAsBytes(message);
        } catch (IOException | RuntimeException e) {
            throw new SerializationException("Error serializing value", e);
        }
    }

    @Override
    public void close() {
    }
}
