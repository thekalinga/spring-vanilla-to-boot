package com.acme.boot.mvc.repository;

import com.acme.boot.mvc.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Integer> {
  Optional<Product> findByCode(String code);
}
