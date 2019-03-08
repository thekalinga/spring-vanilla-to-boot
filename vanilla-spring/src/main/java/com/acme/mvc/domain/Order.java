package com.acme.mvc.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static javax.persistence.CascadeType.ALL;
import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode(of = "id")
public class Order {
  @Id
  @GeneratedValue(strategy = IDENTITY)
  private int id;
  @Min(1)
  private int priceInInr;
  @Min(1)
  private int quantity;
  @Builder.Default
  @NotNull
  private LocalDateTime createdAt = LocalDateTime.now();

  @Builder.Default
  @OneToMany(mappedBy = "order", cascade = ALL)
  private List<LineItem> lineItems = new ArrayList<>();

  void addLineItem(LineItem lineItem) {
    lineItems.add(lineItem);
    quantity += lineItem.getQuantity();
    priceInInr += lineItem.getProduct().getPriceInInr() * lineItem.getQuantity();
    lineItem.setOrder(this);
  }

  public void updateLineItemReferences() {
    lineItems.forEach(lineItem -> lineItem.setOrder(this));
  }

  public void addLineItems(List<LineItem> lineItems) {
    lineItems.forEach(this::addLineItem);
  }
}
