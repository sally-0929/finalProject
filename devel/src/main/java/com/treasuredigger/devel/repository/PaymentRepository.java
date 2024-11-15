package com.treasuredigger.devel.repository;

import com.treasuredigger.devel.entity.PaymentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<PaymentEntity, Long> {
    Optional<PaymentEntity> findByMerchantUid(String merchantUid);
    Optional<PaymentEntity> findById(Long paymentId);
    Optional<PaymentEntity> findByOrderId(Long orderId);
}