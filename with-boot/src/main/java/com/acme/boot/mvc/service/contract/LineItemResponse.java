package com.acme.boot.mvc.service.contract;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class LineItemResponse {
  private String productName;
  private String productCode;
  private int quantity;
}
