package com.treasuredigger.devel.repository;

import com.treasuredigger.devel.entity.RefundReason;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefundReasonRepository extends JpaRepository<RefundReason, Long> {
}