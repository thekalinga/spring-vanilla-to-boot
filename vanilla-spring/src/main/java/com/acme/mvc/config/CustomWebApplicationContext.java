package com.acme.mvc.config;

import com.acme.mvc.resource.OrderResource;
import com.acme.mvc.resource.ProductResource;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@Configuration
@EnableWebMvc
@Import({ProductResource.class, OrderResource.class})
class CustomWebApplicationContext implements WebMvcConfigurer {
  @Bean
  ObjectMapper objectMapper() {
    Jackson2ObjectMapperBuilder builder = Jackson2ObjectMapperBuilder.json();
    builder.serializationInclusion(NON_NULL);
    return builder.build();
  }
}
