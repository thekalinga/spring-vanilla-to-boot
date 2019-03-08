package com.acme.boot.mvc.service;

import com.acme.boot.mvc.domain.LineItem;
import com.acme.boot.mvc.domain.Order;
import com.acme.boot.mvc.domain.Product;
import com.acme.boot.mvc.mapper.OrderMapper;
import com.acme.boot.mvc.repository.OrderRepository;
import com.acme.boot.mvc.repository.ProductRepository;
import com.acme.boot.mvc.resource.contract.LineItemRequest;
import com.acme.boot.mvc.resource.contract.OrderRequest;
import com.acme.boot.mvc.service.contract.OrderDetailResponse;
import com.acme.boot.mvc.service.contract.OrderSummaryResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Service
@Transactional(readOnly = true)
public class OrderServiceImpl implements OrderService {

  private final OrderRepository repository;
  private final OrderMapper mapper;
  private final ProductRepository productRepository;

  public OrderServiceImpl(OrderRepository repository, OrderMapper mapper, ProductRepository productRepository) {
    this.repository = repository;
    this.mapper = mapper;
    this.productRepository = productRepository;
  }

  @Override
  @Transactional
  public Order saveOrder(OrderRequest request) {
    Order.OrderBuilder builder = Order.builder();
    List<LineItem> lineItems = new ArrayList<>();
    for (LineItemRequest itemRequest : request.getLineItems()) {
      Product product = productRepository.findByCode(itemRequest.getProductCode())
          .orElseThrow(() -> new IllegalArgumentException("Invalid product code " + itemRequest.getProductCode() + " specified"));
      if (product.getQuantity() < itemRequest.getQuantity()) {
        throw new IllegalArgumentException("Requested more quantity for product: " + product.getName() + ". Available: " +product
            .getQuantity() + "; Requested: " + itemRequest.getQuantity());
      }
      LineItem lineItem = LineItem.builder().product(product).quantity(itemRequest.getQuantity()).build();
      product.reduceQuantityBy(lineItem.getQuantity());
      lineItems.add(lineItem);
      productRepository.save(product);
    }
    Order order = builder.build();
    order.addLineItems(lineItems);
    return repository.save(order);
  }

  @Override
  public Stream<OrderSummaryResponse> summaries() {
    return repository.findAll().stream().map(mapper::toSummary);
  }

  @Override
  public Optional<OrderDetailResponse> byCode(int id) {
    return repository.eagerFindById(id)
        .map(mapper::toDetail);
  }

}
