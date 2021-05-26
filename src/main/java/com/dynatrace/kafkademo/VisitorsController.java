package com.dynatrace.kafkademo;

import com.dynatrace.kafkademo.util.Constants;
import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class VisitorsController {
        @Autowired
        private KafkaTemplate<String, String> template;

        Tracer tracer = GlobalOpenTelemetry.getTracer("KafkaTracer");

        @Bean
        public NewTopic visitorsTopic() {
                return TopicBuilder.name(Constants.VISITORS_TOPIC)
                        .partitions(1)
                        .replicas(1)
                        .build();
        }

        @GetMapping("/visit")
        public String sayHello(@RequestParam(name="name", required=false, defaultValue="Stranger") String name, Model model) {

                Span span = tracer
                        .spanBuilder("kafkaSpan")
                        .setAttribute("method", "sayHello")
                        .setAttribute("name", name)
                        .startSpan();

                model.addAttribute("name", name);

                this.template.send(Constants.VISITORS_TOPIC, name);

                return "visit";
        }
}
