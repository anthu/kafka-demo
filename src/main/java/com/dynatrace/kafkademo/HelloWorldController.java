package com.dynatrace.kafkademo;

import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class HelloWorldController {
        @Autowired
        private KafkaTemplate<String, String> template;

        @GetMapping("/hello-world")
        public String sayHello(@RequestParam(name="name", required=false, defaultValue="Stranger") String name, Model model) {
                model.addAttribute("name", name);
                this.template.send("topic1", name);
                return "hello-world";
        }
}
