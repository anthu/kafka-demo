package com.dynatrace.kafkademo;

import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;

@SpringBootTest
@DirtiesContext
@EmbeddedKafka(partitions = 1, brokerProperties = { "listeners=PLAINTEXT://localhost:9092", "port=9092" })
class KafkaDemoApplicationTests {

	@Autowired
	VisitorsController visitorsController;

	@Autowired
	VisitorsController heartbeatController;

	@Test
	void contextLoads() {
		assertThat(visitorsController).isNotNull();
		assertThat(heartbeatController).isNotNull();
	}

}
