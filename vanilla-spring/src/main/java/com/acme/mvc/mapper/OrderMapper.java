package com.acme.mvc.mapper;

import com.acme.mvc.domain.LineItem;
import com.acme.mvc.domain.Order;
import com.acme.mvc.service.contract.LineItemResponse;
import com.acme.mvc.service.contract.OrderDetailResponse;
import com.acme.mvc.service.contract.OrderSummaryResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import static org.mapstruct.InjectionStrategy.CONSTRUCTOR;

@Mapper(injectionStrategy = CONSTRUCTOR)
public interface OrderMapper {
  OrderSummaryResponse toSummary(Order order);
  OrderDetailResponse toDetail(Order order);
  @Mapping(target = "productName", source = "product.name")
  @Mapping(target = "productCode", source = "product.code")
  LineItemResponse toResponse(LineItem lineItem);
}
