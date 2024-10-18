package com.treasuredigger.devel.repository;

import com.treasuredigger.devel.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

}