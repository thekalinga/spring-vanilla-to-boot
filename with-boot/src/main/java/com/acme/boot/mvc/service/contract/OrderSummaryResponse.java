package com.acme.boot.mvc.service.contract;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class OrderSummaryResponse {
  private int id;
  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
  private LocalDateTime createdAt;
  private int quantity;
  private int priceInInr;
}
