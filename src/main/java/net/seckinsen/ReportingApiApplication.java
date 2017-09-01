package net.seckinsen;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.client.RestTemplate;

@ComponentScan({"net.seckinsen"})
@EnableAutoConfiguration
@SpringBootApplication
public class ReportingApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(ReportingApiApplication.class, args);
    }

}