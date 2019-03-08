package com.acme.mvc.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;
import javax.persistence.Version;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode(of = "id")
@Table(indexes = {@Index(columnList = "code", unique = true)})
public class Product {
  @Id
  @GeneratedValue(strategy = IDENTITY)
  private int id;
  @Version
  private int version;
  @NotNull
  @Size(min = 1)
  private String name;
  private String description;
  @NotNull
  @Column(unique = true)
  private String code;
  @Min(0)
  private int quantity;
  @Min(1)
  private int priceInInr;

  public void reduceQuantityBy(int quantity) {
    this.quantity -= quantity;
  }
}
