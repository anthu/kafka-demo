package com.dynatrace.kafkademo;

import com.dynatrace.kafkademo.util.Constants;
import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class ConsoleConsumer {
    private static final String INSTRUMENTATION_NAME = ConsoleConsumer.class.getName();
    private final Tracer tracer = GlobalOpenTelemetry.getTracer(INSTRUMENTATION_NAME);

    public static final String KAFKA_CONSUMER_ID = "console-consumer";

    @KafkaListener(id = KAFKA_CONSUMER_ID, topics = {Constants.HEARTBEAT_TOPIC, Constants.VISITORS_TOPIC})
    public void consoleListener(ConsumerRecord<String, String> in) {
        Span span = tracer
                .spanBuilder("consoleListenerSpan")
                .setAttribute("topic", in.topic())
                .setAttribute("type", "String")
                .startSpan();

        System.out.println(String.format("[%20s] %s", in.topic(), in.value()));

        span.end();
    }
}
