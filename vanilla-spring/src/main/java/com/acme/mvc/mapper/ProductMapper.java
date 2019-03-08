package com.acme.mvc.mapper;

import com.acme.mvc.domain.Product;
import com.acme.mvc.resource.contract.ProductDetailResponse;
import com.acme.mvc.resource.contract.ProductSummaryResponse;
import org.mapstruct.Mapper;

import static org.mapstruct.InjectionStrategy.CONSTRUCTOR;

@Mapper(injectionStrategy = CONSTRUCTOR)
public interface ProductMapper {
  ProductSummaryResponse toSummary(Product product);
  ProductDetailResponse toDetail(Product product);
}
