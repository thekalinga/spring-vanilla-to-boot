package com.acme.boot.mvc.repository;

import com.acme.boot.mvc.domain.LineItem;
import org.springframework.data.jpa.repository.JpaRepository;

interface LineItemRepository extends JpaRepository<LineItem, Integer> {
}
