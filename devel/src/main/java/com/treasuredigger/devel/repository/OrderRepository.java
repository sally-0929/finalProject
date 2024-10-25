package com.treasuredigger.devel.repository;

import com.treasuredigger.devel.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    @Query("select o from Order o " +
            "where o.createdBy = :createBy " +
            "order by o.orderDate desc"
    )
    List<Order> findOrders(@Param("createBy") String createdBy, Pageable pageable);

    @Query("select count(o) from Order o " +
            "where o.createdBy = :createBy"
    )
    Long countOrder(@Param("createBy") String createBy);
}