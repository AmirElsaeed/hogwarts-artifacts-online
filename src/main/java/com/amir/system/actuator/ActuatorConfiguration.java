package com.amir.system.actuator;

import org.springframework.boot.actuate.web.exchanges.HttpExchangeRepository;
import org.springframework.boot.actuate.web.exchanges.InMemoryHttpExchangeRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ActuatorConfiguration {
    @Bean
    public HttpExchangeRepository httpExchangeRepository() {
        InMemoryHttpExchangeRepository repository = new InMemoryHttpExchangeRepository();
        return repository;
    }
}
