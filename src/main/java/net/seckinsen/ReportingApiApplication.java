package net.seckinsen;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan({"net.seckinsen"})
@SpringBootApplication
public class ReportingApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(ReportingApiApplication.class, args);
    }

}