package com.acme.boot.mvc.resource;

import com.acme.boot.mvc.mapper.ProductMapper;
import com.acme.boot.mvc.repository.ProductRepository;
import com.acme.boot.mvc.resource.contract.ProductDetailResponse;
import com.acme.boot.mvc.resource.contract.ProductSummaryResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.stream.Stream;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;

@RestController
@RequestMapping(value = "/products", produces = APPLICATION_JSON_UTF8_VALUE)
public class ProductResource {
  private final ProductRepository repository;
  private final ProductMapper mapper;

  public ProductResource(ProductRepository repository, ProductMapper mapper) {
    this.repository = repository;
    this.mapper = mapper;
  }

  @GetMapping
  ResponseEntity<Stream<ProductSummaryResponse>> summaries() {
    return ResponseEntity.ok().body(repository.findAll().stream().map(mapper::toSummary));
  }

  @GetMapping("/{code}")
  ResponseEntity<ProductDetailResponse> byCode(@PathVariable String code) {
    return repository.findByCode(code)
        .map(product -> ResponseEntity.ok().body(mapper.toDetail(product)))
        .orElse(ResponseEntity.notFound().build());
  }
}
