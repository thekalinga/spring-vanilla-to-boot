package com.acme.mvc.config;

import com.acme.mvc.mapper.OrderMapperImpl;
import com.acme.mvc.mapper.ProductMapperImpl;
import com.acme.mvc.service.OrderServiceImpl;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({OrderServiceImpl.class, PersistenceConfiguration.class, OrderMapperImpl.class, ProductMapperImpl.class, InitializerConfiguration.class})
class RootConfiguration {
}
