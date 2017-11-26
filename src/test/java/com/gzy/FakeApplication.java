package com.gzy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication
@EnableAutoConfiguration
@EnableConfigurationProperties
@PropertySource("classpath:application.properties")
public class FakeApplication {
    public static void main(String[] args) {
        SpringApplication.run(FakeApplication.class, args);
    }

}