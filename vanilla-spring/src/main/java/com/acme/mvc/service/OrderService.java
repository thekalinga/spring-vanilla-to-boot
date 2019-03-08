package com.acme.mvc.service;

import com.acme.mvc.domain.Order;
import com.acme.mvc.resource.contract.OrderRequest;
import com.acme.mvc.service.contract.OrderDetailResponse;
import com.acme.mvc.service.contract.OrderSummaryResponse;

import java.util.Optional;
import java.util.stream.Stream;

public interface OrderService {
  Order saveOrder(OrderRequest request);
  Stream<OrderSummaryResponse> summaries();
  Optional<OrderDetailResponse> byCode(int id);
}
