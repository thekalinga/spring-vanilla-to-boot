package com.acme.mvc.config;

import com.acme.mvc.resource.ContentNegotiatingResource;
import com.acme.mvc.resource.OrderResource;
import com.acme.mvc.resource.ProductResource;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ViewResolverRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.view.ContentNegotiatingViewResolver;
import org.springframework.web.servlet.view.InternalResourceView;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.JstlView;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;
import static org.springframework.http.MediaType.TEXT_HTML;

@Configuration
@EnableWebMvc
@Import({ProductResource.class, OrderResource.class, ContentNegotiatingResource.class})
class CustomWebApplicationContext implements WebMvcConfigurer {
  @Bean
  ObjectMapper objectMapper() {
    Jackson2ObjectMapperBuilder builder = Jackson2ObjectMapperBuilder.json();
    builder.serializationInclusion(NON_NULL);
    return builder.build();
  }

  @Override
  public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
    configurer.defaultContentType(TEXT_HTML);
  }

  @Override
  public void configureViewResolvers(ViewResolverRegistry registry) {
    registry.jsp("/view/", ".jsp").viewClass(JstlView.class);
    registry.viewResolver((viewName, locale) -> new MappingJackson2JsonView(objectMapper()));
    registry.enableContentNegotiation();
  }

}
