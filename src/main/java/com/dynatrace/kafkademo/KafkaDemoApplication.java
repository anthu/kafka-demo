package com.dynatrace.kafkademo;

import com.dynatrace.kafkademo.util.Constants;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.kafka.config.TopicBuilder;

@EnableScheduling
@SpringBootApplication
public class KafkaDemoApplication {
	public static void main(String[] args) {
		SpringApplication.run(KafkaDemoApplication.class, args);
	}

	@Bean
	public NewTopic visitorsTopic() {
		return TopicBuilder.name(Constants.VISITORS_TOPIC)
				.partitions(1)
				.replicas(1)
				.build();
	}
}
