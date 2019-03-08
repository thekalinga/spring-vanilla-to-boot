package com.acme.mvc.config;

import com.acme.mvc.domain.LineItem;
import com.acme.mvc.domain.Order;
import com.acme.mvc.domain.Product;
import com.acme.mvc.repository.OrderRepository;
import com.acme.mvc.repository.ProductRepository;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;

import static java.util.Arrays.asList;

@Configuration
class InitializerConfiguration {
  @Bean
  InitializingBean dbInit(ProductRepository productRepository, OrderRepository orderRepository) {
    return () -> {
      Product productA = Product.builder().name("Product A").code("A").description("Very popular product").priceInInr(10).quantity(1).build();
      Product productB = Product.builder().name("Product B").code("B").description("Niche product").priceInInr(1000).quantity(9).build();
      Product productC = Product.builder().name("Product C").code("C").priceInInr(100).quantity(10).build();
      asList(productA, productB, productC).forEach(productRepository::save);
      LineItem lineItem1 = LineItem.builder().product(productA).quantity(9).build();
      LineItem lineItem2 = LineItem.builder().product(productB).quantity(1).build();
      int priceInInr = productA.getPriceInInr() * lineItem1.getQuantity() + productB.getPriceInInr() * lineItem2.getQuantity();
      Order order =
          Order.builder().lineItems(asList(lineItem1, lineItem2)).createdAt(LocalDateTime.now())
              .priceInInr(priceInInr).quantity(lineItem1.getQuantity() + lineItem2.getQuantity())
              .build();
      order.updateLineItemReferences();
      orderRepository.save(order);
    };
  }
}
