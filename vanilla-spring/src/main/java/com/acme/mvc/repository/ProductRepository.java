package com.acme.mvc.repository;

import com.acme.mvc.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Integer> {
  Optional<Product> findByCode(String code);
}
