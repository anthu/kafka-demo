package com.dynatrace.kafkademo;

import com.dynatrace.kafkademo.util.Constants;
import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.Date;

@SpringBootApplication
public class KafkaDemoApplication {
	Tracer tracer = GlobalOpenTelemetry.getTracer("KafkaTracer");
	public static void main(String[] args) {
		SpringApplication.run(KafkaDemoApplication.class, args);
	}

	@Bean
	public NewTopic heartbeatTopic() {
		return TopicBuilder.name(Constants.HEARTBEAT_TOPIC)
				.partitions(1)
				.replicas(1)
				.build();
	}

	@Bean
	public NewTopic visitorsTopic() {
		return TopicBuilder.name(Constants.VISITORS_TOPIC)
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

	@KafkaListener(id = "kafka-demo-listener", topics = {Constants.HEARTBEAT_TOPIC, Constants.VISITORS_TOPIC})
	public void demoListener(ConsumerRecord<String, String> in) {
		Span span = tracer
				.spanBuilder("listenerSpan")
				.setAttribute("topic", in.topic())
				.setAttribute("type", "String")
				.startSpan();
		try {

			// Add slight delay to processing to simulate distributed services
			Thread.sleep(2000);
			System.out.println(String.format("[%20s] %s", in.topic(), in.value()));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		span.end();
	}
}
