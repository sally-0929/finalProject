package com.treasuredigger.devel.repository;

import com.treasuredigger.devel.constant.OrderStatus;
import com.treasuredigger.devel.entity.Member;
import com.treasuredigger.devel.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

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

    @Query("SELECT SUM(o.totalAmount) FROM Order o WHERE o.member = :member AND o.orderStatus <> :excludedStatus")
    long sumTotalByMemberAndOrderStatusNot(@Param("member") Member member, @Param("excludedStatus") OrderStatus excludedStatus);

    Optional<Order> findById(Long orderId);
}