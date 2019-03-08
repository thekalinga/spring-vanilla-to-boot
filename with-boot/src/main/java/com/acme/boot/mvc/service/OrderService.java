package com.acme.boot.mvc.service;

import com.acme.boot.mvc.domain.Order;
import com.acme.boot.mvc.resource.contract.OrderRequest;
import com.acme.boot.mvc.service.contract.OrderDetailResponse;
import com.acme.boot.mvc.service.contract.OrderSummaryResponse;

import java.util.Optional;
import java.util.stream.Stream;

public interface OrderService {
  Order saveOrder(OrderRequest request);
  Stream<OrderSummaryResponse> summaries();
  Optional<OrderDetailResponse> byCode(int id);
}
