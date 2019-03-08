package com.acme.mvc.repository;

import com.acme.mvc.domain.LineItem;
import org.springframework.data.jpa.repository.JpaRepository;

interface LineItemRepository extends JpaRepository<LineItem, Integer> {
}
