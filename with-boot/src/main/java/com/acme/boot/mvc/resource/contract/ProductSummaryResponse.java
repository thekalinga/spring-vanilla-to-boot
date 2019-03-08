package com.acme.boot.mvc.resource.contract;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@AllArgsConstructor
@ToString
public class ProductSummaryResponse {
  private int id;
  private String name;
  private String code;
  private String description;
}
