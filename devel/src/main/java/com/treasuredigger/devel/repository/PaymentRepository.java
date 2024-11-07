package com.treasuredigger.devel.repository;

import com.treasuredigger.devel.entity.PaymentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<PaymentEntity, Long> {
    // 필요한 추가 쿼리 메서드를 작성할 수 있습니다.
}