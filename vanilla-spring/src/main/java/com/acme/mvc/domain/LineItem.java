package com.acme.mvc.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.Min;

import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class LineItem {
  @Id
  @GeneratedValue(strategy = IDENTITY)
  private int id;
  @Min(1)
  private int quantity;

  @ManyToOne(optional = false)
  @JoinColumn
  private Order order;

  @ManyToOne(optional = false)
  @JoinColumn
  private Product product;

  void setOrder(Order order) {
    this.order = order;
  }
}
