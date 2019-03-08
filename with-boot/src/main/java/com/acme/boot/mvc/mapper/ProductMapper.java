package com.acme.boot.mvc.mapper;

import com.acme.boot.mvc.domain.Product;
import com.acme.boot.mvc.resource.contract.ProductDetailResponse;
import com.acme.boot.mvc.resource.contract.ProductSummaryResponse;
import org.mapstruct.Mapper;

import static org.mapstruct.InjectionStrategy.CONSTRUCTOR;

@Mapper(injectionStrategy = CONSTRUCTOR)
public interface ProductMapper {
  ProductSummaryResponse toSummary(Product product);
  ProductDetailResponse toDetail(Product product);
}
