package com.acme.boot.mvc.repository;

import com.acme.boot.mvc.domain.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Integer> {
  @Query("from Order o inner join fetch o.lineItems l inner join fetch l.product where o.id = :id")
  Optional<Order> eagerFindById(@Param("id") int id);
}
