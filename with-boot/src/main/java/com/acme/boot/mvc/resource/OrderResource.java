package com.acme.boot.mvc.resource;

import com.acme.boot.mvc.domain.Order;
import com.acme.boot.mvc.resource.contract.OrderRequest;
import com.acme.boot.mvc.service.OrderService;
import com.acme.boot.mvc.service.contract.OrderDetailResponse;
import com.acme.boot.mvc.service.contract.OrderSummaryResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import javax.validation.Valid;
import java.util.stream.Stream;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;

@RestController
@RequestMapping(value = "/orders", produces = APPLICATION_JSON_UTF8_VALUE)
public class OrderResource {
  private final OrderService service;

  OrderResource(OrderService service) {
    this.service = service;
  }

  @GetMapping
  ResponseEntity<Stream<OrderSummaryResponse>> summaries() {
    return ResponseEntity.ok().body(service.summaries());
  }

  @GetMapping("/{id}")
  ResponseEntity<OrderDetailResponse> byCode(@PathVariable int id) {
    return service.byCode(id)
        .map(summary -> ResponseEntity.ok().body(summary))
        .orElse(ResponseEntity.notFound().build());
  }

  @PostMapping(consumes = APPLICATION_JSON_UTF8_VALUE)
  ResponseEntity<?> save(@Valid @RequestBody OrderRequest request, UriComponentsBuilder componentsBuilder) {
    try {
      Order order = service.saveOrder(request);
      return ResponseEntity.created(componentsBuilder.path("/orders/{id}").buildAndExpand(order.getId()).toUri())
          .build();
    } catch (IllegalArgumentException e) {
      return ResponseEntity.badRequest().body(e.getMessage());
    }
  }
}
