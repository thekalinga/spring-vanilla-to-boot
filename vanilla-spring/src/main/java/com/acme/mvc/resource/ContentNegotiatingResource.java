package com.acme.mvc.resource;

import com.acme.mvc.domain.Product;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

import static java.util.Arrays.asList;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;
import static org.springframework.http.MediaType.TEXT_HTML_VALUE;

@Controller
@RequestMapping("/negotiate")
public class ContentNegotiatingResource {
  @GetMapping(produces = {APPLICATION_JSON_UTF8_VALUE, TEXT_HTML_VALUE})
   String getProducts(Model model) {
    Product productA = Product.builder().name("Product A").code("A").description("Very popular product").priceInInr(10).quantity(1).build();
    Product productB = Product.builder().name("Product B").code("B").description("Niche product").priceInInr(1000).quantity(9).build();
    Product productC = Product.builder().name("Product C").code("C").priceInInr(100).quantity(10).build();
    model.addAttribute(asList(productA, productB, productC));
    return "negotiate";
  }
}
