package com.acme.boot.mvc.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@Configuration
class CustomWebApplicationContext {
  @Bean
  ObjectMapper objectMapper(Jackson2ObjectMapperBuilder builder) {
    builder.serializationInclusion(NON_NULL);
    return builder.build();
  }
}
