package com.dynatrace.kafkademo.Heartbeat;

import com.dynatrace.kafkademo.util.Constants;
import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.UUID;

@Controller
public class HeartbeatController {
    Tracer tracer = GlobalOpenTelemetry.getTracer("HeartbeatTracer");

    @Value("${spring.kafka.bootstrap-servers:'localhost:9092'}")
    private String bootstrapServer;

    @GetMapping("/heartbeat")
    public String getHeartbeats(@RequestParam(name="count", required=false, defaultValue="10") String count, Model model) {
        Span span = tracer.spanBuilder("heartbeatSpan").startSpan();
        span.setAttribute("requestId", UUID.randomUUID().toString());
        int recCount = 10;
        try {
            recCount = Integer.parseInt(count);
        } catch (NumberFormatException e) {
            System.out.println("Could not parse count from parameters - falling back to default");
        }

        KafkaConsumer<String, String> kafkaConsumer = new KafkaConsumer<>(this.getConsumerProperties());

        TopicPartition topicPartition = new TopicPartition(Constants.HEARTBEAT_TOPIC, 0);
        List<TopicPartition> topicPartitionList = new ArrayList<>();
        topicPartitionList.add(topicPartition);

        kafkaConsumer.assign(topicPartitionList);
        kafkaConsumer.poll(Duration.ofMillis(1));

        kafkaConsumer.seekToEnd(topicPartitionList);
        long endPosition = kafkaConsumer.position(topicPartition);
        long recentMessagesStartPosition = Math.max(0, endPosition - recCount);

        kafkaConsumer.seek(topicPartition, recentMessagesStartPosition);

        List<String> res = new ArrayList<>();
        ConsumerRecords<String, String> records = kafkaConsumer.poll(Duration.ofMillis(100));
        for (ConsumerRecord<String, String> record : records) {
            res.add(Heartbeat.extractHeartbeatFromRecord(record));
        }

        model.addAttribute("heartbeats", res);
        span.end();
        return "heartbeat";
    }



    private Properties getConsumerProperties() {
        Properties props = new Properties();

        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, this.bootstrapServer);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "heartbeat");
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, org.apache.kafka.common.serialization.StringDeserializer.class);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, org.apache.kafka.common.serialization.StringDeserializer.class);

        return props;
    }
}
