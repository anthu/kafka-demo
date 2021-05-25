package com.dynatrace.kafkademo;

import com.dynatrace.kafkademo.util.Constants;
import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class HelloWorldController {

        @Autowired
        private KafkaTemplate<String, String> template;

        Tracer tracer = GlobalOpenTelemetry.getTracer("KafkaTracer");

        @GetMapping("/hello-world")
        public String sayHello(@RequestParam(name="name", required=false, defaultValue="Stranger") String name, Model model) {

                Span span = tracer
                        .spanBuilder("kafkaSpan")
                        .setAttribute("method", "sayHello")
                        .setAttribute("name", name)
                        .startSpan();

                model.addAttribute("name", name);

                this.template.send(Constants.VISITORS_TOPIC, name);

                return "hello-world";
        }


}
