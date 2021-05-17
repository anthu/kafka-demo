package com.dynatrace.kafkademo;

import org.apache.kafka.clients.admin.NewTopic;
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

	public static void main(String[] args) {
		SpringApplication.run(KafkaDemoApplication.class, args);
	}

	@Bean
	public NewTopic topic() {
		return TopicBuilder.name("topic1")
				.partitions(10)
				.replicas(1)
				.build();
	}

	@Bean
	public ApplicationRunner runner(KafkaTemplate<String, String> template) {
		return args -> {
			while (true){
				template.send("topic1", (new Date()).toString());
				Thread.sleep(5000);
			}
		};
	}

	@KafkaListener(id = "example-listener", topics = "topic1")
	public void listen(String in) {
		System.out.println(in);
	}
}
