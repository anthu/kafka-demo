package com.dynatrace.kafkademo.Heartbeat;

import com.dynatrace.kafkademo.util.Constants;
import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class Heartbeat {
    Tracer tracer = GlobalOpenTelemetry.getTracer("HeartbeatTracer");

    @Bean
    public NewTopic heartbeatTopic() {
        return TopicBuilder.name(Constants.HEARTBEAT_TOPIC)
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Bean
    public ApplicationRunner runner(KafkaTemplate<String, String> template) {
        return args -> {
            Span span = tracer.spanBuilder("runnerSpan").startSpan();
            while (true) {
                template.send(Constants.HEARTBEAT_TOPIC, (new Date()).toString());
                Thread.sleep(5000);
            }
        };
    }

    public static String extractHeartbeatFromRecord(ConsumerRecord<String, String> record) {
        return record.value();
    }
}
