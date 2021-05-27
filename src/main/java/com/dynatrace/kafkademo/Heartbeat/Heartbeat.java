package com.dynatrace.kafkademo.Heartbeat;

import com.dynatrace.kafkademo.util.Constants;
import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class Heartbeat {
    private static final String INSTRUMENTATION_NAME = Heartbeat.class.getName();
    private final Tracer tracer = GlobalOpenTelemetry.getTracer(INSTRUMENTATION_NAME);

    @Autowired
    KafkaTemplate<String, String> template;

    @Bean
    public NewTopic heartbeatTopic() {
        return TopicBuilder.name(Constants.HEARTBEAT_TOPIC)
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Scheduled(fixedRate = 5000)
    public void heartbeatSchedule() {
        String timestamp = (new Date()).toString();
        Span span =
                tracer
                    .spanBuilder("pushing Heartbeat")
                    .startSpan();

        template.send(Constants.HEARTBEAT_TOPIC, timestamp);
        span.end();
    }

    public static String extractHeartbeatFromRecord(ConsumerRecord<String, String> record) {
        return record.value();
    }
}
